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
/*
 * $Id$
 */
package net.sourceforge.htmlunit.xpath.xml.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @deprecated Since the introduction of the DTM, this class will be removed.
 * This class provides a DOM level 2 "helper", which provides services currently
 * not provided be the DOM standard.
 */
public class DOM2Helper extends DOMHelper
{

  /**
   * Construct an instance.
   */
  public DOM2Helper(){}

  /** Field m_doc: Document Node for the document this helper is currently
   * accessing or building
   * @see #setDocument
   * @see #getDocument
   *  */
  private Document m_doc;

  /**
   * Figure out whether node2 should be considered as being later
   * in the document than node1, in Document Order as defined
   * by the XPath model. This may not agree with the ordering defined
   * by other XML applications.
   * <p>
   * There are some cases where ordering isn't defined, and neither are
   * the results of this function -- though we'll generally return true.
   * <p>
   * TODO: Make sure this does the right thing with attribute nodes!!!
   *
   * @param node1 DOM Node to perform position comparison on.
   * @param node2 DOM Node to perform position comparison on .
   *
   * @return false if node2 comes before node1, otherwise return true.
   * You can think of this as
   * <code>(node1.documentOrderPosition &lt;= node2.documentOrderPosition)</code>.
   */
  public static boolean isNodeAfter(Node node1, Node node2)
  {
      // isNodeAfter will return true if node is after countedNode
      // in document order. The base isNodeAfter is sloooow (relatively).
      return DOMHelper.isNodeAfter(node1, node2);
  }

  /**
   * Returns the local name of the given node, as defined by the
   * XML Namespaces specification. This is prepared to handle documents
   * built using DOM Level 1 methods by falling back upon explicitly
   * parsing the node name.
   *
   * @param n Node to be examined
   *
   * @return String containing the local name, or null if the node
   * was not assigned a Namespace.
   */
  @Override
  public String getLocalNameOfNode(Node n)
  {

    String name = n.getLocalName();

    return (null == name) ? super.getLocalNameOfNode(n) : name;
  }

  /**
   * Returns the Namespace Name (Namespace URI) for the given node.
   * In a Level 2 DOM, you can ask the node itself. Note, however, that
   * doing so conflicts with our decision in getLocalNameOfNode not
   * to trust the that the DOM was indeed created using the Level 2
   * methods. If Level 1 methods were used, these two functions will
   * disagree with each other.
   * <p>
   * TODO: Reconcile with getLocalNameOfNode.
   *
   * @param n Node to be examined
   *
   * @return String containing the Namespace URI bound to this DOM node
   * at the time the Node was created.
   */
  @Override
  public String getNamespaceOfNode(Node n)
  {
    return n.getNamespaceURI();
  }
}
