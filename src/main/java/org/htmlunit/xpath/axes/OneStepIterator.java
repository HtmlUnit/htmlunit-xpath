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
import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.compiler.Compiler;
import org.htmlunit.xpath.compiler.OpMap;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMAxisIterator;
import org.htmlunit.xpath.xml.dtm.DTMIterator;

/**
 * This class implements a general iterator for those LocationSteps with only one step, and perhaps
 * a predicate.
 */
public class OneStepIterator extends ChildTestIterator {

  /** The traversal axis from where the nodes will be filtered. */
  protected int m_axis;

  /** The DTM inner traversal class, that corresponds to the super axis. */
  protected DTMAxisIterator m_iterator;

  /**
   * Create a OneStepIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the location path expression for
   *     this itterator.
   * @throws javax.xml.transform.TransformerException if any
   */
  OneStepIterator(final Compiler compiler, final int opPos, final int analysis)
      throws javax.xml.transform.TransformerException {
    super(compiler, opPos, analysis);
    final int firstStepPos = OpMap.getFirstChildPos(opPos);

    m_axis = WalkerFactory.getAxisFromStep(compiler, firstStepPos);
  }

  /** {@inheritDoc} */
  @Override
  public void setRoot(final int context, final Object environment) {
    super.setRoot(context, environment);
    if (m_axis > -1) {
        m_iterator = m_cdtm.getAxisIterator(m_axis);
    }
    m_iterator.setStartNode(m_context);
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {
    if (m_axis > -1) {
        m_iterator = null;
    }

    // Always call the superclass detach last!
    super.detach();
  }

  /** {@inheritDoc} */
  @Override
  protected int getNextNode() {
    return m_lastFetched = m_iterator.next();
  }

  /** {@inheritDoc} */
  @Override
  public Object clone() throws CloneNotSupportedException {
    // Do not access the location path itterator during this operation!

    final OneStepIterator clone = (OneStepIterator) super.clone();

    if (m_iterator != null) {
      clone.m_iterator = m_iterator.cloneIterator();
    }
    return clone;
  }

  /** {@inheritDoc} */
  @Override
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {

    final OneStepIterator clone = (OneStepIterator) super.cloneWithReset();
    clone.m_iterator = m_iterator;

    return clone;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isReverseAxes() {
    return m_iterator.isReverse();
  }

  /** {@inheritDoc} */
  @Override
  protected int getProximityPosition(final int predicateIndex) {
    if (!isReverseAxes()) {
        return super.getProximityPosition(predicateIndex);
    }

    // A negative predicate index seems to occur with
    // (preceding-sibling::*|following-sibling::*)/ancestor::*[position()]/*[position()]
    // -sb
    if (predicateIndex < 0) {
        return -1;
    }

    if (m_proximityPositions[predicateIndex] <= 0) {
      final XPathContext xctxt = getXPathContext();
      try {
        final OneStepIterator clone = (OneStepIterator) this.clone();

        final int root = getRoot();
        xctxt.pushCurrentNode(root);
        clone.setRoot(root, xctxt);

        // clone.setPredicateCount(predicateIndex);
        clone.m_predCount = predicateIndex;

        // Count 'em all
        int count = 1;

        while (DTM.NULL != (clone.nextNode())) {
          count++;
        }

        m_proximityPositions[predicateIndex] += count;
      }
      catch (final CloneNotSupportedException cnse) {

      // can't happen
      }
      finally {
        xctxt.popCurrentNode();
      }
    }

    return m_proximityPositions[predicateIndex];
  }

  /** {@inheritDoc} */
  @Override
  public int getLength() {
    if (!isReverseAxes()) {
        return super.getLength();
    }

    // Tell if this is being called from within a predicate.
    final boolean isPredicateTest = this == m_execContext.getSubContextList();

    // If we have already calculated the length, and the current predicate
    // is the first predicate, then return the length. We don't cache
    // the anything but the length of the list to the first predicate.
    if (-1 != m_length && isPredicateTest && m_predicateIndex < 1) {
        return m_length;
    }

    int count = 0;

    final XPathContext xctxt = getXPathContext();
    try {
      final OneStepIterator clone = (OneStepIterator) this.cloneWithReset();

      final int root = getRoot();
      xctxt.pushCurrentNode(root);
      clone.setRoot(root, xctxt);

      clone.m_predCount = m_predicateIndex;

      while (DTM.NULL != (clone.nextNode())) {
        count++;
      }
    }
    catch (final CloneNotSupportedException cnse) {
      // can't happen
    }
    finally {
      xctxt.popCurrentNode();
    }
    if (isPredicateTest && m_predicateIndex < 1) {
        m_length = count;
    }

    return count;
  }

  /** {@inheritDoc} */
  @Override
  protected void countProximityPosition(final int i) {
    if (!isReverseAxes()) {
        super.countProximityPosition(i);
    }
    else if (i < m_proximityPositions.length) {
        m_proximityPositions[i]--;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void reset() {

    super.reset();
    if (null != m_iterator) {
        m_iterator.reset();
    }
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

    return m_axis == ((OneStepIterator) expr).m_axis;
  }
}
