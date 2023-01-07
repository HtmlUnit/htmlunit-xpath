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
package net.sourceforge.htmlunit.xpath.xml.dtm.ref;

import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMIterator;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

/**
 * <code>DTMNodeIterator</code> gives us an implementation of the DTMNodeIterator which returns DOM
 * nodes.
 *
 * <p>Please note that this is not necessarily equivlaent to a DOM NodeIterator operating over the
 * same document. In particular:
 *
 * <ul>
 *   <li>If there are several Text nodes in logical succession (ie, across CDATASection and
 *       EntityReference boundaries), we will return only the first; the caller is responsible for
 *       stepping through them. (%REVIEW% Provide a convenience routine here to assist, pending
 *       proposed DOM Level 3 getAdjacentText() operation?)
 *   <li>Since the whole XPath/XSLT architecture assumes that the source document is not altered
 *       while we're working with it, we do not promise to implement the DOM NodeIterator's
 *       "maintain current position" response to document mutation.
 *   <li>Since our design for XPath NodeIterators builds a stateful filter directly into the
 *       traversal object, getNodeFilter() is not supported.
 * </ul>
 *
 * <p>State: In progress!!
 */
public class DTMNodeIterator implements org.w3c.dom.traversal.NodeIterator {
  private final DTMIterator dtm_iter;
  private boolean valid = true;

  // ================================================================
  // Methods unique to this class

  /**
   * Public constructor: Wrap a DTMNodeIterator around an existing and preconfigured DTMIterator
   *
   * @param dtmIterator the iterator to be cloned
   */
  public DTMNodeIterator(DTMIterator dtmIterator) {
    try {
      dtm_iter = (DTMIterator) dtmIterator.clone();
    } catch (CloneNotSupportedException cnse) {
      throw new net.sourceforge.htmlunit.xpath.xml.utils.WrappedRuntimeException(cnse);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {
    // Theoretically, we could release dtm_iter at this point. But
    // some of the operations may still want to consult it even though
    // navigation is now invalid.
    valid = false;
  }

  /** {@inheritDoc} */
  @Override
  public boolean getExpandEntityReferences() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public NodeFilter getFilter() {
    throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "");
  }

  /** {@inheritDoc} */
  @Override
  public Node getRoot() {
    int handle = dtm_iter.getRoot();
    return dtm_iter.getDTM(handle).getNode(handle);
  }

  /** {@inheritDoc} */
  @Override
  public int getWhatToShow() {
    return dtm_iter.getWhatToShow();
  }

  /** {@inheritDoc} */
  @Override
  public Node nextNode() throws DOMException {
    if (!valid) throw new DOMException(DOMException.INVALID_STATE_ERR, "");

    int handle = dtm_iter.nextNode();
    if (handle == DTM.NULL) return null;
    return dtm_iter.getDTM(handle).getNode(handle);
  }

  /** {@inheritDoc} */
  @Override
  public Node previousNode() {
    if (!valid) throw new DOMException(DOMException.INVALID_STATE_ERR, "");

    int handle = dtm_iter.previousNode();
    if (handle == DTM.NULL) return null;
    return dtm_iter.getDTM(handle).getNode(handle);
  }
}
