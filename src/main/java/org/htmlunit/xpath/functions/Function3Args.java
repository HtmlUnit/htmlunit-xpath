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
package org.htmlunit.xpath.functions;

import org.htmlunit.xpath.Expression;
import org.htmlunit.xpath.XPathVisitor;
import org.htmlunit.xpath.res.XPATHMessages;

/** Base class for functions that accept three arguments. */
public class Function3Args extends Function2Args {

  /**
   * The third argument passed to the function (at index 2).
   *
   * @serial
   */
  Expression arg2_;

  /** {@inheritDoc} */
  @Override
  public void setArg(final Expression arg, final int argNum) throws WrongNumberArgsException {

    if (argNum < 2) {
      super.setArg(arg, argNum);
    } else if (2 == argNum) {
      arg2_ = arg;
      arg.exprSetParent(this);
    } else {
      reportWrongNumberArgs();
    }
  }

  /** {@inheritDoc} */
  @Override
  public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
    if (argNum != 3) reportWrongNumberArgs();
  }

  /** {@inheritDoc} */
  @Override
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
    throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("three", null));
  }

  /** {@inheritDoc} */
  @Override
  public boolean canTraverseOutsideSubtree() {
    return super.canTraverseOutsideSubtree() || arg2_.canTraverseOutsideSubtree();
  }

  /** {@inheritDoc} */
  @Override
  public void callArgVisitors(final XPathVisitor visitor) {
    super.callArgVisitors(visitor);
    if (null != arg2_) arg2_.callVisitors(visitor);
  }

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(final Expression expr) {
    if (!super.deepEquals(expr)) return false;

    if (null != arg2_) {
      if ((null == ((Function3Args) expr).arg2_) || !arg2_.deepEquals(((Function3Args) expr).arg2_))
        return false;
    } else if (null != ((Function3Args) expr).arg2_) return false;

    return true;
  }
}
