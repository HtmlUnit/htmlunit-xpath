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
package org.htmlunit.xpath.operations;

import org.htmlunit.xpath.Expression;
import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.XPathVisitor;
import org.htmlunit.xpath.objects.XObject;

/** The unary operation base class. */
public abstract class UnaryOperation extends Expression {

  /**
   * The operand for the operation.
   *
   * @serial
   */
  protected Expression m_right;

  /** {@inheritDoc} */
  @Override
  public boolean canTraverseOutsideSubtree() {

    if (null != m_right && m_right.canTraverseOutsideSubtree()) {
        return true;
    }

    return false;
  }

  /**
   * Set the expression operand for the operation.
   *
   * @param r The expression operand to which the unary operation will be applied.
   */
  public void setRight(final Expression r) {
    m_right = r;
    r.exprSetParent(this);
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    return operate(m_right.execute(xctxt));
  }

  /**
   * Apply the operation to two operands, and return the result.
   *
   * @param right non-null reference to the evaluated right operand.
   * @return non-null reference to the XObject that represents the result of the operation.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  public abstract XObject operate(XObject right) throws javax.xml.transform.TransformerException;

  /** {@inheritDoc} */
  @Override
  public void callVisitors(final XPathVisitor visitor) {
    if (visitor.visitUnaryOperation()) {
      m_right.callVisitors(visitor);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(final Expression expr) {
    if (!isSameClass(expr) || !m_right.deepEquals(((UnaryOperation) expr).m_right)) {
        return false;
    }

    return true;
  }
}
