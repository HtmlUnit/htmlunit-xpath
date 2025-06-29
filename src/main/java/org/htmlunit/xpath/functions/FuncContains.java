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
import org.htmlunit.xpath.objects.XBoolean;
import org.htmlunit.xpath.objects.XObject;

/** Execute the Contains() function. */
public class FuncContains extends Function2Args {

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    final String s1 = m_arg0.execute(xctxt).str();
    final String s2 = m_arg1.execute(xctxt).str();

    // Add this check for JDK consistency for empty strings.
    if (s1.length() == 0 && s2.length() == 0) {
        return XBoolean.S_TRUE;
    }

    final int index = s1.indexOf(s2);

    return (index > -1) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
