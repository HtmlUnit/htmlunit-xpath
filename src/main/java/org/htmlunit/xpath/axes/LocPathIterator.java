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
package org.htmlunit.xpath.axes;

import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.XPathVisitor;
import org.htmlunit.xpath.objects.XNodeSet;
import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMFilter;
import org.htmlunit.xpath.xml.dtm.DTMIterator;
import org.htmlunit.xpath.xml.dtm.DTMManager;
import org.htmlunit.xpath.xml.utils.PrefixResolver;

/**
 * This class extends NodeSetDTM, which implements NodeIterator, and fetches nodes one at a time in
 * document order based on a XPath.<br>
 *
 * @see <a href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a>.
 *     <p>If setShouldCacheNodes(true) is called, as each node is iterated via nextNode(), the node
 *     is also stored in the NodeVector, so that previousNode() can easily be done, except in the
 *     case where the LocPathIterator is "owned" by a UnionPathIterator, in which case the
 *     UnionPathIterator will cache the nodes.
 */
public abstract class LocPathIterator extends PredicatedNodeTest
    implements Cloneable, DTMIterator, PathComponent {

  /** Create a LocPathIterator object. */
  protected LocPathIterator() {
  }

  /**
   * Create a LocPathIterator object.
   *
   * @param nscontext The namespace context for this iterator, should be OK if null.
   */
  protected LocPathIterator(final PrefixResolver nscontext) {

    setLocPathIterator(this);
    m_prefixResolver = nscontext;
  }

  /**
   * Create a LocPathIterator object, including creation of step walkers from the opcode list, and
   * call back into the Compiler to create predicate expressions.
   *
   * @throws javax.xml.transform.TransformerException if any
   */
  protected LocPathIterator(final int analysis) throws javax.xml.transform.TransformerException {
    this(analysis, true);
  }

  /**
   * Create a LocPathIterator object, including creation of step walkers from the opcode list, and
   * call back into the Compiler to create predicate expressions.
   *
   * @param shouldLoadWalkers True if walkers should be loaded, or false if this is a derived
   *     iterator and it doesn't wish to load child walkers.
   * @throws javax.xml.transform.TransformerException if any
   */
  protected LocPathIterator(final int analysis, final boolean shouldLoadWalkers)
      throws javax.xml.transform.TransformerException {
    setLocPathIterator(this);
  }

  /** {@inheritDoc} */
  @Override
  public int getAnalysisBits() {
    final int axis = getAxis();
    return WalkerFactory.getAnalysisBitFromAxes(axis);
  }

  /** {@inheritDoc} */
  @Override
  public DTM getDTM(final int nodeHandle) {
    // %OPT%
    return m_execContext.getDTM(nodeHandle);
  }

  /** {@inheritDoc} */
  @Override
  public DTMManager getDTMManager() {
    return m_execContext.getDTMManager();
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    final XNodeSet iter = new XNodeSet(m_clones.getInstance());

    iter.setRoot(xctxt.getCurrentNode(), xctxt);

    return iter;
  }

  /** {@inheritDoc} */
  @Override
  public DTMIterator asIterator(final XPathContext xctxt, final int contextNode) {
    final XNodeSet iter = new XNodeSet(m_clones.getInstance());

    iter.setRoot(contextNode, xctxt);

    return iter;
  }

  /** {@inheritDoc} */
  @Override
  public int asNode(final XPathContext xctxt) throws javax.xml.transform.TransformerException {
    final DTMIterator iter = m_clones.getInstance();

    final int current = xctxt.getCurrentNode();

    iter.setRoot(current, xctxt);

    final int next = iter.nextNode();
    iter.detach();
    return next;
  }

  /** {@inheritDoc} */
  @Override
  public boolean bool(final XPathContext xctxt) throws javax.xml.transform.TransformerException {
    return asNode(xctxt) != DTM.NULL;
  }

  /**
   * Set if this is an iterator at the upper level of the XPath.
   *
   * @param b true if this location path is at the top level of the expression.
   */
  public void setIsTopLevel(final boolean b) {
    m_isTopLevel = b;
  }

  /**
   * Get if this is an iterator at the upper level of the XPath.
   *
   * @return true if this location path is at the top level of the expression.
   */
  public boolean getIsTopLevel() {
    return m_isTopLevel;
  }

  /** {@inheritDoc} */
  @Override
  public void setRoot(final int context, final Object environment) {

    m_context = context;

    final XPathContext xctxt = (XPathContext) environment;
    m_execContext = xctxt;
    m_cdtm = xctxt.getDTM(context);

    m_currentContextNode = context; // only if top level?

    // Yech, shouldn't have to do this. -sb
    if (null == m_prefixResolver) {
        m_prefixResolver = xctxt.getNamespaceContext();
    }

    m_lastFetched = DTM.NULL;
    m_foundLast = false;
    m_pos = 0;
    m_length = -1;

    // reset();
  }

  /** {@inheritDoc} */
  @Override
  public final int getCurrentPos() {
    return m_pos;
  }

  /** {@inheritDoc} */
  @Override
  public void setShouldCacheNodes(final boolean b) {

    assertion(false, "setShouldCacheNodes not supported by this iterater!");
  }

  /** {@inheritDoc} */
  @Override
  public void setCurrentPos(final int i) {
    assertion(false, "setCurrentPos not supported by this iterator!");
  }

  /** Increment the current position in the node set. */
  public void incrementCurrentPos() {
    m_pos++;
  }

  /** {@inheritDoc} */
  @Override
  public int item(final int index) {
    assertion(false, "item(int index) not supported by this iterator!");
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public int getLength() {
    // Tell if this is being called from within a predicate.
    final boolean isPredicateTest = this == m_execContext.getSubContextList();

    // And get how many total predicates are part of this step.
    final int predCount = getPredicateCount();

    // If we have already calculated the length, and the current predicate
    // is the first predicate, then return the length. We don't cache
    // the anything but the length of the list to the first predicate.
    if (-1 != m_length && isPredicateTest && m_predicateIndex < 1) {
        return m_length;
    }

    // I'm a bit worried about this one, since it doesn't have the
    // checks found above. I suspect it's fine. -sb
    if (m_foundLast) {
        return m_pos;
    }

    // Create a clone, and count from the current position to the end
    // of the list, not taking into account the current predicate and
    // predicates after the current one.
    int pos = (m_predicateIndex >= 0) ? getProximityPosition() : m_pos;

    final LocPathIterator clone;

    try {
      clone = (LocPathIterator) clone();
    }
    catch (final CloneNotSupportedException cnse) {
      return -1;
    }

    // We want to clip off the last predicate, but only if we are a sub
    // context node list, NOT if we are a context list. See pos68 test,
    // also test against bug4638.
    if (predCount > 0 && isPredicateTest) {
      // Don't call setPredicateCount, because it clones and is slower.
      clone.m_predCount = m_predicateIndex;
      // The line above used to be:
      // clone.m_predCount = predCount - 1;
      // ...which looks like a dumb bug to me. -sb
    }

    while (DTM.NULL != clone.nextNode()) {
      pos++;
    }

    if (isPredicateTest && m_predicateIndex < 1) {
        m_length = pos;
    }

    return pos;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFresh() {
    return m_pos == 0;
  }

  /** {@inheritDoc} */
  @Override
  public int previousNode() {
    throw new RuntimeException(
        XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_NODESETDTM_CANNOT_ITERATE, null));
  }

  /** {@inheritDoc} */
  @Override
  public int getWhatToShow() {

    // TODO: ??
    return DTMFilter.SHOW_ALL & ~DTMFilter.SHOW_ENTITY_REFERENCE;
  }

  /** {@inheritDoc} */
  @Override
  public int getRoot() {
    return m_context;
  }

  /** {@inheritDoc} */
  @Override
  public boolean getExpandEntityReferences() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {
    // sb: allow reusing of cached nodes when possible?
    // m_cachedNodes = null;
    m_execContext = null;
    // m_prefixResolver = null; sb: Why would this ever want to be null?
    m_cdtm = null;
    m_length = -1;
    m_pos = 0;
    m_lastFetched = DTM.NULL;
    m_context = DTM.NULL;
    m_currentContextNode = DTM.NULL;

    m_clones.freeInstance(this);
  }

  /** {@inheritDoc} */
  @Override
  public void reset() {
    assertion(false, "This iterator can not reset!");
  }

  /** {@inheritDoc} */
  @Override
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {
    final LocPathIterator clone;
    // clone = (LocPathIterator) clone();
    clone = (LocPathIterator) m_clones.getInstanceOrThrow();
    clone.m_execContext = m_execContext;
    clone.m_cdtm = m_cdtm;

    clone.m_context = m_context;
    clone.m_currentContextNode = m_currentContextNode;
    clone.m_stackFrame = m_stackFrame;

    // clone.reset();

    return clone;
  }

  /** {@inheritDoc} */
  @Override
  public abstract int nextNode();

  /**
   * Bottleneck the return of a next node, to make returns easier from nextNode().
   *
   * @param nextNode The next node found, may be null.
   * @return The same node that was passed as an argument.
   */
  protected int returnNextNode(final int nextNode) {

    if (DTM.NULL != nextNode) {
      m_pos++;
    }

    m_lastFetched = nextNode;

    if (DTM.NULL == nextNode) {
        m_foundLast = true;
    }

    return nextNode;
  }

  /** {@inheritDoc} */
  @Override
  public int getCurrentNode() {
    return m_lastFetched;
  }

  /** {@inheritDoc} */
  @Override
  public void runTo(final int index) {

    if (m_foundLast || ((index >= 0) && (index <= getCurrentPos()))) {
        return;
    }

    if (-1 == index) {
      while (DTM.NULL != nextNode()) {
        ;
    }
    }
    else {
      while (DTM.NULL != nextNode()) {
        if (getCurrentPos() >= index) {
            break;
        }
      }
    }
  }

  /**
   * The XPath execution context we are operating on.
   *
   * @return XPath execution context this iterator is operating on, or null if setRoot has not been
   *     called.
   */
  public final XPathContext getXPathContext() {
    return m_execContext;
  }

  /**
   * Return the saved reference to the prefix resolver that was in effect when this iterator was
   * created.
   *
   * @return The prefix resolver or this iterator, which may be null.
   */
  public final PrefixResolver getPrefixResolver() {
    if (null == m_prefixResolver) {
      m_prefixResolver = (PrefixResolver) getExpressionOwner();
    }

    return m_prefixResolver;
  }

  /** {@inheritDoc} */
  @Override
  public void callVisitors(final XPathVisitor visitor) {
    if (visitor.visitLocationPath()) {
      visitor.visitStep();
      callPredicateVisitors(visitor);
    }
  }

  // ============= State Data =============

  /**
   * The pool for cloned iterators. Iterators need to be cloned because the hold running state, and
   * thus the original iterator expression from the stylesheet pool can not be used.
   */
  protected final transient IteratorPool m_clones = new IteratorPool(this);

  /**
   * The dtm of the context node. Careful about using this... it may not be the dtm of the current
   * node.
   */
  protected transient DTM m_cdtm;

  /** The stack frame index for this iterator. */
  transient int m_stackFrame = -1;

  /**
   * Value determined at compile time, indicates that this is an iterator at the top level of the
   * expression, rather than inside a predicate.
   *
   * @serial
   */
  private boolean m_isTopLevel = false;

  /** The last node that was fetched, usually by nextNode. */
  public transient int m_lastFetched = DTM.NULL;

  /**
   * The context node for this iterator, which doesn't change through the course of the iteration.
   */
  protected transient int m_context = DTM.NULL;

  /**
   * The node context from where the expression is being executed from (i.e. for current() support).
   * Different from m_context in that this is the context for the entire expression, rather than the
   * context for the subexpression.
   */
  protected transient int m_currentContextNode = DTM.NULL;

  /** The current position of the context node. */
  protected transient int m_pos = 0;

  protected transient int m_length = -1;

  /**
   * Fast access to the current prefix resolver. It isn't really clear that this is needed.
   *
   * @serial
   */
  private PrefixResolver m_prefixResolver;

  /** The XPathContext reference, needed for execution of many operations. */
  protected transient XPathContext m_execContext;

  /** {@inheritDoc} */
  @Override
  public boolean isDocOrdered() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public int getAxis() {
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public int getLastPos(final XPathContext xctxt) {
    return getLength();
  }
}
