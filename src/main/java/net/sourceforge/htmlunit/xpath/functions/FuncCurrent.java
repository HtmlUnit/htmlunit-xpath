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
package net.sourceforge.htmlunit.xpath.functions;

import net.sourceforge.htmlunit.xpath.XPathContext;
import net.sourceforge.htmlunit.xpath.axes.LocPathIterator;
import net.sourceforge.htmlunit.xpath.axes.PredicatedNodeTest;
import net.sourceforge.htmlunit.xpath.axes.SubContextList;
import net.sourceforge.htmlunit.xpath.objects.XNodeSet;
import net.sourceforge.htmlunit.xpath.objects.XObject;
import net.sourceforge.htmlunit.xpath.patterns.StepPattern;
import net.sourceforge.htmlunit.xpath.res.XSLMessages;
import net.sourceforge.htmlunit.xpath.res.XSLTErrorResources;
import net.sourceforge.htmlunit.xpath.xml.dtm.DTM;


/**
 * Execute the current() function.
 * @xsl.usage advanced
 */
public class FuncCurrent extends Function
{
    static final long serialVersionUID = 5715316804877715008L;

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
   
    SubContextList subContextList = xctxt.getCurrentNodeList();
    int currentNode = DTM.NULL;

    if (null != subContextList) {
        if (subContextList instanceof PredicatedNodeTest) {
            LocPathIterator iter = ((PredicatedNodeTest)subContextList)
                                                          .getLocPathIterator();
            currentNode = iter.getCurrentContextNode();
         } else if(subContextList instanceof StepPattern) {
           throw new RuntimeException(XSLMessages.createMessage(
              XSLTErrorResources.ER_PROCESSOR_ERROR,null));
         }
    } else {
        // not predicate => ContextNode == CurrentNode
        currentNode = xctxt.getContextNode();
    }
    return new XNodeSet(currentNode, xctxt.getDTMManager());
  }
  
  /**
   * No arguments to process, so this does nothing.
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    // no-op
  }

}
