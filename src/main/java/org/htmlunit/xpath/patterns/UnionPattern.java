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
package org.htmlunit.xpath.patterns;

import org.htmlunit.xpath.Expression;
import org.htmlunit.xpath.XPathContext;
import org.htmlunit.xpath.XPathVisitor;
import org.htmlunit.xpath.objects.XObject;

/**
 * This class represents a union pattern, which can have multiple individual StepPattern patterns.
 */
public class UnionPattern extends Expression {

  /**
   * Array of the contained step patterns to be tested.
   *
   * @serial
   */
  private StepPattern[] patterns_;

  /** {@inheritDoc} */
  @Override
  public boolean canTraverseOutsideSubtree() {
    if (null != patterns_) {
      for (final StepPattern pattern : patterns_) {
        if (pattern.canTraverseOutsideSubtree()) {
            return true;
        }
      }
    }
    return false;
  }

  /**
   * Set the contained step patterns to be tested.
   *
   * @param patterns the contained step patterns to be tested.
   */
  public void setPatterns(final StepPattern[] patterns) {
    patterns_ = patterns;
    if (null != patterns) {
      for (final StepPattern pattern : patterns) {
        pattern.exprSetParent(this);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public XObject execute(final XPathContext xctxt) throws javax.xml.transform.TransformerException {

    XObject bestScore = null;
    for (final StepPattern pattern : patterns_) {
      final XObject score = pattern.execute(xctxt);

      if (score != NodeTest.SCORE_NONE) {
        if (null == bestScore) {
            bestScore = score;
        }
        else if (score.num() > bestScore.num()) {
            bestScore = score;
        }
      }
    }

    if (null == bestScore) {
      bestScore = NodeTest.SCORE_NONE;
    }

    return bestScore;
  }

  /** {@inheritDoc} */
  @Override
  public void callVisitors(final XPathVisitor visitor) {
    visitor.visitUnionPattern();
    if (null != patterns_) {
      for (final StepPattern pattern : patterns_) {
        pattern.callVisitors(visitor);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(final Expression expr) {
    if (!isSameClass(expr)) {
        return false;
    }

    final UnionPattern up = (UnionPattern) expr;

    if (null != patterns_) {
      final int n = patterns_.length;
      if ((null == up.patterns_) || (up.patterns_.length != n)) {
          return false;
      }

      for (int i = 0; i < n; i++) {
        if (!patterns_[i].deepEquals(up.patterns_[i])) {
            return false;
        }
      }
    }
    else if (up.patterns_ != null) {
        return false;
    }

    return true;
  }
}
