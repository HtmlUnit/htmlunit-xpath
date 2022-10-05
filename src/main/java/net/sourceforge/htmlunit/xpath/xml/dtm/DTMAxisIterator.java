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
package net.sourceforge.htmlunit.xpath.xml.dtm;

/** This class iterates over a single XPath Axis, and returns node handles. */
public interface DTMAxisIterator extends Cloneable {

  /** Specifies the end of the iteration, and is the same as DTM.NULL. */
  int END = DTM.NULL;

  /**
   * Get the next node in the iteration.
   *
   * @return The next node handle in the iteration, or END.
   */
  int next();

  /** Resets the iterator to the last start node. */
  void reset();

  /**
   * Set start to END should 'close' the iterator, i.e. subsequent call to next() should return END.
   *
   * @param node Sets the root of the iteration.
   */
  void setStartNode(int node);

  /** @return true if this iterator has a reversed axis, else false. */
  boolean isReverse();

  /**
   * @return a deep copy of this iterator. The clone should not be reset from its current position.
   */
  DTMAxisIterator cloneIterator();
}
