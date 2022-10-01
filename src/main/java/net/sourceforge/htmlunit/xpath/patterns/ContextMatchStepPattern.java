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
import net.sourceforge.htmlunit.xpath.axes.WalkerFactory;
import net.sourceforge.htmlunit.xpath.objects.XObject;
import net.sourceforge.htmlunit.xpath.xml.dtm.Axis;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMAxisTraverser;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTMFilter;

/** Special context node pattern matcher. */
public class ContextMatchStepPattern extends StepPattern {

  /** Construct a ContextMatchStepPattern. */
  public ContextMatchStepPattern(int axis, int paxis) {
    super(DTMFilter.SHOW_ALL, axis, paxis);
  }

  /**
   * Execute this pattern step, including predicates.
   *
   * @param xctxt XPath runtime context.
   * @return {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NODETEST}, {@link
   *     net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NONE}, {@link
   *     net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NSWILD}, {@link
   *     net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_QNAME}, or {@link
   *     net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_OTHER}.
   * @throws javax.xml.transform.TransformerException
   */
  @Override
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException {

    if (xctxt.getIteratorRoot() == xctxt.getCurrentNode()) {
      return getStaticScore();
    }
    return SCORE_NONE;
  }

  /**
   * Execute the match pattern step relative to another step.
   *
   * @param xctxt The XPath runtime context. NEEDSDOC @param prevStep
   * @return {@link net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NODETEST}, {@link
   *     net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NONE}, {@link
   *     net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_NSWILD}, {@link
   *     net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_QNAME}, or {@link
   *     net.sourceforge.htmlunit.xpath.patterns.NodeTest#SCORE_OTHER}.
   * @throws javax.xml.transform.TransformerException
   */
  public XObject executeRelativePathPattern(XPathContext xctxt, StepPattern prevStep)
      throws javax.xml.transform.TransformerException {

    XObject score = NodeTest.SCORE_NONE;
    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);

    if (null != dtm) {
      DTMAxisTraverser traverser;

      int axis = m_axis;

      boolean needToTraverseAttrs = WalkerFactory.isDownwardAxisOfMany(axis);
      boolean iterRootIsAttr = dtm.getNodeType(xctxt.getIteratorRoot()) == DTM.ATTRIBUTE_NODE;

      if ((Axis.PRECEDING == axis) && iterRootIsAttr) {
        axis = Axis.PRECEDINGANDANCESTOR;
      }

      traverser = dtm.getAxisTraverser(axis);

      for (int relative = traverser.first(context);
          DTM.NULL != relative;
          relative = traverser.next(context, relative)) {
        try {
          xctxt.pushCurrentNode(relative);

          score = execute(xctxt);

          if (score != NodeTest.SCORE_NONE) {
            if (executePredicates(xctxt, dtm, context)) return score;

            score = NodeTest.SCORE_NONE;
          }

          if (needToTraverseAttrs
              && iterRootIsAttr
              && (DTM.ELEMENT_NODE == dtm.getNodeType(relative))) {
            int xaxis = Axis.ATTRIBUTE;
            for (int i = 0; i < 2; i++) {
              DTMAxisTraverser atraverser = dtm.getAxisTraverser(xaxis);

              for (int arelative = atraverser.first(relative);
                  DTM.NULL != arelative;
                  arelative = atraverser.next(relative, arelative)) {
                try {
                  xctxt.pushCurrentNode(arelative);

                  score = execute(xctxt);

                  if (score != NodeTest.SCORE_NONE) {
                    if (score != NodeTest.SCORE_NONE) return score;
                  }
                } finally {
                  xctxt.popCurrentNode();
                }
              }
              xaxis = Axis.NAMESPACE;
            }
          }

        } finally {
          xctxt.popCurrentNode();
        }
      }
    }

    return score;
  }
}
