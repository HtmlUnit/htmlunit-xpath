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
import org.htmlunit.xpath.objects.XNumber;
import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.objects.XString;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMIterator;

/** Execute the Sum() function. */
public class FuncSum extends FunctionOneArg {

  /**
   * Execute the function. The function must return a valid object.
   *
   * @param xctxt The current execution context.
   * @return A valid XObject.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    final DTMIterator nodes = m_arg0.asIterator(xctxt, xctxt.getCurrentNode());
    double sum = 0.0;
    int pos;

    while (DTM.NULL != (pos = nodes.nextNode())) {
      final DTM dtm = nodes.getDTM(pos);
      final XString s = dtm.getStringValue(pos);

      if (null != s) {
          sum += s.toDouble();
      }
    }
    nodes.detach();

    return new XNumber(sum);
  }
}
