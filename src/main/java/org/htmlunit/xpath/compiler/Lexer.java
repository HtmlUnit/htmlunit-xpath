/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.htmlunit.xpath.compiler;

import javax.xml.transform.TransformerException;

import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.xml.utils.PrefixResolver;

/**
 * This class is in charge of lexical processing of the XPath expression into
 * tokens.
 *
 * @author Apache Xalan
 * @author Ronald Brill
 */
class Lexer {

    /** The target XPath. */
    private final Compiler m_compiler;

    /** The prefix resolver to map prefixes to namespaces in the XPath. */
    private final PrefixResolver m_namespaceContext;

    /** The XPath processor object. */
    private final XPathParser m_processor;

    /**
     * Create a Lexer object.
     *
     * @param compiler       The owning compiler for this lexer.
     * @param resolver       The prefix resolver for mapping qualified name prefixes
     *                       to namespace URIs.
     * @param xpathProcessor The parser that is processing strings to opcodes.
     */
    Lexer(final Compiler compiler, final PrefixResolver resolver, final XPathParser xpathProcessor) {
        m_compiler = compiler;
        m_namespaceContext = resolver;
        m_processor = xpathProcessor;
    }

    /**
     * Walk through the expression and build a token queue.
     *
     * @param pat XSLT Expression.
     * @throws TransformerException if any
     */
    void tokenize(final String pat) throws TransformerException {

        m_compiler.currentPattern = pat;
        m_patternMapSize = 0;

        // This needs to grow too. Use a conservative estimate that the OpMapVector
        // needs about five time the length of the input path expression - to a
        // maximum of MAXTOKENQUEUESIZE*5. If the OpMapVector needs to grow, grow
        // it freely (second argument to constructor).
        final int initTokQueueSize = (Math.min(pat.length(), OpMap.MAXTOKENQUEUESIZE)) * 5;
        m_compiler.opMap = new OpMapVector(initTokQueueSize, OpMap.BLOCKTOKENQUEUESIZE * 5, OpMap.MAPINDEX_LENGTH);

        final int nChars = pat.length();
        int startSubstring = -1;
        int posOfNSSep = -1;
        boolean isNum = false;
        int nesting = 0;

        for (int i = 0; i < nChars; i++) {
            char c = pat.charAt(i);

            switch (c) {
            case '\"':
                if (startSubstring != -1) {
                    isNum = false;

                    if (-1 != posOfNSSep) {
                        posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
                    }
                    else {
                        addToTokenQueue(pat.substring(startSubstring, i));
                    }
                }

                startSubstring = i;

                for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\"'); i++) {
                    // empty
                }

                if (c == '\"' && i < nChars) {
                    addToTokenQueue(pat.substring(startSubstring, i + 1));
                    startSubstring = -1;
                }
                else {
                    m_processor.error(XPATHErrorResources.ER_EXPECTED_DOUBLE_QUOTE, null);
                }
                break;

            case '\'':
                if (startSubstring != -1) {
                    isNum = false;

                    if (-1 != posOfNSSep) {
                        posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
                    }
                    else {
                        addToTokenQueue(pat.substring(startSubstring, i));
                    }
                }

                startSubstring = i;

                for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\''); i++) {
                    // empty
                }

                if (c == '\'' && i < nChars) {
                    addToTokenQueue(pat.substring(startSubstring, i + 1));
                    startSubstring = -1;
                }
                else {
                    m_processor.error(XPATHErrorResources.ER_EXPECTED_SINGLE_QUOTE, null);
                }
                break;

            case 0x0A:
            case 0x0D:
            case ' ':
            case '\t':
                if (startSubstring != -1) {
                    isNum = false;

                    if (-1 != posOfNSSep) {
                        posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
                    }
                    else {
                        addToTokenQueue(pat.substring(startSubstring, i));
                    }

                    startSubstring = -1;
                }
                break;

            case '@':
                // fall-through on purpose
            case '-':
                if ('-' == c) {
                    if (!(isNum || (startSubstring == -1))) {
                        break;
                    }
                    isNum = false;
                }

                // fall-through on purpose
            case '(':
            case '[':
            case ')':
            case ']':
            case '|':
            case '/':
            case '*':
            case '+':
            case '=':
            case ',':
            case '\\': // Unused at the moment
            case '^':  // Unused at the moment
            case '!':  // Unused at the moment
            case '$':
            case '<':
            case '>':
                if (startSubstring != -1) {
                    isNum = false;

                    if (-1 != posOfNSSep) {
                        posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
                    }
                    else {
                        addToTokenQueue(pat.substring(startSubstring, i));
                    }

                    startSubstring = -1;
                }

                if ((')' == c) || (']' == c)) {
                    nesting--;
                }
                else if (('(' == c) || ('[' == c)) {
                    nesting++;
                }

                addToTokenQueue(pat.substring(i, i + 1));
                break;

            case ':':
                if (i > 0) {
                    if (posOfNSSep == (i - 1)) {
                        if (startSubstring != -1) {
                            if (startSubstring < (i - 1)) {
                                addToTokenQueue(pat.substring(startSubstring, i - 1));
                            }
                        }

                        isNum = false;
                        startSubstring = -1;
                        posOfNSSep = -1;

                        addToTokenQueue(pat.substring(i - 1, i + 1));
                        break;
                    }
                    posOfNSSep = i;
                }

                // fall through on purpose
            default:
                if (-1 == startSubstring) {
                    startSubstring = i;
                    isNum = Character.isDigit(c);
                }
                else if (isNum) {
                    isNum = Character.isDigit(c);
                }
            }
        }

        if (startSubstring != -1) {
            isNum = false;

            if ((-1 != posOfNSSep) || ((m_namespaceContext != null) && (m_namespaceContext.handlesNullPrefixes()))) {
                posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, nChars);
            }
            else {
                addToTokenQueue(pat.substring(startSubstring, nChars));
            }
        }

        if (0 == m_compiler.getTokenQueueSize()) {
            m_processor.error(XPATHErrorResources.ER_EMPTY_EXPRESSION, null);
        }

        m_processor.m_queueMark = 0;
    }

    /**
     * Given a string, return the corresponding keyword token.
     *
     * @param key The keyword.
     * @return An opcode value.
     */
    final int getKeywordToken(final String key) {
        int tok;

        try {
            final Integer itok = Keywords.getKeyWord(key);
            tok = (null != itok) ? itok.intValue() : 0;
        }
        catch (NullPointerException | ClassCastException npe) {
            tok = 0;
        }

        return tok;
    }

    /**
     * Add a token to the token queue.
     *
     * @param s The token.
     */
    private void addToTokenQueue(final String s) {
        m_compiler.getTokenQueue().add(s);
    }

    /**
     * When a separator token is found, see if there's a element name or the like to map.
     *
     * @param pat            The XPath name string.
     * @param startSubstring The start of the name string.
     * @param posOfNSSep     The position of the namespace separator (':').
     * @param posOfScan      The end of the name index.
     * @throws TransformerException if any
     * @return -1 always.
     */
    private int mapNSTokens(final String pat, final int startSubstring, final int posOfNSSep, final int posOfScan)
            throws TransformerException {

        String prefix = "";

        if ((startSubstring >= 0) && (posOfNSSep >= 0)) {
            prefix = pat.substring(startSubstring, posOfNSSep);
        }
        String uName;

        if ((null != m_namespaceContext) && !"*".equals(prefix) && !"xmlns".equals(prefix)) {
            try {
                if (prefix.length() > 0) {
                    uName = m_namespaceContext.getNamespaceForPrefix(prefix);
                }
                else {
                    uName = m_namespaceContext.getNamespaceForPrefix(prefix);
                }
            }
            catch (final ClassCastException cce) {
                uName = m_namespaceContext.getNamespaceForPrefix(prefix);
            }
        }
        else {
            uName = prefix;
        }

        if ((null != uName) && (uName.length() > 0)) {
            addToTokenQueue(uName);
            addToTokenQueue(":");

            final String s = pat.substring(posOfNSSep + 1, posOfScan);

            if (s.length() > 0) {
                addToTokenQueue(s);
            }
        }
        else {
            m_processor.error(XPATHErrorResources.ER_PREFIX_MUST_RESOLVE, new String[] {prefix});
        }

        return -1;
    }
}