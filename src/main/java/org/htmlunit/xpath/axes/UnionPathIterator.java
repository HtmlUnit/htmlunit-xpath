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
import org.htmlunit.xpath.compiler.Compiler;
import org.htmlunit.xpath.compiler.OpCodes;
import org.htmlunit.xpath.compiler.OpMap;
import org.htmlunit.xpath.xml.dtm.Axis;
import org.htmlunit.xpath.xml.dtm.DTM;
import org.htmlunit.xpath.xml.dtm.DTMIterator;

/**
 * This class extends NodeSetDTM, which implements DTMIterator, and fetches nodes one at a time in
 * document order based on a XPath <a href="http://www.w3.org/TR/xpath#NT-UnionExpr">UnionExpr</a>.
 * As each node is iterated via nextNode(), the node is also stored in the NodeVector, so that
 * previousNode() can easily be done.
 */
public class UnionPathIterator extends LocPathIterator
    implements Cloneable, DTMIterator, PathComponent {

  /** {@inheritDoc} */
  @Override
  public void setRoot(final int context, final Object environment) {
    super.setRoot(context, environment);

    try {
      if (null != exprs_) {
        final int n = exprs_.length;
        final DTMIterator[] newIters = new DTMIterator[n];

        for (int i = 0; i < n; i++) {
          final DTMIterator iter = exprs_[i].asIterator(m_execContext, context);
          newIters[i] = iter;
          iter.nextNode();
        }
        iterators_ = newIters;
      }
    } catch (final Exception e) {
      throw new org.htmlunit.xpath.xml.utils.WrappedRuntimeException(e);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void detach() {
    if (null != iterators_) {
      for (final DTMIterator iterator : iterators_) {
        iterator.detach();
      }
      iterators_ = null;
    }
  }

  /**
   * Create a UnionPathIterator object, including creation of location path iterators from the
   * opcode list, and call back into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating this expression.
   * @param opPos The position of this iterator in the opcode list from the compiler.
   * @throws javax.xml.transform.TransformerException if any
   */
  public UnionPathIterator(final Compiler compiler, int opPos)
      throws javax.xml.transform.TransformerException {

    super();

    opPos = OpMap.getFirstChildPos(opPos);

    loadLocationPaths(compiler, opPos, 0);
  }

  /**
   * This will return an iterator capable of handling the union of paths given.
   *
   * @param compiler The Compiler which is creating this expression.
   * @param opPos The position of this iterator in the opcode list from the compiler.
   * @return Object that is derived from LocPathIterator.
   * @throws javax.xml.transform.TransformerException if any
   */
  public static LocPathIterator createUnionIterator(final Compiler compiler, final int opPos)
      throws javax.xml.transform.TransformerException {
    // For the moment, I'm going to first create a full UnionPathIterator, and
    // then see if I can reduce it to a UnionChildIterator. It would obviously
    // be more effecient to just test for the conditions for a UnionChildIterator,
    // and then create that directly.
    final UnionPathIterator upi = new UnionPathIterator(compiler, opPos);
    final int nPaths = upi.exprs_.length;
    boolean isAllChildIterators = true;
    for (int i = 0; i < nPaths; i++) {
      final LocPathIterator lpi = upi.exprs_[i];

      if (lpi.getAxis() != Axis.CHILD) {
        isAllChildIterators = false;
        break;
      }
      // check for positional predicates or position function, which won't work.
      if (HasPositionalPredChecker.check(lpi)) {
        isAllChildIterators = false;
        break;
      }
    }
    if (isAllChildIterators) {
      final UnionChildIterator uci = new UnionChildIterator();

      for (int i = 0; i < nPaths; i++) {
        final PredicatedNodeTest lpi = upi.exprs_[i];
        // I could strip the lpi down to a pure PredicatedNodeTest, but
        // I don't think it's worth it. Note that the test can be used
        // as a static object... so it doesn't have to be cloned.
        uci.addNodeTest(lpi);
      }
      return uci;
    }
    return upi;
  }

  /** {@inheritDoc} */
  @Override
  public int getAnalysisBits() {
    int bits = 0;

    if (exprs_ != null) {
      for (final LocPathIterator expr : exprs_) {
        final int bit = expr.getAnalysisBits();
        bits |= bit;
      }
    }

    return bits;
  }

  /** {@inheritDoc} */
  @Override
  public Object clone() throws CloneNotSupportedException {

    final UnionPathIterator clone = (UnionPathIterator) super.clone();
    if (iterators_ != null) {
      final int n = iterators_.length;

      clone.iterators_ = new DTMIterator[n];

      for (int i = 0; i < n; i++) {
        clone.iterators_[i] = (DTMIterator) iterators_[i].clone();
      }
    }

    return clone;
  }

  /**
   * Create a new location path iterator.
   *
   * @param compiler The Compiler which is creating this expression.
   * @param opPos The position of this iterator in the
   * @return New location path iterator.
   * @throws javax.xml.transform.TransformerException if any
   */
  protected LocPathIterator createDTMIterator(final Compiler compiler, final int opPos)
      throws javax.xml.transform.TransformerException {
    return (LocPathIterator)
        WalkerFactory.newDTMIterator(compiler, opPos, compiler.getLocationPathDepth() <= 0);
  }

  /**
   * Initialize the location path iterators. Recursive.
   *
   * @param compiler The Compiler which is creating this expression.
   * @param opPos The position of this iterator in the opcode list from the compiler.
   * @param count The insert position of the iterator.
   * @throws javax.xml.transform.TransformerException if any
   */
  protected void loadLocationPaths(final Compiler compiler, final int opPos, final int count)
      throws javax.xml.transform.TransformerException {

    // TODO: Handle unwrapped FilterExpr
    final int steptype = compiler.getOp(opPos);

    if (steptype == OpCodes.OP_LOCATIONPATH) {
      loadLocationPaths(compiler, compiler.getNextOpPos(opPos), count + 1);

      exprs_[count] = createDTMIterator(compiler, opPos);
      exprs_[count].exprSetParent(this);
    } else {

      // Have to check for unwrapped functions, which the LocPathIterator
      // doesn't handle.
      switch (steptype) {
        case OpCodes.OP_VARIABLE:
        case OpCodes.OP_FUNCTION:
        case OpCodes.OP_GROUP:
          loadLocationPaths(compiler, compiler.getNextOpPos(opPos), count + 1);

          final WalkingIterator iter = new WalkingIterator(compiler.getNamespaceContext());
          iter.exprSetParent(this);

          if (compiler.getLocationPathDepth() <= 0) iter.setIsTopLevel(true);

          iter.m_firstWalker = new org.htmlunit.xpath.axes.FilterExprWalker(iter);

          iter.m_firstWalker.init(compiler, opPos, steptype);

          exprs_[count] = iter;
          break;
        default:
          exprs_ = new LocPathIterator[count];
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public int nextNode() {
    if (m_foundLast) return DTM.NULL;

    // Loop through the iterators getting the current fetched
    // node, and get the earliest occuring in document order
    int earliestNode = DTM.NULL;

    if (null != iterators_) {
      final int n = iterators_.length;
      int iteratorUsed = -1;

      for (int i = 0; i < n; i++) {
        final int node = iterators_[i].getCurrentNode();

        if (DTM.NULL == node) continue;
        else if (DTM.NULL == earliestNode) {
          iteratorUsed = i;
          earliestNode = node;
        } else {
          if (node == earliestNode) {

            // Found a duplicate, so skip past it.
            iterators_[i].nextNode();
          } else {
            final DTM dtm = getDTM(node);

            if (dtm.isNodeAfter(node, earliestNode)) {
              iteratorUsed = i;
              earliestNode = node;
            }
          }
        }
      }

      if (DTM.NULL != earliestNode) {
        iterators_[iteratorUsed].nextNode();

        incrementCurrentPos();
      } else m_foundLast = true;
    }

    m_lastFetched = earliestNode;

    return earliestNode;
  }

  /**
   * The location path iterators, one for each <a
   * href="http://www.w3.org/TR/xpath#NT-LocationPath">location path</a> contained in the union
   * expression.
   *
   * @serial
   */
  protected LocPathIterator[] exprs_;

  /**
   * The location path iterators, one for each <a
   * href="http://www.w3.org/TR/xpath#NT-LocationPath">location path</a> contained in the union
   * expression.
   *
   * @serial
   */
  protected DTMIterator[] iterators_;

  /** {@inheritDoc} */
  @Override
  public int getAxis() {
    // Could be smarter.
    return -1;
  }

  /** {@inheritDoc} */
  @Override
  public void callVisitors(final XPathVisitor visitor) {
    if (visitor.visitUnionPath()) {
      if (null != exprs_) {
        for (final LocPathIterator expr : exprs_) {
          expr.callVisitors(visitor);
        }
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean deepEquals(final Expression expr) {
    if (!super.deepEquals(expr)) return false;

    final UnionPathIterator upi = (UnionPathIterator) expr;

    if (null != exprs_) {
      final int n = exprs_.length;

      if ((null == upi.exprs_) || (upi.exprs_.length != n)) return false;

      for (int i = 0; i < n; i++) {
        if (!exprs_[i].deepEquals(upi.exprs_[i])) return false;
      }
    } else if (null != upi.exprs_) {
      return false;
    }

    return true;
  }
}
