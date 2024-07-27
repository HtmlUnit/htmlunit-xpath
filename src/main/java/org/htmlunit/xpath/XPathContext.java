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
package org.htmlunit.xpath;

import java.util.Stack;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.URIResolver;
import org.htmlunit.xpath.axes.SubContextList;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMManager;
import org.htmlunit.xpath.xml.utils.PrefixResolver;

/**
 * Default class for the runtime execution context for XPath.
 *
 * <p>This class extends DTMManager but does not directly implement it.
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

  /** {@inheritDoc} */
  @Override
  public DTM getDTM(
      final javax.xml.transform.Source source,
      final boolean unique,
      final boolean incremental,
      final boolean doIndexing) {
    return m_dtmManager.getDTM(source, unique, incremental, doIndexing);
  }

  /** {@inheritDoc} */
  @Override
  public DTM getDTM(final int nodeHandle) {
    return m_dtmManager.getDTM(nodeHandle);
  }

  /** {@inheritDoc} */
  @Override
  public int getDTMHandleFromNode(final org.w3c.dom.Node node) {
    return m_dtmManager.getDTMHandleFromNode(node);
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
  public XPathContext(final boolean recursiveVarContext) {
    m_prefixResolvers.push(null);
    m_currentNodes.push(DTM.NULL);
  }

  /** Reset for new run. */
  public void reset() {
    m_dtmManager = DTMManager.newInstance();

    m_axesIteratorStack.removeAllElements();
    m_currentNodes.removeAllElements();
    m_predicatePos.removeAllElements();
    m_prefixResolvers.removeAllElements();

    m_prefixResolvers.push(null);
    m_currentNodes.push(DTM.NULL);
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
    if (null != m_errorListener) {
        return m_errorListener;
    }

    if (null == m_defaultErrorListener) {
      m_defaultErrorListener = new org.htmlunit.xpath.xml.utils.DefaultErrorHandler();
    }
    return m_defaultErrorListener;
  }

  /**
   * Set the ErrorListener where errors and warnings are to be reported.
   *
   * @param listener A non-null ErrorListener reference.
   */
  public void setErrorListener(final ErrorListener listener) throws IllegalArgumentException {
    if (listener == null) {
        throw new IllegalArgumentException(
              XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_NULL_ERROR_HANDLER, null));
    }
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
  public void setURIResolver(final URIResolver resolver) {
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
  private final Stack<Integer> m_currentNodes = new Stack<>();

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
   */
  public final void pushCurrentNodeAndExpression(final int cn) {
    m_currentNodes.push(cn);
  }

  /** Set the current context node. */
  public final void popCurrentNodeAndExpression() {
    m_currentNodes.pop();
  }

  /**
   * Set the current context node.
   *
   * @param n the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   */
  public final void pushCurrentNode(final int n) {
    m_currentNodes.push(n);
  }

  /** Pop the current context node. */
  public final void popCurrentNode() {
    m_currentNodes.pop();
  }

  private final Stack<Integer> m_predicatePos = new Stack<>();

  public final int getPredicatePos() {
    return m_predicatePos.peek();
  }

  public final void pushPredicatePos(final int n) {
    m_predicatePos.push(n);
  }

  public final void popPredicatePos() {
    m_predicatePos.pop();
  }

  private final Stack<PrefixResolver> m_prefixResolvers = new Stack<>();

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
  public final void setNamespaceContext(final PrefixResolver pr) {
    m_prefixResolvers.pop();
    m_prefixResolvers.push(pr);
  }

  /**
   * Push a current namespace context for the xpath.
   *
   * @param pr the prefix resolver to be used for resolving prefixes to namespace URLs.
   */
  public final void pushNamespaceContext(final PrefixResolver pr) {
    m_prefixResolvers.push(pr);
  }

  /** Pop the current namespace context for the xpath. */
  public final void popNamespaceContext() {
    m_prefixResolvers.pop();
  }

  // ==========================================================
  // SECTION: Current TreeWalker contexts (for internal use)
  // ==========================================================

  /** Stack of AxesIterators. */
  private final Stack<SubContextList> m_axesIteratorStack = new Stack<>();

  /**
   * Push a TreeWalker on the stack.
   *
   * @param iter A sub-context AxesWalker.
   */
  public final void pushSubContextList(final SubContextList iter) {
    m_axesIteratorStack.push(iter);
  }

  /** Pop the last pushed axes iterator. */
  public final void popSubContextList() {
    m_axesIteratorStack.pop();
  }

  /**
   * Get the current axes iterator, or return null if none.
   *
   * @return the sub-context node list.
   */
  public SubContextList getSubContextList() {
    return m_axesIteratorStack.isEmpty() ? null : m_axesIteratorStack.peek();
  }

  // ==========================================================
  // SECTION: Implementation of ExpressionContext interface
  // ==========================================================

}
