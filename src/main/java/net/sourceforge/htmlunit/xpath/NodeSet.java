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

import net.sourceforge.htmlunit.xpath.axes.ContextNodeList;
import net.sourceforge.htmlunit.xpath.res.XPATHErrorResources;
import net.sourceforge.htmlunit.xpath.res.XPATHMessages;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

/**
 * The NodeSet class can act as either a NodeVector, NodeList, or NodeIterator. However, in order
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
 * <p>Thought: Should NodeSet really implement NodeList and NodeIterator, or should there be
 * specific subclasses of it which do so? The advantage of doing it all here is that all NodeSets
 * will respond to the same calls; the disadvantage is that some of them may return
 * less-than-enlightening results when you do so.
 */
public class NodeSet implements NodeList, NodeIterator, Cloneable, ContextNodeList {

  /**
   * Create an empty, using the given block size.
   *
   * @param blocksize Size of blocks to allocate
   */
  public NodeSet(int blocksize) {
    m_blocksize = blocksize;
    m_mapSize = 0;
  }

  /** {@inheritDoc} */
  @Override
  public Node getRoot() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public NodeIterator cloneWithReset() throws CloneNotSupportedException {

    NodeSet clone = (NodeSet) clone();

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
    return NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE;
  }

  /** {@inheritDoc} */
  @Override
  public NodeFilter getFilter() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean getExpandEntityReferences() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public Node nextNode() throws DOMException {

    if (m_next < this.size()) {
      Node next = this.elementAt(m_next);

      m_next++;

      return next;
    } else return null;
  }

  /** {@inheritDoc} */
  @Override
  public Node previousNode() throws DOMException {

    if (!m_cacheNodes)
      throw new RuntimeException(
          XPATHMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_CANNOT_ITERATE, null)); // "This
    // NodeSet can
    // not iterate
    // to a
    // previous
    // node!");

    if ((m_next - 1) > 0) {
      m_next--;

      return this.elementAt(m_next);
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {}

  /** {@inheritDoc} */
  @Override
  public void runTo(int index) {

    if (!m_cacheNodes)
      throw new RuntimeException(
          XPATHMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_CANNOT_INDEX, null)); // "This NodeSet
    // can not do
    // indexing or
    // counting
    // functions!");

    if ((index >= 0) && (m_next < m_firstFree)) m_next = index;
    else m_next = m_firstFree - 1;
  }

  /** {@inheritDoc} */
  @Override
  public Node item(int index) {

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
   * Add a node to the NodeSet. Not all types of NodeSets support this operation
   *
   * @param n Node to be added
   * @throws RuntimeException thrown if this NodeSet is not of a mutable type.
   */
  public void addNode(Node n) {

    if (!m_mutable)
      throw new RuntimeException(
          XPATHMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_NOT_MUTABLE, null)); // "This NodeSet
    // is not
    // mutable!");

    this.addElement(n);
  }

  /** If this node is being used as an iterator, the next index that nextNode() will return. */
  protected transient int m_next = 0;

  /** {@inheritDoc} */
  @Override
  public Node getCurrentNode() {

    if (!m_cacheNodes)
      throw new RuntimeException(
          XPATHMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_CANNOT_INDEX, null)); // "This NodeSet
    // can not do
    // indexing or
    // counting
    // functions!");

    int saved = m_next;
    Node n = (m_next < m_firstFree) ? elementAt(m_next) : null;
    m_next = saved; // HACK: I think this is a bit of a hack. -sb
    return n;
  }

  /** True if this list can be mutated. */
  protected final transient boolean m_mutable = true;

  /**
   * True if this list is cached.
   *
   * @serial
   */
  protected final transient boolean m_cacheNodes = true;

  private transient int m_last = 0;

  /** {@inheritDoc} */
  @Override
  public int getLast() {
    return m_last;
  }

  /** {@inheritDoc} */
  @Override
  public void setLast(int last) {
    m_last = last;
  }

  /**
   * Size of blocks to allocate.
   *
   * @serial
   */
  private final int m_blocksize;

  /**
   * Array of nodes this points to.
   *
   * @serial
   */
  Node m_map[];

  /**
   * Number of nodes in this NodeVector.
   *
   * @serial
   */
  protected int m_firstFree = 0;

  /**
   * Size of the array this points to.
   *
   * @serial
   */
  private int m_mapSize; // lazy initialization

  /** {@inheritDoc} */
  @Override
  public Object clone() throws CloneNotSupportedException {

    NodeSet clone = (NodeSet) super.clone();

    if ((null != this.m_map) && (this.m_map == clone.m_map)) {
      clone.m_map = new Node[this.m_map.length];

      System.arraycopy(this.m_map, 0, clone.m_map, 0, this.m_map.length);
    }

    return clone;
  }

  /** {@inheritDoc} */
  @Override
  public int size() {
    return m_firstFree;
  }

  /**
   * Append a Node onto the vector.
   *
   * @param value Node to add to the vector
   */
  public void addElement(Node value) {
    if (!m_mutable)
      throw new RuntimeException(
          XPATHMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_NOT_MUTABLE, null)); // "This NodeSet
    // is not
    // mutable!");

    if ((m_firstFree + 1) >= m_mapSize) {
      if (null == m_map) {
        m_map = new Node[m_blocksize];
        m_mapSize = m_blocksize;
      } else {
        m_mapSize += m_blocksize;

        Node newMap[] = new Node[m_mapSize];

        System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

        m_map = newMap;
      }
    }

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Sets the component at the specified index of this vector to be the specified object. The
   * previous component at that position is discarded.
   *
   * <p>The index must be a value greater than or equal to 0 and less than the current size of the
   * vector.
   *
   * @param node Node to set
   * @param index Index of where to set the node
   */
  public void setElementAt(Node node, int index) {
    if (!m_mutable)
      throw new RuntimeException(
          XPATHMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_NOT_MUTABLE, null)); // "This NodeSet
    // is not
    // mutable!");

    if (null == m_map) {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }

    m_map[index] = node;
  }

  /**
   * Get the nth element.
   *
   * @param i Index of node to get
   * @return Node at specified index
   */
  public Node elementAt(int i) {

    if (null == m_map) return null;

    return m_map[i];
  }

  /**
   * Tell if the table contains the given node.
   *
   * @param s Node to look for
   * @return True if the given node was found.
   */
  public boolean contains(Node s) {
    runTo(-1);

    if (null == m_map) return false;

    for (int i = 0; i < m_firstFree; i++) {
      Node node = m_map[i];

      if ((null != node) && node.equals(s)) return true;
    }

    return false;
  }

  /**
   * Searches for the first occurence of the given argument, beginning the search at index, and
   * testing for equality using the equals method.
   *
   * @param elem Node to look for
   * @param index Index of where to start the search
   * @return the index of the first occurrence of the object argument in this vector at position
   *     index or later in the vector; returns -1 if the object is not found.
   */
  public int indexOf(Node elem, int index) {
    runTo(-1);

    if (null == m_map) return -1;

    for (int i = index; i < m_firstFree; i++) {
      Node node = m_map[i];

      if ((null != node) && node.equals(elem)) return i;
    }

    return -1;
  }

  /**
   * Searches for the first occurence of the given argument, beginning the search at index, and
   * testing for equality using the equals method.
   *
   * @param elem Node to look for
   * @return the index of the first occurrence of the object argument in this vector at position
   *     index or later in the vector; returns -1 if the object is not found.
   */
  public int indexOf(Node elem) {
    runTo(-1);

    if (null == m_map) return -1;

    for (int i = 0; i < m_firstFree; i++) {
      Node node = m_map[i];

      if ((null != node) && node.equals(elem)) return i;
    }

    return -1;
  }
}
