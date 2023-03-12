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
package org.htmlunit.xpath.patterns;

import org.htmlunit.xpath.Expression;
import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.XPathVisitor;
import org.htmlunit.xpath.objects.XNumber;
import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMIterator;

/** Match pattern step that contains a function. */
public class FunctionPattern extends StepPattern {

  /**
   * Construct a FunctionPattern from a {@link org.htmlunit.xpath.functions.Function expression}.
   *
   * <p>NEEDSDOC @param expr
   */
  public FunctionPattern(Expression expr, int axis) {

    super(0, null, null, axis);

    m_functionExpr = expr;
  }

  /** {@inheritDoc} */
  @Override
  public final void calcScore() {

    m_score = SCORE_OTHER;

    if (null == m_targetString) calcTargetString();
  }

  /**
   * Should be a {@link org.htmlunit.xpath.functions.Function expression}.
   *
   * @serial
   */
  final Expression m_functionExpr;

  /** {@inheritDoc} */
  @Override
  public XObject execute(XPathContext xctxt, int context)
      throws javax.xml.transform.TransformerException {

    DTMIterator nl = m_functionExpr.asIterator(xctxt, context);
    XNumber score = SCORE_NONE;

    if (null != nl) {
      int n;

      while (DTM.NULL != (n = nl.nextNode())) {
        score = (n == context) ? SCORE_OTHER : SCORE_NONE;

        if (score == SCORE_OTHER) {
          context = n;

          break;
        }
      }
    }
    nl.detach();

    return score;
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(XPathContext xctxt, int context, DTM dtm, int expType)
      throws javax.xml.transform.TransformerException {

    DTMIterator nl = m_functionExpr.asIterator(xctxt, context);
    XNumber score = SCORE_NONE;

    if (null != nl) {
      int n;

      while (DTM.NULL != (n = nl.nextNode())) {
        score = (n == context) ? SCORE_OTHER : SCORE_NONE;

        if (score == SCORE_OTHER) {
          context = n;

          break;
        }
      }

      nl.detach();
    }

    return score;
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

    int context = xctxt.getCurrentNode();
    DTMIterator nl = m_functionExpr.asIterator(xctxt, context);
    XNumber score = SCORE_NONE;

    if (null != nl) {
      int n;

      while (DTM.NULL != (n = nl.nextNode())) {
        score = (n == context) ? SCORE_OTHER : SCORE_NONE;

        if (score == SCORE_OTHER) {
          context = n;

          break;
        }
      }

      nl.detach();
    }

    return score;
  }

  /** {@inheritDoc} */
  @Override
  protected void callSubtreeVisitors(XPathVisitor visitor) {
    m_functionExpr.callVisitors(visitor);
    super.callSubtreeVisitors(visitor);
  }
}
