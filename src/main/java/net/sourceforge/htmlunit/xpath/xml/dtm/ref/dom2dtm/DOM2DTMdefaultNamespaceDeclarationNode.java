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
package net.sourceforge.htmlunit.xpath.xml.dtm.ref.dom2dtm;

import net.sourceforge.htmlunit.xpath.xml.dtm.DTMException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * This is a kluge to let us shove a declaration for xml: into the DOM2DTM model. Basically, it
 * creates a proxy node in DOM space to carry the additional information. This is _NOT_ a full DOM
 * implementation, and shouldn't be one since it sits alongside the DOM rather than becoming part of
 * the DOM model.
 *
 * <p>(This used to be an internal class within DOM2DTM. Moved out because I need to perform an
 * instanceof operation on it to support a temporary workaround in DTMManagerDefault.)
 *
 * <p>%REVIEW% What if the DOM2DTM was built around a DocumentFragment and there isn't a single root
 * element? I think this fails that case...
 *
 * <p>%REVIEW% An alternative solution would be to create the node _only_ in DTM space, but given
 * how DOM2DTM is currently written I think this is simplest.
 */
public class DOM2DTMdefaultNamespaceDeclarationNode implements Attr, TypeInfo {
  static final String NOT_SUPPORTED_ERR = "Unsupported operation on pseudonode";

  Element pseudoparent;
  String prefix, uri, nodename;
  int handle;

  DOM2DTMdefaultNamespaceDeclarationNode(
      Element pseudoparent, String prefix, String uri, int handle) {
    this.pseudoparent = pseudoparent;
    this.prefix = prefix;
    this.uri = uri;
    this.handle = handle;
    this.nodename = "xmlns:" + prefix;
  }

  /** {@inheritDoc} */
  @Override
  public String getNodeName() {
    return nodename;
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return nodename;
  }

  /** {@inheritDoc} */
  @Override
  public String getNamespaceURI() {
    return "http://www.w3.org/2000/xmlns/";
  }

  /** {@inheritDoc} */
  @Override
  public String getPrefix() {
    return prefix;
  }

  /** {@inheritDoc} */
  @Override
  public String getLocalName() {
    return prefix;
  }

  /** {@inheritDoc} */
  @Override
  public String getNodeValue() {
    return uri;
  }

  /** {@inheritDoc} */
  @Override
  public String getValue() {
    return uri;
  }

  /** {@inheritDoc} */
  @Override
  public Element getOwnerElement() {
    return pseudoparent;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSupported(String feature, String version) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasChildNodes() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasAttributes() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public Node getParentNode() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Node getFirstChild() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Node getLastChild() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Node getPreviousSibling() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Node getNextSibling() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean getSpecified() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public void normalize() {}

  /** {@inheritDoc} */
  @Override
  public NodeList getChildNodes() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public NamedNodeMap getAttributes() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public short getNodeType() {
    return Node.ATTRIBUTE_NODE;
  }

  /** {@inheritDoc} */
  @Override
  public void setNodeValue(String value) {
    throw new DTMException(NOT_SUPPORTED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public void setValue(String value) {
    throw new DTMException(NOT_SUPPORTED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public void setPrefix(String value) {
    throw new DTMException(NOT_SUPPORTED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public Node insertBefore(Node a, Node b) {
    throw new DTMException(NOT_SUPPORTED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public Node replaceChild(Node a, Node b) {
    throw new DTMException(NOT_SUPPORTED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public Node appendChild(Node a) {
    throw new DTMException(NOT_SUPPORTED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public Node removeChild(Node a) {
    throw new DTMException(NOT_SUPPORTED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public Document getOwnerDocument() {
    return pseudoparent.getOwnerDocument();
  }

  /** {@inheritDoc} */
  @Override
  public Node cloneNode(boolean deep) {
    throw new DTMException(NOT_SUPPORTED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public String getTypeName() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String getTypeNamespace() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isDerivedFrom(String ns, String localName, int derivationMethod) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public TypeInfo getSchemaTypeInfo() {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isId() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public Object setUserData(String key, Object data, UserDataHandler handler) {
    return getOwnerDocument().setUserData(key, data, handler);
  }

  /** {@inheritDoc} */
  @Override
  public Object getUserData(String key) {
    return getOwnerDocument().getUserData(key);
  }

  /** {@inheritDoc} */
  @Override
  public Object getFeature(String feature, String version) {
    // we don't have any alternate node, either this node does the job
    // or we don't have anything that does
    return isSupported(feature, version) ? this : null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEqualNode(Node arg) {
    if (arg == this) {
      return true;
    }
    if (arg.getNodeType() != getNodeType()) {
      return false;
    }
    // in theory nodeName can't be null but better be careful
    // who knows what other implementations may be doing?...
    if (getNodeName() == null) {
      if (arg.getNodeName() != null) {
        return false;
      }
    } else if (!getNodeName().equals(arg.getNodeName())) {
      return false;
    }

    if (getLocalName() == null) {
      if (arg.getLocalName() != null) {
        return false;
      }
    } else if (!getLocalName().equals(arg.getLocalName())) {
      return false;
    }

    if (getNamespaceURI() == null) {
      if (arg.getNamespaceURI() != null) {
        return false;
      }
    } else if (!getNamespaceURI().equals(arg.getNamespaceURI())) {
      return false;
    }

    if (getPrefix() == null) {
      if (arg.getPrefix() != null) {
        return false;
      }
    } else if (!getPrefix().equals(arg.getPrefix())) {
      return false;
    }

    if (getNodeValue() == null) {
      if (arg.getNodeValue() != null) {
        return false;
      }
    } else if (!getNodeValue().equals(arg.getNodeValue())) {
      return false;
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public String lookupNamespaceURI(String specifiedPrefix) {
    short type = this.getNodeType();
    switch (type) {
      case Node.ELEMENT_NODE:
        {
          String namespace = this.getNamespaceURI();
          String prefix = this.getPrefix();
          if (namespace != null) {
            // REVISIT: is it possible that prefix is empty string?
            if (specifiedPrefix == null && prefix == specifiedPrefix) {
              // looking for default namespace
              return namespace;
            } else if (prefix != null && prefix.equals(specifiedPrefix)) {
              // non default namespace
              return namespace;
            }
          }
          if (this.hasAttributes()) {
            NamedNodeMap map = this.getAttributes();
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
              Node attr = map.item(i);
              String attrPrefix = attr.getPrefix();
              String value = attr.getNodeValue();
              namespace = attr.getNamespaceURI();
              if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/")) {
                // at this point we are dealing with DOM Level 2 nodes only
                if (specifiedPrefix == null && attr.getNodeName().equals("xmlns")) {
                  // default namespace
                  return value;
                } else if (attrPrefix != null
                    && attrPrefix.equals("xmlns")
                    && attr.getLocalName().equals(specifiedPrefix)) {
                  // non default namespace
                  return value;
                }
              }
            }
          }
          return null;
        }
      case Node.ENTITY_NODE:
      case Node.NOTATION_NODE:
      case Node.DOCUMENT_FRAGMENT_NODE:
      case Node.DOCUMENT_TYPE_NODE:
        // type is unknown
        return null;
      case Node.ATTRIBUTE_NODE:
        {
          if (this.getOwnerElement().getNodeType() == Node.ELEMENT_NODE) {
            return getOwnerElement().lookupNamespaceURI(specifiedPrefix);
          }
          return null;
        }
      default:
        {
          return null;
        }
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isDefaultNamespace(String namespaceURI) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String lookupPrefix(String namespaceURI) {

    // REVISIT: When Namespaces 1.1 comes out this may not be true
    // Prefix can't be bound to null namespace
    if (namespaceURI == null) {
      return null;
    }

    short type = this.getNodeType();

    switch (type) {
      case Node.ENTITY_NODE:
      case Node.NOTATION_NODE:
      case Node.DOCUMENT_FRAGMENT_NODE:
      case Node.DOCUMENT_TYPE_NODE:
        // type is unknown
        return null;
      case Node.ATTRIBUTE_NODE:
        {
          if (this.getOwnerElement().getNodeType() == Node.ELEMENT_NODE) {
            return getOwnerElement().lookupPrefix(namespaceURI);
          }
          return null;
        }
      default:
        {
          return null;
        }
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameNode(Node other) {
    // we do not use any wrapper so the answer is obvious
    return this == other;
  }

  /** {@inheritDoc} */
  @Override
  public void setTextContent(String textContent) throws DOMException {
    setNodeValue(textContent);
  }

  /** {@inheritDoc} */
  @Override
  public String getTextContent() throws DOMException {
    return getNodeValue(); // overriden in some subclasses
  }

  /** {@inheritDoc} */
  @Override
  public short compareDocumentPosition(Node other) throws DOMException {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public String getBaseURI() {
    return null;
  }
}
