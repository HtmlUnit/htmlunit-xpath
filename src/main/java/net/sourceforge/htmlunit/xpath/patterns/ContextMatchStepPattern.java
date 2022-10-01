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
package net.sourceforge.htmlunit.xpath.patterns;

import net.sourceforge.htmlunit.xpath.XPathContext;
import net.sourceforge.htmlunit.xpath.objects.XObject;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMFilter;

/** Special context node pattern matcher. */
public class ContextMatchStepPattern extends StepPattern {

  /**
   * Construct a ContextMatchStepPattern.
   *
   * @param axis the axis
   * @param paxis the p axis
   */
  public ContextMatchStepPattern(int axis, int paxis) {
    super(DTMFilter.SHOW_ALL, axis, paxis);
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

    if (xctxt.getIteratorRoot() == xctxt.getCurrentNode()) {
      return getStaticScore();
    }
    return SCORE_NONE;
  }
}
