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
import net.sourceforge.htmlunit.xpath.res.XSLMessages;
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

  /**
   * @return The root node of the Iterator, as specified when it was created. For non-Iterator
   *     NodeSets, this will be null.
   */
  @Override
  public Node getRoot() {
    return null;
  }

  /**
   * Get a cloned Iterator, and reset its state to the beginning of the iteration.
   *
   * @return a new NodeSet of the same type, having the same state... except that the reset()
   *     operation has been called.
   * @throws CloneNotSupportedException if this subclass of NodeSet does not support the clone()
   *     operation.
   */
  @Override
  public NodeIterator cloneWithReset() throws CloneNotSupportedException {

    NodeSet clone = (NodeSet) clone();

    clone.reset();

    return clone;
  }

  /** Reset the iterator. May have no effect on non-iterator Nodesets. */
  @Override
  public void reset() {
    m_next = 0;
  }

  /**
   * This attribute determines which node types are presented via the iterator. The available set of
   * constants is defined in the <code>NodeFilter</code> interface. For NodeSets, the mask has been
   * hardcoded to show all nodes except EntityReference nodes, which have no equivalent in the XPath
   * data model.
   *
   * @return integer used as a bit-array, containing flags defined in the DOM's NodeFilter class.
   *     The value will be <code>SHOW_ALL & ~SHOW_ENTITY_REFERENCE</code>, meaning that only entity
   *     references are suppressed.
   */
  @Override
  public int getWhatToShow() {
    return NodeFilter.SHOW_ALL & ~NodeFilter.SHOW_ENTITY_REFERENCE;
  }

  /**
   * The filter object used to screen nodes. Filters are applied to further reduce (and restructure)
   * the NodeIterator's view of the document. In our case, we will be using hardcoded filters built
   * into our iterators... but getFilter() is part of the DOM's NodeIterator interface, so we have
   * to support it.
   *
   * @return null, which is slightly misleading. True, there is no user-written filter object, but
   *     in fact we are doing some very sophisticated custom filtering. A DOM purist might suggest
   *     returning a placeholder object just to indicate that this is not going to return all nodes
   *     selected by whatToShow.
   */
  @Override
  public NodeFilter getFilter() {
    return null;
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
   * @return true for all iterators based on NodeSet, meaning that the contents of EntityRefrence
   *     nodes may be returned (though whatToShow says that the EntityReferences themselves are not
   *     shown.)
   */
  @Override
  public boolean getExpandEntityReferences() {
    return true;
  }

  /**
   * Returns the next node in the set and advances the position of the iterator in the set. After a
   * NodeIterator is created, the first call to nextNode() returns the first node in the set.
   *
   * @return The next <code>Node</code> in the set being iterated over, or <code>null</code> if
   *     there are no more members in that set.
   * @throws DOMException INVALID_STATE_ERR: Raised if this method is called after the <code>
   *     detach</code> method was invoked.
   */
  @Override
  public Node nextNode() throws DOMException {

    if (m_next < this.size()) {
      Node next = this.elementAt(m_next);

      m_next++;

      return next;
    } else return null;
  }

  /**
   * Returns the previous node in the set and moves the position of the iterator backwards in the
   * set.
   *
   * @return The previous <code>Node</code> in the set being iterated over, or<code>null</code> if
   *     there are no more members in that set.
   * @throws DOMException INVALID_STATE_ERR: Raised if this method is called after the <code>
   *     detach</code> method was invoked.
   * @throws RuntimeException thrown if this NodeSet is not of a cached type, and hence doesn't know
   *     what the previous node was.
   */
  @Override
  public Node previousNode() throws DOMException {

    if (!m_cacheNodes)
      throw new RuntimeException(
          XSLMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_CANNOT_ITERATE, null)); // "This
    // NodeSet can
    // not iterate
    // to a
    // previous
    // node!");

    if ((m_next - 1) > 0) {
      m_next--;

      return this.elementAt(m_next);
    } else return null;
  }

  /**
   * Detaches the iterator from the set which it iterated over, releasing any computational
   * resources and placing the iterator in the INVALID state. After<code>detach</code> has been
   * invoked, calls to <code>nextNode</code> or<code>previousNode</code> will raise the exception
   * INVALID_STATE_ERR.
   *
   * <p>This operation is a no-op in NodeSet, and will not cause INVALID_STATE_ERR to be raised by
   * later operations.
   */
  @Override
  public void detach() {}

  /**
   * If an index is requested, NodeSet will call this method to run the iterator to the index. By
   * default this sets m_next to the index. If the index argument is -1, this signals that the
   * iterator should be run to the end.
   *
   * @param index Position to advance (or retreat) to, with 0 requesting the reset ("fresh")
   *     position and -1 (or indeed any out-of-bounds value) requesting the final position.
   * @throws RuntimeException thrown if this NodeSet is not one of the types which supports
   *     indexing/counting.
   */
  @Override
  public void runTo(int index) {

    if (!m_cacheNodes)
      throw new RuntimeException(
          XSLMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_CANNOT_INDEX, null)); // "This NodeSet
    // can not do
    // indexing or
    // counting
    // functions!");

    if ((index >= 0) && (m_next < m_firstFree)) m_next = index;
    else m_next = m_firstFree - 1;
  }

  /**
   * Returns the <code>index</code>th item in the collection. If <code>index</code> is greater than
   * or equal to the number of nodes in the list, this returns <code>null</code>.
   *
   * <p>TODO: What happens if index is out of range?
   *
   * @param index Index into the collection.
   * @return The node at the <code>index</code>th position in the <code>NodeList</code>, or <code>
   *     null</code> if that is not a valid index.
   */
  @Override
  public Node item(int index) {

    runTo(index);

    return this.elementAt(index);
  }

  /**
   * The number of nodes in the list. The range of valid child node indices is 0 to <code>length-1
   * </code> inclusive. Note that this operation requires finding all the matching nodes, which may
   * defeat attempts to defer that work.
   *
   * @return integer indicating how many nodes are represented by this list.
   */
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
          XSLMessages.createXPATHMessage(
              XPATHErrorResources.ER_NODESET_NOT_MUTABLE, null)); // "This NodeSet
    // is not
    // mutable!");

    this.addElement(n);
  }

  /** If this node is being used as an iterator, the next index that nextNode() will return. */
  protected transient int m_next = 0;

  /**
   * Return the last fetched node. Needed to support the UnionPathIterator.
   *
   * @return the last fetched node.
   * @throws RuntimeException thrown if this NodeSet is not of a cached type, and thus doesn't
   *     permit indexed access.
   */
  @Override
  public Node getCurrentNode() {

    if (!m_cacheNodes)
      throw new RuntimeException(
          XSLMessages.createXPATHMessage(
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
  protected transient boolean m_mutable = true;

  /**
   * True if this list is cached.
   *
   * @serial
   */
  protected transient boolean m_cacheNodes = true;

  private transient int m_last = 0;

  @Override
  public int getLast() {
    return m_last;
  }

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

  /**
   * Get a cloned LocPathIterator.
   *
   * @return A clone of this
   * @throws CloneNotSupportedException
   */
  @Override
  public Object clone() throws CloneNotSupportedException {

    NodeSet clone = (NodeSet) super.clone();

    if ((null != this.m_map) && (this.m_map == clone.m_map)) {
      clone.m_map = new Node[this.m_map.length];

      System.arraycopy(this.m_map, 0, clone.m_map, 0, this.m_map.length);
    }

    return clone;
  }

  /**
   * Get the length of the list.
   *
   * @return Number of nodes in this NodeVector
   */
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
          XSLMessages.createXPATHMessage(
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
          XSLMessages.createXPATHMessage(
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
