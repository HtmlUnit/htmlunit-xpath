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
 * Copyright (c) 2002 World Wide Web Consortium,
 * (Massachusetts Institute of Technology, Institut National de
 * Recherche en Informatique et en Automatique, Keio University). All
 * Rights Reserved. This program is distributed under the W3C's Software
 * Intellectual Property License. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE.
 * See W3C License http://www.w3.org/Consortium/Legal/ for more details.
 */

package net.sourceforge.htmlunit.xpath.domapi;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 *
 * A new exception to add support for DOM Level 3 XPath API.
 * This class is needed to throw a org.w3c.dom.DOMException with proper error code in
 * createExpression method of XPathEvaluatorImpl (a DOM Level 3 class).
 *
 * This class extends TransformerException because the error message includes information
 * about where the XPath problem is in the stylesheet as well as the XPath expression itself.
 *
 * @xsl.usage internal
 */
final public class XPathStylesheetDOM3Exception extends TransformerException {
  public XPathStylesheetDOM3Exception(String msg, SourceLocator arg1)
  {
    super(msg, arg1);
  }
}
