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

        for (int i = 0; i < nChars; i++) {
            char c = pat.charAt(i);

            switch (c) {
            case '\"':
                if (startSubstring != -1) {
                    isNum = false;

                    if (-1 != posOfNSSep) {
                        mapNSTokens(pat, startSubstring, posOfNSSep, i);
                        posOfNSSep = -1;
                    }
                    else {
                        m_compiler.getTokenQueue().add(pat.substring(startSubstring, i));
                    }
                }

                startSubstring = i;

                for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\"'); i++) {
                    // empty
                }

                if (c == '\"' && i < nChars) {
                    m_compiler.getTokenQueue().add(pat.substring(startSubstring, i + 1));
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
                        mapNSTokens(pat, startSubstring, posOfNSSep, i);
                        posOfNSSep = -1;
                    }
                    else {
                        m_compiler.getTokenQueue().add(pat.substring(startSubstring, i));
                    }
                }

                startSubstring = i;

                for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\''); i++) {
                    // empty
                }

                if (c == '\'' && i < nChars) {
                    m_compiler.getTokenQueue().add(pat.substring(startSubstring, i + 1));
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
                        mapNSTokens(pat, startSubstring, posOfNSSep, i);
                        posOfNSSep = -1;
                    }
                    else {
                        m_compiler.getTokenQueue().add(pat.substring(startSubstring, i));
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
                        mapNSTokens(pat, startSubstring, posOfNSSep, i);
                        posOfNSSep = -1;
                    }
                    else {
                        m_compiler.getTokenQueue().add(pat.substring(startSubstring, i));
                    }

                    startSubstring = -1;
                }

                m_compiler.getTokenQueue().add(pat.substring(i, i + 1));
                break;

            case ':':
                if (i > 0) {
                    if (posOfNSSep == (i - 1)) {
                        if (startSubstring != -1) {
                            if (startSubstring < (i - 1)) {
                                m_compiler.getTokenQueue().add(pat.substring(startSubstring, i - 1));
                            }
                        }

                        isNum = false;
                        startSubstring = -1;
                        posOfNSSep = -1;

                        m_compiler.getTokenQueue().add(pat.substring(i - 1, i + 1));
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
                mapNSTokens(pat, startSubstring, posOfNSSep, nChars);
            }
            else {
                m_compiler.getTokenQueue().add(pat.substring(startSubstring, nChars));
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
        final Integer itok = Keywords.getKeyWord(key);
        return (null != itok) ? itok : 0;
    }

    /**
     * When a separator token is found, resolve any namespace prefix and add the
     * resulting tokens to the token queue.
     *
     * @param pat            The XPath name string.
     * @param startSubstring The start of the name string.
     * @param posOfNSSep     The position of the namespace separator (':').
     * @param posOfScan      The end of the name index.
     * @throws TransformerException if the prefix cannot be resolved.
     */
    private void mapNSTokens(final String pat, final int startSubstring, final int posOfNSSep, final int posOfScan)
            throws TransformerException {

        String prefix = "";

        if ((startSubstring >= 0) && (posOfNSSep >= 0)) {
            prefix = pat.substring(startSubstring, posOfNSSep);
        }

        final String uName;

        if ((null != m_namespaceContext) && !"*".equals(prefix) && !"xmlns".equals(prefix)) {
            uName = m_namespaceContext.getNamespaceForPrefix(prefix);
        }
        else {
            uName = prefix;
        }

        if ((null != uName) && (uName.length() > 0)) {
            m_compiler.getTokenQueue().add(uName);
            m_compiler.getTokenQueue().add(":");

            final String s = pat.substring(posOfNSSep + 1, posOfScan);

            if (s.length() > 0) {
                m_compiler.getTokenQueue().add(s);
            }
        }
        else {
            m_processor.error(XPATHErrorResources.ER_PREFIX_MUST_RESOLVE, new String[] {prefix});
        }
    }
}