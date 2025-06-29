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
package org.htmlunit.xpath.xml.utils;

import org.htmlunit.xpath.xml.dtm.DTM;

/** A very simple table that stores a list of Nodes. */
public class NodeVector implements Cloneable {

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
  private int[] m_map;

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

  /** Default constructor. */
  public NodeVector() {
    m_blocksize = 32;
    m_mapSize = 0;
  }

  /** {@inheritDoc} */
  @Override
  public Object clone() throws CloneNotSupportedException {

    final NodeVector clone = (NodeVector) super.clone();

    if ((null != this.m_map) && (this.m_map == clone.m_map)) {
      clone.m_map = new int[this.m_map.length];

      System.arraycopy(this.m_map, 0, clone.m_map, 0, this.m_map.length);
    }

    return clone;
  }

  /**
   * Get the length of the list.
   *
   * @return Number of nodes in this NodeVector
   */
  public int size() {
    return m_firstFree;
  }

  /**
   * Append a Node onto the vector.
   *
   * @param value Node to add to the vector
   */
  public void addElement(final int value) {

    if ((m_firstFree + 1) >= m_mapSize) {
      if (null == m_map) {
        m_map = new int[m_blocksize];
        m_mapSize = m_blocksize;
      }
      else {
        m_mapSize += m_blocksize;

        final int[] newMap = new int[m_mapSize];

        System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

        m_map = newMap;
      }
    }

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Inserts the specified node in this vector at the specified index. Each component in this vector
   * with an index greater or equal to the specified index is shifted upward to have an index one
   * greater than the value it had previously.
   *
   * @param value Node to insert
   * @param at Position where to insert
   */
  public void insertElementAt(final int value, final int at) {

    if (null == m_map) {
      m_map = new int[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else if ((m_firstFree + 1) >= m_mapSize) {
      m_mapSize += m_blocksize;

      final int[] newMap = new int[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    if (at <= (m_firstFree - 1)) {
      System.arraycopy(m_map, at, m_map, at + 1, m_firstFree - at);
    }

    m_map[at] = value;

    m_firstFree++;
  }

  /**
   * Get the nth element.
   *
   * @param i Index of node to get
   * @return Node at specified index
   */
  public int elementAt(final int i) {
    if (null == m_map) {
        return DTM.NULL;
    }

    return m_map[i];
  }

  /**
   * Tell if the table contains the given node.
   *
   * @param s Node to look for
   * @return True if the given node was found.
   */
  public boolean contains(final int s) {
    if (null == m_map) {
        return false;
    }

    for (int i = 0; i < m_firstFree; i++) {
      final int node = m_map[i];

      if (node == s) {
          return true;
      }
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
  public int indexOf(final int elem, final int index) {
    if (null == m_map) {
        return -1;
    }

    for (int i = index; i < m_firstFree; i++) {
      final int node = m_map[i];

      if (node == elem) {
          return i;
      }
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
  public int indexOf(final int elem) {
    if (null == m_map) {
        return -1;
    }

    for (int i = 0; i < m_firstFree; i++) {
      final int node = m_map[i];

      if (node == elem) {
          return i;
      }
    }

    return -1;
  }
}
