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

import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.objects.XString;
import org.htmlunit.xpath.res.XPATHMessages;

/** Execute the Concat() function. */
public class FuncConcat extends FunctionMultiArgs {

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    final StringBuilder sb = new StringBuilder();

    // Compiler says we must have at least two arguments.
    sb.append(m_arg0.execute(xctxt).str());
    sb.append(m_arg1.execute(xctxt).str());

    if (null != arg2_) {
        sb.append(arg2_.execute(xctxt).str());
    }

    if (null != args_) {
      for (final org.htmlunit.xpath.Expression arg : args_) {
        sb.append(arg.execute(xctxt).str());
      }
    }

    return new XString(sb.toString());
  }

  /** {@inheritDoc} */
  @Override
  public void checkNumberArgs(final int argNum) throws WrongNumberArgsException {
    if (argNum < 2) {
        reportWrongNumberArgs();
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
    throw new WrongNumberArgsException(XPATHMessages.createXPATHMessage("gtone", null));
  }
}
