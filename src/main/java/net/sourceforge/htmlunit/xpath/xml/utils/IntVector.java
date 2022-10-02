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
package net.sourceforge.htmlunit.xpath.xml.utils;

/**
 * A very simple table that stores a list of int.
 *
 * <p>This version is based on a "realloc" strategy -- a simle array is used, and when more storage
 * is needed, a larger array is obtained and all existing data is recopied into it. As a result,
 * read/write access to existing nodes is O(1) fast but appending may be O(N**2) slow. See also
 * SuballocatedIntVector.
 */
public class IntVector implements Cloneable {

  /** Size of blocks to allocate */
  protected int m_blocksize;

  /** Array of ints */
  protected int m_map[]; // IntStack is trying to see this directly

  /** Number of ints in array */
  protected int m_firstFree = 0;

  /** Size of array */
  protected int m_mapSize;

  /**
   * Construct a IntVector, using the given block size.
   *
   * @param blocksize Size of block to allocate
   */
  public IntVector(int blocksize) {

    m_blocksize = blocksize;
    m_mapSize = blocksize;
    m_map = new int[blocksize];
  }

  /**
   * Copy constructor for IntVector
   *
   * @param v Existing IntVector to copy
   */
  public IntVector(IntVector v) {
    m_map = new int[v.m_mapSize];
    m_mapSize = v.m_mapSize;
    m_firstFree = v.m_firstFree;
    m_blocksize = v.m_blocksize;
    System.arraycopy(v.m_map, 0, m_map, 0, m_firstFree);
  }

  /**
   * Get the length of the list.
   *
   * @return length of the list
   */
  public final int size() {
    return m_firstFree;
  }

  /**
   * Append a int onto the vector.
   *
   * @param value Int to add to the list
   */
  public final void addElement(int value) {

    if ((m_firstFree + 1) >= m_mapSize) {
      m_mapSize += m_blocksize;

      int newMap[] = new int[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_map[m_firstFree] = value;

    m_firstFree++;
  }

  /**
   * Inserts the specified node in this vector at the specified index. Each component in this vector
   * with an index greater or equal to the specified index is shifted upward to have an index one
   * greater than the value it had previously.
   */
  public final void removeAllElements() {

    for (int i = 0; i < m_firstFree; i++) {
      m_map[i] = java.lang.Integer.MIN_VALUE;
    }

    m_firstFree = 0;
  }

  /**
   * Sets the component at the specified index of this vector to be the specified object. The
   * previous component at that position is discarded.
   *
   * <p>The index must be a value greater than or equal to 0 and less than the current size of the
   * vector.
   *
   * @param value object to set
   * @param index Index of where to set the object
   */
  public final void setElementAt(int value, int index) {
    m_map[index] = value;
  }

  /**
   * Get the nth element.
   *
   * @param i index of object to get
   * @return object at given index
   */
  public final int elementAt(int i) {
    return m_map[i];
  }

  /**
   * Tell if the table contains the given node.
   *
   * @param s object to look for
   * @return true if the object is in the list
   */
  public final boolean contains(int s) {

    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i] == s) return true;
    }

    return false;
  }

  /**
   * Searches for the first occurence of the given argument, beginning the search at index, and
   * testing for equality using the equals method.
   *
   * @param elem object to look for
   * @param index Index of where to begin search
   * @return the index of the first occurrence of the object argument in this vector at position
   *     index or later in the vector; returns -1 if the object is not found.
   */
  public final int indexOf(int elem, int index) {

    for (int i = index; i < m_firstFree; i++) {
      if (m_map[i] == elem) return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }

  /**
   * Searches for the first occurence of the given argument, beginning the search at index, and
   * testing for equality using the equals method.
   *
   * @param elem object to look for
   * @return the index of the first occurrence of the object argument in this vector at position
   *     index or later in the vector; returns -1 if the object is not found.
   */
  public final int indexOf(int elem) {

    for (int i = 0; i < m_firstFree; i++) {
      if (m_map[i] == elem) return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }

  /**
   * Searches for the first occurence of the given argument, beginning the search at index, and
   * testing for equality using the equals method.
   *
   * @param elem Object to look for
   * @return the index of the first occurrence of the object argument in this vector at position
   *     index or later in the vector; returns -1 if the object is not found.
   */
  public final int lastIndexOf(int elem) {

    for (int i = m_firstFree - 1; i >= 0; i--) {
      if (m_map[i] == elem) return i;
    }

    return java.lang.Integer.MIN_VALUE;
  }

  /** {@inheritDoc} */
  @Override
  public Object clone() throws CloneNotSupportedException {
    return new IntVector(this);
  }
}
