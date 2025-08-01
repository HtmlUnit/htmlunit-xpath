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
package org.htmlunit.xpath.functions;

import org.htmlunit.xpath.Expression;
import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.XPathVisitor;
import org.htmlunit.xpath.compiler.Compiler;
import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.res.XPATHMessages;

/**
 * This is a superclass of all XPath functions. This allows two ways for the class to be called. One
 * method is that the super class processes the arguments and hands the results to the derived
 * class, the other method is that the derived class may process it's own arguments, which is faster
 * since the arguments don't have to be added to an array, but causes a larger code footprint.
 */
public abstract class Function extends Expression {

  /**
   * Set an argument expression for a function. This method is called by the XPath compiler.
   *
   * @param arg non-null expression that represents the argument.
   * @param argNum The argument number index.
   * @throws WrongNumberArgsException If the argNum parameter is beyond what is specified for this
   *     function.
   */
  public void setArg(final Expression arg, final int argNum) throws WrongNumberArgsException {
    reportWrongNumberArgs();
  }

  /**
   * Check that the number of arguments passed to this function is correct. This method is meant to
   * be overloaded by derived classes, to check for the number of arguments for a specific function
   * type. This method is called by the compiler for static number of arguments checking.
   *
   * @param argNum The number of arguments that is being passed to the function.
   * @throws WrongNumberArgsException if any
   */
  public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
    if (argNum != 0) {
        reportWrongNumberArgs();
    }
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate message for this function
   * object. This method is meant to be overloaded by derived classes so that the message will be as
   * specific as possible.
   *
   * @throws WrongNumberArgsException if any
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
    throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("zero", null));
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    // Programmer's assert. (And, no, I don't want the method to be abstract).
    System.out.println("Error! Function.execute should not be called!");

    return null;
  }

  /** Call the visitors for the function arguments. */
  public void callArgVisitors(final XPathVisitor visitor) {
  }

  /** {@inheritDoc} */
  @Override
  public void callVisitors(final XPathVisitor visitor) {
    if (visitor.visitFunction(this)) {
      callArgVisitors(visitor);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(final Expression expr) {
    return isSameClass(expr);
  }

  /**
   * This function is currently only being used by Position() and Last(). See respective functions
   * for more detail.
   */
  public void postCompileStep(final Compiler compiler) {
    // no default action
  }
}
