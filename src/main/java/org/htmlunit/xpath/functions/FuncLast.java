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
package org.htmlunit.xpath.functions;

import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.axes.SubContextList;
import org.htmlunit.xpath.compiler.Compiler;
import org.htmlunit.xpath.objects.XNumber;
import org.htmlunit.xpath.objects.XObject;

/** Execute the Last() function. */
public class FuncLast extends Function {
  private boolean m_isTopLevel;

  /** {@inheritDoc} */
  @Override
  public void postCompileStep(final Compiler compiler) {
    m_isTopLevel = compiler.getLocationPathDepth() == -1;
  }

  /**
   * Get the position in the current context node list.
   *
   * @param xctxt non-null reference to XPath runtime context.
   * @return The number of nodes in the list.
   * @throws javax.xml.transform.TransformerException in case of error
   */
  public int getCountOfContextNodeList(final XPathContext xctxt)
      throws javax.xml.transform.TransformerException {

    // assert(null != m_contextNodeList, "m_contextNodeList must be non-null");
    // If we're in a predicate, then this will return non-null.
    final SubContextList iter = m_isTopLevel ? null : xctxt.getSubContextList();

    // System.out.println("iter: "+iter);
    if (null != iter) {
      return iter.getLastPos(xctxt);
    }

    return 0;
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {
    return new XNumber(getCountOfContextNodeList(xctxt));
  }
}
