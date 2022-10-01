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
package net.sourceforge.htmlunit.xpath.operations;

import net.sourceforge.htmlunit.xpath.Expression;
import net.sourceforge.htmlunit.xpath.ExpressionOwner;
import net.sourceforge.htmlunit.xpath.XPathContext;
import net.sourceforge.htmlunit.xpath.XPathVisitor;
import net.sourceforge.htmlunit.xpath.objects.XObject;

/** The unary operation base class. */
public abstract class UnaryOperation extends Expression implements ExpressionOwner {

  /**
   * The operand for the operation.
   *
   * @serial
   */
  protected Expression m_right;

  /**
   * Tell if this expression or it's subexpressions can traverse outside the current subtree.
   *
   * @return true if traversal outside the context node's subtree can occur.
   */
  @Override
  public boolean canTraverseOutsideSubtree() {

    if (null != m_right && m_right.canTraverseOutsideSubtree()) return true;

    return false;
  }

  /**
   * Set the expression operand for the operation.
   *
   * @param r The expression operand to which the unary operation will be applied.
   */
  public void setRight(Expression r) {
    m_right = r;
    r.exprSetParent(this);
  }

  /**
   * Execute the operand and apply the unary operation to the result.
   *
   * @param xctxt The runtime execution context.
   * @return An XObject that represents the result of applying the unary operation to the evaluated
   *     operand.
   * @throws javax.xml.transform.TransformerException
   */
  @Override
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

    return operate(m_right.execute(xctxt));
  }

  /**
   * Apply the operation to two operands, and return the result.
   *
   * @param right non-null reference to the evaluated right operand.
   * @return non-null reference to the XObject that represents the result of the operation.
   * @throws javax.xml.transform.TransformerException
   */
  public abstract XObject operate(XObject right) throws javax.xml.transform.TransformerException;

  /** @return the operand of unary operation, as an Expression. */
  public Expression getOperand() {
    return m_right;
  }

  /**
   * @see net.sourceforge.htmlunit.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  @Override
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor) {
    if (visitor.visitUnaryOperation(owner, this)) {
      m_right.callVisitors(this, visitor);
    }
  }

  /** @see ExpressionOwner#getExpression() */
  @Override
  public Expression getExpression() {
    return m_right;
  }

  /** @see ExpressionOwner#setExpression(Expression) */
  @Override
  public void setExpression(Expression exp) {
    exp.exprSetParent(this);
    m_right = exp;
  }

  /** @see Expression#deepEquals(Expression) */
  @Override
  public boolean deepEquals(Expression expr) {
    if (!isSameClass(expr)) return false;

    if (!m_right.deepEquals(((UnaryOperation) expr).m_right)) return false;

    return true;
  }
}
