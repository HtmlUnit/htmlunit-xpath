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
package net.sourceforge.htmlunit.xpath.functions;

import net.sourceforge.htmlunit.xpath.Expression;
import net.sourceforge.htmlunit.xpath.ExpressionOwner;
import net.sourceforge.htmlunit.xpath.XPathVisitor;
import net.sourceforge.htmlunit.xpath.res.XSLMessages;

/** Base class for functions that accept two arguments. */
public class Function2Args extends FunctionOneArg {
  static final long serialVersionUID = 5574294996842710641L;

  /**
   * The second argument passed to the function (at index 1).
   *
   * @serial
   */
  Expression m_arg1;

  /**
   * Return the second argument passed to the function (at index 1).
   *
   * @return An expression that represents the second argument passed to the function.
   */
  public Expression getArg1() {
    return m_arg1;
  }

  /**
   * Set an argument expression for a function. This method is called by the XPath compiler.
   *
   * @param arg non-null expression that represents the argument.
   * @param argNum The argument number index.
   * @throws WrongNumberArgsException If the argNum parameter is greater than 1.
   */
  @Override
  public void setArg(Expression arg, int argNum) throws WrongNumberArgsException {

    // System.out.println("argNum: "+argNum);
    if (argNum == 0) super.setArg(arg, argNum);
    else if (1 == argNum) {
      m_arg1 = arg;
      arg.exprSetParent(this);
    } else reportWrongNumberArgs();
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   * @param argNum The number of arguments that is being passed to the function.
   * @throws WrongNumberArgsException
   */
  @Override
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
    if (argNum != 2) reportWrongNumberArgs();
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate message for this function
   * object.
   *
   * @throws WrongNumberArgsException
   */
  @Override
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
    throw new WrongNumberArgsException(XSLMessages.createXPATHMessage("two", null));
  }

  /**
   * Tell if this expression or it's subexpressions can traverse outside the current subtree.
   *
   * @return true if traversal outside the context node's subtree can occur.
   */
  @Override
  public boolean canTraverseOutsideSubtree() {
    return super.canTraverseOutsideSubtree() ? true : m_arg1.canTraverseOutsideSubtree();
  }

  class Arg1Owner implements ExpressionOwner {
    /** @see ExpressionOwner#getExpression() */
    @Override
    public Expression getExpression() {
      return m_arg1;
    }

    /** @see ExpressionOwner#setExpression(Expression) */
    @Override
    public void setExpression(Expression exp) {
      exp.exprSetParent(Function2Args.this);
      m_arg1 = exp;
    }
  }

  /**
   * @see net.sourceforge.htmlunit.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  @Override
  public void callArgVisitors(XPathVisitor visitor) {
    super.callArgVisitors(visitor);
    if (null != m_arg1) m_arg1.callVisitors(new Arg1Owner(), visitor);
  }

  /** @see Expression#deepEquals(Expression) */
  @Override
  public boolean deepEquals(Expression expr) {
    if (!super.deepEquals(expr)) return false;

    if (null != m_arg1) {
      if (null == ((Function2Args) expr).m_arg1) return false;

      if (!m_arg1.deepEquals(((Function2Args) expr).m_arg1)) return false;
    } else if (null != ((Function2Args) expr).m_arg1) return false;

    return true;
  }
}
