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
import net.sourceforge.htmlunit.xpath.ExpressionOwner;
import net.sourceforge.htmlunit.xpath.XPathContext;
import net.sourceforge.htmlunit.xpath.XPathVisitor;
import net.sourceforge.htmlunit.xpath.objects.XNodeSet;
import net.sourceforge.htmlunit.xpath.xml.dtm.Axis;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;
import net.sourceforge.htmlunit.xpath.xml.utils.PrefixResolver;

/**
 * Class to use for one-step iteration that doesn't have a predicate, and doesn't need to set the
 * context.
 */
public class FilterExprIteratorSimple extends LocPathIterator {

  /**
   * The contained expression. Should be non-null.
   *
   * @serial
   */
  private Expression m_expr;

  /** The result of executing m_expr. Needs to be deep cloned on clone op. */
  private transient XNodeSet m_exprObj;

  /** Create a FilterExprIteratorSimple object. */
  public FilterExprIteratorSimple(Expression expr) {
    super(null);
    m_expr = expr;
  }

  /** {@inheritDoc} */
  @Override
  public void setRoot(int context, Object environment) {
    super.setRoot(context, environment);
    m_exprObj =
        executeFilterExpr(context, m_execContext, getPrefixResolver(), getIsTopLevel(), m_expr);
  }

  /**
   * Execute the expression. Meant for reuse by other FilterExpr iterators that are not derived from
   * this object.
   */
  public static XNodeSet executeFilterExpr(
      int context,
      XPathContext xctxt,
      PrefixResolver prefixResolver,
      boolean isTopLevel,
      Expression expr)
      throws net.sourceforge.htmlunit.xpath.xml.utils.WrappedRuntimeException {
    PrefixResolver savedResolver = xctxt.getNamespaceContext();
    XNodeSet result;

    try {
      xctxt.pushCurrentNode(context);
      xctxt.setNamespaceContext(prefixResolver);

      // The setRoot operation can take place with a reset operation,
      // and so we may not be in the context of LocPathIterator#nextNode,
      // so we have to set up the variable context, execute the expression,
      // and then restore the variable context.

      if (isTopLevel) {
        // System.out.println("calling m_expr.execute(getXPathContext())");

        result = (net.sourceforge.htmlunit.xpath.objects.XNodeSet) expr.execute(xctxt);
        result.setShouldCacheNodes(true);
      } else result = (net.sourceforge.htmlunit.xpath.objects.XNodeSet) expr.execute(xctxt);

    } catch (javax.xml.transform.TransformerException se) {

      // TODO: Fix...
      throw new net.sourceforge.htmlunit.xpath.xml.utils.WrappedRuntimeException(se);
    } finally {
      xctxt.popCurrentNode();
      xctxt.setNamespaceContext(savedResolver);
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public int nextNode() {
    if (m_foundLast) return DTM.NULL;

    int next;

    if (null != m_exprObj) {
      m_lastFetched = next = m_exprObj.nextNode();
    } else m_lastFetched = next = DTM.NULL;

    // m_lastFetched = next;
    if (DTM.NULL != next) {
      m_pos++;
      return next;
    } else {
      m_foundLast = true;

      return DTM.NULL;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {
    if (m_allowDetach) {
      super.detach();
      m_exprObj.detach();
      m_exprObj = null;
    }
  }

  /** {@inheritDoc} */
  @Override
  public int getAnalysisBits() {
    if (null != m_expr && m_expr instanceof PathComponent) {
      return ((PathComponent) m_expr).getAnalysisBits();
    }
    return WalkerFactory.BIT_FILTER;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isDocOrdered() {
    return m_exprObj.isDocOrdered();
  }

  class filterExprOwner implements ExpressionOwner {
    /** {@inheritDoc} */
    @Override
    public Expression getExpression() {
      return m_expr;
    }

    /** {@inheritDoc} */
    @Override
    public void setExpression(Expression exp) {
      exp.exprSetParent(FilterExprIteratorSimple.this);
      m_expr = exp;
    }
  }

  /** {@inheritDoc} */
  @Override
  public void callPredicateVisitors(XPathVisitor visitor) {
    m_expr.callVisitors(new filterExprOwner(), visitor);

    super.callPredicateVisitors(visitor);
  }

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(Expression expr) {
    if (!super.deepEquals(expr)) return false;

    FilterExprIteratorSimple fet = (FilterExprIteratorSimple) expr;
    if (!m_expr.deepEquals(fet.m_expr)) return false;

    return true;
  }

  /** {@inheritDoc} */
  @Override
  public int getAxis() {
    if (null != m_exprObj) return m_exprObj.getAxis();
    else return Axis.FILTEREDLIST;
  }
}
