package org.htmlunit.xpath.compiler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import org.htmlunit.xpath.xml.utils.PrefixResolver;
import org.junit.jupiter.api.Test;

/**
 * Tests for the two interlocked bugs in {@link XPathParser#initXPath} that
 * together produce a {@link ClassCastException} (and in degenerate cases
 * unbounded recursion) when a swallowing {@link ErrorListener} is installed
 * and a malformed expression containing string literals is parsed.
 *
 * <h2>Root causes</h2>
 * <ol>
 *   <li><b>Bug A — no recursion guard.</b>
 *       {@code consumeExpected()} always throws
 *       {@code XPathProcessorException(CONTINUE_AFTER_FATAL_ERROR)} regardless
 *       of whether the {@code ErrorListener} swallowed the error or rethrew it.
 *       The catch block calls {@code initXPath} unconditionally, so if the
 *       fallback {@code "/.."} also fails, the stack grows without bound.</li>
 *
 *   <li><b>Bug B — Compiler not reset before fallback.</b>
 *       {@code Lexer.tokenize()} <em>appends</em> to the existing token queue
 *       on the shared {@code Compiler} rather than replacing it. When
 *       {@code Literal()} has already run it mutates queue slots from
 *       {@code String} to {@code XString}. The fallback parse then hits those
 *       poisoned slots in {@code nextToken()}, which hard-casts to
 *       {@code String} → {@code ClassCastException}.</li>
 * </ol>
 *
 * <h2>Fix — two parts, applied together</h2>
 * <pre>
 * // At the top of initXPath(), BEFORE Lexer construction:
 * compiler.getTokenQueue().removeAllElements();   // purge poisoned XString slots
 * compiler.getOpMap().setSize(0);                 // reset op-map array
 *
 * // In the CONTINUE_AFTER_FATAL_ERROR catch block:
 * if (m_inFallbackXPath) {
 *     return;                      // Bug A: do not recurse again
 * }
 * m_inFallbackXPath = true;
 * try {
 *     initXPath(compiler, "/..", namespaceContext);
 * } finally {
 *     m_inFallbackXPath = false;
 * }
 * </pre>
 *
 * Resetting at the top of {@code initXPath} (rather than inside the catch) is
 * correct because {@code Lexer.tokenize()} is called before the try/catch, so
 * the queue must be clean before tokenization, not after the exception is caught.
 */
class XPathParserRecursionTest {

    /**
     * Swallows all fatal errors. This is the configuration that exposes both bugs:
     * because it does not rethrow, {@code consumeExpected()} still throws
     * {@code CONTINUE_AFTER_FATAL_ERROR} and the catch block is reached.
     */
    private static final ErrorListener SWALLOWING_LISTENER = new ErrorListener() {
        @Override public void warning(TransformerException e) {}
        @Override public void error(TransformerException e) {}
        @Override public void fatalError(TransformerException e) { /* swallow */ }
    };

    // -----------------------------------------------------------------------
    // Test 1 — primary regression.
    //
    // Failure sequence without fix:
    //   1. Literal() mutates 'a' and 'b' slots from String → XString.
    //   2. Missing ')' fires consumeExpected() → CONTINUE_AFTER_FATAL_ERROR.
    //   3. Catch block calls initXPath(compiler, "/..", ...) — same Compiler.
    //   4. tokenize("/..")  appends to the queue; poisoned slots survive.
    //   5. nextToken() casts an XString slot to String → ClassCastException.
    //
    // With the fix both bugs are suppressed and the call terminates cleanly.
    // -----------------------------------------------------------------------
    @Test
    void initXPath_malformedExpressionWithLiterals_mustNotCrashOrRecurse() {
        XPathParser parser = new XPathParser(SWALLOWING_LISTENER);
        Compiler   compiler = new Compiler(SWALLOWING_LISTENER, null);

        // Two string literals ensure Literal() mutates two queue slots.
        // The missing ')' triggers consumeExpected() to throw.
        final String bad = "concat('a', 'b'";

        assertTimeoutPreemptively(
            java.time.Duration.ofSeconds(5),
            () -> {
                try {
                    parser.initXPath(compiler, bad, null);
                } catch (StackOverflowError e) {
                    fail("Bug A reproduced: unbounded recursion → StackOverflowError");
                } catch (ClassCastException e) {
                    fail("Bug B reproduced: poisoned Compiler reused in fallback → "
                         + "ClassCastException: " + e.getMessage());
                } catch (TransformerException ok) {
                    // Acceptable: error was surfaced via the normal exception path.
                }
            }
        );
    }

    // -----------------------------------------------------------------------
    // Test 2 — recursion depth.
    //
    // initXPath must be entered at most twice:
    //   call 1 — the original expression
    //   call 2 — the single allowed fallback "/.."
    //   call 3+ — must never happen
    // -----------------------------------------------------------------------
    @Test
    void initXPath_fallbackAttemptedAtMostOnce() {
        AtomicInteger callCount = new AtomicInteger(0);

        XPathParser parser = new XPathParser(SWALLOWING_LISTENER) {
            @Override
            public void initXPath(Compiler compiler, String expression,
                                  PrefixResolver ns) throws TransformerException {
                callCount.incrementAndGet();
                super.initXPath(compiler, expression, ns);
            }
        };

        Compiler compiler = new Compiler(SWALLOWING_LISTENER, null);

        try {
            parser.initXPath(compiler, "concat('a', 'b'", null);
        } catch (StackOverflowError e) {
            fail("Bug A reproduced: unbounded recursion");
        } catch (ClassCastException e) {
            fail("Bug B reproduced: Compiler state not reset before fallback tokenize");
        } catch (TransformerException ignored) {}

        assertTrue(
            callCount.get() <= 2,
            "initXPath entered " + callCount.get() + " times — expected ≤ 2"
        );
    }

    // -----------------------------------------------------------------------
    // Test 3 — Compiler queue is clean when fallback parse starts.
    //
    // Specifically detects a ClassCastException inside any invocation of
    // initXPath, including the recursive fallback call.
    // -----------------------------------------------------------------------
    @Test
    void initXPath_fallbackMustNotSeeXStringQueueSlots() {
        AtomicInteger classcastCount = new AtomicInteger(0);

        XPathParser parser = new XPathParser(SWALLOWING_LISTENER) {
            @Override
            public void initXPath(Compiler compiler, String expression,
                                  PrefixResolver ns) throws TransformerException {
                try {
                    super.initXPath(compiler, expression, ns);
                } catch (ClassCastException e) {
                    classcastCount.incrementAndGet();
                    throw e;
                }
            }
        };

        Compiler compiler = new Compiler(SWALLOWING_LISTENER, null);

        try {
            parser.initXPath(compiler, "concat('hello', 'world'", null);
        } catch (ClassCastException e) {
            fail("Bug B reproduced: XString slot reached by nextToken() in fallback. "
                 + "Queue must be cleared before fallback tokenize().");
        } catch (TransformerException ignored) {}

        assertEquals(0, classcastCount.get(),
            "ClassCastException count must be 0 after fix");
    }

    // -----------------------------------------------------------------------
    // Test 4 — single-literal boundary: one mutated slot is sufficient.
    // -----------------------------------------------------------------------
    @Test
    void initXPath_singleLiteralMalformed_mustNotCrash() {
        XPathParser parser   = new XPathParser(SWALLOWING_LISTENER);
        Compiler   compiler  = new Compiler(SWALLOWING_LISTENER, null);

        try {
            parser.initXPath(compiler, "concat('a'", null);
        } catch (StackOverflowError e) {
            fail("Bug A reproduced: StackOverflowError");
        } catch (ClassCastException e) {
            fail("Bug B reproduced: ClassCastException: " + e.getMessage());
        } catch (TransformerException ignored) {}
    }

    // -----------------------------------------------------------------------
    // Test 5 — happy path must be completely unaffected.
    // -----------------------------------------------------------------------
    @Test
    void initXPath_validExpression_parsesWithoutError() {
        XPathParser parser  = new XPathParser(SWALLOWING_LISTENER);
        Compiler   compiler = new Compiler(SWALLOWING_LISTENER, null);

        assertDoesNotThrow(() ->
            parser.initXPath(compiler, "//foo/bar[@id='x']", null)
        );
    }

    // -----------------------------------------------------------------------
    // Test 6 — non-CONTINUE exceptions must still propagate.
    //           Regression guard for the `else { throw e; }` branch.
    // -----------------------------------------------------------------------
    @Test
    void initXPath_nonContinueException_propagatesNormally() {
        ErrorListener rethrowingListener = new ErrorListener() {
            @Override public void warning(TransformerException e) {}
            @Override public void error(TransformerException e) {}
            @Override public void fatalError(TransformerException e) throws TransformerException {
                throw e;
            }
        };

        XPathParser parser  = new XPathParser(rethrowingListener);
        Compiler   compiler = new Compiler(rethrowingListener, null);

        assertThrows(TransformerException.class,
            () -> parser.initXPath(compiler, "concat('a', 'b'", null)
        );
    }
}