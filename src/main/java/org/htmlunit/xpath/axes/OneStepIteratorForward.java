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

import org.htmlunit.xpath.Expression;
import org.htmlunit.xpath.compiler.Compiler;
import org.htmlunit.xpath.compiler.OpMap;
import org.htmlunit.xpath.xml.dtm.DTM;

/**
 * This class implements a general iterator for those LocationSteps with only one step, and perhaps
 * a predicate, that only go forward (i.e. it can not be used with ancestors, preceding, etc.)
 */
public class OneStepIteratorForward extends ChildTestIterator {

  /** The traversal axis from where the nodes will be filtered. */
  protected int m_axis;

  /**
   * Create a OneStepIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the location path expression for
   *     this itterator.
   * @throws javax.xml.transform.TransformerException if any
   */
  OneStepIteratorForward(final Compiler compiler, final int opPos, final int analysis)
      throws javax.xml.transform.TransformerException {
    super(compiler, opPos, analysis);
    final int firstStepPos = OpMap.getFirstChildPos(opPos);

    m_axis = WalkerFactory.getAxisFromStep(compiler, firstStepPos);
  }

  /** {@inheritDoc} */
  @Override
  public void setRoot(final int context, final Object environment) {
    super.setRoot(context, environment);
    m_traverser = m_cdtm.getAxisTraverser(m_axis);
  }

  /** {@inheritDoc} */
  @Override
  protected int getNextNode() {
    m_lastFetched =
        (DTM.NULL == m_lastFetched)
            ? m_traverser.first(m_context)
            : m_traverser.next(m_context, m_lastFetched);
    return m_lastFetched;
  }

  /** {@inheritDoc} */
  @Override
  public int getAxis() {
    return m_axis;
  }

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(final Expression expr) {
    if (!super.deepEquals(expr)) {
        return false;
    }

    return m_axis == ((OneStepIteratorForward) expr).m_axis;
  }
}
