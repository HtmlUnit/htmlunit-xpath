/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.htmlunit.xpath;

import java.util.Stack;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;
import net.sourceforge.htmlunit.xpath.axes.SubContextList;
import net.sourceforge.htmlunit.xpath.res.XPATHErrorResources;
import net.sourceforge.htmlunit.xpath.res.XSLMessages;
import net.sourceforge.htmlunit.xpath.xml.dtm.Axis;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMFilter;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMIterator;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMManager;
import net.sourceforge.htmlunit.xpath.xml.utils.IntStack;
import net.sourceforge.htmlunit.xpath.xml.utils.NodeVector;
import net.sourceforge.htmlunit.xpath.xml.utils.PrefixResolver;

/**
 * Default class for the runtime execution context for XPath.
 *
 * <p>This class extends DTMManager but does not directly implement it.
 *
 * @xsl.usage advanced
 */
public class XPathContext extends DTMManager {
  /**
   * Though XPathContext context extends the DTMManager, it really is a proxy for this object, which
   * is the real DTMManager.
   */
  protected DTMManager m_dtmManager = DTMManager.newInstance();

  /**
   * Return the DTMManager object. Though XPathContext context extends the DTMManager, it really is
   * a proxy for the real DTMManager. If a caller needs to make a lot of calls to the DTMManager, it
   * is faster if it gets the real one from this function.
   */
  public DTMManager getDTMManager() {
    return m_dtmManager;
  }

  /**
   * Get an instance of a DTM, loaded with the content from the specified source. If the unique flag
   * is true, a new instance will always be returned. Otherwise it is up to the DTMManager to return
   * a new instance or an instance that it already created and may be being used by someone else. (I
   * think more parameters will need to be added for error handling, and entity resolution).
   *
   * @param source the specification of the source object, which may be null, in which case it is
   *     assumed that node construction will take by some other means.
   * @param unique true if the returned DTM must be unique, probably because it is going to be
   *     mutated.
   * @param wsfilter Enables filtering of whitespace nodes, and may be null.
   * @param incremental true if the construction should try and be incremental.
   * @param doIndexing true if the caller considers it worth it to use indexing schemes.
   * @return a non-null DTM reference.
   */
  @Override
  public DTM getDTM(
      javax.xml.transform.Source source, boolean unique, boolean incremental, boolean doIndexing) {
    return m_dtmManager.getDTM(source, unique, incremental, doIndexing);
  }

  /**
   * Get an instance of a DTM that "owns" a node handle.
   *
   * @param nodeHandle the nodeHandle.
   * @return a non-null DTM reference.
   */
  @Override
  public DTM getDTM(int nodeHandle) {
    return m_dtmManager.getDTM(nodeHandle);
  }

  /**
   * Given a W3C DOM node, try and return a DTM handle. Note: calling this may be non-optimal.
   *
   * @param node Non-null reference to a DOM node.
   * @return a valid DTM handle.
   */
  @Override
  public int getDTMHandleFromNode(org.w3c.dom.Node node) {
    return m_dtmManager.getDTMHandleFromNode(node);
  }

  /**
   * Create a new <code>DTMIterator</code> based on an XPath <a
   * href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or a <a
   * href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
   *
   * @param xpathCompiler ??? Somehow we need to pass in a subpart of the expression. I hate to do
   *     this with strings, since the larger expression has already been parsed.
   * @param pos The position in the expression.
   * @return The newly created <code>DTMIterator</code>.
   */
  @Override
  public DTMIterator createDTMIterator(Object xpathCompiler, int pos) {
    return m_dtmManager.createDTMIterator(xpathCompiler, pos);
  }

  //
  /**
   * Create a new <code>DTMIterator</code> based on an XPath <a
   * href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or a <a
   * href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
   *
   * @param xpathString Must be a valid string expressing a <a
   *     href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a> or a <a href=
   *     "http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
   * @param presolver An object that can resolve prefixes to namespace URLs.
   * @return The newly created <code>DTMIterator</code>.
   */
  @Override
  public DTMIterator createDTMIterator(String xpathString, PrefixResolver presolver) {
    return m_dtmManager.createDTMIterator(xpathString, presolver);
  }

  //
  /**
   * Create a new <code>DTMIterator</code> based only on a whatToShow and a DTMFilter. The traversal
   * semantics are defined as the descendant access.
   *
   * @param whatToShow This flag specifies which node types may appear in the logical view of the
   *     tree presented by the iterator. See the description of <code>NodeFilter</code> for the set
   *     of possible <code>SHOW_</code> values.These flags can be combined using <code>OR
   *     </code> .
   * @param filter The <code>NodeFilter</code> to be used with this <code>TreeWalker</code>, or
   *     <code>null</code> to indicate no filter.
   * @param entityReferenceExpansion The value of this flag determines whether entity reference
   *     nodes are expanded.
   * @return The newly created <code>NodeIterator</code>.
   */
  @Override
  public DTMIterator createDTMIterator(
      int whatToShow, DTMFilter filter, boolean entityReferenceExpansion) {
    return m_dtmManager.createDTMIterator(whatToShow, filter, entityReferenceExpansion);
  }

  /**
   * Create a new <code>DTMIterator</code> that holds exactly one node.
   *
   * @param node The node handle that the DTMIterator will iterate to.
   * @return The newly created <code>DTMIterator</code>.
   */
  @Override
  public DTMIterator createDTMIterator(int node) {
    // DescendantIterator iter = new DescendantIterator();
    DTMIterator iter = new net.sourceforge.htmlunit.xpath.axes.OneStepIteratorForward(Axis.SELF);
    iter.setRoot(node, this);
    return iter;
    // return m_dtmManager.createDTMIterator(node);
  }

  /**
   * Create an XPathContext instance. This is equivalent to calling the {@link
   * #XPathContext(boolean)} constructor with the value <code>true</code>.
   */
  public XPathContext() {
    this(true);
  }

  /**
   * Create an XPathContext instance.
   *
   * @param recursiveVarContext A <code>boolean</code> value indicating whether the XPath context
   *     needs to support pushing of scopes for variable resolution
   */
  public XPathContext(boolean recursiveVarContext) {
    m_prefixResolvers.push(null);
    m_currentNodes.push(DTM.NULL);
    m_currentExpressionNodes.push(DTM.NULL);
  }

  /** Reset for new run. */
  public void reset() {
    m_dtmManager = DTMManager.newInstance();

    m_axesIteratorStack.removeAllElements();
    m_currentExpressionNodes.removeAllElements();
    m_currentNodes.removeAllElements();
    m_iteratorRoots.RemoveAllNoClear();
    m_predicatePos.removeAllElements();
    m_predicateRoots.RemoveAllNoClear();
    m_prefixResolvers.removeAllElements();

    m_prefixResolvers.push(null);
    m_currentNodes.push(DTM.NULL);
    m_currentExpressionNodes.push(DTM.NULL);
  }

  // =================================================

  /** The ErrorListener where errors and warnings are to be reported. */
  private ErrorListener m_errorListener;

  /**
   * A default ErrorListener in case our m_errorListener was not specified and our owner either does
   * not have an ErrorListener or has a null one.
   */
  private ErrorListener m_defaultErrorListener;

  /**
   * Get the ErrorListener where errors and warnings are to be reported.
   *
   * @return A non-null ErrorListener reference.
   */
  public final ErrorListener getErrorListener() {

    if (null != m_errorListener) return m_errorListener;

    ErrorListener retval = null;

    if (null == retval) {
      if (null == m_defaultErrorListener)
        m_defaultErrorListener = new net.sourceforge.htmlunit.xpath.xml.utils.DefaultErrorHandler();
      retval = m_defaultErrorListener;
    }

    return retval;
  }

  /**
   * Set the ErrorListener where errors and warnings are to be reported.
   *
   * @param listener A non-null ErrorListener reference.
   */
  public void setErrorListener(ErrorListener listener) throws IllegalArgumentException {
    if (listener == null)
      throw new IllegalArgumentException(
          XSLMessages.createXPATHMessage(
              XPATHErrorResources.ER_NULL_ERROR_HANDLER, null)); // "Null error
    // handler");
    m_errorListener = listener;
  }

  // =================================================

  /**
   * The TrAX URI Resolver for resolving URIs from the document(...) function to source tree nodes.
   */
  private URIResolver m_uriResolver;

  /**
   * Get the URIResolver associated with this execution context.
   *
   * @return a URI resolver, which may be null.
   */
  public final URIResolver getURIResolver() {
    return m_uriResolver;
  }

  /**
   * Set the URIResolver associated with this execution context.
   *
   * @param resolver the URIResolver to be associated with this execution context, may be null to
   *     clear an already set resolver.
   */
  public void setURIResolver(URIResolver resolver) {
    m_uriResolver = resolver;
  }

  // ==========================================================
  // SECTION: Execution context state tracking
  // ==========================================================

  /** The ammount to use for stacks that record information during the recursive execution. */
  public static final int RECURSIONLIMIT = 1024 * 4;

  /**
   * The stack of <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a> objects. Not
   * to be confused with the current node list. %REVIEW% Note that there are no bounds check and
   * resize for this stack, so if it is blown, it's all over.
   */
  private IntStack m_currentNodes = new IntStack(RECURSIONLIMIT);

  /**
   * Get the current context node.
   *
   * @return the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   */
  public final int getCurrentNode() {
    return m_currentNodes.peek();
  }

  /**
   * Set the current context node and expression node.
   *
   * @param cn the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   * @param en the sub-expression context node.
   */
  public final void pushCurrentNodeAndExpression(int cn, int en) {
    m_currentNodes.push(cn);
    m_currentExpressionNodes.push(cn);
  }

  /** Set the current context node. */
  public final void popCurrentNodeAndExpression() {
    m_currentNodes.quickPop(1);
    m_currentExpressionNodes.quickPop(1);
  }

  /**
   * Set the current context node.
   *
   * @param n the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   */
  public final void pushCurrentNode(int n) {
    m_currentNodes.push(n);
  }

  /** Pop the current context node. */
  public final void popCurrentNode() {
    m_currentNodes.quickPop(1);
  }

  /** Set the current predicate root. */
  public final void pushPredicateRoot(int n) {
    m_predicateRoots.push(n);
  }

  /** Pop the current predicate root. */
  public final void popPredicateRoot() {
    m_predicateRoots.popQuick();
  }

  /** Get the current predicate root. */
  public final int getPredicateRoot() {
    return m_predicateRoots.peepOrNull();
  }

  /** Set the current location path iterator root. */
  public final void pushIteratorRoot(int n) {
    m_iteratorRoots.push(n);
  }

  /** Pop the current location path iterator root. */
  public final void popIteratorRoot() {
    m_iteratorRoots.popQuick();
  }

  /** Get the current location path iterator root. */
  public final int getIteratorRoot() {
    return m_iteratorRoots.peepOrNull();
  }

  /** A stack of the current sub-expression nodes. */
  private NodeVector m_iteratorRoots = new NodeVector();

  /** A stack of the current sub-expression nodes. */
  private NodeVector m_predicateRoots = new NodeVector();

  /** A stack of the current sub-expression nodes. */
  private IntStack m_currentExpressionNodes = new IntStack(RECURSIONLIMIT);

  private Stack<Integer> m_predicatePos = new Stack<>();

  public final int getPredicatePos() {
    return m_predicatePos.peek();
  }

  public final void pushPredicatePos(int n) {
    m_predicatePos.push(n);
  }

  public final void popPredicatePos() {
    m_predicatePos.pop();
  }

  /**
   * Set the current node that is the expression's context (i.e. for current() support).
   *
   * @param n The sub-expression node to be current.
   */
  public final void pushCurrentExpressionNode(int n) {
    m_currentExpressionNodes.push(n);
  }

  /** Pop the current node that is the expression's context (i.e. for current() support). */
  public final void popCurrentExpressionNode() {
    m_currentExpressionNodes.quickPop(1);
  }

  private Stack<PrefixResolver> m_prefixResolvers = new Stack<>();

  /**
   * Get the current namespace context for the xpath.
   *
   * @return the current prefix resolver for resolving prefixes to namespace URLs.
   */
  public final PrefixResolver getNamespaceContext() {
    return m_prefixResolvers.peek();
  }

  /**
   * Get the current namespace context for the xpath.
   *
   * @param pr the prefix resolver to be used for resolving prefixes to namespace URLs.
   */
  public final void setNamespaceContext(PrefixResolver pr) {
    m_prefixResolvers.pop();
    m_prefixResolvers.push(pr);
  }

  /**
   * Push a current namespace context for the xpath.
   *
   * @param pr the prefix resolver to be used for resolving prefixes to namespace URLs.
   */
  public final void pushNamespaceContext(PrefixResolver pr) {
    m_prefixResolvers.push(pr);
  }

  /**
   * Just increment the namespace contest stack, so that setNamespaceContext can be used on the
   * slot.
   */
  public final void pushNamespaceContextNull() {
    m_prefixResolvers.push(null);
  }

  /** Pop the current namespace context for the xpath. */
  public final void popNamespaceContext() {
    m_prefixResolvers.pop();
  }

  // ==========================================================
  // SECTION: Current TreeWalker contexts (for internal use)
  // ==========================================================

  /** Stack of AxesIterators. */
  private Stack<SubContextList> m_axesIteratorStack = new Stack<>();

  public Stack<SubContextList> getAxesIteratorStackStacks() {
    return m_axesIteratorStack;
  }

  public void setAxesIteratorStackStacks(Stack<SubContextList> s) {
    m_axesIteratorStack = s;
  }

  /**
   * Push a TreeWalker on the stack.
   *
   * @param iter A sub-context AxesWalker.
   * @xsl.usage internal
   */
  public final void pushSubContextList(SubContextList iter) {
    m_axesIteratorStack.push(iter);
  }

  /**
   * Pop the last pushed axes iterator.
   *
   * @xsl.usage internal
   */
  public final void popSubContextList() {
    m_axesIteratorStack.pop();
  }

  /**
   * Get the current axes iterator, or return null if none.
   *
   * @return the sub-context node list.
   * @xsl.usage internal
   */
  public SubContextList getSubContextList() {
    return m_axesIteratorStack.isEmpty() ? null : m_axesIteratorStack.peek();
  }

  /**
   * Get the <a href="http://www.w3.org/TR/xslt#dt-current-node-list">current node list</a> as
   * defined by the XSLT spec.
   *
   * @return the <a href="http://www.w3.org/TR/xslt#dt-current-node-list">current node list</a>.
   * @xsl.usage internal
   */
  public net.sourceforge.htmlunit.xpath.axes.SubContextList getCurrentNodeList() {
    return m_axesIteratorStack.isEmpty() ? null : m_axesIteratorStack.elementAt(0);
  }
  // ==========================================================
  // SECTION: Implementation of ExpressionContext interface
  // ==========================================================

  /**
   * Get the current context node.
   *
   * @return The current context node.
   */
  public final int getContextNode() {
    return this.getCurrentNode();
  }
}
