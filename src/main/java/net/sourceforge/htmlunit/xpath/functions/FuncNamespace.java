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
import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;

/** Execute the Namespace() function. */
public class FuncNamespace extends FunctionDef1Arg {

  /** {@inheritDoc} */
  @Override
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

    int context = getArg0AsNode(xctxt);

    String s;
    if (context != DTM.NULL) {
      DTM dtm = xctxt.getDTM(context);
      int t = dtm.getNodeType(context);
      if (t == DTM.ELEMENT_NODE) {
        s = dtm.getNamespaceURI(context);
      } else if (t == DTM.ATTRIBUTE_NODE) {

        // This function always returns an empty string for namespace nodes.
        // We check for those here. Fix inspired by Davanum Srinivas.

        s = dtm.getNodeName(context);
        if (s.startsWith("xmlns:") || s.equals("xmlns")) return XString.EMPTYSTRING;

        s = dtm.getNamespaceURI(context);
      } else return XString.EMPTYSTRING;
    } else return XString.EMPTYSTRING;

    return (null == s) ? XString.EMPTYSTRING : new XString(s);
  }
}
