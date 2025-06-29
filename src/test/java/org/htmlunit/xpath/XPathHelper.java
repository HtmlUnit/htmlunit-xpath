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
package org.htmlunit.xpath;

import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.TransformerException;
import org.htmlunit.xpath.objects.XBoolean;
import org.htmlunit.xpath.objects.XNodeSet;
import org.htmlunit.xpath.objects.XNumber;
import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.objects.XString;
import org.htmlunit.xpath.xml.utils.PrefixResolver;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Collection of XPath utility methods.
 *
 * @author Ahmed Ashour
 * @author Chuck Dumont
 * @author Ronald Brill
 */
public final class XPathHelper {

  /** Private to avoid instantiation. */
  private XPathHelper() {
    // Empty.
  }

  /**
   * Evaluates an XPath expression from the specified node, returning the resultant nodes.
   *
   * @param <T> the type class
   * @param node the node to start searching from
   * @param xpathExpr the XPath expression
   * @param resolver the prefix resolver to use for resolving namespace prefixes, or null
   * @return the list of objects found
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> getByXPath(
      final Node node,
      final String xpathExpr,
      final PrefixResolver resolver,
      final boolean caseSensitive) {
    if (xpathExpr == null) {
      throw new IllegalArgumentException("Null is not a valid XPath expression");
    }

    final List<T> list = new ArrayList<>();
    try {
      final XObject result = evaluateXPath(node, xpathExpr, resolver, caseSensitive);

      if (result instanceof XNodeSet) {
        final NodeList nodelist = result.nodelist();
        for (int i = 0; i < nodelist.getLength(); i++) {
          list.add((T) nodelist.item(i));
        }
      }
      else if (result instanceof XNumber) {
        list.add((T) Double.valueOf(result.num()));
      }
      else if (result instanceof XBoolean) {
        list.add((T) Boolean.valueOf(result.bool()));
      }
      else if (result instanceof XString) {
        list.add((T) result.str());
      }
      else {
        throw new RuntimeException("Unproccessed " + result.getClass().getName());
      }
    }
    catch (final Exception e) {
      throw new RuntimeException("Could not retrieve XPath >" + xpathExpr + "< on " + node, e);
    }
    return list;
  }

  /**
   * Evaluates an XPath expression to an XObject.
   *
   * @param contextNode the node to start searching from
   * @param str a valid XPath string
   * @param prefixResolver prefix resolver to use for resolving namespace prefixes, or null
   * @return an XObject, which can be used to obtain a string, number, nodelist, etc (should never
   *     be {@code null})
   * @throws TransformerException if a syntax or other error occurs
   */
  private static XObject evaluateXPath(
      final Node contextNode,
      final String str,
      final PrefixResolver prefixResolver,
      final boolean caseSensitive)
      throws TransformerException {
    final XPathContext xpathSupport = new XPathContext();

    final XPathAdapter xpath = new XPathAdapter(str, prefixResolver, null, caseSensitive);
    final int ctxtNode = xpathSupport.getDTMHandleFromNode(contextNode);
    return xpath.execute(xpathSupport, ctxtNode, prefixResolver);
  }
}
