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
package org.htmlunit.xpath.xml.dtm.ref;

import java.util.ArrayList;
import java.util.List;
import org.htmlunit.xpath.NodeSet;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * <code>DTMNodeProxy</code> presents a DOM Node API front-end to the DTM model.
 *
 * <p>It does _not_ attempt to address the "node identity" question; no effort is made to prevent
 * the creation of multiple proxies referring to a single DTM node. Users can create a mechanism for
 * managing this, or relinquish the use of "==" and use the .sameNodeAs() mechanism, which is under
 * consideration for future versions of the DOM.
 *
 * <p>DTMNodeProxy may be subclassed further to present specific DOM node types.
 *
 * @see org.w3c.dom
 */
public class DTMNodeProxy
    implements Node,
        Document,
        Text,
        Element,
        Attr,
        ProcessingInstruction,
        Comment,
        DocumentFragment {

  /** The DTM for this node. */
  public final DTM dtm_;

  /** The DTM node handle. */
  final int node_;

  /** The return value as Empty String. */
  private static final String EMPTYSTRING = "";

  /** The DOMImplementation object */
  static final DOMImplementation implementation = new DTMNodeProxyImplementation();

  /**
   * Create a DTMNodeProxy Node representing a specific Node in a DTM
   *
   * @param dtm The DTM Reference, must be non-null.
   * @param node The DTM node handle.
   */
  public DTMNodeProxy(final DTM dtm, final int node) {
    this.dtm_ = dtm;
    this.node_ = node;
  }

  /**
   * NON-DOM: Return the DTM node number
   *
   * @return The DTM node handle.
   */
  public final int getDTMNodeNumber() {
    return node_;
  }

  /**
   * Test for equality based on node number.
   *
   * @param node A DTM node proxy reference.
   * @return true if the given node has the same handle as this node.
   */
  public final boolean equals(final Node node) {
    try {
      final DTMNodeProxy dtmp = (DTMNodeProxy) node;

      // return (dtmp.node == this.node);
      // Patch attributed to Gary L Peskin <garyp@firstech.com>
      return (dtmp.node_ == this.node_) && (dtmp.dtm_ == this.dtm_);
    }
    catch (final ClassCastException cce) {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final boolean equals(final Object node) {

    try {

      // DTMNodeProxy dtmp = (DTMNodeProxy)node;
      // return (dtmp.node == this.node);
      // Patch attributed to Gary L Peskin <garyp@firstech.com>
      return equals((Node) node);
    }
    catch (final ClassCastException cce) {
      return false;
    }
  }

  /** {@inheritDoc} */
  @Override
  public final String getNodeName() {
    return dtm_.getNodeName(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final String getTarget() {
    return dtm_.getNodeName(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final String getLocalName() {
    return dtm_.getLocalName(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final String getPrefix() {
    return dtm_.getPrefix(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final void setPrefix(final String prefix) throws DOMException {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final String getNamespaceURI() {
    return dtm_.getNamespaceURI(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isSupported(final String feature, final String version) {
    return implementation.hasFeature(feature, version);
    // throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final String getNodeValue() throws DOMException {
    return dtm_.getNodeValue(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final void setNodeValue(final String nodeValue) throws DOMException {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final short getNodeType() {
    return dtm_.getNodeType(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final Node getParentNode() {

    if (getNodeType() == Node.ATTRIBUTE_NODE) {
      return null;
    }

    final int newnode = dtm_.getParent(node_);

    return (newnode == DTM.NULL) ? null : dtm_.getNode(newnode);
  }

  /** {@inheritDoc} */
  @Override
  public final NodeList getChildNodes() {

    // Annoyingly, AxisIterators do not currently implement DTMIterator, so
    // we can't just wap DTMNodeList around an Axis.CHILD iterator.
    // Instead, we've created a special-case operating mode for that object.
    return new DTMChildIterNodeList(dtm_, node_);
  }

  /** {@inheritDoc} */
  @Override
  public final Node getFirstChild() {

    final int newnode = dtm_.getFirstChild(node_);

    return (newnode == DTM.NULL) ? null : dtm_.getNode(newnode);
  }

  /** {@inheritDoc} */
  @Override
  public final Node getLastChild() {

    final int newnode = dtm_.getLastChild(node_);

    return (newnode == DTM.NULL) ? null : dtm_.getNode(newnode);
  }

  /** {@inheritDoc} */
  @Override
  public final Node getPreviousSibling() {

    final int newnode = dtm_.getPreviousSibling(node_);

    return (newnode == DTM.NULL) ? null : dtm_.getNode(newnode);
  }

  /** {@inheritDoc} */
  @Override
  public final Node getNextSibling() {

    // Attr's Next is defined at DTM level, but not at DOM level.
    if (dtm_.getNodeType(node_) == Node.ATTRIBUTE_NODE) {
      return null;
    }

    final int newnode = dtm_.getNextSibling(node_);

    return (newnode == DTM.NULL) ? null : dtm_.getNode(newnode);
  }

  /** {@inheritDoc} */
  @Override
  public final NamedNodeMap getAttributes() {

    return new DTMNamedNodeMap(dtm_, node_);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasAttribute(final String name) {
    return DTM.NULL != dtm_.getAttributeNode(node_, null, name);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasAttributeNS(final String namespaceURI, final String localName) {
    return DTM.NULL != dtm_.getAttributeNode(node_, namespaceURI, localName);
  }

  /** {@inheritDoc} */
  @Override
  public final Document getOwnerDocument() {
    // Note that this uses the DOM-compatable version of the call
    return (Document) (dtm_.getNode(dtm_.getOwnerDocument(node_)));
  }

  /** {@inheritDoc} */
  @Override
  public final Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Node removeChild(final Node oldChild) throws DOMException {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Node appendChild(final Node newChild) throws DOMException {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasChildNodes() {
    return DTM.NULL != dtm_.getFirstChild(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final Node cloneNode(final boolean deep) {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final DocumentType getDoctype() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public final DOMImplementation getImplementation() {
    return implementation;
  }

  /** {@inheritDoc} */
  @Override
  public final Element getDocumentElement() {
    final int dochandle = dtm_.getDocument();
    int elementhandle = DTM.NULL;
    for (int kidhandle = dtm_.getFirstChild(dochandle);
        kidhandle != DTM.NULL;
        kidhandle = dtm_.getNextSibling(kidhandle)) {
      switch (dtm_.getNodeType(kidhandle)) {
        case Node.ELEMENT_NODE:
          if (elementhandle != DTM.NULL) {
            elementhandle = DTM.NULL; // More than one; ill-formed.
            kidhandle = dtm_.getLastChild(dochandle); // End loop
          }
          else {
            elementhandle = kidhandle;
          }
          break;

          // These are harmless; document is still wellformed
        case Node.COMMENT_NODE:
        case Node.PROCESSING_INSTRUCTION_NODE:
        case Node.DOCUMENT_TYPE_NODE:
          break;

        default:
          elementhandle = DTM.NULL; // ill-formed
          kidhandle = dtm_.getLastChild(dochandle); // End loop
          break;
      }
    }
    if (elementhandle == DTM.NULL) {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }
    return (Element) (dtm_.getNode(elementhandle));
  }

  /** {@inheritDoc} */
  @Override
  public final Element createElement(final String tagName) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final DocumentFragment createDocumentFragment() {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Text createTextNode(final String data) {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Comment createComment(final String data) {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final CDATASection createCDATASection(final String data) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final ProcessingInstruction createProcessingInstruction(
      final String target, final String data) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Attr createAttribute(final String name) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final EntityReference createEntityReference(final String name) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final NodeList getElementsByTagName(final String tagname) {
    final List<Node> nodes = new ArrayList<>();
    final Node retNode = dtm_.getNode(node_);
    if (retNode != null) {
      final boolean isTagNameWildCard = "*".equals(tagname);
      if (DTM.ELEMENT_NODE == retNode.getNodeType()) {
        final NodeList nodeList = retNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
          traverseChildren(nodes, nodeList.item(i), tagname, isTagNameWildCard);
        }
      }
      else if (DTM.DOCUMENT_NODE == retNode.getNodeType()) {
        traverseChildren(nodes, dtm_.getNode(node_), tagname, isTagNameWildCard);
      }
    }
    return new NodeSet(nodes);
  }

  private void traverseChildren(
      final List<Node> listVector,
      final Node tempNode,
      final String tagname,
      final boolean isTagNameWildCard) {
    if (tempNode != null) {
      if (tempNode.getNodeType() == DTM.ELEMENT_NODE
          && (isTagNameWildCard || tempNode.getNodeName().equals(tagname))) {
        listVector.add(tempNode);
      }
      if (tempNode.hasChildNodes()) {
        final NodeList nodeList = tempNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
          traverseChildren(listVector, nodeList.item(i), tagname, isTagNameWildCard);
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final Node importNode(final Node importedNode, final boolean deep) throws DOMException {
    throw new DOMException(DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Element createElementNS(final String namespaceURI, final String qualifiedName)
      throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Attr createAttributeNS(final String namespaceURI, final String qualifiedName)
      throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final NodeList getElementsByTagNameNS(final String namespaceURI, final String localName) {
    final List<Node> nodes = new ArrayList<>();
    final Node retNode = dtm_.getNode(node_);
    if (retNode != null) {
      final boolean isNamespaceURIWildCard = "*".equals(namespaceURI);
      final boolean isLocalNameWildCard = "*".equals(localName);
      if (DTM.ELEMENT_NODE == retNode.getNodeType()) {
        final NodeList nodeList = retNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
          traverseChildren(
              nodes,
              nodeList.item(i),
              namespaceURI,
              localName,
              isNamespaceURIWildCard,
              isLocalNameWildCard);
        }
      }
      else if (DTM.DOCUMENT_NODE == retNode.getNodeType()) {
        traverseChildren(
            nodes,
            dtm_.getNode(node_),
            namespaceURI,
            localName,
            isNamespaceURIWildCard,
            isLocalNameWildCard);
      }
    }
    return new NodeSet(nodes);
  }

  /**
   * @param listVector
   * @param tempNode
   * @param namespaceURI
   * @param localname
   * @param isNamespaceURIWildCard
   * @param isLocalNameWildCard
   *     <p>Private method to be used for recursive iterations to obtain elements by tag name and
   *     namespaceURI.
   */
  private void traverseChildren(
      final List<Node> listVector,
      final Node tempNode,
      final String namespaceURI,
      final String localname,
      final boolean isNamespaceURIWildCard,
      final boolean isLocalNameWildCard) {
    if (tempNode == null) {
    }
    else {
      if (tempNode.getNodeType() == DTM.ELEMENT_NODE
          && (isLocalNameWildCard || tempNode.getLocalName().equals(localname))) {
        final String nsURI = tempNode.getNamespaceURI();
        if ((namespaceURI == null && nsURI == null)
            || isNamespaceURIWildCard
            || (namespaceURI != null && namespaceURI.equals(nsURI))) {
          listVector.add(tempNode);
        }
      }
      if (tempNode.hasChildNodes()) {
        final NodeList nl = tempNode.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
          traverseChildren(
              listVector,
              nl.item(i),
              namespaceURI,
              localname,
              isNamespaceURIWildCard,
              isLocalNameWildCard);
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final Element getElementById(final String elementId) {
    return (Element) dtm_.getNode(dtm_.getElementById(elementId));
  }

  /** {@inheritDoc} */
  @Override
  public final Text splitText(final int offset) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final String getData() throws DOMException {
    return dtm_.getNodeValue(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final void setData(final String data) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final int getLength() {
    // %OPT% This should do something smarter?
    return dtm_.getNodeValue(node_).length();
  }

  /** {@inheritDoc} */
  @Override
  public final String substringData(final int offset, final int count) throws DOMException {
    return getData().substring(offset, offset + count);
  }

  /** {@inheritDoc} */
  @Override
  public final void appendData(final String arg) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final void insertData(final int offset, final String arg) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final void deleteData(final int offset, final int count) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final void replaceData(final int offset, final int count, final String arg)
      throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final String getTagName() {
    return dtm_.getNodeName(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final String getAttribute(final String name) {
    final DTMNamedNodeMap map = new DTMNamedNodeMap(dtm_, node_);
    final Node n = map.getNamedItem(name);
    return (null == n) ? EMPTYSTRING : n.getNodeValue();
  }

  /** {@inheritDoc} */
  @Override
  public final void setAttribute(final String name, final String value) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final void removeAttribute(final String name) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Attr getAttributeNode(final String name) {

    final DTMNamedNodeMap map = new DTMNamedNodeMap(dtm_, node_);
    return (Attr) map.getNamedItem(name);
  }

  /** {@inheritDoc} */
  @Override
  public final Attr setAttributeNode(final Attr newAttr) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasAttributes() {
    return DTM.NULL != dtm_.getFirstAttribute(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final void normalize() {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final String getAttributeNS(final String namespaceURI, final String localName) {
    Node retNode = null;
    final int n = dtm_.getAttributeNode(node_, namespaceURI, localName);
    if (n != DTM.NULL) {
      retNode = dtm_.getNode(n);
    }
    return (null == retNode) ? EMPTYSTRING : retNode.getNodeValue();
  }

  /** {@inheritDoc} */
  @Override
  public final void setAttributeNS(
      final String namespaceURI, final String qualifiedName, final String value)
      throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final void removeAttributeNS(final String namespaceURI, final String localName)
      throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Attr getAttributeNodeNS(final String namespaceURI, final String localName) {
    Attr retAttr = null;
    final int n = dtm_.getAttributeNode(node_, namespaceURI, localName);
    if (n != DTM.NULL) {
      retAttr = (Attr) dtm_.getNode(n);
    }
    return retAttr;
  }

  /** {@inheritDoc} */
  @Override
  public final Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final String getName() {
    return dtm_.getNodeName(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean getSpecified() {
    // We really don't know which attributes might have come from the
    // source document versus from the DTD. Treat them all as having
    // been provided by the user.
    // %REVIEW% if/when we become aware of DTDs/schemae.
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public final String getValue() {
    return dtm_.getNodeValue(node_);
  }

  /** {@inheritDoc} */
  @Override
  public final void setValue(final String value) {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public final Element getOwnerElement() {
    if (getNodeType() != Node.ATTRIBUTE_NODE) {
      return null;
    }
    // In XPath and DTM data models, unlike DOM, an Attr's parent is its
    // owner element.
    final int newnode = dtm_.getParent(node_);
    return (newnode == DTM.NULL) ? null : (Element) (dtm_.getNode(newnode));
  }

  /** {@inheritDoc} */
  @Override
  public Node adoptNode(final Node source) throws DOMException {

    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public String getInputEncoding() {

    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public boolean getStrictErrorChecking() {

    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public void setStrictErrorChecking(final boolean strictErrorChecking) {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** Inner class to support getDOMImplementation. */
  static class DTMNodeProxyImplementation implements DOMImplementation {
    /** {@inheritDoc} */
    @Override
    public DocumentType createDocumentType(
        final String qualifiedName, final String publicId, final String systemId) {
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    /** {@inheritDoc} */
    @Override
    public Document createDocument(
        final String namespaceURI, final String qualfiedName, final DocumentType doctype) {
      // Could create a DTM... but why, when it'd have to be permanantly empty?
      throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasFeature(final String feature, final String version) {
      return ("CORE".equalsIgnoreCase(feature) || "XML".equalsIgnoreCase(feature))
          && ("1.0".equals(version) || "2.0".equals(version));
    }

    /** {@inheritDoc} */
    @Override
    public Object getFeature(final String feature, final String version) {
      // we don't have any alternate node, either this node does the job
      // or we don't have anything that does
      // return hasFeature(feature, version) ? this : null;
      return null; // PENDING
    }
  }

  /** {@inheritDoc} */
  @Override
  public Object setUserData(final String key, final Object data, final UserDataHandler handler) {
    return getOwnerDocument().setUserData(key, data, handler);
  }

  /** {@inheritDoc} */
  @Override
  public Object getUserData(final String key) {
    return getOwnerDocument().getUserData(key);
  }

  /** {@inheritDoc} */
  @Override
  public Object getFeature(final String feature, final String version) {
    // we don't have any alternate node, either this node does the job
    // or we don't have anything that does
    return isSupported(feature, version) ? this : null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isEqualNode(final Node arg) {
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
    }
    else if (!getNodeName().equals(arg.getNodeName())) {
      return false;
    }

    if (getLocalName() == null) {
      if (arg.getLocalName() != null) {
        return false;
      }
    }
    else if (!getLocalName().equals(arg.getLocalName())) {
      return false;
    }

    if (getNamespaceURI() == null) {
      if (arg.getNamespaceURI() != null) {
        return false;
      }
    }
    else if (!getNamespaceURI().equals(arg.getNamespaceURI())) {
      return false;
    }

    if (getPrefix() == null) {
      if (arg.getPrefix() != null) {
        return false;
      }
    }
    else if (!getPrefix().equals(arg.getPrefix())) {
      return false;
    }

    if (getNodeValue() == null) {
      return arg.getNodeValue() == null;
    }
    return getNodeValue().equals(arg.getNodeValue());
  }

  /** {@inheritDoc} */
  @Override
  public String lookupNamespaceURI(final String specifiedPrefix) {
    final short type = this.getNodeType();
    switch (type) {
      case Node.ELEMENT_NODE: {
          String namespace = this.getNamespaceURI();
          final String prefix = this.getPrefix();
          if (namespace != null) {
            // REVISIT: is it possible that prefix is empty string?
            if (specifiedPrefix == null && prefix == specifiedPrefix) {
              // looking for default namespace
              return namespace;
            }
            else if (prefix != null && prefix.equals(specifiedPrefix)) {
              // non default namespace
              return namespace;
            }
          }
          if (this.hasAttributes()) {
            final NamedNodeMap map = this.getAttributes();
            final int length = map.getLength();
            for (int i = 0; i < length; i++) {
              final Node attr = map.item(i);
              final String attrPrefix = attr.getPrefix();
              final String value = attr.getNodeValue();
              namespace = attr.getNamespaceURI();
              if (namespace != null && namespace.equals("http://www.w3.org/2000/xmlns/")) {
                // at this point we are dealing with DOM Level 2 nodes only
                if (specifiedPrefix == null && attr.getNodeName().equals("xmlns")) {
                  // default namespace
                  return value;
                }
                else if (attrPrefix != null
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
      case Node.ATTRIBUTE_NODE: {
          if (this.getOwnerElement().getNodeType() == Node.ELEMENT_NODE) {
            return getOwnerElement().lookupNamespaceURI(specifiedPrefix);
          }
          return null;
        }
      default: {
          return null;
        }
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isDefaultNamespace(final String namespaceURI) {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public String lookupPrefix(final String namespaceURI) {

    // REVISIT: When Namespaces 1.1 comes out this may not be true
    // Prefix can't be bound to null namespace
    if (namespaceURI == null) {
      return null;
    }

    final short type = this.getNodeType();

    switch (type) {
      case Node.ENTITY_NODE:
      case Node.NOTATION_NODE:
      case Node.DOCUMENT_FRAGMENT_NODE:
      case Node.DOCUMENT_TYPE_NODE:
        // type is unknown
        return null;
      case Node.ATTRIBUTE_NODE: {
          if (this.getOwnerElement().getNodeType() == Node.ELEMENT_NODE) {
            return getOwnerElement().lookupPrefix(namespaceURI);
          }
          return null;
        }
      default: {
          return null;
        }
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isSameNode(final Node other) {
    // we do not use any wrapper so the answer is obvious
    return this == other;
  }

  /** {@inheritDoc} */
  @Override
  public void setTextContent(final String textContent) throws DOMException {
    setNodeValue(textContent);
  }

  /** {@inheritDoc} */
  @Override
  public String getTextContent() throws DOMException {
    return dtm_.getStringValue(node_).toString();
  }

  /** {@inheritDoc} */
  @Override
  public short compareDocumentPosition(final Node other) throws DOMException {
    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public String getBaseURI() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Node renameNode(final Node n, final String namespaceURI, final String name)
      throws DOMException {
    return n;
  }

  /** {@inheritDoc} */
  @Override
  public void normalizeDocument() {
  }

  /** {@inheritDoc} */
  @Override
  public DOMConfiguration getDomConfig() {
    return null;
  }

  protected String fDocumentURI;

  /** {@inheritDoc} */
  @Override
  public void setDocumentURI(final String documentURI) {

    fDocumentURI = documentURI;
  }

  /** {@inheritDoc} */
  @Override
  public String getDocumentURI() {
    return fDocumentURI;
  }

  /** {@inheritDoc} */
  @Override
  public Text replaceWholeText(final String content) throws DOMException {
    return null; // Pending
  }

  /** {@inheritDoc} */
  @Override
  public String getWholeText() {
    return null; // PENDING
  }

  /** {@inheritDoc} */
  @Override
  public boolean isElementContentWhitespace() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public void setIdAttribute(final String name, final boolean makeId) {
    // PENDING
  }

  /** {@inheritDoc} */
  @Override
  public void setIdAttributeNode(final Attr at, final boolean makeId) {
    // PENDING
  }

  /** {@inheritDoc} */
  @Override
  public void setIdAttributeNS(
      final String namespaceURI, final String localName, final boolean makeId) {
    // PENDING
  }

  /** {@inheritDoc} */
  @Override
  public TypeInfo getSchemaTypeInfo() {
    return null; // PENDING
  }

  /** {@inheritDoc} */
  @Override
  public boolean isId() {
    return false; // PENDING
  }

  /** {@inheritDoc} */
  @Override
  public String getXmlEncoding() {
    return null;
  }

  private boolean xmlStandalone;

  /** {@inheritDoc} */
  @Override
  public boolean getXmlStandalone() {
    return xmlStandalone;
  }

  /** {@inheritDoc} */
  @Override
  public void setXmlStandalone(final boolean xmlStandalone) throws DOMException {
    this.xmlStandalone = xmlStandalone;
  }

  private String xmlVersion;

  /** {@inheritDoc} */
  @Override
  public String getXmlVersion() {
    return xmlVersion;
  }

  /** {@inheritDoc} */
  @Override
  public void setXmlVersion(final String xmlVersion) throws DOMException {
    this.xmlVersion = xmlVersion;
  }
}
