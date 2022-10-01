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

import net.sourceforge.htmlunit.xpath.Expression;
import net.sourceforge.htmlunit.xpath.compiler.Compiler;
import net.sourceforge.htmlunit.xpath.compiler.OpMap;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;

/**
 * This class implements a general iterator for those LocationSteps with only one step, and perhaps
 * a predicate, that only go forward (i.e. it can not be used with ancestors, preceding, etc.)
 *
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
   * @throws javax.xml.transform.TransformerException
   */
  OneStepIteratorForward(Compiler compiler, int opPos, int analysis)
      throws javax.xml.transform.TransformerException {
    super(compiler, opPos, analysis);
    int firstStepPos = OpMap.getFirstChildPos(opPos);

    m_axis = WalkerFactory.getAxisFromStep(compiler, firstStepPos);
  }

  /**
   * Initialize the context values for this expression after it is cloned.
   *
   * @param context The XPath runtime context for this transformation.
   */
  @Override
  public void setRoot(int context, Object environment) {
    super.setRoot(context, environment);
    m_traverser = m_cdtm.getAxisTraverser(m_axis);
  }

  /** Get the next node via getFirstAttribute && getNextAttribute. */
  @Override
  protected int getNextNode() {
    m_lastFetched =
        (DTM.NULL == m_lastFetched)
            ? m_traverser.first(m_context)
            : m_traverser.next(m_context, m_lastFetched);
    return m_lastFetched;
  }

  /**
   * Returns the axis being iterated, if it is known.
   *
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple types.
   */
  @Override
  public int getAxis() {
    return m_axis;
  }

  /** @see Expression#deepEquals(Expression) */
  @Override
  public boolean deepEquals(Expression expr) {
    if (!super.deepEquals(expr)) return false;

    if (m_axis != ((OneStepIteratorForward) expr).m_axis) return false;

    return true;
  }
}
