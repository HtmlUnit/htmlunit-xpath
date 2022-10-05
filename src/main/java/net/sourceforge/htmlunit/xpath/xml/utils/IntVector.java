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
public class IntVector {

  /** Size of blocks to allocate */
  protected final int m_blocksize;

  /** Array of ints */
  protected int[] m_map; // IntStack is trying to see this directly

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
}
