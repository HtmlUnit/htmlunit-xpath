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
package org.htmlunit.xpath.xml.utils;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * This class implements a generic PrefixResolver that can be used to perform prefix-to-namespace
 * lookup for the XPath object.
 */
public class PrefixResolverDefault implements PrefixResolver {

  /** The context to resolve the prefix from, if the context is not given. */
  final Node m_context;

  /**
   * Construct a PrefixResolverDefault object.
   *
   * @param xpathExpressionContext The context from which XPath expression prefixes will be
   *     resolved. Warning: This will not work correctly if xpathExpressionContext is an attribute
   *     node.
   */
  public PrefixResolverDefault(final Node xpathExpressionContext) {
    m_context = xpathExpressionContext;
  }

  /** {@inheritDoc} */
  @Override
  public String getNamespaceForPrefix(final String prefix) {
    return getNamespaceForPrefix(prefix, m_context);
  }

  /** {@inheritDoc} */
  @Override
  public String getNamespaceForPrefix(
      final String prefix, final org.w3c.dom.Node namespaceContext) {

    Node parent = namespaceContext;
    String namespace = null;

    if (prefix.equals("xml")) {
      namespace = "http://www.w3.org/XML/1998/namespace";
    }
    else {
      int type;

      while ((null != parent)
          && (null == namespace)
          && (((type = parent.getNodeType()) == Node.ELEMENT_NODE)
              || (type == Node.ENTITY_REFERENCE_NODE))) {
        if (type == Node.ELEMENT_NODE) {
          if (parent.getNodeName().indexOf(prefix + ":") == 0) {
              return parent.getNamespaceURI();
          }
          final NamedNodeMap nnm = parent.getAttributes();

          for (int i = 0; i < nnm.getLength(); i++) {
            final Node attr = nnm.item(i);
            final String aname = attr.getNodeName();
            final boolean isPrefix = aname.startsWith("xmlns:");

            if (isPrefix || aname.equals("xmlns")) {
              final int index = aname.indexOf(':');
              final String p = isPrefix ? aname.substring(index + 1) : "";

              if (p.equals(prefix)) {
                namespace = attr.getNodeValue();

                break;
              }
            }
          }
        }

        parent = parent.getParentNode();
      }
    }

    return namespace;
  }

  /** {@inheritDoc} */
  @Override
  public boolean handlesNullPrefixes() {
    return false;
  }
}
