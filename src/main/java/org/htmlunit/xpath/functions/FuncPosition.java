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

/** Execute the Position() function. */
public class FuncPosition extends Function {

  private boolean m_isTopLevel;

  /** {@inheritDoc} */
  @Override
  public void postCompileStep(final Compiler compiler) {
    m_isTopLevel = compiler.getLocationPathDepth() == -1;
  }

  /**
   * Get the position in the current context node list.
   *
   * @param xctxt Runtime XPath context.
   * @return The current position of the iteration in the context node list, or -1 if there is no
   *     active context node list.
   */
  public int getPositionInContextNodeList(final XPathContext xctxt) {

    // System.out.println("FuncPosition- entry");
    // If we're in a predicate, then this will return non-null.
    final SubContextList iter = m_isTopLevel ? null : xctxt.getSubContextList();

    if (null != iter) {
      return iter.getProximityPosition(xctxt);
    }

    // System.out.println("FuncPosition - out of guesses: -1");
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {
    final double pos = getPositionInContextNodeList(xctxt);

    return new XNumber(pos);
  }
}
