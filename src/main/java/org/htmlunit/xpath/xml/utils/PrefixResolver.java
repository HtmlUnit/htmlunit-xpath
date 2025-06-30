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
package org.htmlunit.xpath.xml.utils;

/**
 * The class that implements this interface can resolve prefixes to namespaces. Examples would
 * include resolving the meaning of a prefix at a particular point in a document, or mapping the
 * prefixes used in an XPath expression.
 */
public interface PrefixResolver {

  /**
   * Given a namespace, get the corresponding prefix. This assumes that the PrefixResolver holds its
   * own namespace context, or is a namespace context itself.
   *
   * @param prefix The prefix to look up, which may be an empty string ("") for the default
   *     Namespace.
   * @return The associated Namespace URI, or null if the prefix is undeclared in this context.
   */
  String getNamespaceForPrefix(String prefix);

  /**
   * Given a namespace, get the corresponding prefix, based on the context node.
   *
   * @param prefix The prefix to look up, which may be an empty string ("") for the default
   *     Namespace.
   * @param context The node context from which to look up the URI.
   * @return The associated Namespace URI as a string, or null if the prefix is undeclared in this
   *     context.
   */
  String getNamespaceForPrefix(String prefix, org.w3c.dom.Node context);

  boolean handlesNullPrefixes();
}
