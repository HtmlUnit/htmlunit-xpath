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

import net.sourceforge.htmlunit.xpath.XPathContext;
import net.sourceforge.htmlunit.xpath.objects.XObject;
import net.sourceforge.htmlunit.xpath.objects.XString;
import net.sourceforge.htmlunit.xpath.res.XPATHErrorResources;
import net.sourceforge.htmlunit.xpath.res.XPATHMessages;

/** Execute the Substring() function. */
public class FuncSubstring extends Function3Args {

  /** {@inheritDoc} */
  @Override
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

    XString s1 = m_arg0.execute(xctxt).xstr();
    double start = m_arg1.execute(xctxt).num();
    int lenOfS1 = s1.length();
    XString substr;

    if (lenOfS1 <= 0) {
      return XString.EMPTYSTRING;
    }

    int startIndex;

    if (Double.isNaN(start)) {

      // Double.MIN_VALUE doesn't work with math below
      // so just use a big number and hope I never get caught.
      start = -1000000;
      startIndex = 0;
    } else {
      start = Math.round(start);
      startIndex = (start > 0) ? (int) start - 1 : 0;
    }

    if (null != m_arg2) {
      double len = m_arg2.num(xctxt);
      if (len < 1) {
        return XString.EMPTYSTRING;
      }

      int end = (int) (Math.round(len) + start) - 1;

      // Normalize end index.
      if (end < 0) end = 0;
      else if (end > lenOfS1) end = lenOfS1;

      if (startIndex > lenOfS1) startIndex = lenOfS1;

      substr = s1.substring(startIndex, end);
    } else {
      if (startIndex > lenOfS1) startIndex = lenOfS1;
      substr = s1.substring(startIndex);
    }

    return substr;
  }

  /** {@inheritDoc} */
  @Override
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException {
    if (argNum < 2) reportWrongNumberArgs();
  }

  /** {@inheritDoc} */
  @Override
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
    throw new WrongNumberArgsException(
        XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_TWO_OR_THREE, null));
  }
}
