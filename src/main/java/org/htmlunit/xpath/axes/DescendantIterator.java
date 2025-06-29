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
import org.htmlunit.xpath.compiler.OpCodes;
import org.htmlunit.xpath.compiler.OpMap;
import org.htmlunit.xpath.patterns.NodeTest;
import org.htmlunit.xpath.xml.dtm.Axis;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMAxisTraverser;
import org.htmlunit.xpath.xml.dtm.DTMFilter;
import org.htmlunit.xpath.xml.dtm.DTMIterator;

/**
 * This class implements an optimized iterator for descendant, descendant-or-self, or "//foo"
 * patterns.
 *
 * @see org.htmlunit.xpath.axes.LocPathIterator
 */
public class DescendantIterator extends LocPathIterator {

  /**
   * Create a DescendantIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the location path expression for
   *     this itterator.
   * @throws javax.xml.transform.TransformerException if any
   */
  DescendantIterator(final Compiler compiler, final int opPos, final int analysis)
      throws javax.xml.transform.TransformerException {

    super(analysis);

    int firstStepPos = OpMap.getFirstChildPos(opPos);
    final int stepType = compiler.getOp(firstStepPos);

    boolean orSelf = OpCodes.FROM_DESCENDANTS_OR_SELF == stepType;
    boolean fromRoot = false;
    if (OpCodes.FROM_SELF == stepType) {
      orSelf = true;
      // firstStepPos += 8;
    }
    else if (OpCodes.FROM_ROOT == stepType) {
      fromRoot = true;
      // Ugly code... will go away when AST work is done.
      final int nextStepPos = compiler.getNextStepPos(firstStepPos);
      if (compiler.getOp(nextStepPos) == OpCodes.FROM_DESCENDANTS_OR_SELF) {
          orSelf = true;
      }
      // firstStepPos += 8;
    }

    // Find the position of the last step.
    int nextStepPos = firstStepPos;
    while (true) {
      nextStepPos = compiler.getNextStepPos(nextStepPos);
      if (nextStepPos > 0) {
        final int stepOp = compiler.getOp(nextStepPos);
        if (OpCodes.ENDOP != stepOp) {
            firstStepPos = nextStepPos;
        }
        else {
            break;
        }
      }
      else {
          break;
      }
    }

    // Fix for http://nagoya.apache.org/bugzilla/show_bug.cgi?id=1336
    if ((analysis & WalkerFactory.BIT_CHILD) != 0) {
        orSelf = false;
    }

    if (fromRoot) {
      if (orSelf) {
          m_axis = Axis.DESCENDANTSORSELFFROMROOT;
      }
      else {
          m_axis = Axis.DESCENDANTSFROMROOT;
      }
    }
    else if (orSelf) {
        m_axis = Axis.DESCENDANTORSELF;
    }
    else {
        m_axis = Axis.DESCENDANT;
    }

    final int whatToShow = compiler.getWhatToShow(firstStepPos);

    if ((0
            == (whatToShow
                & (DTMFilter.SHOW_ATTRIBUTE
                    | DTMFilter.SHOW_ELEMENT
                    | DTMFilter.SHOW_PROCESSING_INSTRUCTION)))
        || (whatToShow == DTMFilter.SHOW_ALL)) {
        initNodeTest(whatToShow);
    }
    else {
      initNodeTest(
          whatToShow, compiler.getStepNS(firstStepPos), compiler.getStepLocalName(firstStepPos));
    }
    initPredicateInfo(compiler, firstStepPos);
  }

  /** {@inheritDoc} */
  @Override
  public DTMIterator cloneWithReset() throws CloneNotSupportedException {

    final DescendantIterator clone = (DescendantIterator) super.cloneWithReset();
    clone.m_traverser = m_traverser;

    clone.resetProximityPositions();

    return clone;
  }

  /** {@inheritDoc} */
  @Override
  public int nextNode() {
    if (m_foundLast) {
        return DTM.NULL;
    }

    if (DTM.NULL == m_lastFetched) {
      resetProximityPositions();
    }

    int next;

    try {
      do {
        if (0 == m_extendedTypeID) {
          next =
              m_lastFetched =
                  (DTM.NULL == m_lastFetched)
                      ? m_traverser.first(m_context)
                      : m_traverser.next(m_context, m_lastFetched);
        }
        else {
          next =
              m_lastFetched =
                  (DTM.NULL == m_lastFetched)
                      ? m_traverser.first(m_context, m_extendedTypeID)
                      : m_traverser.next(m_context, m_lastFetched, m_extendedTypeID);
        }

        if (DTM.NULL != next) {
          if (DTMIterator.FILTER_ACCEPT == acceptNode(next)) {
              break;
          }
          continue;
        }

        break;
      }
      while (next != DTM.NULL);

      if (DTM.NULL != next) {
        m_pos++;
        return next;
      }

      m_foundLast = true;
      return DTM.NULL;
    }
    finally {
    }
  }

  /** {@inheritDoc} */
  @Override
  public void setRoot(final int context, final Object environment) {
    super.setRoot(context, environment);
    m_traverser = m_cdtm.getAxisTraverser(m_axis);

    final String localName = getLocalName();
    final String namespace = getNamespace();
    final int what = m_whatToShow;
    if (DTMFilter.SHOW_ALL == what
        || NodeTest.WILD.equals(localName)
        || NodeTest.WILD.equals(namespace)) {
      m_extendedTypeID = 0;
    }
    else {
      final int type = getNodeTypeTest(what);
      m_extendedTypeID = m_cdtm.getExpandedTypeID(namespace, localName, type);
    }
  }

  /** {@inheritDoc} */
  @Override
  public int asNode(final XPathContext xctxt) throws javax.xml.transform.TransformerException {
    if (getPredicateCount() > 0) {
        return super.asNode(xctxt);
    }

    final int current = xctxt.getCurrentNode();

    final DTM dtm = xctxt.getDTM(current);
    final DTMAxisTraverser traverser = dtm.getAxisTraverser(m_axis);

    final String localName = getLocalName();
    final String namespace = getNamespace();
    final int what = m_whatToShow;
    if (DTMFilter.SHOW_ALL == what || localName == NodeTest.WILD || namespace == NodeTest.WILD) {
      return traverser.first(current);
    }
    final int type = getNodeTypeTest(what);
    final int extendedType = dtm.getExpandedTypeID(namespace, localName, type);
    return traverser.first(current, extendedType);
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {
    m_traverser = null;
    m_extendedTypeID = 0;

    // Always call the superclass detach last!
    super.detach();
  }

  /** {@inheritDoc} */
  @Override
  public int getAxis() {
    return m_axis;
  }

  /** The traverser to use to navigate over the descendants. */
  protected transient DTMAxisTraverser m_traverser;

  /** The axis that we are traversing. */
  protected int m_axis;

  /** The extended type ID, not set until setRoot. */
  protected int m_extendedTypeID;

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(final Expression expr) {
    if (!super.deepEquals(expr)) {
        return false;
    }

    return m_axis == ((DescendantIterator) expr).m_axis;
  }
}
