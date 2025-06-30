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
import org.htmlunit.xpath.XPathVisitor;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;

/** Base class for functions that accept an undetermined number of multiple arguments. */
public class FunctionMultiArgs extends Function3Args {

  /**
   * Argument expressions that are at index 3 or greater.
   *
   * @serial
   */
  Expression[] args_;

  /** {@inheritDoc} */
  @Override
  public void setArg(final Expression arg, final int argNum) throws WrongNumberArgsException {

    if (argNum < 3) {
        super.setArg(arg, argNum);
    }
    else {
      if (null == args_) {
        args_ = new Expression[1];
        args_[0] = arg;
      }
      else {
        // Slow but space conservative.
        final Expression[] args = new Expression[args_.length + 1];

        System.arraycopy(args_, 0, args, 0, args_.length);

        args[args_.length] = arg;
        args_ = args;
      }
      arg.exprSetParent(this);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
  }

  /** {@inheritDoc} */
  @Override
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
    final String fMsg =
        XPATHMessages.createXPATHMessage(
            XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
            new Object[] {
              "Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called."
            });

    throw new RuntimeException(fMsg);
  }

  /** {@inheritDoc} */
  @Override
  public boolean canTraverseOutsideSubtree() {

    if (super.canTraverseOutsideSubtree()) {
      return true;
    }

    for (final Expression arg : args_) {
      if (arg.canTraverseOutsideSubtree()) {
        return true;
      }
    }

    return false;
  }

  /** {@inheritDoc} */
  @Override
  public void callArgVisitors(final XPathVisitor visitor) {
    super.callArgVisitors(visitor);
    if (null != args_) {
      for (final Expression arg : args_) {
        arg.callVisitors(visitor);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(final Expression expr) {
    if (!super.deepEquals(expr)) {
        return false;
    }

    final FunctionMultiArgs fma = (FunctionMultiArgs) expr;
    if (null != args_) {
      final int n = args_.length;
      if ((null == fma) || (fma.args_.length != n)) {
          return false;
      }

      for (int i = 0; i < n; i++) {
        if (!args_[i].deepEquals(fma.args_[i])) {
            return false;
        }
      }

    }
    else if (null != fma.args_) {
      return false;
    }

    return true;
  }
}
