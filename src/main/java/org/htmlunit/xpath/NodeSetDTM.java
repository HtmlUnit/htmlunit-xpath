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
package org.htmlunit.xpath;

import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMFilter;
import org.htmlunit.xpath.xml.dtm.DTMIterator;
import org.htmlunit.xpath.xml.dtm.DTMManager;
import org.htmlunit.xpath.xml.utils.NodeVector;

/**
 * The NodeSetDTM class can act as either a NodeVector, NodeList, or NodeIterator. However, in order
 * for it to act as a NodeVector or NodeList, it's required that setShouldCacheNodes(true) be called
 * before the first nextNode() is called, in order that nodes can be added as they are fetched.
 * Derived classes that implement iterators must override runTo(int index), in order that they may
 * run the iteration to the given index.
 *
 * <p>Note that we directly implement the DOM's NodeIterator interface. We do not emulate all the
 * behavior of the standard NodeIterator. In particular, we do not guarantee to present a "live
 * view" of the document ... but in XSLT, the source document should never be mutated, so this
 * should never be an issue.
 *
 * <p>Thought: Should NodeSetDTM really implement NodeList and NodeIterator, or should there be
 * specific subclasses of it which do so? The advantage of doing it all here is that all NodeSetDTMs
 * will respond to the same calls; the disadvantage is that some of them may return
 * less-than-enlightening results when you do so.
 */
public class NodeSetDTM extends NodeVector implements DTMIterator, Cloneable {

  /** Create an empty nodelist. */
  public NodeSetDTM(final DTMManager dtmManager) {
    super();
    m_manager = dtmManager;
  }

  /**
   * Create a NodeSetDTM, and copy the members of the given DTMIterator into it.
   *
   * @param ni Iterator which yields Nodes to be made members of the new set.
   */
  public NodeSetDTM(final DTMIterator ni) {

    super();

    m_manager = ni.getDTMManager();
    m_root = ni.getRoot();
    addNodes(ni);
  }

  /** {@inheritDoc} */
  @Override
  public int getRoot() {
    if (DTM.NULL == m_root) {
      if (size() > 0) {
        return item(0);
      }
      return DTM.NULL;
    }
    return m_root;
  }

  /** {@inheritDoc} */
  @Override
  public void setRoot(final int context, final Object environment) {
    // no-op, I guess... (-sb)
  }

  /** {@inheritDoc} */
  @Override
  public Object clone() throws CloneNotSupportedException {

    return super.clone();
  }

  /** {@inheritDoc} */
  @Override
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {

    final NodeSetDTM clone = (NodeSetDTM) clone();

    clone.reset();

    return clone;
  }

  /** {@inheritDoc} */
  @Override
  public void reset() {
    m_next = 0;
  }

  /** {@inheritDoc} */
  @Override
  public int getWhatToShow() {
    return DTMFilter.SHOW_ALL & ~DTMFilter.SHOW_ENTITY_REFERENCE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean getExpandEntityReferences() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public DTM getDTM(final int nodeHandle) {

    return m_manager.getDTM(nodeHandle);
  }

  /* An instance of the DTMManager. */
  final DTMManager m_manager;

  /** {@inheritDoc} */
  @Override
  public DTMManager getDTMManager() {

    return m_manager;
  }

  /** {@inheritDoc} */
  @Override
  public int nextNode() {

    if (m_next < this.size()) {
      final int next = this.elementAt(m_next);

      m_next++;

      return next;
    }
    return DTM.NULL;
  }

  /** {@inheritDoc} */
  @Override
  public int previousNode() {

    if (!m_cacheNodes) {
        throw new RuntimeException(
              XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_NODESETDTM_CANNOT_ITERATE, null));
    }

    if ((m_next - 1) > 0) {
      m_next--;
      return this.elementAt(m_next);
    }
    return DTM.NULL;
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFresh() {
    return m_next == 0;
  }

  /** {@inheritDoc} */
  @Override
  public void runTo(final int index) {

    if (!m_cacheNodes) {
        throw new RuntimeException(
              XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_NODESETDTM_CANNOT_INDEX, null));
    }

    if ((index >= 0) && (m_next < m_firstFree)) {
        m_next = index;
    }
    else {
        m_next = m_firstFree - 1;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int item(final int index) {

    runTo(index);

    return this.elementAt(index);
  }

  /** {@inheritDoc} */
  @Override
  public int getLength() {

    runTo(-1);

    return this.size();
  }

  /**
   * Add a node to the NodeSetDTM. Not all types of NodeSetDTMs support this operation
   *
   * @param n Node to be added
   * @throws RuntimeException thrown if this NodeSetDTM is not of a mutable type.
   */
  public void addNode(final int n) {
    this.addElement(n);
  }

  /**
   * Copy NodeList members into this nodelist, adding in document order. Null references are not
   * added.
   *
   * @param iterator DTMIterator which yields the nodes to be added.
   * @throws RuntimeException thrown if this NodeSetDTM is not of a mutable type.
   */
  public void addNodes(final DTMIterator iterator) {
    if (null != iterator) { // defensive to fix a bug that Sanjiva reported.
      int obj;

      while (DTM.NULL != (obj = iterator.nextNode())) {
        addElement(obj);
      }
    }

    // checkDups();
  }

  /**
   * Add the node into a vector of nodes where it should occur in document order.
   *
   * @param node The node to be added.
   * @param test true if we should test for doc order
   * @param support The XPath runtime context.
   * @throws RuntimeException thrown if this NodeSetDTM is not of a mutable type.
   */
  public void addNodeInDocOrder(final int node, final boolean test, final XPathContext support) {
    if (test) {

      // This needs to do a binary search, but a binary search
      // is somewhat tough because the sequence test involves
      // two nodes.
      final int size = size();
      int i;
      for (i = size - 1; i >= 0; i--) {
        final int child = elementAt(i);

        if (child == node) {
          i = -2; // Duplicate, suppress insert

          break;
        }

        final DTM dtm = support.getDTM(node);
        if (!dtm.isNodeAfter(node, child)) {
          break;
        }
      }

      if (i != -2) {
        insertElementAt(node, i + 1);
      }
    }
    else {
      final int insertIndex = this.size();

      boolean foundit = false;

      for (int i = 0; i < insertIndex; i++) {
        if (i == node) {
          foundit = true;

          break;
        }
      }

      if (!foundit) {
        addElement(node);
    }
    }

    // checkDups();
  } // end addNodeInDocOrder(Vector v, Object obj)

  /**
   * Add the node into a vector of nodes where it should occur in document order.
   *
   * @param node The node to be added.
   * @param support The XPath runtime context.
   * @throws RuntimeException thrown if this NodeSetDTM is not of a mutable type.
   */
  public void addNodeInDocOrder(final int node, final XPathContext support) {
    addNodeInDocOrder(node, true, support);
  } // end addNodeInDocOrder(Vector v, Object obj)

  /** {@inheritDoc} */
  @Override
  public int elementAt(final int i) {

    runTo(i);

    return super.elementAt(i);
  }

  /** {@inheritDoc} */
  @Override
  public boolean contains(final int s) {

    runTo(-1);

    return super.contains(s);
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final int elem, final int index) {

    runTo(-1);

    return super.indexOf(elem, index);
  }

  /** {@inheritDoc} */
  @Override
  public int indexOf(final int elem) {

    runTo(-1);

    return super.indexOf(elem);
  }

  /** If this node is being used as an iterator, the next index that nextNode() will return. */
  protected transient int m_next = 0;

  /** {@inheritDoc} */
  @Override
  public int getCurrentPos() {
    return m_next;
  }

  /** {@inheritDoc} */
  @Override
  public void setCurrentPos(final int i) {

    if (!m_cacheNodes) {
        throw new RuntimeException(
              XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_NODESETDTM_CANNOT_INDEX, null));
    }

    m_next = i;
  }

  /** {@inheritDoc} */
  @Override
  public int getCurrentNode() {

    if (!m_cacheNodes) {
        throw new RuntimeException("This NodeSetDTM can not do indexing or counting functions!");
    }

    final int saved = m_next;
    // because nextNode always increments
    // But watch out for copy29, where the root iterator didn't
    // have nextNode called on it.
    final int current = (m_next > 0) ? m_next - 1 : m_next;
    final int n = (current < m_firstFree) ? elementAt(current) : DTM.NULL;
    m_next = saved; // HACK: I think this is a bit of a hack. -sb
    return n;
  }

  /**
   * True if this list is cached.
   *
   * @serial
   */
  protected transient boolean m_cacheNodes = true;

  /** The root of the iteration, if available. */
  protected int m_root = DTM.NULL;

  /** {@inheritDoc} */
  @Override
  public void setShouldCacheNodes(final boolean b) {

    if (!isFresh()) {
        throw new RuntimeException(
              XPATHMessages.createXPATHMessage(
                  XPATHErrorResources.ER_CANNOT_CALL_SETSHOULDCACHENODE, null));
    }
    m_cacheNodes = b;
  }

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
}
