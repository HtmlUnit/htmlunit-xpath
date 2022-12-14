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

import net.sourceforge.htmlunit.xpath.res.XPATHErrorResources;
import net.sourceforge.htmlunit.xpath.res.XPATHMessages;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
public class NodeSet implements NodeList, Cloneable {

  /**
   * Create an empty, using the given block size.
   *
   * @param blocksize Size of blocks to allocate
   */
  public NodeSet(int blocksize) {
    m_blocksize = blocksize;
    m_mapSize = 0;
  }

  private void runTo(int index) {
    if (!m_cacheNodes)
      throw new RuntimeException(
          XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_NODESET_CANNOT_INDEX, null));

    if ((index >= 0) && (m_next < m_firstFree)) m_next = index;
    else m_next = m_firstFree - 1;
  }

  /** {@inheritDoc} */
  @Override
  public Node item(int index) {

    runTo(index);

    if (null == m_map) return null;

    return m_map[index];
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
          XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_NODESET_NOT_MUTABLE, null));

    if ((m_firstFree + 1) >= m_mapSize) {
      if (null == m_map) {
        m_map = new Node[m_blocksize];
        m_mapSize = m_blocksize;
      } else {
        m_mapSize += m_blocksize;

        Node[] newMap = new Node[m_mapSize];

        System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

        m_map = newMap;
      }
    }

    m_map[m_firstFree] = n;

    m_firstFree++;
  }

  /** If this node is being used as an iterator, the next index that nextNode() will return. */
  protected transient int m_next = 0;

  /** True if this list can be mutated. */
  protected final transient boolean m_mutable = true;

  /**
   * True if this list is cached.
   *
   * @serial
   */
  protected final transient boolean m_cacheNodes = true;

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
  Node[] m_map;

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

  public int size() {
    return m_firstFree;
  }
}
