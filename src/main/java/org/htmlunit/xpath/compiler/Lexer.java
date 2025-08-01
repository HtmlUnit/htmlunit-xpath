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

import java.util.List;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.xml.utils.PrefixResolver;

/** This class is in charge of lexical processing of the XPath expression into tokens. */
class Lexer {

  /** The target XPath. */
  private final Compiler m_compiler;

  /** The prefix resolver to map prefixes to namespaces in the XPath. */
  final PrefixResolver m_namespaceContext;

  /** The XPath processor object. */
  final XPathParser m_processor;

  /**
   * This value is added to each element name in the TARGETEXTRA that is a 'target' (right-most
   * top-level element name).
   */
  static final int TARGETEXTRA = 10000;

  /**
   * Ignore this, it is going away. This holds a map to the m_tokenQueue that tells where the
   * top-level elements are. It is used for pattern matching so the m_tokenQueue can be walked
   * backwards. Each element that is a 'target', (right-most top level element name) has TARGETEXTRA
   * added to it.
   */
  private int[] m_patternMap = new int[100];

  /** Ignore this, it is going away. The number of elements that m_patternMap maps; */
  private int m_patternMapSize;

  /**
   * Create a Lexer object.
   *
   * @param compiler The owning compiler for this lexer.
   * @param resolver The prefix resolver for mapping qualified name prefixes to namespace URIs.
   * @param xpathProcessor The parser that is processing strings to opcodes.
   */
  Lexer(final Compiler compiler, final PrefixResolver resolver, final XPathParser xpathProcessor) {

    m_compiler = compiler;
    m_namespaceContext = resolver;
    m_processor = xpathProcessor;
  }

  /**
   * Walk through the expression and build a token queue, and a map of the top-level elements.
   *
   * @param pat XSLT Expression.
   * @param targetStrings Vector to hold Strings, may be null.
   * @throws javax.xml.transform.TransformerException if any
   */
  void tokenize(final String pat, final List<String> targetStrings)
      throws javax.xml.transform.TransformerException {

    m_compiler.m_currentPattern = pat;
    m_patternMapSize = 0;

    // This needs to grow too. Use a conservative estimate that the OpMapVector
    // needs about five time the length of the input path expression - to a
    // maximum of MAXTOKENQUEUESIZE*5. If the OpMapVector needs to grow, grow
    // it freely (second argument to constructor).
    final int initTokQueueSize = (Math.min(pat.length(), OpMap.MAXTOKENQUEUESIZE)) * 5;
    m_compiler.m_opMap =
        new OpMapVector(initTokQueueSize, OpMap.BLOCKTOKENQUEUESIZE * 5, OpMap.MAPINDEX_LENGTH);

    final int nChars = pat.length();
    int startSubstring = -1;
    int posOfNSSep = -1;
    boolean isStartOfPat = true;
    boolean isAttrName = false;
    boolean isNum = false;

    // Nesting of '[' so we can know if the given element should be
    // counted inside the m_patternMap.
    int nesting = 0;

    // char[] chars = pat.toCharArray();
    for (int i = 0; i < nChars; i++) {
      char c = pat.charAt(i);

      switch (c) {
        case '\"': {
            if (startSubstring != -1) {
              isNum = false;
              isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
              isAttrName = false;

              if (-1 != posOfNSSep) {
                posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
              }
              else {
                addToTokenQueue(pat.substring(startSubstring, i));
              }
            }

            startSubstring = i;

            for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\"'); i++) {
            }

            if (c == '\"' && i < nChars) {
              addToTokenQueue(pat.substring(startSubstring, i + 1));

              startSubstring = -1;
            }
            else {
              m_processor.error(XPATHErrorResources.ER_EXPECTED_DOUBLE_QUOTE, null);
            }
          }
          break;
        case '\'':
          if (startSubstring != -1) {
            isNum = false;
            isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
            isAttrName = false;

            if (-1 != posOfNSSep) {
              posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
            }
            else {
              addToTokenQueue(pat.substring(startSubstring, i));
            }
          }

          startSubstring = i;

          for (i++; (i < nChars) && ((c = pat.charAt(i)) != '\''); i++) {
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
            isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
            isAttrName = false;

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
          isAttrName = true;

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
        case '^': // Unused at the moment
        case '!': // Unused at the moment
        case '$':
        case '<':
        case '>':
          if (startSubstring != -1) {
            isNum = false;
            isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
            isAttrName = false;

            if (-1 != posOfNSSep) {
              posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, i);
            }
            else {
              addToTokenQueue(pat.substring(startSubstring, i));
            }

            startSubstring = -1;
          }
          else if (('/' == c) && isStartOfPat) {
            isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
          }
          else if ('*' == c) {
            isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);
            isAttrName = false;
          }

          if (0 == nesting) {
            if ('|' == c) {
              if (null != targetStrings) {
                recordTokenString(targetStrings);
              }

              isStartOfPat = true;
            }
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
              isAttrName = false;
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
      isStartOfPat = mapPatternElemPos(nesting, isStartOfPat, isAttrName);

      if ((-1 != posOfNSSep)
          || ((m_namespaceContext != null) && (m_namespaceContext.handlesNullPrefixes()))) {
        posOfNSSep = mapNSTokens(pat, startSubstring, posOfNSSep, nChars);
      }
      else {
        addToTokenQueue(pat.substring(startSubstring, nChars));
      }
    }

    if (0 == m_compiler.getTokenQueueSize()) {
      m_processor.error(XPATHErrorResources.ER_EMPTY_EXPRESSION, null);
    }
    else {
      if (null != targetStrings) {
        recordTokenString(targetStrings);
      }
    }

    m_processor.m_queueMark = 0;
  }

  /**
   * Record the current position on the token queue as long as this is a top-level element. Must be
   * called before the next token is added to the m_tokenQueue.
   *
   * @param nesting The nesting count for the pattern element.
   * @param isStart true if this is the start of a pattern.
   * @param isAttrName true if we have determined that this is an attribute name.
   * @return true if this is the start of a pattern.
   */
  private boolean mapPatternElemPos(final int nesting, boolean isStart, final boolean isAttrName) {

    if (0 == nesting) {
      if (m_patternMapSize >= m_patternMap.length) {
        final int[] patternMap = m_patternMap;
        final int len = m_patternMap.length;
        m_patternMap = new int[m_patternMapSize + 100];
        System.arraycopy(patternMap, 0, m_patternMap, 0, len);
      }
      if (!isStart) {
        m_patternMap[m_patternMapSize - 1] -= TARGETEXTRA;
      }
      m_patternMap[m_patternMapSize] =
          (m_compiler.getTokenQueueSize() - (isAttrName ? 1 : 0)) + TARGETEXTRA;

      m_patternMapSize++;

      isStart = false;
    }

    return isStart;
  }

  /**
   * Given a map pos, return the corresponding token queue pos.
   *
   * @param i The index in the m_patternMap.
   * @return the token queue position.
   */
  private int getTokenQueuePosFromMap(final int i) {

    final int pos = m_patternMap[i];

    return (pos >= TARGETEXTRA) ? (pos - TARGETEXTRA) : pos;
  }

  /**
   * Reset token queue mark and m_token to a given position.
   *
   * @param mark The new position.
   */
  private void resetTokenMark(final int mark) {

    final int qsz = m_compiler.getTokenQueueSize();

    m_processor.m_queueMark = (mark > 0) ? ((mark <= qsz) ? mark - 1 : mark) : 0;

    if (m_processor.m_queueMark < qsz) {
      m_processor.m_token = (String) m_compiler.getTokenQueue().get(m_processor.m_queueMark++);
      m_processor.m_tokenChar = m_processor.m_token.charAt(0);
    }
    else {
      m_processor.m_token = null;
      m_processor.m_tokenChar = 0;
    }
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
      final Integer itok = (Integer) Keywords.getKeyWord(key);

      tok = (null != itok) ? itok.intValue() : 0;
    }
    catch (NullPointerException | ClassCastException npe) {
      tok = 0;
    }

    return tok;
  }

  /**
   * Record the current token in the passed vector.
   *
   * @param targetStrings Vector of string.
   */
  private void recordTokenString(final List<String> targetStrings) {

    int tokPos = getTokenQueuePosFromMap(m_patternMapSize - 1);

    resetTokenMark(tokPos + 1);

    if (m_processor.lookahead('(', 1)) {
      final int tok = getKeywordToken(m_processor.m_token);

      switch (tok) {
        case OpCodes.NODETYPE_COMMENT:
          targetStrings.add(PseudoNames.PSEUDONAME_COMMENT);
          break;
        case OpCodes.NODETYPE_TEXT:
          targetStrings.add(PseudoNames.PSEUDONAME_TEXT);
          break;
        case OpCodes.NODETYPE_NODE:
          targetStrings.add(PseudoNames.PSEUDONAME_ANY);
          break;
        case OpCodes.NODETYPE_ROOT:
          targetStrings.add(PseudoNames.PSEUDONAME_ROOT);
          break;
        case OpCodes.NODETYPE_ANYELEMENT:
          targetStrings.add(PseudoNames.PSEUDONAME_ANY);
          break;
        case OpCodes.NODETYPE_PI:
          targetStrings.add(PseudoNames.PSEUDONAME_ANY);
          break;
        default:
          targetStrings.add(PseudoNames.PSEUDONAME_ANY);
      }
    }
    else {
      if (m_processor.tokenIs('@')) {
        tokPos++;

        resetTokenMark(tokPos + 1);
      }

      if (m_processor.lookahead(':', 1)) {
        tokPos += 2;
      }

      targetStrings.add((String) m_compiler.getTokenQueue().get(tokPos));
    }
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
   * When a seperator token is found, see if there's a element name or the like to map.
   *
   * @param pat The XPath name string.
   * @param startSubstring The start of the name string.
   * @param posOfNSSep The position of the namespace seperator (':').
   * @param posOfScan The end of the name index.
   * @throws javax.xml.transform.TransformerException if any
   * @return -1 always.
   */
  private int mapNSTokens(
      final String pat, final int startSubstring, final int posOfNSSep, final int posOfScan)
      throws javax.xml.transform.TransformerException {

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

          // Assume last was wildcard. This is not legal according
          // to the draft. Set the below to true to make namespace
          // wildcards work.
          if (false) {
            addToTokenQueue(":");

            final String s = pat.substring(posOfNSSep + 1, posOfScan);

            if (s.length() > 0) {
                addToTokenQueue(s);
            }

            return -1;
          }
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
