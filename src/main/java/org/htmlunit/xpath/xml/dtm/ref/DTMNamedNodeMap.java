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

import org.htmlunit.xpath.xml.dtm.DTM;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * DTMNamedNodeMap is a quickie (as opposed to quick) implementation of the DOM's NamedNodeMap
 * interface, intended to support DTMProxy's getAttributes() call.
 *
 * <p>***** Note: this does _not_ current attempt to cache any of the data; if you ask for attribute
 * 27 and then 28, you'll have to rescan the first 27. It should probably at least keep track of the
 * last one retrieved, and possibly buffer the whole array.
 *
 * <p>***** Also note that there's no fastpath for the by-name query; we search linearly until we
 * find it or fail to find it. Again, that could be optimized at some cost in object
 * creation/storage.
 */
public class DTMNamedNodeMap implements NamedNodeMap {

  /** The DTM for this node. */
  final DTM dtm;

  /** The DTM element handle. */
  final int element;

  /** The number of nodes in this map. */
  short m_count = -1;

  /**
   * Create a getAttributes NamedNodeMap for a given DTM element node
   *
   * @param dtm The DTM Reference, must be non-null.
   * @param element The DTM element handle.
   */
  public DTMNamedNodeMap(final DTM dtm, final int element) {
    this.dtm = dtm;
    this.element = element;
  }

  /** {@inheritDoc} */
  @Override
  public int getLength() {

    if (m_count == -1) {
      short count = 0;

      for (int n = dtm.getFirstAttribute(element); n != -1; n = dtm.getNextAttribute(n)) {
        ++count;
      }

      m_count = count;
    }

    return m_count;
  }

  /** {@inheritDoc} */
  @Override
  public Node getNamedItem(final String name) {

    for (int n = dtm.getFirstAttribute(element); n != DTM.NULL; n = dtm.getNextAttribute(n)) {
      if (dtm.getNodeName(n).equals(name)) return dtm.getNode(n);
    }

    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Node item(final int i) {

    int count = 0;

    for (int n = dtm.getFirstAttribute(element); n != -1; n = dtm.getNextAttribute(n)) {
      if (count == i) return dtm.getNode(n);
      else ++count;
    }

    return null;
  }

  /** {@inheritDoc} */
  @Override
  public Node setNamedItem(final Node newNode) {
    throw new DTMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public Node removeNamedItem(final String name) {
    throw new DTMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public Node getNamedItemNS(final String namespaceURI, final String localName) {
    Node retNode = null;
    for (int n = dtm.getFirstAttribute(element); n != DTM.NULL; n = dtm.getNextAttribute(n)) {
      if (localName.equals(dtm.getLocalName(n))) {
        final String nsURI = dtm.getNamespaceURI(n);
        if ((namespaceURI == null && nsURI == null)
            || (namespaceURI != null && namespaceURI.equals(nsURI))) {
          retNode = dtm.getNode(n);
          break;
        }
      }
    }
    return retNode;
  }

  /** {@inheritDoc} */
  @Override
  public Node setNamedItemNS(final Node arg) throws DOMException {
    throw new DTMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** {@inheritDoc} */
  @Override
  public Node removeNamedItemNS(final String namespaceURI, final String localName)
      throws DOMException {
    throw new DTMException(DOMException.NO_MODIFICATION_ALLOWED_ERR);
  }

  /** Simple implementation of DOMException. */
  public static class DTMException extends org.w3c.dom.DOMException {

    /**
     * Constructor DTMException
     *
     * @param code the code
     */
    public DTMException(final short code) {
      super(code, "");
    }
  }
}
