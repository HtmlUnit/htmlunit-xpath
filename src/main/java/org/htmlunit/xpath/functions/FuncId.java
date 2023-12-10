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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.htmlunit.xpath.NodeSetDTM;
import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.objects.XNodeSet;
import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMIterator;

/** Execute the Id() function. */
public class FuncId extends FunctionOneArg {

  /**
   * Fill in a list with nodes that match a space delimited list if ID ID references.
   *
   * @param xctxt The runtime XPath context.
   * @param docContext The document where the nodes are being looked for.
   * @param refval A space delimited list of ID references.
   * @param usedrefs List of references for which nodes were found.
   * @param nodeSet Node set where the nodes will be added to.
   * @param mayBeMore true if there is another set of nodes to be looked for.
   * @return The usedrefs value.
   */
  private List<String> getNodesByID(
      final XPathContext xctxt,
      final int docContext,
      final String refval,
      List<String> usedrefs,
      final NodeSetDTM nodeSet,
      final boolean mayBeMore) {

    if (null != refval) {
      // DOMHelper dh = xctxt.getDOMHelper();
      final StringTokenizer tokenizer = new StringTokenizer(refval);
      boolean hasMore = tokenizer.hasMoreTokens();
      final DTM dtm = xctxt.getDTM(docContext);

      while (hasMore) {
        final String ref = tokenizer.nextToken();
        hasMore = tokenizer.hasMoreTokens();

        if ((null != usedrefs) && usedrefs.contains(ref)) {
          continue;
        }

        final int node = dtm.getElementById(ref);

        if (DTM.NULL != node) nodeSet.addNodeInDocOrder(node, xctxt);

        if ((null != ref) && (hasMore || mayBeMore)) {
          if (null == usedrefs) usedrefs = new ArrayList<>();

          usedrefs.add(ref);
        }
      }
    }

    return usedrefs;
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    final int context = xctxt.getCurrentNode();
    final DTM dtm = xctxt.getDTM(context);
    final int docContext = dtm.getDocument();

    if (DTM.NULL == docContext) error(xctxt, XPATHErrorResources.ER_CONTEXT_HAS_NO_OWNERDOC, null);

    final XObject arg = m_arg0.execute(xctxt);
    final int argType = arg.getType();
    final XNodeSet nodes = new XNodeSet(xctxt.getDTMManager());
    final NodeSetDTM nodeSet = nodes.mutableNodeset();

    if (XObject.CLASS_NODESET == argType) {
      final DTMIterator ni = arg.iter();
      List<String> usedrefs = null;
      int pos = ni.nextNode();

      while (DTM.NULL != pos) {
        final DTM ndtm = ni.getDTM(pos);
        final String refval = ndtm.getStringValue(pos).toString();

        pos = ni.nextNode();
        usedrefs = getNodesByID(xctxt, docContext, refval, usedrefs, nodeSet, DTM.NULL != pos);
      }
      // ni.detach();
    }
    else if (XObject.CLASS_NULL == argType) {
      return nodes;
    }
    else {
      final String refval = arg.str();

      getNodesByID(xctxt, docContext, refval, null, nodeSet, false);
    }

    return nodes;
  }
}
