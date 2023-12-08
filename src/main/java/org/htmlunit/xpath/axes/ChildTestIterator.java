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
package org.htmlunit.xpath.axes;

import org.htmlunit.xpath.compiler.Compiler;
import org.htmlunit.xpath.xml.dtm.Axis;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMAxisTraverser;
import org.htmlunit.xpath.xml.dtm.DTMIterator;

/**
 * This class implements an optimized iterator for children patterns that have a node test, and
 * possibly a predicate.
 *
 * @see org.htmlunit.xpath.axes.BasicTestIterator
 */
public class ChildTestIterator extends BasicTestIterator {
  /** The traverser to use to navigate over the descendants. */
  protected transient DTMAxisTraverser m_traverser;

  /**
   * Create a ChildTestIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the location path expression for
   *     this itterator.
   * @throws javax.xml.transform.TransformerException if any
   */
  ChildTestIterator(final Compiler compiler, final int opPos, final int analysis)
      throws javax.xml.transform.TransformerException {
    super(compiler, opPos, analysis);
  }

  /**
   * Create a ChildTestIterator object.
   *
   * @param traverser Traverser that tells how the KeyIterator is to be handled.
   */
  public ChildTestIterator(final DTMAxisTraverser traverser) {

    super(null);

    m_traverser = traverser;
  }

  /** {@inheritDoc} */
  @Override
  protected int getNextNode() {
    if (true /* 0 == m_extendedTypeID */) {
      m_lastFetched =
          (DTM.NULL == m_lastFetched)
              ? m_traverser.first(m_context)
              : m_traverser.next(m_context, m_lastFetched);
    }
    return m_lastFetched;
  }

  /** {@inheritDoc} */
  @Override
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {

    final ChildTestIterator clone = (ChildTestIterator) super.cloneWithReset();
    clone.m_traverser = m_traverser;

    return clone;
  }

  /** {@inheritDoc} */
  @Override
  public void setRoot(final int context, final Object environment) {
    super.setRoot(context, environment);
    m_traverser = m_cdtm.getAxisTraverser(Axis.CHILD);
  }

  /** {@inheritDoc} */
  @Override
  public int getAxis() {
    return org.htmlunit.xpath.xml.dtm.Axis.CHILD;
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {
    m_traverser = null;

    // Always call the superclass detach last!
    super.detach();
  }
}
