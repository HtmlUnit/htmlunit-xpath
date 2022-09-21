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
/*
 * $Id$
 */
package net.sourceforge.htmlunit.xpath.patterns;

import net.sourceforge.htmlunit.xpath.Expression;
import net.sourceforge.htmlunit.xpath.ExpressionOwner;
import net.sourceforge.htmlunit.xpath.XPathContext;
import net.sourceforge.htmlunit.xpath.XPathVisitor;
import net.sourceforge.htmlunit.xpath.objects.XNumber;
import net.sourceforge.htmlunit.xpath.objects.XObject;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMIterator;

/**
 * Match pattern step that contains a function.
 * @xsl.usage advanced
 */
public class FunctionPattern extends StepPattern
{
    static final long serialVersionUID = -5426793413091209944L;

  /**
   * Construct a FunctionPattern from a
   * {@link net.sourceforge.htmlunit.xpath.functions.Function expression}.
   *
   * NEEDSDOC @param expr
   */
  public FunctionPattern(Expression expr, int axis, int predaxis)
  {

    super(0, null, null, axis, predaxis);

    m_functionExpr = expr;
  }

  /**
   * Static calc of match score.
   */
  @Override
public final void calcScore()
  {

    m_score = SCORE_OTHER;

    if (null == m_targetString)
      calcTargetString();
  }

  /**
   * Should be a {@link net.sourceforge.htmlunit.xpath.functions.Function expression}.
   *  @serial   
   */
  Expression m_functionExpr;
  
  /**
   * Test a node to see if it matches the given node test.
   *
   * @param xctxt XPath runtime context.
   *
   * @return {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  @Override
public XObject execute(XPathContext xctxt, int context)
          throws javax.xml.transform.TransformerException
  {

    DTMIterator nl = m_functionExpr.asIterator(xctxt, context);
    XNumber score = SCORE_NONE;

    if (null != nl)
    {
      int n;

      while (DTM.NULL != (n = nl.nextNode()))
      {
        score = (n == context) ? SCORE_OTHER : SCORE_NONE;

        if (score == SCORE_OTHER)
        {
          context = n;

          break;
        }
      }

      // nl.detach();
    }
    nl.detach();

    return score;
  }
  
  /**
   * Test a node to see if it matches the given node test.
   *
   * @param xctxt XPath runtime context.
   *
   * @return {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  @Override
public XObject execute(XPathContext xctxt, int context, 
                         DTM dtm, int expType)
          throws javax.xml.transform.TransformerException
  {

    DTMIterator nl = m_functionExpr.asIterator(xctxt, context);
    XNumber score = SCORE_NONE;

    if (null != nl)
    {
      int n;

      while (DTM.NULL != (n = nl.nextNode()))
      {
        score = (n == context) ? SCORE_OTHER : SCORE_NONE;

        if (score == SCORE_OTHER)
        {
          context = n;

          break;
        }
      }

      nl.detach();
    }

    return score;
  }
  
  /**
   * Test a node to see if it matches the given node test.
   *
   * @param xctxt XPath runtime context.
   *
   * @return {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  @Override
public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    int context = xctxt.getCurrentNode();
    DTMIterator nl = m_functionExpr.asIterator(xctxt, context);
    XNumber score = SCORE_NONE;

    if (null != nl)
    {
      int n;

      while (DTM.NULL != (n = nl.nextNode()))
      {
        score = (n == context) ? SCORE_OTHER : SCORE_NONE;

        if (score == SCORE_OTHER)
        {
          context = n;

          break;
        }
      }

      nl.detach();
    }

    return score;
  }
  
  class FunctionOwner implements ExpressionOwner
  {
    /**
     * @see ExpressionOwner#getExpression()
     */
    @Override
    public Expression getExpression()
    {
      return m_functionExpr;
    }


    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    @Override
    public void setExpression(Expression exp)
    {
      exp.exprSetParent(FunctionPattern.this);
      m_functionExpr = exp;
    }
  }
  
  /**
   * Call the visitor for the function.
   */
  @Override
protected void callSubtreeVisitors(XPathVisitor visitor)
  {
    m_functionExpr.callVisitors(new FunctionOwner(), visitor);
    super.callSubtreeVisitors(visitor);
  }

}
