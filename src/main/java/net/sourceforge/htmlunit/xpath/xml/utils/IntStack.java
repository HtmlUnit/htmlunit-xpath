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

import java.util.EmptyStackException;

/**
 * Implement a stack of simple integers.
 *
 * <p>%OPT% This is currently based on IntVector, which permits fast acess but pays a heavy
 * recopying penalty if/when its size is increased. If we expect deep stacks, we should consider a
 * version based on ChunkedIntVector.
 */
public class IntStack extends IntVector {

  /**
   * Construct a IntVector, using the given block size.
   *
   * @param blocksize Size of block to allocate
   */
  public IntStack(int blocksize) {
    super(blocksize);
  }

  /**
   * Pushes an item onto the top of this stack.
   *
   * @param i the int to be pushed onto this stack.
   */
  public void push(int i) {

    if ((m_firstFree + 1) >= m_mapSize) {
      m_mapSize += m_blocksize;

      int[] newMap = new int[m_mapSize];

      System.arraycopy(m_map, 0, newMap, 0, m_firstFree + 1);

      m_map = newMap;
    }

    m_map[m_firstFree] = i;

    m_firstFree++;
  }

  /**
   * Quickly pops a number of items from the stack.
   *
   * @param n the number of items to pop
   */
  public final void quickPop(int n) {
    m_firstFree -= n;
  }

  /**
   * Looks at the object at the top of this stack without removing it from the stack.
   *
   * @return the object at the top of this stack.
   * @throws EmptyStackException if this stack is empty.
   */
  public final int peek() {
    try {
      return m_map[m_firstFree - 1];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new EmptyStackException();
    }
  }
}
