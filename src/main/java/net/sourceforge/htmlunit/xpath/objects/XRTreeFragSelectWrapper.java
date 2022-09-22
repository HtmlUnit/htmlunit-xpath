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
package net.sourceforge.htmlunit.xpath.objects;

import net.sourceforge.htmlunit.xpath.Expression;
import net.sourceforge.htmlunit.xpath.XPathContext;
import net.sourceforge.htmlunit.xpath.res.XPATHErrorResources;
import net.sourceforge.htmlunit.xpath.res.XSLMessages;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMIterator;
import net.sourceforge.htmlunit.xpath.xml.utils.XMLString;

/** This class makes an select statement act like an result tree fragment. */
public class XRTreeFragSelectWrapper extends XRTreeFrag implements Cloneable {
  static final long serialVersionUID = -6526177905590461251L;

  public XRTreeFragSelectWrapper(Expression expr) {
    super(expr);
  }

  /**
   * For support of literal objects in xpaths.
   *
   * @param xctxt The XPath execution context.
   * @return the result of executing the select expression
   * @throws javax.xml.transform.TransformerException
   */
  @Override
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {
    XObject m_selected;
    m_selected = ((Expression) m_obj).execute(xctxt);
    m_selected.allowDetachToRelease(m_allowRelease);
    if (m_selected.getType() == CLASS_STRING) return m_selected;
    else return new XString(m_selected.str());
  }

  /**
   * Detaches the <code>DTMIterator</code> from the set which it iterated over, releasing any
   * computational resources and placing the iterator in the INVALID state. After <code>detach
   * </code> has been invoked, calls to <code>nextNode</code> or <code>previousNode</code> will
   * raise a runtime exception.
   *
   * <p>In general, detach should only be called once on the object.
   */
  @Override
  public void detach() {
    throw new RuntimeException(
        XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_DETACH_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
            null)); // "detach() not supported by XRTreeFragSelectWrapper!");
  }

  /**
   * Cast result object to a number.
   *
   * @return The result tree fragment as a number or NaN
   */
  @Override
  public double num() throws javax.xml.transform.TransformerException {

    throw new RuntimeException(
        XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_NUM_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
            null)); // "num() not supported by XRTreeFragSelectWrapper!");
  }

  /**
   * Cast result object to an XMLString.
   *
   * @return The document fragment node data or the empty string.
   */
  @Override
  public XMLString xstr() {
    throw new RuntimeException(
        XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_XSTR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
            null)); // "xstr() not supported by XRTreeFragSelectWrapper!");
  }

  /**
   * Cast result object to a string.
   *
   * @return The document fragment node data or the empty string.
   */
  @Override
  public String str() {
    throw new RuntimeException(
        XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_STR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
            null)); // "str() not supported by XRTreeFragSelectWrapper!");
  }

  /**
   * Tell what kind of class this is.
   *
   * @return the string type
   */
  @Override
  public int getType() {
    return CLASS_STRING;
  }

  /**
   * Cast result object to a DTMIterator.
   *
   * @return The document fragment as a DTMIterator
   */
  @Override
  public DTMIterator asNodeIterator() {
    throw new RuntimeException(
        XSLMessages.createXPATHMessage(
            XPATHErrorResources.ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER,
            null)); // "asNodeIterator() not supported by XRTreeFragSelectWrapper!");
  }
}
