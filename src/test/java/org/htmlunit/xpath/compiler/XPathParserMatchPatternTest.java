package org.htmlunit.xpath.compiler;

import org.junit.jupiter.api.Test;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression tests for the missing {@link OpMap#reset()} call in
 * {@link XPathParser#initMatchPattern}.
 *
 * <h2>Root cause</h2>
 * {@code Lexer.tokenize()} appends to the existing token queue rather than
 * replacing it, and {@link XPathParser#Literal()} mutates queue slots from
 * {@code String} to {@code XString}. If the same {@code Compiler} instance is
 * reused across two {@code initMatchPattern} calls without a {@code reset()},
 * {@code nextToken()} will cast an {@code XString} slot to {@code String}
 * → {@code ClassCastException}.
 *
 * <h2>Difference from initXPath</h2>
 * {@code initMatchPattern} has no {@code CONTINUE_AFTER_FATAL_ERROR} catch
 * block and no fallback recursion, so no recursion guard ({@code m_inFallbackXPath})
 * is needed. The only required change is {@code compiler.reset()} before
 * {@code tokenize()}.
 */
class XPathParserMatchPatternTest {

    private static final ErrorListener SWALLOWING_LISTENER = new ErrorListener() {
        @Override public void warning(TransformerException e) {}
        @Override public void error(TransformerException e) {}
        @Override public void fatalError(TransformerException e) { /* swallow */ }
    };

    // -----------------------------------------------------------------------
    // Test 1 — primary regression: reusing a Compiler across two
    // initMatchPattern calls must not ClassCastException.
    //
    // The first call parses a pattern with a string literal, mutating a queue
    // slot to XString. Without reset(), the second call's nextToken() hits
    // that slot and throws.
    // -----------------------------------------------------------------------
    @Test
    void initMatchPattern_compilerReuse_mustNotClassCast() {
        XPathParser parser  = new XPathParser(SWALLOWING_LISTENER);
        Compiler   compiler = new Compiler(SWALLOWING_LISTENER, null);

        // First call: the string literal 'a' causes Literal() to mutate a
        // queue slot from String → XString.
        assertDoesNotThrow(() ->
            parser.initMatchPattern(compiler, "foo[@id='a']", null),
            "First initMatchPattern call should succeed"
        );

        // Second call on the same Compiler: without reset() the poisoned slot
        // survives into tokenize() and nextToken() ClassCastExceptions.
        try {
            parser.initMatchPattern(compiler, "bar[@class='b']", null);
        } catch (ClassCastException e) {
            fail("Bug reproduced: Compiler not reset before second initMatchPattern → "
                 + "ClassCastException: " + e.getMessage());
        } catch (TransformerException ignored) {
            // Acceptable — any proper parse error is fine.
        }
    }

    // -----------------------------------------------------------------------
    // Test 2 — multiple literals: more mutated slots increases the likelihood
    // of hitting the bug. Verify all are cleared by reset().
    // -----------------------------------------------------------------------
    @Test
    void initMatchPattern_multipleLiterals_compilerReuse_mustNotClassCast() {
        XPathParser parser  = new XPathParser(SWALLOWING_LISTENER);
        Compiler   compiler = new Compiler(SWALLOWING_LISTENER, null);

        assertDoesNotThrow(() ->
            parser.initMatchPattern(compiler, "foo[@a='x' and @b='y']", null),
            "First call with multiple literals should succeed"
        );

        try {
            parser.initMatchPattern(compiler, "bar[@c='z']", null);
        } catch (ClassCastException e) {
            fail("ClassCastException on reuse with multiple prior literals: " + e.getMessage());
        } catch (TransformerException ignored) {}
    }

    // -----------------------------------------------------------------------
    // Test 3 — three sequential calls on the same Compiler, each with
    // literals. Every call must leave the Compiler in a state fit for reuse.
    // -----------------------------------------------------------------------
    @Test
    void initMatchPattern_threeSequentialCalls_allSucceed() {
        XPathParser parser  = new XPathParser(SWALLOWING_LISTENER);
        Compiler   compiler = new Compiler(SWALLOWING_LISTENER, null);

        String[] patterns = {
            "foo[@id='first']",
            "bar[@id='second']",
            "baz[@id='third']"
        };

        for (int i = 0; i < patterns.length; i++) {
            final String pat = patterns[i];
            final int    idx = i;
            try {
                parser.initMatchPattern(compiler, pat, null);
            } catch (ClassCastException e) {
                fail("ClassCastException on call " + (idx + 1) + " (\"" + pat + "\"): "
                     + e.getMessage());
            } catch (TransformerException ignored) {}
        }
    }

    // -----------------------------------------------------------------------
    // Test 4 — happy path: a fresh Compiler with a valid pattern must parse
    // without error. Ensures reset() doesn't break the normal case.
    // -----------------------------------------------------------------------
    @Test
    void initMatchPattern_validPattern_parsesWithoutError() {
        XPathParser parser  = new XPathParser(SWALLOWING_LISTENER);
        Compiler   compiler = new Compiler(SWALLOWING_LISTENER, null);

        assertDoesNotThrow(() ->
            parser.initMatchPattern(compiler, "//foo/bar", null)
        );
    }

    // -----------------------------------------------------------------------
    // Test 5 — initXPath and initMatchPattern interleaved on the same
    // Compiler. Each must reset cleanly regardless of which method ran last.
    // -----------------------------------------------------------------------
    @Test
    void initXPath_thenInitMatchPattern_onSameCompiler_mustNotClassCast() {
        XPathParser parser  = new XPathParser(SWALLOWING_LISTENER);
        Compiler   compiler = new Compiler(SWALLOWING_LISTENER, null);

        try {
            parser.initXPath(compiler, "concat('a', 'b')", null);
        } catch (TransformerException ignored) {}

        try {
            parser.initMatchPattern(compiler, "foo[@id='x']", null);
        } catch (ClassCastException e) {
            fail("ClassCastException when initMatchPattern follows initXPath: "
                 + e.getMessage());
        } catch (TransformerException ignored) {}
    }
}
