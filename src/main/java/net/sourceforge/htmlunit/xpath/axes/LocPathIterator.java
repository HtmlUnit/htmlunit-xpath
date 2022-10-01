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
package net.sourceforge.htmlunit.xpath.axes;

import net.sourceforge.htmlunit.xpath.ExpressionOwner;
import net.sourceforge.htmlunit.xpath.XPathContext;
import net.sourceforge.htmlunit.xpath.XPathVisitor;
import net.sourceforge.htmlunit.xpath.objects.XNodeSet;
import net.sourceforge.htmlunit.xpath.objects.XObject;
import net.sourceforge.htmlunit.xpath.res.XPATHErrorResources;
import net.sourceforge.htmlunit.xpath.res.XSLMessages;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMFilter;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMIterator;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMManager;
import net.sourceforge.htmlunit.xpath.xml.utils.PrefixResolver;

/**
 * This class extends NodeSetDTM, which implements NodeIterator, and fetches nodes one at a time in
 * document order based on a XPath <a
 * href="http://www.w3.org/TR/xpath#NT-LocationPath>LocationPath</a>.
 *
 * <p>If setShouldCacheNodes(true) is called, as each node is iterated via nextNode(), the node is
 * also stored in the NodeVector, so that previousNode() can easily be done, except in the case
 * where the LocPathIterator is "owned" by a UnionPathIterator, in which case the UnionPathIterator
 * will cache the nodes.
 */
public abstract class LocPathIterator extends PredicatedNodeTest
    implements Cloneable, DTMIterator, PathComponent {

  /** Create a LocPathIterator object. */
  protected LocPathIterator() {}

  /**
   * Create a LocPathIterator object.
   *
   * @param nscontext The namespace context for this iterator, should be OK if null.
   */
  protected LocPathIterator(PrefixResolver nscontext) {

    setLocPathIterator(this);
    m_prefixResolver = nscontext;
  }

  /**
   * Create a LocPathIterator object, including creation of step walkers from the opcode list, and
   * call back into the Compiler to create predicate expressions.
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected LocPathIterator(int analysis)
      throws javax.xml.transform.TransformerException {
    this(analysis, true);
  }

  /**
   * Create a LocPathIterator object, including creation of step walkers from the opcode list, and
   * call back into the Compiler to create predicate expressions.
   *
   * @param shouldLoadWalkers True if walkers should be loaded, or false if this is a derived
   *     iterator and it doesn't wish to load child walkers.
   * @throws javax.xml.transform.TransformerException
   */
  protected LocPathIterator(int analysis, boolean shouldLoadWalkers)
      throws javax.xml.transform.TransformerException {
    setLocPathIterator(this);
  }

  /**
   * Get the analysis bits for this walker, as defined in the WalkerFactory.
   *
   * @return One of WalkerFactory#BIT_DESCENDANT, etc.
   */
  @Override
  public int getAnalysisBits() {
    int axis = getAxis();
    return WalkerFactory.getAnalysisBitFromAxes(axis);
  }

  /**
   * Get an instance of a DTM that "owns" a node handle. Since a node iterator may be passed without
   * a DTMManager, this allows the caller to easily get the DTM using just the iterator.
   *
   * @param nodeHandle the nodeHandle.
   * @return a non-null DTM reference.
   */
  @Override
  public DTM getDTM(int nodeHandle) {
    // %OPT%
    return m_execContext.getDTM(nodeHandle);
  }

  /**
   * Get an instance of the DTMManager. Since a node iterator may be passed without a DTMManager,
   * this allows the caller to easily get the DTMManager using just the iterator.
   *
   * @return a non-null DTMManager reference.
   */
  @Override
  public DTMManager getDTMManager() {
    return m_execContext.getDTMManager();
  }

  /**
   * Execute this iterator, meaning create a clone that can store state, and initialize it for fast
   * execution from the current runtime state. When this is called, no actual query from the current
   * context node is performed.
   *
   * @param xctxt The XPath execution context.
   * @return An XNodeSet reference that holds this iterator.
   * @throws javax.xml.transform.TransformerException
   */
  @Override
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

    XNodeSet iter = new XNodeSet(m_clones.getInstance());

    iter.setRoot(xctxt.getCurrentNode(), xctxt);

    return iter;
  }

  /**
   * Given an select expression and a context, evaluate the XPath and return the resulting iterator.
   *
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   */
  @Override
  public DTMIterator asIterator(XPathContext xctxt, int contextNode) {
    XNodeSet iter = new XNodeSet(m_clones.getInstance());

    iter.setRoot(contextNode, xctxt);

    return iter;
  }

  /**
   * Tell if the expression is a nodeset expression.
   *
   * @return true if the expression can be represented as a nodeset.
   */
  @Override
  public boolean isNodesetExpr() {
    return true;
  }

  /**
   * Return the first node out of the nodeset, if this expression is a nodeset expression. This is
   * the default implementation for nodesets. Derived classes should try and override this and
   * return a value without having to do a clone operation.
   *
   * @param xctxt The XPath runtime context.
   * @return the first node out of the nodeset, or DTM.NULL.
   */
  @Override
  public int asNode(XPathContext xctxt) throws javax.xml.transform.TransformerException {
    DTMIterator iter = m_clones.getInstance();

    int current = xctxt.getCurrentNode();

    iter.setRoot(current, xctxt);

    int next = iter.nextNode();
    // m_clones.freeInstance(iter);
    iter.detach();
    return next;
  }

  /**
   * Evaluate this operation directly to a boolean.
   *
   * @param xctxt The runtime execution context.
   * @return The result of the operation as a boolean.
   * @throws javax.xml.transform.TransformerException
   */
  @Override
  public boolean bool(XPathContext xctxt) throws javax.xml.transform.TransformerException {
    return asNode(xctxt) != DTM.NULL;
  }

  /**
   * Set if this is an iterator at the upper level of the XPath.
   *
   * @param b true if this location path is at the top level of the expression.
   */
  public void setIsTopLevel(boolean b) {
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

  /**
   * Initialize the context values for this expression after it is cloned.
   *
   * @param context The XPath runtime context for this transformation.
   */
  @Override
  public void setRoot(int context, Object environment) {

    m_context = context;

    XPathContext xctxt = (XPathContext) environment;
    m_execContext = xctxt;
    m_cdtm = xctxt.getDTM(context);

    m_currentContextNode = context; // only if top level?

    // Yech, shouldn't have to do this. -sb
    if (null == m_prefixResolver) m_prefixResolver = xctxt.getNamespaceContext();

    m_lastFetched = DTM.NULL;
    m_foundLast = false;
    m_pos = 0;
    m_length = -1;

    // reset();
  }

  /**
   * Get the current position, which is one less than the next nextNode() call will retrieve. i.e.
   * if you call getCurrentPos() and the return is 0, the next fetch will take place at index 1.
   *
   * @return A value greater than or equal to zero that indicates the next node position to fetch.
   */
  @Override
  public final int getCurrentPos() {
    return m_pos;
  }

  /**
   * If setShouldCacheNodes(true) is called, then nodes will be cached. They are not cached by
   * default.
   *
   * @param b True if this iterator should cache nodes.
   */
  @Override
  public void setShouldCacheNodes(boolean b) {

    assertion(false, "setShouldCacheNodes not supported by this iterater!");
  }

  /**
   * Set the current position in the node set.
   *
   * @param i Must be a valid index greater than or equal to zero and less than
   *     m_cachedNodes.size().
   */
  @Override
  public void setCurrentPos(int i) {
    assertion(false, "setCurrentPos not supported by this iterator!");
  }

  /** Increment the current position in the node set. */
  public void incrementCurrentPos() {
    m_pos++;
  }

  /**
   * Get the length of the cached nodes.
   *
   * <p>Note: for the moment at least, this only returns the size of the nodes that have been
   * fetched to date, it doesn't attempt to run to the end to make sure we have found everything.
   * This should be reviewed.
   *
   * @return The size of the current cache list.
   */
  public int size() {
    assertion(false, "size() not supported by this iterator!");
    return 0;
  }

  /**
   * Returns the <code>index</code> th item in the collection. If <code>index</code> is greater than
   * or equal to the number of nodes in the list, this returns <code>null</code> .
   *
   * @param index Index into the collection.
   * @return The node at the <code>index</code> th position in the <code>NodeList</code> , or <code>
   *     null</code> if that is not a valid index.
   */
  @Override
  public int item(int index) {
    assertion(false, "item(int index) not supported by this iterator!");
    return 0;
  }

  /**
   * Sets the node at the specified index of this vector to be the specified node. The previous
   * component at that position is discarded.
   *
   * <p>The index must be a value greater than or equal to 0 and less than the current size of the
   * vector. The iterator must be in cached mode.
   *
   * <p>Meant to be used for sorted iterators.
   *
   * @param node Node to set
   * @param index Index of where to set the node
   */
  @Override
  public void setItem(int node, int index) {
    assertion(false, "setItem not supported by this iterator!");
  }

  /**
   * The number of nodes in the list. The range of valid child node indices is 0 to <code>length-1
   * </code> inclusive.
   *
   * @return The number of nodes in the list, always greater or equal to zero.
   */
  @Override
  public int getLength() {
    // Tell if this is being called from within a predicate.
    boolean isPredicateTest = this == m_execContext.getSubContextList();

    // And get how many total predicates are part of this step.
    int predCount = getPredicateCount();

    // If we have already calculated the length, and the current predicate
    // is the first predicate, then return the length. We don't cache
    // the anything but the length of the list to the first predicate.
    if (-1 != m_length && isPredicateTest && m_predicateIndex < 1) return m_length;

    // I'm a bit worried about this one, since it doesn't have the
    // checks found above. I suspect it's fine. -sb
    if (m_foundLast) return m_pos;

    // Create a clone, and count from the current position to the end
    // of the list, not taking into account the current predicate and
    // predicates after the current one.
    int pos = (m_predicateIndex >= 0) ? getProximityPosition() : m_pos;

    LocPathIterator clone;

    try {
      clone = (LocPathIterator) clone();
    } catch (CloneNotSupportedException cnse) {
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

    if (isPredicateTest && m_predicateIndex < 1) m_length = pos;

    return pos;
  }

  /**
   * Tells if this NodeSetDTM is "fresh", in other words, if the first nextNode() that is called
   * will return the first node in the set.
   *
   * @return true of nextNode has not been called.
   */
  @Override
  public boolean isFresh() {
    return m_pos == 0;
  }

  /**
   * Returns the previous node in the set and moves the position of the iterator backwards in the
   * set.
   *
   * @return The previous <code>Node</code> in the set being iterated over, or<code>null</code> if
   *     there are no more members in that set.
   */
  @Override
  public int previousNode() {
    throw new RuntimeException(
        XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_NODESETDTM_CANNOT_ITERATE, null)); // "This
    // NodeSetDTM
    // can not
    // iterate to a
    // previous
    // node!");
  }

  /**
   * This attribute determines which node types are presented via the iterator. The available set of
   * constants is defined in the <code>NodeFilter</code> interface.
   *
   * <p>This is somewhat useless at this time, since it doesn't really return information that tells
   * what this iterator will show. It is here only to fullfill the DOM NodeIterator interface.
   *
   * @return For now, always NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE.
   * @see org.w3c.dom.traversal.NodeIterator
   */
  @Override
  public int getWhatToShow() {

    // TODO: ??
    return DTMFilter.SHOW_ALL & ~DTMFilter.SHOW_ENTITY_REFERENCE;
  }

  /**
   * The filter used to screen nodes. Not used at this time, this is here only to fullfill the DOM
   * NodeIterator interface.
   *
   * @return Always null.
   * @see org.w3c.dom.traversal.NodeIterator
   */
  public DTMFilter getFilter() {
    return null;
  }

  /**
   * The root node of the Iterator, as specified when it was created.
   *
   * @return The "root" of this iterator, which, in XPath terms, is the node context for this
   *     iterator.
   */
  @Override
  public int getRoot() {
    return m_context;
  }

  /**
   * The value of this flag determines whether the children of entity reference nodes are visible to
   * the iterator. If false, they will be skipped over. <br>
   * To produce a view of the document that has entity references expanded and does not expose the
   * entity reference node itself, use the whatToShow flags to hide the entity reference node and
   * set expandEntityReferences to true when creating the iterator. To produce a view of the
   * document that has entity reference nodes but no entity expansion, use the whatToShow flags to
   * show the entity reference node and set expandEntityReferences to false.
   *
   * @return Always true, since entity reference nodes are not visible in the XPath model.
   */
  @Override
  public boolean getExpandEntityReferences() {
    return true;
  }

  /** Control over whether it is OK for detach to reset the iterator. */
  protected boolean m_allowDetach = true;

  /**
   * Specify if it's OK for detach to release the iterator for reuse.
   *
   * @param allowRelease true if it is OK for detach to release this iterator for pooling.
   */
  @Override
  public void allowDetachToRelease(boolean allowRelease) {
    m_allowDetach = allowRelease;
  }

  /**
   * Detaches the iterator from the set which it iterated over, releasing any computational
   * resources and placing the iterator in the INVALID state. After<code>detach</code> has been
   * invoked, calls to <code>nextNode</code> or<code>previousNode</code> will raise the exception
   * INVALID_STATE_ERR.
   */
  @Override
  public void detach() {
    if (m_allowDetach) {
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
  }

  /** Reset the iterator. */
  @Override
  public void reset() {
    assertion(false, "This iterator can not reset!");
  }

  /**
   * Get a cloned Iterator that is reset to the beginning of the query.
   *
   * @return A cloned NodeIterator set of the start of the query.
   * @throws CloneNotSupportedException
   */
  @Override
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {
    LocPathIterator clone;
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

  // /**
  // * Get a cloned LocPathIterator that holds the same
  // * position as this iterator.
  // *
  // * @return A clone of this iterator that holds the same node position.
  // *
  // * @throws CloneNotSupportedException
  // */
  // public Object clone() throws CloneNotSupportedException
  // {
  //
  // LocPathIterator clone = (LocPathIterator) super.clone();
  //
  // return clone;
  // }

  /**
   * Returns the next node in the set and advances the position of the iterator in the set. After a
   * NodeIterator is created, the first call to nextNode() returns the first node in the set.
   *
   * @return The next <code>Node</code> in the set being iterated over, or <code>null</code> if
   *     there are no more members in that set.
   */
  @Override
  public abstract int nextNode();

  /**
   * Bottleneck the return of a next node, to make returns easier from nextNode().
   *
   * @param nextNode The next node found, may be null.
   * @return The same node that was passed as an argument.
   */
  protected int returnNextNode(int nextNode) {

    if (DTM.NULL != nextNode) {
      m_pos++;
    }

    m_lastFetched = nextNode;

    if (DTM.NULL == nextNode) m_foundLast = true;

    return nextNode;
  }

  /**
   * Return the last fetched node. Needed to support the UnionPathIterator.
   *
   * @return The last fetched node, or null if the last fetch was null.
   */
  @Override
  public int getCurrentNode() {
    return m_lastFetched;
  }

  /**
   * If an index is requested, NodeSetDTM will call this method to run the iterator to the index. By
   * default this sets m_next to the index. If the index argument is -1, this signals that the
   * iterator should be run to the end.
   *
   * @param index The index to run to, or -1 if the iterator should run to the end.
   */
  @Override
  public void runTo(int index) {

    if (m_foundLast || ((index >= 0) && (index <= getCurrentPos()))) return;

    if (-1 == index) {
      while (DTM.NULL != nextNode()) ;
    } else {
      while (DTM.NULL != nextNode()) {
        if (getCurrentPos() >= index) break;
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
   * The node context for the iterator.
   *
   * @return The node context, same as getRoot().
   */
  public final int getContext() {
    return m_context;
  }

  // /**
  // * Set the current context node for this iterator.
  // *
  // * @param n Must be a non-null reference to the node context.
  // */
  // public void setRoot(int n)
  // {
  // m_context = n;
  // m_cdtm = m_execContext.getDTM(n);
  // }

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

  // /**
  // * Get the analysis pattern built by the WalkerFactory.
  // *
  // * @return The analysis pattern built by the WalkerFactory.
  // */
  // int getAnalysis()
  // {
  // return m_analysis;
  // }

  // /**
  // * Set the analysis pattern built by the WalkerFactory.
  // *
  // * @param a The analysis pattern built by the WalkerFactory.
  // */
  // void setAnalysis(int a)
  // {
  // m_analysis = a;
  // }

  /**
   * @see net.sourceforge.htmlunit.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  @Override
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
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
  protected transient IteratorPool m_clones = new IteratorPool(this);

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

  /**
   * Returns true if all the nodes in the iteration well be returned in document order.
   *
   * @return true as a default.
   */
  @Override
  public boolean isDocOrdered() {
    return true;
  }

  /**
   * Returns the axis being iterated, if it is known.
   *
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple types.
   */
  @Override
  public int getAxis() {
    return -1;
  }

  // /**
  // * The analysis pattern built by the WalkerFactory.
  // * TODO: Move to LocPathIterator.
  // * @see net.sourceforge.htmlunit.xpath.axes.WalkerFactory
  // * @serial
  // */
  // protected int m_analysis = 0x00000000;
  /** @see PredicatedNodeTest#getLastPos(XPathContext) */
  @Override
  public int getLastPos(XPathContext xctxt) {
    return getLength();
  }
}
