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

import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.objects.XBoolean;
import org.htmlunit.xpath.objects.XObject;

/** The 'and' operation expression executor. */
public class And extends Operation {

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    final XObject expr1 = m_left.execute(xctxt);

    if (expr1.bool()) {
      final XObject expr2 = m_right.execute(xctxt);

      return expr2.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    return XBoolean.S_FALSE;
  }

  /** {@inheritDoc} */
  @Override
  public boolean bool(final XPathContext xctxt) throws javax.xml.transform.TransformerException {
    return m_left.bool(xctxt) && m_right.bool(xctxt);
  }
}
