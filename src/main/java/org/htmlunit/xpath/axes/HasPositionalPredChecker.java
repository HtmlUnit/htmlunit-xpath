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
package org.htmlunit.xpath.axes;

import org.htmlunit.xpath.Expression;
import org.htmlunit.xpath.XPathVisitor;
import org.htmlunit.xpath.functions.FuncLast;
import org.htmlunit.xpath.functions.FuncPosition;
import org.htmlunit.xpath.functions.Function;
import org.htmlunit.xpath.objects.XNumber;
import org.htmlunit.xpath.operations.Div;
import org.htmlunit.xpath.operations.Minus;
import org.htmlunit.xpath.operations.Mod;
import org.htmlunit.xpath.operations.Mult;
import org.htmlunit.xpath.operations.Plus;

public class HasPositionalPredChecker extends XPathVisitor {
  private boolean m_hasPositionalPred = false;
  private int m_predDepth = 0;

  /**
   * Process the LocPathIterator to see if it contains variables or functions that may make it
   * context dependent.
   *
   * @param path LocPathIterator that is assumed to be absolute, but needs checking.
   * @return true if the path is confirmed to be absolute, false if it may contain context
   *     dependencies.
   */
  public static boolean check(final LocPathIterator path) {
    final HasPositionalPredChecker hppc = new HasPositionalPredChecker();
    path.callVisitors(hppc);
    return hppc.m_hasPositionalPred;
  }

  /** {@inheritDoc} */
  @Override
  public boolean visitFunction(final Function func) {
    if ((func instanceof FuncPosition) || (func instanceof FuncLast)) {
        m_hasPositionalPred = true;
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean visitPredicate(final Expression pred) {
    m_predDepth++;

    if (m_predDepth == 1) {
      if ((pred instanceof XNumber)
          || (pred instanceof Div)
          || (pred instanceof Plus)
          || (pred instanceof Minus)
          || (pred instanceof Mod)
          || (pred instanceof Mult)
          || (pred instanceof org.htmlunit.xpath.operations.Number)
          || (pred instanceof Function)) {
          m_hasPositionalPred = true;
      }
      else {
          pred.callVisitors(this);
      }
    }

    m_predDepth--;

    // Don't go have the caller go any further down the subtree.
    return false;
  }
}
