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
import org.htmlunit.xpath.xml.dtm.DTM;

/** Execute the Lang() function. */
public class FuncLang extends FunctionOneArg {

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    final String lang = m_arg0.execute(xctxt).str();
    int parent = xctxt.getCurrentNode();
    boolean isLang = false;
    final DTM dtm = xctxt.getDTM(parent);

    while (DTM.NULL != parent) {
      if (DTM.ELEMENT_NODE == dtm.getNodeType(parent)) {
        final int langAttr =
            dtm.getAttributeNode(parent, "http://www.w3.org/XML/1998/namespace", "lang");

        if (DTM.NULL != langAttr) {
          final String langVal = dtm.getNodeValue(langAttr);
          // %OPT%
          if (langVal.toLowerCase().startsWith(lang.toLowerCase())) {
            final int valLen = lang.length();

            if ((langVal.length() == valLen) || (langVal.charAt(valLen) == '-')) {
              isLang = true;
            }
          }

          break;
        }
      }

      parent = dtm.getParent(parent);
    }

    return isLang ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
}
