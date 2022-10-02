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

import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Classes who implement this interface can be a <a
 * href="http://www.w3.org/TR/xslt#dt-current-node-list">current node list</a>, also refered to here
 * as a <term>context node list</term>.
 */
public interface ContextNodeList {

  /**
   * Get the <a href="http://www.w3.org/TR/xslt#dt-current-node">current node</a>.
   *
   * @return The current node, or null.
   */
  Node getCurrentNode();

  /** Reset the iterator. */
  void reset();

  /**
   * If an index is requested, NodeSetDTM will call this method to run the iterator to the index. By
   * default this sets m_next to the index. If the index argument is -1, this signals that the
   * iterator should be run to the end.
   *
   * @param index The index to run to, or -1 if the iterator should be run to the end.
   */
  void runTo(int index);

  /**
   * Get the length of the list.
   *
   * @return The number of nodes in this node list.
   */
  int size();

  /**
   * Get a cloned Iterator that is reset to the start of the iteration.
   *
   * @return A clone of this iteration that has been reset.
   * @throws CloneNotSupportedException if any
   */
  NodeIterator cloneWithReset() throws CloneNotSupportedException;

  /**
   * Get a clone of this iterator. Be aware that this operation may be somewhat expensive.
   *
   * @return A clone of this object.
   * @throws CloneNotSupportedException if any
   */
  Object clone() throws CloneNotSupportedException;

  /**
   * Get the index of the last node in this list.
   *
   * @return the index of the last node in this list.
   */
  int getLast();

  /**
   * Set the index of the last node in this list.
   *
   * @param last the index of the last node in this list.
   */
  void setLast(int last);
}
