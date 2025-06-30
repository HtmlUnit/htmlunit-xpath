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
package org.htmlunit.xpath.xml.dtm.ref;

import org.w3c.dom.Node;

/**
 * <code>DTMNodeList</code> gives us an implementation of the DOM's NodeList interface wrapped
 * around a DTM Iterator. The author considers this something of an abominations, since NodeList was
 * not intended to be a general purpose "list of nodes" API and is generally considered by the DOM
 * WG to have be a mistake... but I'm told that some of the XPath/XSLT folks say they must have this
 * solution.
 *
 * <p>Please note that this is not necessarily equivlaent to a DOM NodeList operating over the same
 * document. In particular:
 *
 * <ul>
 *   <li>If there are several Text nodes in logical succession (ie, across CDATASection and
 *       EntityReference boundaries), we will return only the first; the caller is responsible for
 *       stepping through them. (%REVIEW% Provide a convenience routine here to assist, pending
 *       proposed DOM Level 3 getAdjacentText() operation?)
 *   <li>Since the whole XPath/XSLT architecture assumes that the source document is not altered
 *       while we're working with it, we do not promise to implement the DOM NodeList's "live view"
 *       response to document mutation.
 * </ul>
 *
 * <p>State: In progress!!
 */
public class DTMNodeListBase implements org.w3c.dom.NodeList {
  public DTMNodeListBase() {
  }

  /** {@inheritDoc} */
  @Override
  public Node item(final int index) {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public int getLength() {
    return 0;
  }
}
