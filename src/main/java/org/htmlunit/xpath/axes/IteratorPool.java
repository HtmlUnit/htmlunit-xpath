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

import java.util.ArrayList;
import org.htmlunit.xpath.xml.dtm.DTMIterator;
import org.htmlunit.xpath.xml.utils.WrappedRuntimeException;

/** Pool of object of a given type to pick from to help memory usage */
public final class IteratorPool {

  /** Type of objects in this pool. */
  private final DTMIterator m_orig;

  /** Stack of given objects this points to. */
  private final ArrayList<DTMIterator> m_freeStack;

  /**
   * Constructor IteratorPool
   *
   * @param original The original iterator from which all others will be cloned.
   */
  public IteratorPool(final DTMIterator original) {
    m_orig = original;
    m_freeStack = new ArrayList<>();
  }

  /**
   * Get an instance of the given object in this pool
   *
   * @return An instance of the given object
   */
  public synchronized DTMIterator getInstanceOrThrow() throws CloneNotSupportedException {
    // Check if the pool is empty.
    if (m_freeStack.isEmpty()) {

      // Create a new object if so.
      return (DTMIterator) m_orig.clone();
    }
    // Remove object from end of free pool.
    return m_freeStack.remove(m_freeStack.size() - 1);
  }

  /**
   * Get an instance of the given object in this pool
   *
   * @return An instance of the given object
   */
  public synchronized DTMIterator getInstance() {
    // Check if the pool is empty.
    if (m_freeStack.isEmpty()) {

      // Create a new object if so.
      try {
        return (DTMIterator) m_orig.clone();
      }
      catch (final Exception ex) {
        throw new WrappedRuntimeException(ex);
      }
    }
    // Remove object from end of free pool.
    return m_freeStack.remove(m_freeStack.size() - 1);
  }

  /**
   * Add an instance of the given object to the pool
   *
   * @param obj Object to add.
   */
  public synchronized void freeInstance(final DTMIterator obj) {
    m_freeStack.add(obj);
  }
}
