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

import java.util.Objects;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.htmlunit.xpath.XPathProcessorException;
import org.htmlunit.xpath.objects.XNumber;
import org.htmlunit.xpath.objects.XString;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;
import org.htmlunit.xpath.xml.utils.PrefixResolver;

/**
 * Tokenizes and parses XPath expressions. This should really be named XPathParserImpl, and may be
 * renamed in the future.
 */
public class XPathParser {
  // %REVIEW% Is there a better way of doing this?
  // Upside is minimum object churn. Downside is that we don't have a useful
  // backtrace in the exception itself -- but we don't expect to need one.
  public static final String CONTINUE_AFTER_FATAL_ERROR = "CONTINUE_AFTER_FATAL_ERROR";

  /** The XPath to be processed. */
  private OpMap m_ops;

  /** The next token in the pattern. */
  transient String m_token;

  /**
   * The first char in m_token, the theory being that this is an optimization because we won't have
   * to do charAt(0) as often.
   */
  transient char m_tokenChar = 0;

  /** The position in the token queue is tracked by m_queueMark. */
  int m_queueMark = 0;

  /** Results from checking FilterExpr syntax */
  protected static final int FILTER_MATCH_FAILED = 0;

  protected static final int FILTER_MATCH_PRIMARY = 1;
  protected static final int FILTER_MATCH_PREDICATES = 2;

  private enum RELATIVE_PATH_STATUS {
      NOT_PERMITTED,
      PERMITTED,
      REQUIRED
  }

  /** The parser constructor. */
  public XPathParser(final ErrorListener errorListener) {
    m_errorListener = errorListener;
  }

  /** The prefix resolver to map prefixes to namespaces in the OpMap. */
  PrefixResolver m_namespaceContext;

  /**
   * Given an string, init an XPath object for selections, in order that a parse doesn't have to be
   * done each time the expression is evaluated.
   *
   * @param compiler The compiler object.
   * @param expression A string conforming to the XPath grammar.
   * @param namespaceContext An object that is able to resolve prefixes in the XPath to namespaces.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  public void initXPath(
      final Compiler compiler, final String expression, final PrefixResolver namespaceContext)
      throws javax.xml.transform.TransformerException {

    m_ops = compiler;
    m_namespaceContext = namespaceContext;
    m_functionTable = compiler.getFunctionTable();

    final Lexer lexer = new Lexer(compiler, namespaceContext, this);

    lexer.tokenize(expression, null);

    m_ops.setOp(0, OpCodes.OP_XPATH);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, 2);

    // Patch for Christine's gripe. She wants her errorHandler to return from
    // a fatal error and continue trying to parse, rather than throwing an
    // exception.
    // Without the patch, that put us into an endless loop.
    //
    // %REVIEW% Is there a better way of doing this?
    // %REVIEW% Are there any other cases which need the safety net?
    // (and if so do we care right now, or should we rewrite the XPath
    // grammar engine and can fix it at that time?)
    try {

      nextToken();
      Expr();

      if (null != m_token) {
        final StringBuilder extraTokens = new StringBuilder();

        while (null != m_token) {
          extraTokens.append('\'').append(m_token).append('\'');

          nextToken();

          if (null != m_token) {
            extraTokens.append(", ");
          }
        }

        error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS, new Object[] {extraTokens.toString()});
      }

    }
    catch (final org.htmlunit.xpath.XPathProcessorException e) {
      if (CONTINUE_AFTER_FATAL_ERROR.equals(e.getMessage())) {
        // What I _want_ to do is null out this XPath.
        // I doubt this has the desired effect, but I'm not sure what else to do.
        // %REVIEW%!!!
        initXPath(compiler, "/..", namespaceContext);
      }
      else {
        throw e;
      }
    }

    compiler.shrink();
  }

  /**
   * Given an string, init an XPath object for pattern matches, in order that a parse doesn't have
   * to be done each time the expression is evaluated.
   *
   * @param compiler The XPath object to be initialized.
   * @param expression A String representing the XPath.
   * @param namespaceContext An object that is able to resolve prefixes in the XPath to namespaces.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  public void initMatchPattern(
      final Compiler compiler, final String expression, final PrefixResolver namespaceContext)
      throws javax.xml.transform.TransformerException {

    m_ops = compiler;
    m_namespaceContext = namespaceContext;
    m_functionTable = compiler.getFunctionTable();

    final Lexer lexer = new Lexer(compiler, namespaceContext, this);

    lexer.tokenize(expression, null);

    m_ops.setOp(0, OpCodes.OP_MATCHPATTERN);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, 2);

    nextToken();
    Pattern();

    if (null != m_token) {
      final StringBuilder extraTokens = new StringBuilder();

      while (null != m_token) {
        extraTokens.append('\'').append(m_token).append('\'');

        nextToken();

        if (null != m_token) {
          extraTokens.append(", ");
        }
      }

      error(XPATHErrorResources.ER_EXTRA_ILLEGAL_TOKENS, new Object[] {extraTokens.toString()});
    }

    // Terminate for safety.
    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    m_ops.shrink();
  }

  /** The error listener where syntax errors are to be sent. */
  private ErrorListener m_errorListener;

  /** The table contains build-in functions and customized functions */
  private FunctionTable m_functionTable;

  /**
   * Allow an application to register an error event handler, where syntax errors will be sent. If
   * the error listener is not set, syntax errors will be sent to System.err.
   *
   * @param handler Reference to error listener where syntax errors will be sent.
   */
  public void setErrorHandler(final ErrorListener handler) {
    m_errorListener = handler;
  }

  /**
   * Return the current error listener.
   *
   * @return The error listener, which should not normally be null, but may be.
   */
  public ErrorListener getErrorListener() {
    return m_errorListener;
  }

  /**
   * Check whether m_token matches the target string.
   *
   * @param s A string reference or null.
   * @return If m_token is null, returns false (or true if s is also null), or return true if the
   *     current token matches the string, else false.
   */
  final boolean tokenIs(final String s) {
    return Objects.equals(m_token, s);
  }

  /**
   * Check whether m_tokenChar==c.
   *
   * @param c A character to be tested.
   * @return If m_token is null, returns false, or return true if c matches the current token.
   */
  final boolean tokenIs(final char c) {
    return m_token != null && (m_tokenChar == c);
  }

  /**
   * Look ahead of the current token in order to make a branching decision.
   *
   * @param c the character to be tested for.
   * @param n number of tokens to look ahead. Must be greater than 1.
   * @return true if the next token matches the character argument.
   */
  final boolean lookahead(final char c, final int n) {

    final int pos = m_queueMark + n;
    final boolean b;

    if ((pos <= m_ops.getTokenQueueSize()) && (pos > 0) && (m_ops.getTokenQueueSize() != 0)) {
      final String tok = (String) m_ops.m_tokenQueue.get(pos - 1);

      b = tok.length() == 1 && (tok.charAt(0) == c);
    }
    else {
      b = false;
    }

    return b;
  }

  /**
   * Look ahead of the current token in order to make a branching decision.
   *
   * @param s the string to compare it to.
   * @param n number of tokens to lookahead. Must be greater than 1.
   * @return true if the token behind the current token matches the string argument.
   */
  private boolean lookahead(final String s, final int n) {

    final boolean isToken;

    if ((m_queueMark + n) <= m_ops.getTokenQueueSize()) {
      final String lookahead = (String) m_ops.m_tokenQueue.get(m_queueMark + (n - 1));

      isToken = Objects.equals(lookahead, s);
    }
    else {
      isToken = null == s;
    }

    return isToken;
  }

  /** Retrieve the next token from the command and store it in m_token string. */
  private void nextToken() {

    if (m_queueMark < m_ops.getTokenQueueSize()) {
      m_token = (String) m_ops.m_tokenQueue.get(m_queueMark++);
      m_tokenChar = m_token.charAt(0);
    }
    else {
      m_token = null;
      m_tokenChar = 0;
    }
  }

  /**
   * Consume an expected token, throwing an exception if it isn't there.
   *
   * @param expected the character to be expected.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  private void consumeExpected(final char expected)
      throws javax.xml.transform.TransformerException {

    if (tokenIs(expected)) {
      nextToken();
    }
    else {
      error(
          XPATHErrorResources.ER_EXPECTED_BUT_FOUND,
          new Object[] {String.valueOf(expected), m_token});

      // Patch for Christina's gripe. She wants her errorHandler to return from
      // this error and continue trying to parse, rather than throwing an exception.
      // Without the patch, that put us into an endless loop.
      throw new XPathProcessorException(CONTINUE_AFTER_FATAL_ERROR);
    }
  }

  /**
   * Notify the user of an error, and probably throw an exception.
   *
   * @param msg An error msgkey that corresponds to one of the constants found in {@link
   *     org.htmlunit.xpath.res.XPATHErrorResources}, which is a key for a format string.
   * @param args An array of arguments represented in the format string, which may be null.
   * @throws TransformerException if the current ErrorListoner determines to throw an exception.
   */
  void error(final String msg, final Object[] args) throws TransformerException {

    final String fmsg = XPATHMessages.createXPATHMessage(msg, args);
    final ErrorListener ehandler = this.getErrorListener();

    final TransformerException te = new TransformerException(fmsg);
    if (null != ehandler) {
      ehandler.fatalError(te);
    }
    else {
      throw te;
    }
  }

  /**
   * Dump the remaining token queue. Thanks to Craig for this.
   *
   * @return A dump of the remaining token queue, which may be appended to an error message.
   */
  protected String dumpRemainingTokenQueue() {

    int q = m_queueMark;
    final String returnMsg;

    if (q < m_ops.getTokenQueueSize()) {
      final StringBuilder msg = new StringBuilder("\n Remaining tokens: (");

      while (q < m_ops.getTokenQueueSize()) {
        final String t = (String) m_ops.m_tokenQueue.get(q++);

        msg.append(" '").append(t).append('\'');
      }

      returnMsg = msg + ")";
    }
    else {
      returnMsg = "";
    }

    return returnMsg;
  }

  /**
   * Given a string, return the corresponding function token.
   *
   * @param key A local name of a function.
   * @return The function ID, which may correspond to one of the FUNC_XXX values found in {@link
   *     org.htmlunit.xpath.compiler.FunctionTable}, but may be a value installed by an external
   *     module.
   */
  final int getFunctionToken(final String key) {

    int tok;

    Object id;

    try {
      // These are nodetests, xpathparser treats them as functions when parsing
      // a FilterExpr.
      id = Keywords.lookupNodeTest(key);
      if (null == id) {
        id = m_functionTable.getFunctionID(key);
      }
      tok = ((Integer) id).intValue();
    }
    catch (NullPointerException | ClassCastException ignored) {
      tok = -1;
    }

    return tok;
  }

  /**
   * Insert room for operation. This will NOT set the length value of the operation, but will update
   * the length value for the total expression.
   *
   * @param pos The position where the op is to be inserted.
   * @param length The length of the operation space in the op map.
   * @param op The op code to the inserted.
   */
  void insertOp(final int pos, final int length, final int op) {

    final int totalLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    for (int i = totalLen - 1; i >= pos; i--) {
      m_ops.setOp(i + length, m_ops.getOp(i));
    }

    m_ops.setOp(pos, op);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, totalLen + length);
  }

  /**
   * Insert room for operation. This WILL set the length value of the operation, and will update the
   * length value for the total expression.
   *
   * @param length The length of the operation.
   * @param op The op code to the inserted.
   */
  void appendOp(final int length, final int op) {

    final int totalLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    m_ops.setOp(totalLen, op);
    m_ops.setOp(totalLen + OpMap.MAPINDEX_LENGTH, length);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, totalLen + length);
  }

  // ============= EXPRESSIONS FUNCTIONS =================

  /**
   * Expr ::= OrExpr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void Expr() throws javax.xml.transform.TransformerException {
    OrExpr();
  }

  /**
   * OrExpr ::= AndExpr | OrExpr 'or' AndExpr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void OrExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    AndExpr();

    if ((null != m_token) && tokenIs("or")) {
      nextToken();
      insertOp(opPos, 2, OpCodes.OP_OR);
      OrExpr();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    }
  }

  /**
   * AndExpr ::= EqualityExpr | AndExpr 'and' EqualityExpr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void AndExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    EqualityExpr(-1);

    if ((null != m_token) && tokenIs("and")) {
      nextToken();
      insertOp(opPos, 2, OpCodes.OP_AND);
      AndExpr();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    }
  }

  /**
   * EqualityExpr ::= RelationalExpr | EqualityExpr '=' RelationalExpr
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   * @return the position at the end of the equality expression.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected int EqualityExpr(int addPos) throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (-1 == addPos) {
      addPos = opPos;
    }

    RelationalExpr(-1);

    if (null != m_token) {
      if (tokenIs('!') && lookahead('=', 1)) {
        nextToken();
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_NOTEQUALS);

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = EqualityExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs('=')) {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_EQUALS);

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = EqualityExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * RelationalExpr ::= AdditiveExpr | RelationalExpr '&lt;' AdditiveExpr | RelationalExpr '&gt;'
   * AdditiveExpr | RelationalExpr '&lt;=' AdditiveExpr | RelationalExpr '&gt;=' AdditiveExpr
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   * @return the position at the end of the relational expression.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected int RelationalExpr(int addPos) throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (-1 == addPos) {
      addPos = opPos;
    }

    AdditiveExpr(-1);

    if (null != m_token) {
      if (tokenIs('<')) {
        nextToken();

        if (tokenIs('=')) {
          nextToken();
          insertOp(addPos, 2, OpCodes.OP_LTE);
        }
        else {
          insertOp(addPos, 2, OpCodes.OP_LT);
        }

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = RelationalExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs('>')) {
        nextToken();

        if (tokenIs('=')) {
          nextToken();
          insertOp(addPos, 2, OpCodes.OP_GTE);
        }
        else {
          insertOp(addPos, 2, OpCodes.OP_GT);
        }

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = RelationalExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * This has to handle construction of the operations so that they are evaluated in pre-fix order.
   * So, for 9+7-6, instead of |+|9|-|7|6|, this needs to be evaluated as |-|+|9|7|6|.
   *
   * <p>AdditiveExpr ::= MultiplicativeExpr | AdditiveExpr '+' MultiplicativeExpr | AdditiveExpr '-'
   * MultiplicativeExpr
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   * @return the position at the end of the equality expression.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected int AdditiveExpr(int addPos) throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (-1 == addPos) {
      addPos = opPos;
    }

    MultiplicativeExpr(-1);

    if (null != m_token) {
      if (tokenIs('+')) {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_PLUS);

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = AdditiveExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs('-')) {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MINUS);

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = AdditiveExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * This has to handle construction of the operations so that they are evaluated in pre-fix order.
   * So, for 9+7-6, instead of |+|9|-|7|6|, this needs to be evaluated as |-|+|9|7|6|.
   *
   * <p>MultiplicativeExpr ::= UnaryExpr | MultiplicativeExpr MultiplyOperator UnaryExpr |
   * MultiplicativeExpr 'div' UnaryExpr | MultiplicativeExpr 'mod' UnaryExpr | MultiplicativeExpr
   * 'quo' UnaryExpr
   *
   * @param addPos Position where expression is to be added, or -1 for append.
   * @return the position at the end of the equality expression.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected int MultiplicativeExpr(int addPos) throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if (-1 == addPos) {
      addPos = opPos;
    }

    UnaryExpr();

    if (null != m_token) {
      if (tokenIs('*')) {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MULT);

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs("div")) {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_DIV);

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs("mod")) {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_MOD);

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
      else if (tokenIs("quo")) {
        nextToken();
        insertOp(addPos, 2, OpCodes.OP_QUO);

        final int opPlusLeftHandLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - addPos;

        addPos = MultiplicativeExpr(addPos);
        m_ops.setOp(
            addPos + OpMap.MAPINDEX_LENGTH,
            m_ops.getOp(addPos + opPlusLeftHandLen + 1) + opPlusLeftHandLen);
        addPos += 2;
      }
    }

    return addPos;
  }

  /**
   * UnaryExpr ::= UnionExpr | '-' UnaryExpr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void UnaryExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    boolean isNeg = false;

    if (m_tokenChar == '-') {
      nextToken();
      appendOp(2, OpCodes.OP_NEG);

      isNeg = true;
    }

    UnionExpr();

    if (isNeg) {
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    }
  }

  /**
   * StringExpr ::= Expr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void StringExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_STRING);
    Expr();

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * StringExpr ::= Expr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void BooleanExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_BOOL);
    Expr();

    final int opLen = m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos;

    if (opLen == 2) {
      error(XPATHErrorResources.ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL, null);
    }

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, opLen);
  }

  /**
   * NumberExpr ::= Expr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void NumberExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_NUMBER);
    Expr();

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * The context of the right hand side expressions is the context of the left hand side expression.
   * The results of the right hand side expressions are node sets. The result of the left hand side
   * UnionExpr is the union of the results of the right hand side expressions.
   *
   * <p>UnionExpr ::= PathExpr | UnionExpr '|' PathExpr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void UnionExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    final boolean continueOrLoop = true;
    boolean foundUnion = false;

    do {
      PathExpr();

      if (tokenIs('|')) {
        if (!foundUnion) {
          foundUnion = true;

          insertOp(opPos, 2, OpCodes.OP_UNION);
        }

        nextToken();
      }
      else {
        break;
      }

      // this.m_testForDocOrder = true;
    }
    while (continueOrLoop);

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * PathExpr ::= LocationPath | FilterExpr | FilterExpr '/' RelativeLocationPath | FilterExpr '//'
   * RelativeLocationPath
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void PathExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    final int filterExprMatch = FilterExpr();

    if (filterExprMatch != FILTER_MATCH_FAILED) {
      // If FilterExpr had Predicates, a OP_LOCATIONPATH opcode would already
      // have been inserted.
      boolean locationPathStarted = filterExprMatch == FILTER_MATCH_PREDICATES;

      if (tokenIs('/')) {
        nextToken();

        if (!locationPathStarted) {
          // int locationPathOpPos = opPos;
          insertOp(opPos, 2, OpCodes.OP_LOCATIONPATH);

          locationPathStarted = true;
        }

        if (!RelativeLocationPath()) {
          error(XPATHErrorResources.ER_EXPECTED_REL_LOC_PATH, null);
        }
      }

      // Terminate for safety.
      if (locationPathStarted) {
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
        m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
        m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
      }
    }
    else {
      LocationPath();
    }
  }

  /**
   * FilterExpr ::= PrimaryExpr | FilterExpr Predicate
   *
   * @return FILTER_MATCH_PREDICATES, if this method successfully matched a FilterExpr with one or
   *     more Predicates; FILTER_MATCH_PRIMARY, if this method successfully matched a FilterExpr
   *     that was just a PrimaryExpr; or FILTER_MATCH_FAILED, if this method did not match a
   *     FilterExpr
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected int FilterExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    final int filterMatch;

    if (PrimaryExpr()) {
      if (tokenIs('[')) {

        // int locationPathOpPos = opPos;
        insertOp(opPos, 2, OpCodes.OP_LOCATIONPATH);

        while (tokenIs('[')) {
          Predicate();
        }

        filterMatch = FILTER_MATCH_PREDICATES;
      }
      else {
        filterMatch = FILTER_MATCH_PRIMARY;
      }
    }
    else {
      filterMatch = FILTER_MATCH_FAILED;
    }

    return filterMatch;

    /*
     * if(tokenIs('[')) { Predicate(); m_ops.m_opMap[opPos + OpMap.MAPINDEX_LENGTH]
     * = m_ops.m_opMap[OpMap.MAPINDEX_LENGTH] - opPos; }
     */
  }

  /**
   * PrimaryExpr ::= VariableReference | '(' Expr ')' | Literal | Number | FunctionCall
   *
   * @return true if this method successfully matched a PrimaryExpr
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected boolean PrimaryExpr() throws javax.xml.transform.TransformerException {

    final boolean matchFound;
    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    if ((m_tokenChar == '\'') || (m_tokenChar == '"')) {
      appendOp(2, OpCodes.OP_LITERAL);
      Literal();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      matchFound = true;
    }
    else if (m_tokenChar == '$') {
      nextToken(); // consume '$'
      appendOp(2, OpCodes.OP_VARIABLE);
      QName();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      matchFound = true;
    }
    else if (m_tokenChar == '(') {
      nextToken();
      appendOp(2, OpCodes.OP_GROUP);
      Expr();
      consumeExpected(')');

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      matchFound = true;
    }
    else if ((null != m_token)
        && ((('.' == m_tokenChar) && (m_token.length() > 1) && Character.isDigit(m_token.charAt(1)))
            || Character.isDigit(m_tokenChar))) {
      appendOp(2, OpCodes.OP_NUMBERLIT);
      Number();

      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      matchFound = true;
    }
    else if (lookahead('(', 1) || (lookahead(':', 1) && lookahead('(', 3))) {
      matchFound = FunctionCall();
    }
    else {
      matchFound = false;
    }

    return matchFound;
  }

  /**
   * Argument ::= Expr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void Argument() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_ARGUMENT);
    Expr();

    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * FunctionCall ::= FunctionName '(' ( Argument ( ',' Argument)*)? ')'
   *
   * @return true if, and only if, a FunctionCall was matched
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected boolean FunctionCall() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    final int funcTok = getFunctionToken(m_token);

    if (-1 == funcTok) {
      error(XPATHErrorResources.ER_COULDNOT_FIND_FUNCTION, new Object[] {m_token});
    }

    switch (funcTok) {
      case OpCodes.NODETYPE_PI:
      case OpCodes.NODETYPE_COMMENT:
      case OpCodes.NODETYPE_TEXT:
      case OpCodes.NODETYPE_NODE:
        // Node type tests look like function calls, but they're not
        return false;
      default:
        appendOp(3, OpCodes.OP_FUNCTION);

        m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, funcTok);
    }

    nextToken();

    consumeExpected('(');

    while (!tokenIs(')') && m_token != null) {
      if (tokenIs(',')) {
        error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG, null);
      }

      Argument();

      if (!tokenIs(')')) {
        consumeExpected(',');

        if (tokenIs(')')) {
          error(XPATHErrorResources.ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG, null);
        }
      }
    }

    consumeExpected(')');

    // Terminate for safety.
    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

    return true;
  }

  // ============= GRAMMAR FUNCTIONS =================

  /**
   * LocationPath ::= RelativeLocationPath | AbsoluteLocationPath
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void LocationPath() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    // int locationPathOpPos = opPos;
    appendOp(2, OpCodes.OP_LOCATIONPATH);

    final boolean seenSlash = tokenIs('/');

    if (seenSlash) {
      appendOp(4, OpCodes.FROM_ROOT);

      // Tell how long the step is without the predicate
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2, 4);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_ROOT);

      nextToken();
    }
    else if (m_token == null) {
      error(XPATHErrorResources.ER_EXPECTED_LOC_PATH_AT_END_EXPR, null);
    }

    if (m_token != null) {
      if (!RelativeLocationPath() && !seenSlash) {
        error(XPATHErrorResources.ER_EXPECTED_LOC_PATH, new Object[] {m_token});
      }
    }

    // Terminate for safety.
    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * RelativeLocationPath ::= Step | RelativeLocationPath '/' Step | AbbreviatedRelativeLocationPath
   *
   * @return true if, and only if, a RelativeLocationPath was matched
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected boolean RelativeLocationPath() throws javax.xml.transform.TransformerException {
    if (!Step()) {
      return false;
    }

    while (tokenIs('/')) {
      nextToken();

      if (!Step()) {
        error(XPATHErrorResources.ER_EXPECTED_LOC_STEP, null);
      }
    }

    return true;
  }

  /**
   * Step ::= Basis Predicate | AbbreviatedStep
   *
   * @return false if step was empty (or only a '/'); true, otherwise
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected boolean Step() throws javax.xml.transform.TransformerException {
    int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    final boolean doubleSlash = tokenIs('/');

    // At most a single '/' before each Step is consumed by caller; if the
    // first thing is a '/', that means we had '//' and the Step must not
    // be empty.
    if (doubleSlash) {
      nextToken();

      appendOp(2, OpCodes.FROM_DESCENDANTS_OR_SELF);

      // Have to fix up for patterns such as '//@foo' or '//attribute::foo',
      // which translate to 'descendant-or-self::node()/attribute::foo'.
      // notice I leave the '/' on the queue, so the next will be processed
      // by a regular step pattern.

      // Make room for telling how long the step is without the predicate
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.NODETYPE_NODE);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      // Tell how long the step is without the predicate
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      // Tell how long the step is with the predicate
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

      opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    }

    if (tokenIs(".")) {
      nextToken();

      if (tokenIs('[')) {
        error(XPATHErrorResources.ER_PREDICATE_ILLEGAL_SYNTAX, null);
      }

      appendOp(4, OpCodes.FROM_SELF);

      // Tell how long the step is without the predicate
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2, 4);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_NODE);
    }
    else if (tokenIs("..")) {
      nextToken();
      appendOp(4, OpCodes.FROM_PARENT);

      // Tell how long the step is without the predicate
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2, 4);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_NODE);
    }

    // There is probably a better way to test for this
    // transition... but it gets real hairy if you try
    // to do it in basis().
    else if (tokenIs('*')
        || tokenIs('@')
        || tokenIs('_')
        || (m_token != null && Character.isLetter(m_token.charAt(0)))) {
      Basis();

      while (tokenIs('[')) {
        Predicate();
      }

      // Tell how long the entire step is.
      m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
    }
    else {
      // No Step matched - that's an error if previous thing was a '//'
      if (doubleSlash) {
        error(XPATHErrorResources.ER_EXPECTED_LOC_STEP, null);
      }

      return false;
    }

    return true;
  }

  /**
   * Basis ::= AxisName '::' NodeTest | AbbreviatedBasis
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void Basis() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    final int axesType;

    // The next blocks guarantee that a FROM_XXX will be added.
    if (lookahead("::", 1)) {
      axesType = AxisName();

      nextToken();
      nextToken();
    }
    else if (tokenIs('@')) {
      axesType = OpCodes.FROM_ATTRIBUTES;

      appendOp(2, axesType);
      nextToken();
    }
    else {
      axesType = OpCodes.FROM_CHILDREN;

      appendOp(2, axesType);
    }

    // Make room for telling how long the step is without the predicate
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    NodeTest(axesType);

    // Tell how long the step is without the predicate
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * Basis ::= AxisName '::' NodeTest | AbbreviatedBasis
   *
   * @return FROM_XXX axes type, found in {@link org.htmlunit.xpath.compiler.Keywords}.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected int AxisName() throws javax.xml.transform.TransformerException {

    final Object val = Keywords.getAxisName(m_token);

    if (null == val) {
      error(XPATHErrorResources.ER_ILLEGAL_AXIS_NAME, new Object[] {m_token});
    }

    final int axesType = ((Integer) val).intValue();

    appendOp(2, axesType);

    return axesType;
  }

  /**
   * NodeTest ::= WildcardName | NodeType '(' ')' | 'processing-instruction' '(' Literal ')'
   *
   * @param axesType FROM_XXX axes type, found in {@link org.htmlunit.xpath.compiler.Keywords}.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void NodeTest(final int axesType) throws javax.xml.transform.TransformerException {

    if (lookahead('(', 1)) {
      final Object nodeTestOp = Keywords.getNodeType(m_token);

      if (null == nodeTestOp) {
        error(XPATHErrorResources.ER_UNKNOWN_NODETYPE, new Object[] {m_token});
      }
      else {
        nextToken();

        final int nt = ((Integer) nodeTestOp).intValue();

        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), nt);
        m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

        consumeExpected('(');

        if (OpCodes.NODETYPE_PI == nt) {
          if (!tokenIs(')')) {
            Literal();
          }
        }

        consumeExpected(')');
      }
    }
    else {

      // Assume name of attribute or element.
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.NODENAME);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      if (lookahead(':', 1)) {
        if (tokenIs('*')) {
          m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ELEMWILDCARD);
        }
        else {
          m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);

          // Minimalist check for an NCName - just check first character
          // to distinguish from other possible tokens
          if (!Character.isLetter(m_tokenChar) && !tokenIs('_')) {
            error(XPATHErrorResources.ER_EXPECTED_NODE_TEST, null);
          }
        }

        nextToken();
        consumeExpected(':');
      }
      else {
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.EMPTY);
      }

      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      if (tokenIs('*')) {
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ELEMWILDCARD);
      }
      else {
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);

        // Minimalist check for an NCName - just check first character
        // to distinguish from other possible tokens
        if (!Character.isLetter(m_tokenChar) && !tokenIs('_')) {
          error(XPATHErrorResources.ER_EXPECTED_NODE_TEST, null);
        }
      }

      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      nextToken();
    }
  }

  /**
   * Predicate ::= '[' PredicateExpr ']'
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void Predicate() throws javax.xml.transform.TransformerException {

    if (tokenIs('[')) {
      nextToken();
      PredicateExpr();
      consumeExpected(']');
    }
  }

  /**
   * PredicateExpr ::= Expr
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void PredicateExpr() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    appendOp(2, OpCodes.OP_PREDICATE);
    Expr();

    // Terminate for safety.
    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * QName ::= (Prefix ':')? LocalPart Prefix ::= NCName LocalPart ::= NCName
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void QName() throws javax.xml.transform.TransformerException {
    // Namespace
    if (lookahead(':', 1)) {
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      nextToken();
      consumeExpected(':');
    }
    else {
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.EMPTY);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    }

    // Local name
    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    nextToken();
  }

  /**
   * NCName ::= (Letter | '_') (NCNameChar) NCNameChar ::= Letter | Digit | '.' | '-' | '_' |
   * CombiningChar | Extender
   */
  protected void NCName() {

    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    nextToken();
  }

  /**
   * The value of the Literal is the sequence of characters inside the " or ' characters.
   *
   * <p>Literal ::= '"' [^"]* '"' | "'" [^']* "'"
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void Literal() throws javax.xml.transform.TransformerException {

    final int last = m_token.length() - 1;
    final char c0 = m_tokenChar;
    final char cX = m_token.charAt(last);

    if (((c0 == '\"') && (cX == '\"')) || ((c0 == '\'') && (cX == '\''))) {

      // Mutate the token to remove the quotes and have the XString object
      // already made.
      final int tokenQueuePos = m_queueMark - 1;

      m_ops.m_tokenQueue.set(tokenQueuePos, null);

      final Object obj = new XString(m_token.substring(1, last));

      m_ops.m_tokenQueue.set(tokenQueuePos, obj);

      // lit = m_token.substring(1, last);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), tokenQueuePos);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      nextToken();
    }
    else {
      error(XPATHErrorResources.ER_PATTERN_LITERAL_NEEDS_BE_QUOTED, new Object[] {m_token});
    }
  }

  /**
   * Number ::= [0-9]+('.'[0-9]+)? | '.'[0-9]+
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void Number() throws javax.xml.transform.TransformerException {

    if (null != m_token) {

      // Mutate the token to remove the quotes and have the XNumber object
      // already made.
      double num;

      try {
        // XPath 1.0 does not support number in exp notation
        if ((m_token.indexOf('e') > -1) || (m_token.indexOf('E') > -1)) {
          throw new NumberFormatException();
        }
        num = Double.valueOf(m_token).doubleValue();
      }
      catch (final NumberFormatException ignored) {
        num = 0.0; // to shut up compiler.

        error(XPATHErrorResources.ER_COULDNOT_BE_FORMATTED_TO_NUMBER, new Object[] {m_token});
      }

      m_ops.m_tokenQueue.set(m_queueMark - 1, new XNumber(num));
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), m_queueMark - 1);
      m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

      nextToken();
    }
  }

  // ============= PATTERN FUNCTIONS =================

  /**
   * Pattern ::= LocationPathPattern | Pattern '|' LocationPathPattern
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void Pattern() throws javax.xml.transform.TransformerException {

    while (true) {
      LocationPathPattern();

      if (tokenIs('|')) {
        nextToken();
      }
      else {
        break;
      }
    }
  }

  /**
   * LocationPathPattern ::= '/' RelativePathPattern? | IdKeyPattern (('/' | '//')
   * RelativePathPattern)? | '//'? RelativePathPattern
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void LocationPathPattern() throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);

    RELATIVE_PATH_STATUS relativePathStatus = RELATIVE_PATH_STATUS.NOT_PERMITTED;

    appendOp(2, OpCodes.OP_LOCATIONPATHPATTERN);

    if (lookahead('(', 1) && tokenIs(Keywords.FUNC_ID_STRING)) {
      IdKeyPattern();

      if (tokenIs('/')) {
        nextToken();

        if (tokenIs('/')) {
          appendOp(4, OpCodes.MATCH_ANY_ANCESTOR);

          nextToken();
        }
        else {
          appendOp(4, OpCodes.MATCH_IMMEDIATE_ANCESTOR);
        }

        // Tell how long the step is without the predicate
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2, 4);
        m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_FUNCTEST);

        relativePathStatus = RELATIVE_PATH_STATUS.REQUIRED;
      }
    }
    else if (tokenIs('/')) {
      if (lookahead('/', 1)) {
        appendOp(4, OpCodes.MATCH_ANY_ANCESTOR);

        // Added this to fix bug reported by Myriam for match="//x/a"
        // patterns. If you don't do this, the 'x' step will think it's part
        // of a '//' pattern, and so will cause 'a' to be matched when it has
        // any ancestor that is 'x'.
        nextToken();

        relativePathStatus = RELATIVE_PATH_STATUS.REQUIRED;
      }
      else {
        appendOp(4, OpCodes.FROM_ROOT);

        relativePathStatus = RELATIVE_PATH_STATUS.PERMITTED;
      }

      // Tell how long the step is without the predicate
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 2, 4);
      m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH) - 1, OpCodes.NODETYPE_ROOT);

      nextToken();
    }
    else {
      relativePathStatus = RELATIVE_PATH_STATUS.REQUIRED;
    }

    if (relativePathStatus != RELATIVE_PATH_STATUS.NOT_PERMITTED) {
      if (!tokenIs('|') && (null != m_token)) {
        RelativePathPattern();
      }
      else if (relativePathStatus == RELATIVE_PATH_STATUS.REQUIRED) {
        error(XPATHErrorResources.ER_EXPECTED_REL_PATH_PATTERN, null);
      }
    }

    // Terminate for safety.
    m_ops.setOp(m_ops.getOp(OpMap.MAPINDEX_LENGTH), OpCodes.ENDOP);
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);
  }

  /**
   * IdKeyPattern ::= 'id' '(' Literal ')' | 'key' '(' Literal ',' Literal ')' (Also handle doc())
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void IdKeyPattern() throws javax.xml.transform.TransformerException {
    FunctionCall();
  }

  /**
   * RelativePathPattern ::= StepPattern | RelativePathPattern '/' StepPattern | RelativePathPattern
   * '//' StepPattern
   *
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected void RelativePathPattern() throws javax.xml.transform.TransformerException {

    // Caller will have consumed any '/' or '//' preceding the
    // RelativePathPattern, so let StepPattern know it can't begin with a '/'
    boolean trailingSlashConsumed = StepPattern(false);

    while (tokenIs('/')) {
      nextToken();

      // StepPattern() may consume first slash of pair in "a//b" while
      // processing StepPattern "a". On next iteration, let StepPattern know
      // that happened, so it doesn't match ill-formed patterns like "a///b".
      trailingSlashConsumed = StepPattern(!trailingSlashConsumed);
    }
  }

  /**
   * StepPattern ::= AbbreviatedNodeTestStep
   *
   * @param isLeadingSlashPermitted a boolean indicating whether a slash can appear at the start of
   *     this step
   * @return boolean indicating whether a slash following the step was consumed
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected boolean StepPattern(final boolean isLeadingSlashPermitted)
      throws javax.xml.transform.TransformerException {
    return AbbreviatedNodeTestStep(isLeadingSlashPermitted);
  }

  /**
   * AbbreviatedNodeTestStep ::= '@'? NodeTest Predicate
   *
   * @param isLeadingSlashPermitted a boolean indicating whether a slash can appear at the start of
   *     this step
   * @return boolean indicating whether a slash following the step was consumed
   * @throws javax.xml.transform.TransformerException in case of error
   */
  protected boolean AbbreviatedNodeTestStep(final boolean isLeadingSlashPermitted)
      throws javax.xml.transform.TransformerException {

    final int opPos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
    final int axesType;

    // The next blocks guarantee that a MATCH_XXX will be added.
    int matchTypePos = -1;

    if (tokenIs('@')) {
      axesType = OpCodes.MATCH_ATTRIBUTE;

      appendOp(2, axesType);
      nextToken();
    }
    else if (this.lookahead("::", 1)) {
      if (tokenIs("attribute")) {
        axesType = OpCodes.MATCH_ATTRIBUTE;

        appendOp(2, axesType);
      }
      else if (tokenIs("child")) {
        matchTypePos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
        axesType = OpCodes.MATCH_IMMEDIATE_ANCESTOR;

        appendOp(2, axesType);
      }
      else {
        axesType = -1;

        this.error(XPATHErrorResources.ER_AXES_NOT_ALLOWED, new Object[] {this.m_token});
      }

      nextToken();
      nextToken();
    }
    else if (tokenIs('/')) {
      if (!isLeadingSlashPermitted) {
        error(XPATHErrorResources.ER_EXPECTED_STEP_PATTERN, null);
      }
      axesType = OpCodes.MATCH_ANY_ANCESTOR;

      appendOp(2, axesType);
      nextToken();
    }
    else {
      matchTypePos = m_ops.getOp(OpMap.MAPINDEX_LENGTH);
      axesType = OpCodes.MATCH_IMMEDIATE_ANCESTOR;

      appendOp(2, axesType);
    }

    // Make room for telling how long the step is without the predicate
    m_ops.setOp(OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) + 1);

    NodeTest(axesType);

    // Tell how long the step is without the predicate
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH + 1, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

    while (tokenIs('[')) {
      Predicate();
    }

    final boolean trailingSlashConsumed;

    // For "a//b", where "a" is current step, we need to mark operation of
    // current step as "MATCH_ANY_ANCESTOR". Then we'll consume the first
    // slash and subsequent step will be treated as a MATCH_IMMEDIATE_ANCESTOR
    // (unless it too is followed by '//'.)
    //
    // %REVIEW% Following is what happens today, but I'm not sure that's
    // %REVIEW% correct behaviour. Perhaps no valid case could be constructed
    // %REVIEW% where it would matter?
    //
    // If current step is on the attribute axis (e.g., "@x//b"), we won't
    // change the current step, and let following step be marked as
    // MATCH_ANY_ANCESTOR on next call instead.
    if ((matchTypePos > -1) && tokenIs('/') && lookahead('/', 1)) {
      m_ops.setOp(matchTypePos, OpCodes.MATCH_ANY_ANCESTOR);

      nextToken();

      trailingSlashConsumed = true;
    }
    else {
      trailingSlashConsumed = false;
    }

    // Tell how long the entire step is.
    m_ops.setOp(opPos + OpMap.MAPINDEX_LENGTH, m_ops.getOp(OpMap.MAPINDEX_LENGTH) - opPos);

    return trailingSlashConsumed;
  }
}
