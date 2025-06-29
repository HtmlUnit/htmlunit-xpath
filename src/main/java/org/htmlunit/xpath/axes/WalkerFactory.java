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
package org.htmlunit.xpath.axes;

import org.htmlunit.xpath.Expression;
import org.htmlunit.xpath.compiler.Compiler;
import org.htmlunit.xpath.compiler.FunctionTable;
import org.htmlunit.xpath.compiler.OpCodes;
import org.htmlunit.xpath.compiler.OpMap;
import org.htmlunit.xpath.objects.XNumber;
import org.htmlunit.xpath.patterns.ContextMatchStepPattern;
import org.htmlunit.xpath.patterns.FunctionPattern;
import org.htmlunit.xpath.patterns.NodeTest;
import org.htmlunit.xpath.patterns.StepPattern;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;
import org.htmlunit.xpath.xml.dtm.Axis;
import org.htmlunit.xpath.xml.dtm.DTMFilter;
import org.htmlunit.xpath.xml.dtm.DTMIterator;

/**
 * This class is both a factory for XPath location path expressions, which are built from the opcode
 * map output, and an analysis engine for the location path expressions in order to provide
 * optimization hints.
 */
public class WalkerFactory {

  /**
   * This method is for building an array of possible levels where the target element(s) could be
   * found for a match.
   *
   * @param lpi The owning location path iterator object.
   * @param compiler non-null reference to compiler object that has processed the XPath operations
   *     into an opcode map.
   * @param stepOpCodePos The opcode position for the step.
   * @return non-null AxesWalker derivative.
   * @throws javax.xml.transform.TransformerException if any
   */
  static AxesWalker loadWalkers(
      final WalkingIterator lpi, final Compiler compiler, int stepOpCodePos)
      throws javax.xml.transform.TransformerException {

    int stepType;
    AxesWalker firstWalker = null;
    AxesWalker walker, prevWalker = null;

    final int analysis = analyze(compiler, stepOpCodePos);

    while (OpCodes.ENDOP != (stepType = compiler.getOp(stepOpCodePos))) {
      walker = createDefaultWalker(compiler, stepOpCodePos, lpi, analysis);

      walker.init(compiler, stepOpCodePos, stepType);
      walker.exprSetParent(lpi);

      // walker.setAnalysis(analysis);
      if (null == firstWalker) {
        firstWalker = walker;
      }
      else {
        prevWalker.setNextWalker(walker);
        walker.setPrevWalker(prevWalker);
      }

      prevWalker = walker;
      stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);

      if (stepOpCodePos < 0) {
          break;
      }
    }

    return firstWalker;
  }

  public static boolean isSet(final int analysis, final int bits) {
    return 0 != (analysis & bits);
  }

  public static void diagnoseIterator(
      final String name, final int analysis, final Compiler compiler) {
    System.out.println(
        compiler.toString()
            + ", "
            + name
            + ", "
            + Integer.toBinaryString(analysis)
            + ", "
            + getAnalysisString(analysis));
  }

  /**
   * Create a new LocPathIterator iterator. The exact type of iterator returned is based on an
   * analysis of the XPath operations.
   *
   * @param compiler non-null reference to compiler object that has processed the XPath operations
   *     into an opcode map.
   * @param opPos The position of the operation code for this itterator.
   * @return non-null reference to a LocPathIterator or derivative.
   * @throws javax.xml.transform.TransformerException if any
   */
  public static DTMIterator newDTMIterator(
      final Compiler compiler, final int opPos, final boolean isTopLevel)
      throws javax.xml.transform.TransformerException {

    final int firstStepPos = OpMap.getFirstChildPos(opPos);
    final int analysis = analyze(compiler, firstStepPos);
    final boolean isOneStep = isOneStep(analysis);
    final LocPathIterator iter;

    // Is the iteration a one-step attribute pattern (i.e. select="@foo")?
    if (isOneStep && walksSelfOnly(analysis) && isWild(analysis) && !hasPredicate(analysis)) {
      if (DEBUG_ITERATOR_CREATION) {
          diagnoseIterator("SelfIteratorNoPredicate", analysis, compiler);
      }

      // Then use a simple iteration of the attributes, with node test
      // and predicate testing.
      iter = new SelfIteratorNoPredicate(analysis);
    }
    // Is the iteration exactly one child step?
    else if (walksChildrenOnly(analysis) && isOneStep) {

      // Does the pattern specify *any* child with no predicate? (i.e.
      // select="child::node()".
      if (isWild(analysis) && !hasPredicate(analysis)) {
        if (DEBUG_ITERATOR_CREATION) {
            diagnoseIterator("ChildIterator", analysis, compiler);
        }

        // Use simple child iteration without any test.
        iter = new ChildIterator(analysis);
      }
      else {
        if (DEBUG_ITERATOR_CREATION) {
            diagnoseIterator("ChildTestIterator", analysis, compiler);
        }

        // Else use simple node test iteration with predicate test.
        iter = new ChildTestIterator(compiler, opPos, analysis);
      }
    }
    // Is the iteration a one-step attribute pattern (i.e. select="@foo")?
    else if (isOneStep && walksAttributes(analysis)) {
      if (DEBUG_ITERATOR_CREATION) {
          diagnoseIterator("AttributeIterator", analysis, compiler);
      }

      // Then use a simple iteration of the attributes, with node test
      // and predicate testing.
      iter = new AttributeIterator(compiler, opPos, analysis);
    }
    else if (isOneStep && !walksFilteredList(analysis)) {
      if (!walksNamespaces(analysis)
          && (walksInDocOrder(analysis) || isSet(analysis, BIT_PARENT))) {
        if (false || DEBUG_ITERATOR_CREATION) {
          diagnoseIterator("OneStepIteratorForward", analysis, compiler);
        }

        // Then use a simple iteration of the attributes, with node test
        // and predicate testing.
        iter = new OneStepIteratorForward(compiler, opPos, analysis);
      }
      else {
        if (false || DEBUG_ITERATOR_CREATION) {
          diagnoseIterator("OneStepIterator", analysis, compiler);
        }

        // Then use a simple iteration of the attributes, with node test
        // and predicate testing.
        iter = new OneStepIterator(compiler, opPos, analysis);
      }
    }

    // Analysis of "//center":
    // bits: 1001000000001010000000000000011
    // count: 3
    // root
    // child:node()
    // BIT_DESCENDANT_OR_SELF
    // It's highly possible that we should have a seperate bit set for
    // "//foo" patterns.
    // For at least the time being, we can't optimize patterns like
    // "//table[3]", because this has to be analyzed as
    // "/descendant-or-self::node()/table[3]" in order for the indexes
    // to work right.
    else if (isOptimizableForDescendantIterator(compiler, firstStepPos)
    // && getStepCount(analysis) <= 3
    // && walksDescendants(analysis)
    // && walksSubtreeOnlyFromRootOrContext(analysis)
    ) {
      if (DEBUG_ITERATOR_CREATION) {
          diagnoseIterator("DescendantIterator", analysis, compiler);
      }

      iter = new DescendantIterator(compiler, opPos, analysis);
    }
    else {
      if (isNaturalDocOrder(compiler, firstStepPos, analysis)) {
        if (false || DEBUG_ITERATOR_CREATION) {
          diagnoseIterator("WalkingIterator", analysis, compiler);
        }

        iter = new WalkingIterator(compiler, opPos, analysis, true);
      }
      else {
        // if (DEBUG_ITERATOR_CREATION)
        // diagnoseIterator("MatchPatternIterator", analysis, compiler);
        //
        // return new MatchPatternIterator(compiler, opPos, analysis);
        if (DEBUG_ITERATOR_CREATION) {
            diagnoseIterator("WalkingIteratorSorted", analysis, compiler);
        }

        iter = new WalkingIteratorSorted(compiler, opPos, analysis);
      }
    }

    iter.setIsTopLevel(isTopLevel);

    return iter;
  }

  /**
   * Special purpose function to see if we can optimize the pattern for a DescendantIterator.
   *
   * @param compiler non-null reference to compiler object that has processed the XPath operations
   *     into an opcode map.
   * @param stepOpCodePos The opcode position for the step.
   * @return 32 bits as an integer that give information about the location path as a whole.
   */
  public static int getAxisFromStep(final Compiler compiler, final int stepOpCodePos) {

    final int stepType = compiler.getOp(stepOpCodePos);

    switch (stepType) {
      case OpCodes.FROM_FOLLOWING:
        return Axis.FOLLOWING;
      case OpCodes.FROM_FOLLOWING_SIBLINGS:
        return Axis.FOLLOWINGSIBLING;
      case OpCodes.FROM_PRECEDING:
        return Axis.PRECEDING;
      case OpCodes.FROM_PRECEDING_SIBLINGS:
        return Axis.PRECEDINGSIBLING;
      case OpCodes.FROM_PARENT:
        return Axis.PARENT;
      case OpCodes.FROM_NAMESPACE:
        return Axis.NAMESPACE;
      case OpCodes.FROM_ANCESTORS:
        return Axis.ANCESTOR;
      case OpCodes.FROM_ANCESTORS_OR_SELF:
        return Axis.ANCESTORORSELF;
      case OpCodes.FROM_ATTRIBUTES:
        return Axis.ATTRIBUTE;
      case OpCodes.FROM_ROOT:
        return Axis.ROOT;
      case OpCodes.FROM_CHILDREN:
        return Axis.CHILD;
      case OpCodes.FROM_DESCENDANTS_OR_SELF:
        return Axis.DESCENDANTORSELF;
      case OpCodes.FROM_DESCENDANTS:
        return Axis.DESCENDANT;
      case OpCodes.FROM_SELF:
        return Axis.SELF;
      case OpCodes.OP_FUNCTION:
      case OpCodes.OP_GROUP:
      case OpCodes.OP_VARIABLE:
        return Axis.FILTEREDLIST;
    }

    throw new RuntimeException(
        XPATHMessages.createXPATHMessage(
            XPATHErrorResources.ER_NULL_ERROR_HANDLER, new Object[] {Integer.toString(stepType)}));
  }

  /**
   * Get a corresponding BIT_XXX from an axis.
   *
   * @param axis One of Axis.ANCESTOR, etc.
   * @return One of BIT_ANCESTOR, etc.
   */
  public static int getAnalysisBitFromAxes(final int axis) {
    switch (axis) { // Generate new traverser
      case Axis.ANCESTOR:
        return BIT_ANCESTOR;
      case Axis.ANCESTORORSELF:
        return BIT_ANCESTOR_OR_SELF;
      case Axis.ATTRIBUTE:
        return BIT_ATTRIBUTE;
      case Axis.CHILD:
        return BIT_CHILD;
      case Axis.DESCENDANT:
        return BIT_DESCENDANT;
      case Axis.DESCENDANTORSELF:
        return BIT_DESCENDANT_OR_SELF;
      case Axis.FOLLOWING:
        return BIT_FOLLOWING;
      case Axis.FOLLOWINGSIBLING:
        return BIT_FOLLOWING_SIBLING;
      case Axis.NAMESPACE:
      case Axis.NAMESPACEDECLS:
        return BIT_NAMESPACE;
      case Axis.PARENT:
        return BIT_PARENT;
      case Axis.PRECEDING:
        return BIT_PRECEDING;
      case Axis.PRECEDINGSIBLING:
        return BIT_PRECEDING_SIBLING;
      case Axis.SELF:
        return BIT_SELF;
      case Axis.ALLFROMNODE:
        return BIT_DESCENDANT_OR_SELF;
        // case Axis.PRECEDINGANDANCESTOR :
      case Axis.DESCENDANTSFROMROOT:
      case Axis.ALL:
      case Axis.DESCENDANTSORSELFFROMROOT:
        return BIT_ANY_DESCENDANT_FROM_ROOT;
      case Axis.ROOT:
        return BIT_ROOT;
      case Axis.FILTEREDLIST:
        return BIT_FILTER;
      default:
        return BIT_FILTER;
    }
  }

  static boolean functionProximateOrContainsProximate(final Compiler compiler, int opPos) {
    final int endFunc = opPos + compiler.getOp(opPos + 1) - 1;
    opPos = OpMap.getFirstChildPos(opPos);
    final int funcID = compiler.getOp(opPos);
    // System.out.println("funcID: "+funcID);
    // System.out.println("opPos: "+opPos);
    // System.out.println("endFunc: "+endFunc);
    switch (funcID) {
      case FunctionTable.FUNC_LAST:
      case FunctionTable.FUNC_POSITION:
        return true;
      default:
        opPos++;
        for (int p = opPos; p < endFunc; p = compiler.getNextOpPos(p)) {
          final int innerExprOpPos = p + 2;
          final boolean prox = isProximateInnerExpr(compiler, innerExprOpPos);
          if (prox) {
              return true;
          }
        }
    }
    return false;
  }

  static boolean isProximateInnerExpr(final Compiler compiler, final int opPos) {
    final int op = compiler.getOp(opPos);
    final int innerExprOpPos = opPos + 2;
    switch (op) {
      case OpCodes.OP_ARGUMENT:
        if (isProximateInnerExpr(compiler, innerExprOpPos)) {
            return true;
        }
        break;
      case OpCodes.OP_VARIABLE:
      case OpCodes.OP_NUMBERLIT:
      case OpCodes.OP_LITERAL:
      case OpCodes.OP_LOCATIONPATH:
        break; // OK
      case OpCodes.OP_FUNCTION:
        boolean isProx = functionProximateOrContainsProximate(compiler, opPos);
        if (isProx) {
            return true;
        }
        break;
      case OpCodes.OP_GT:
      case OpCodes.OP_GTE:
      case OpCodes.OP_LT:
      case OpCodes.OP_LTE:
      case OpCodes.OP_EQUALS:
        final int leftPos = OpMap.getFirstChildPos(op);
        final int rightPos = compiler.getNextOpPos(leftPos);
        isProx = isProximateInnerExpr(compiler, leftPos);
        if (isProx) {
            return true;
        }
        isProx = isProximateInnerExpr(compiler, rightPos);
        if (isProx) {
            return true;
        }
        break;
      default:
        return true; // be conservative...
    }
    return false;
  }

  /** Tell if the predicates need to have proximity knowledge. */
  public static boolean mightBeProximate(
      final Compiler compiler, final int opPos, final int stepType)
      throws javax.xml.transform.TransformerException {

    final boolean mightBeProximate = false;

    switch (stepType) {
      case OpCodes.OP_VARIABLE:
      case OpCodes.OP_FUNCTION:
      case OpCodes.OP_GROUP:
        break;
      default:
    }

    int predPos = compiler.getFirstPredicateOpPos(opPos);

    while (OpCodes.OP_PREDICATE == compiler.getOp(predPos)) {
      final int innerExprOpPos = predPos + 2;
      final int predOp = compiler.getOp(innerExprOpPos);

      switch (predOp) {
        case OpCodes.OP_VARIABLE:
          return true; // Would need more smarts to tell if this could be a number or not!
        case OpCodes.OP_LOCATIONPATH:
          // OK.
          break;
        case OpCodes.OP_NUMBER:
        case OpCodes.OP_NUMBERLIT:
          return true; // that's all she wrote!
        case OpCodes.OP_FUNCTION:
          boolean isProx = functionProximateOrContainsProximate(compiler, innerExprOpPos);
          if (isProx) {
              return true;
          }
          break;
        case OpCodes.OP_GT:
        case OpCodes.OP_GTE:
        case OpCodes.OP_LT:
        case OpCodes.OP_LTE:
        case OpCodes.OP_EQUALS:
          final int leftPos = OpMap.getFirstChildPos(innerExprOpPos);
          final int rightPos = compiler.getNextOpPos(leftPos);
          isProx = isProximateInnerExpr(compiler, leftPos);
          if (isProx) {
              return true;
          }
          isProx = isProximateInnerExpr(compiler, rightPos);
          if (isProx) {
              return true;
          }
          break;
        default:
          return true; // be conservative...
      }

      predPos = compiler.getNextOpPos(predPos);
    }

    return mightBeProximate;
  }

  /**
   * Special purpose function to see if we can optimize the pattern for a DescendantIterator.
   *
   * @param compiler non-null reference to compiler object that has processed the XPath operations
   *     into an opcode map.
   * @param stepOpCodePos The opcode position for the step.
   * @return 32 bits as an integer that give information about the location path as a whole.
   * @throws javax.xml.transform.TransformerException if any
   */
  private static boolean isOptimizableForDescendantIterator(
      final Compiler compiler, int stepOpCodePos) throws javax.xml.transform.TransformerException {

    int stepType;
    int stepCount = 0;
    boolean foundDorDS = false;
    boolean foundSelf = false;
    boolean foundDS = false;

    int nodeTestType = OpCodes.NODETYPE_NODE;

    while (OpCodes.ENDOP != (stepType = compiler.getOp(stepOpCodePos))) {
      // The DescendantIterator can only do one node test. If there's more
      // than one, use another iterator.
      if (nodeTestType != OpCodes.NODETYPE_NODE && nodeTestType != OpCodes.NODETYPE_ROOT) {
        return false;
      }

      stepCount++;
      if (stepCount > 3) {
          return false;
      }

      final boolean mightBeProximate = mightBeProximate(compiler, stepOpCodePos, stepType);
      if (mightBeProximate) {
          return false;
      }

      switch (stepType) {
        case OpCodes.FROM_FOLLOWING:
        case OpCodes.FROM_FOLLOWING_SIBLINGS:
        case OpCodes.FROM_PRECEDING:
        case OpCodes.FROM_PRECEDING_SIBLINGS:
        case OpCodes.FROM_PARENT:
        case OpCodes.OP_VARIABLE:
        case OpCodes.OP_FUNCTION:
        case OpCodes.OP_GROUP:
        case OpCodes.FROM_NAMESPACE:
        case OpCodes.FROM_ANCESTORS:
        case OpCodes.FROM_ANCESTORS_OR_SELF:
        case OpCodes.FROM_ATTRIBUTES:
        case OpCodes.MATCH_ATTRIBUTE:
        case OpCodes.MATCH_ANY_ANCESTOR:
        case OpCodes.MATCH_IMMEDIATE_ANCESTOR:
          return false;
        case OpCodes.FROM_ROOT:
          if (1 != stepCount) {
              return false;
          }
          break;
        case OpCodes.FROM_CHILDREN:
          if (!foundDS && !(foundDorDS && foundSelf)) {
              return false;
          }
          break;
        case OpCodes.FROM_DESCENDANTS_OR_SELF:
          foundDS = true;
        case OpCodes.FROM_DESCENDANTS:
          if (3 == stepCount) {
              return false;
          }
          foundDorDS = true;
          break;
        case OpCodes.FROM_SELF:
          if (1 != stepCount) {
              return false;
          }
          foundSelf = true;
          break;
        default:
          throw new RuntimeException(
              XPATHMessages.createXPATHMessage(
                  XPATHErrorResources.ER_NULL_ERROR_HANDLER,
                  new Object[] {Integer.toString(stepType)}));
      }

      nodeTestType = compiler.getStepTestType(stepOpCodePos);

      final int nextStepOpCodePos = compiler.getNextStepPos(stepOpCodePos);

      if (nextStepOpCodePos < 0) {
          break;
      }

      if (OpCodes.ENDOP != compiler.getOp(nextStepOpCodePos)) {
        if (compiler.countPredicates(stepOpCodePos) > 0) {
          return false;
        }
      }

      stepOpCodePos = nextStepOpCodePos;
    }

    return true;
  }

  /**
   * Analyze the location path and return 32 bits that give information about the location path as a
   * whole. See the BIT_XXX constants for meaning about each of the bits.
   *
   * @param compiler non-null reference to compiler object that has processed the XPath operations
   *     into an opcode map.
   * @param stepOpCodePos The opcode position for the step.
   * @return 32 bits as an integer that give information about the location path as a whole.
   * @throws javax.xml.transform.TransformerException if any
   */
  private static int analyze(final Compiler compiler, int stepOpCodePos)
      throws javax.xml.transform.TransformerException {

    int stepType;
    int stepCount = 0;
    int analysisResult = 0x00000000; // 32 bits of analysis

    while (OpCodes.ENDOP != (stepType = compiler.getOp(stepOpCodePos))) {
      stepCount++;

      final boolean predAnalysis = analyzePredicate(compiler, stepOpCodePos, stepType);

      if (predAnalysis) {
          analysisResult |= BIT_PREDICATE;
      }

      switch (stepType) {
        case OpCodes.OP_VARIABLE:
        case OpCodes.OP_FUNCTION:
        case OpCodes.OP_GROUP:
          analysisResult |= BIT_FILTER;
          break;
        case OpCodes.FROM_ROOT:
          analysisResult |= BIT_ROOT;
          break;
        case OpCodes.FROM_ANCESTORS:
          analysisResult |= BIT_ANCESTOR;
          break;
        case OpCodes.FROM_ANCESTORS_OR_SELF:
          analysisResult |= BIT_ANCESTOR_OR_SELF;
          break;
        case OpCodes.FROM_ATTRIBUTES:
          analysisResult |= BIT_ATTRIBUTE;
          break;
        case OpCodes.FROM_NAMESPACE:
          analysisResult |= BIT_NAMESPACE;
          break;
        case OpCodes.FROM_CHILDREN:
          analysisResult |= BIT_CHILD;
          break;
        case OpCodes.FROM_DESCENDANTS:
          analysisResult |= BIT_DESCENDANT;
          break;
        case OpCodes.FROM_DESCENDANTS_OR_SELF:

          // Use a special bit to to make sure we get the right analysis of "//foo".
          if (2 == stepCount && BIT_ROOT == analysisResult) {
            analysisResult |= BIT_ANY_DESCENDANT_FROM_ROOT;
          }

          analysisResult |= BIT_DESCENDANT_OR_SELF;
          break;
        case OpCodes.FROM_FOLLOWING:
          analysisResult |= BIT_FOLLOWING;
          break;
        case OpCodes.FROM_FOLLOWING_SIBLINGS:
          analysisResult |= BIT_FOLLOWING_SIBLING;
          break;
        case OpCodes.FROM_PRECEDING:
          analysisResult |= BIT_PRECEDING;
          break;
        case OpCodes.FROM_PRECEDING_SIBLINGS:
          analysisResult |= BIT_PRECEDING_SIBLING;
          break;
        case OpCodes.FROM_PARENT:
          analysisResult |= BIT_PARENT;
          break;
        case OpCodes.FROM_SELF:
          analysisResult |= BIT_SELF;
          break;
        case OpCodes.MATCH_ATTRIBUTE:
          analysisResult |= BIT_MATCH_PATTERN | BIT_ATTRIBUTE;
          break;
        case OpCodes.MATCH_ANY_ANCESTOR:
          analysisResult |= BIT_MATCH_PATTERN | BIT_ANCESTOR;
          break;
        case OpCodes.MATCH_IMMEDIATE_ANCESTOR:
          analysisResult |= BIT_MATCH_PATTERN | BIT_PARENT;
          break;
        default:
          throw new RuntimeException(
              XPATHMessages.createXPATHMessage(
                  XPATHErrorResources.ER_NULL_ERROR_HANDLER,
                  new Object[] {Integer.toString(stepType)}));
      }

      if (OpCodes.NODETYPE_NODE == compiler.getOp(stepOpCodePos + 3)) { // child::node()
        analysisResult |= BIT_NODETEST_ANY;
      }

      stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);

      if (stepOpCodePos < 0) {
          break;
      }
    }

    analysisResult |= stepCount & BITS_COUNT;

    return analysisResult;
  }

  /**
   * Tell if the given axis goes downword. Bogus name, if you can think of a better one, please do
   * tell. This really has to do with inverting attribute axis.
   *
   * @param axis One of Axis.XXX.
   * @return true if the axis is not a child axis and does not go up from the axis root.
   */
  public static boolean isDownwardAxisOfMany(final int axis) {
    return Axis.DESCENDANTORSELF == axis
        || (Axis.DESCENDANT == axis)
        || (Axis.FOLLOWING == axis)
        // || (Axis.FOLLOWINGSIBLING == axis)
        || (Axis.PRECEDING == axis);
        // || (Axis.PRECEDINGSIBLING == axis)
  }

  /**
   * Read a <a href="http://www.w3.org/TR/xpath#location-paths">LocationPath</a> as a generalized
   * match pattern. What this means is that the LocationPath is read backwards, as a test on a given
   * node, to see if it matches the criteria of the selection, and ends up at the context node.
   * Essentially, this is a backwards query from a given node, to find the context node.
   *
   * <p>So, the selection "foo/daz[2]" is, in non-abreviated expanded syntax,
   * "self::node()/following-sibling::foo/child::daz[position()=2]". Taking this as a match pattern
   * for a probable node, it works out to "self::daz/parent::foo[child::daz[position()=2 and
   * isPrevStepNode()] precedingSibling::node()[isContextNodeOfLocationPath()]", adding magic
   * isPrevStepNode and isContextNodeOfLocationPath operations. Predicates in the location path have
   * to be executed by the following step, because they have to know the context of their execution.
   *
   * @param compiler The compiler that holds the syntax tree/op map to construct from.
   * @param stepOpCodePos The current op code position within the opmap.
   * @return A StepPattern object, which may contain relative StepPatterns.
   * @throws javax.xml.transform.TransformerException if any
   */
  static StepPattern loadSteps(final Compiler compiler, int stepOpCodePos)
      throws javax.xml.transform.TransformerException {
    if (DEBUG_PATTERN_CREATION) {
      System.out.println("================");
      System.out.println("loadSteps for: " + compiler.getPatternString());
    }
    StepPattern step = null;
    StepPattern firstStep = null, prevStep = null;
    analyze(compiler, stepOpCodePos);

    while (OpCodes.ENDOP != compiler.getOp(stepOpCodePos)) {
      step = createDefaultStepPattern(compiler, stepOpCodePos);

      if (null == firstStep) {
        firstStep = step;
      }
      else {
        // prevStep.setNextWalker(step);
        step.setRelativePathPattern(prevStep);
      }

      prevStep = step;
      stepOpCodePos = compiler.getNextStepPos(stepOpCodePos);

      if (stepOpCodePos < 0) {
          break;
      }
    }

    int axis = Axis.SELF;
    final int paxis = Axis.SELF;
    StepPattern tail = step;
    for (StepPattern pat = step; null != pat; pat = pat.getRelativePathPattern()) {
      final int nextAxis = pat.getAxis();
      // int nextPaxis = pat.getPredicateAxis();
      pat.setAxis(axis);

      // The predicate axis can't be moved!!! Test Axes103
      // pat.setPredicateAxis(paxis);

      // If we have an attribute or namespace axis that went up, then
      // it won't find the attribute in the inverse, since the select-to-match
      // axes are not invertable (an element is a parent of an attribute, but
      // and attribute is not a child of an element).
      // If we don't do the magic below, then "@*/ancestor-or-self::*" gets
      // inverted for match to "self::*/descendant-or-self::@*/parent::node()",
      // which obviously won't work.
      // So we will rewrite this as:
      // "self::*/descendant-or-self::*/attribute::*/parent::node()"
      // Child has to be rewritten a little differently:
      // select: "@*/parent::*"
      // inverted match: "self::*/child::@*/parent::node()"
      // rewrite: "self::*/attribute::*/parent::node()"
      // Axes that go down in the select, do not have to have special treatment
      // in the rewrite. The following inverted match will still not select
      // anything.
      // select: "@*/child::*"
      // inverted match: "self::*/parent::@*/parent::node()"
      // Lovely business, this.
      // -sb
      final int whatToShow = pat.getWhatToShow();
      if (whatToShow == DTMFilter.SHOW_ATTRIBUTE || whatToShow == DTMFilter.SHOW_NAMESPACE) {
        final int newAxis =
            (whatToShow == DTMFilter.SHOW_ATTRIBUTE) ? Axis.ATTRIBUTE : Axis.NAMESPACE;
        if (isDownwardAxisOfMany(axis)) {
          final StepPattern attrPat =
              new StepPattern(
                  whatToShow,
                  pat.getNamespace(),
                  pat.getLocalName(),
                  // newAxis, pat.getPredicateAxis);
                  newAxis); // don't care about the predicate axis
          final XNumber score = pat.getStaticScore();
          pat.setNamespace(null);
          pat.setLocalName(NodeTest.WILD);
          attrPat.setPredicates(pat.getPredicates());
          pat.setPredicates(null);
          pat.setWhatToShow(DTMFilter.SHOW_ELEMENT);
          final StepPattern rel = pat.getRelativePathPattern();
          pat.setRelativePathPattern(attrPat);
          attrPat.setRelativePathPattern(rel);
          attrPat.setStaticScore(score);

          // This is needed to inverse a following pattern, because of the
          // wacky Xalan rules for following from an attribute. See axes108.
          // By these rules, following from an attribute is not strictly
          // inverseable.
          if (Axis.PRECEDING == pat.getAxis()) {
              pat.setAxis(Axis.PRECEDINGANDANCESTOR);
          }
          else if (Axis.DESCENDANT == pat.getAxis()) {
              pat.setAxis(Axis.DESCENDANTORSELF);
          }

          pat = attrPat;
        }
        else if (Axis.CHILD == pat.getAxis()) {
          // In this case just change the axis.
          // pat.setWhatToShow(whatToShow);
          pat.setAxis(Axis.ATTRIBUTE);
        }
      }
      axis = nextAxis;
      // paxis = nextPaxis;
      tail = pat;
    }

    if (axis < Axis.ALL) {
      final StepPattern selfPattern = new ContextMatchStepPattern(axis, paxis);
      // We need to keep the new nodetest from affecting the score...
      final XNumber score = tail.getStaticScore();
      tail.setRelativePathPattern(selfPattern);
      tail.setStaticScore(score);
      selfPattern.setStaticScore(score);
    }

    if (DEBUG_PATTERN_CREATION) {
      System.out.println("Done loading steps: " + step.toString());

      System.out.println();
    }
    return step; // start from last pattern?? //firstStep;
  }

  /**
   * Create a StepPattern that is contained within a LocationPath.
   *
   * @param compiler The compiler that holds the syntax tree/op map to construct from.
   * @return the head of the list.
   * @throws javax.xml.transform.TransformerException if any
   */
  private static StepPattern createDefaultStepPattern(final Compiler compiler, final int opPos)
      throws javax.xml.transform.TransformerException {

    final int stepType = compiler.getOp(opPos);

    StepPattern ai = null;
    final int axis;

    switch (stepType) {
      case OpCodes.OP_VARIABLE:
      case OpCodes.OP_FUNCTION:
      case OpCodes.OP_GROUP:
        final Expression expr;

        switch (stepType) {
          case OpCodes.OP_VARIABLE:
          case OpCodes.OP_FUNCTION:
          case OpCodes.OP_GROUP:
            expr = compiler.compile(opPos);
            break;
          default:
            expr = compiler.compile(opPos + 2);
        }

        axis = Axis.FILTEREDLIST;
        ai = new FunctionPattern(expr, axis);
        break;
      case OpCodes.FROM_ROOT:
        axis = Axis.ROOT;
        ai = new StepPattern(DTMFilter.SHOW_DOCUMENT | DTMFilter.SHOW_DOCUMENT_FRAGMENT, axis);
        break;
      case OpCodes.FROM_ATTRIBUTES:
        axis = Axis.PARENT;
        break;
      case OpCodes.FROM_NAMESPACE:
        axis = Axis.PARENT;
        break;
      case OpCodes.FROM_ANCESTORS:
        axis = Axis.DESCENDANT;
        break;
      case OpCodes.FROM_CHILDREN:
        axis = Axis.PARENT;
        break;
      case OpCodes.FROM_ANCESTORS_OR_SELF:
        axis = Axis.DESCENDANTORSELF;
        break;
      case OpCodes.FROM_SELF:
        axis = Axis.SELF;
        break;
      case OpCodes.FROM_PARENT:
        axis = Axis.CHILD;
        break;
      case OpCodes.FROM_PRECEDING_SIBLINGS:
        axis = Axis.FOLLOWINGSIBLING;
        break;
      case OpCodes.FROM_PRECEDING:
        axis = Axis.FOLLOWING;
        break;
      case OpCodes.FROM_FOLLOWING_SIBLINGS:
        axis = Axis.PRECEDINGSIBLING;
        break;
      case OpCodes.FROM_FOLLOWING:
        axis = Axis.PRECEDING;
        break;
      case OpCodes.FROM_DESCENDANTS_OR_SELF:
        axis = Axis.ANCESTORORSELF;
        break;
      case OpCodes.FROM_DESCENDANTS:
        axis = Axis.ANCESTOR;
        break;
      default:
        throw new RuntimeException(
            XPATHMessages.createXPATHMessage(
                XPATHErrorResources.ER_NULL_ERROR_HANDLER,
                new Object[] {Integer.toString(stepType)}));
    }
    if (null == ai) {
      final int whatToShow = compiler.getWhatToShow(opPos); // %REVIEW%
      ai =
          new StepPattern(
              whatToShow, compiler.getStepNS(opPos), compiler.getStepLocalName(opPos), axis);
    }

    final int argLen = compiler.getFirstPredicateOpPos(opPos);

    ai.setPredicates(compiler.getCompiledPredicates(argLen));

    return ai;
  }

  /**
   * Analyze a step and give information about it's predicates. Right now this just returns true or
   * false if the step has a predicate.
   *
   * @param compiler non-null reference to compiler object that has processed the XPath operations
   *     into an opcode map.
   * @param opPos The opcode position for the step.
   * @param stepType The type of step, one of OP_GROUP, etc.
   * @return true if step has a predicate.
   * @throws javax.xml.transform.TransformerException if any
   */
  static boolean analyzePredicate(final Compiler compiler, final int opPos, final int stepType)
      throws javax.xml.transform.TransformerException {

    switch (stepType) {
      case OpCodes.OP_VARIABLE:
      case OpCodes.OP_FUNCTION:
      case OpCodes.OP_GROUP:
        break;
      default:
    }

    final int pos = compiler.getFirstPredicateOpPos(opPos);
    final int nPredicates = compiler.countPredicates(pos);

    return nPredicates > 0;
  }

  /**
   * Create the proper Walker from the axes type.
   *
   * @param compiler non-null reference to compiler object that has processed the XPath operations
   *     into an opcode map.
   * @param opPos The opcode position for the step.
   * @param lpi The owning location path iterator.
   * @param analysis 32 bits of analysis, from which the type of AxesWalker may be influenced.
   * @return non-null reference to AxesWalker derivative.
   * @throws RuntimeException if the input is bad.
   */
  private static AxesWalker createDefaultWalker(
      final Compiler compiler, final int opPos, final WalkingIterator lpi, final int analysis) {

    final AxesWalker ai;
    final int stepType = compiler.getOp(opPos);

    /*
     * System.out.println("0: "+compiler.getOp(opPos));
     * System.out.println("1: "+compiler.getOp(opPos+1));
     * System.out.println("2: "+compiler.getOp(opPos+2));
     * System.out.println("3: "+compiler.getOp(opPos+3));
     * System.out.println("4: "+compiler.getOp(opPos+4));
     * System.out.println("5: "+compiler.getOp(opPos+5));
     */
    boolean simpleInit = false;

    switch (stepType) {
      case OpCodes.OP_VARIABLE:
      case OpCodes.OP_FUNCTION:
      case OpCodes.OP_GROUP:
        if (DEBUG_WALKER_CREATION) {
          System.out.println("new walker:  FilterExprWalker: " + analysis + ", " + compiler);
        }

        ai = new FilterExprWalker(lpi);
        simpleInit = true;
        break;
      case OpCodes.FROM_ROOT:
        ai = new AxesWalker(lpi, Axis.ROOT);
        break;
      case OpCodes.FROM_ANCESTORS:
        ai = new ReverseAxesWalker(lpi, Axis.ANCESTOR);
        break;
      case OpCodes.FROM_ANCESTORS_OR_SELF:
        ai = new ReverseAxesWalker(lpi, Axis.ANCESTORORSELF);
        break;
      case OpCodes.FROM_ATTRIBUTES:
        ai = new AxesWalker(lpi, Axis.ATTRIBUTE);
        break;
      case OpCodes.FROM_NAMESPACE:
        ai = new AxesWalker(lpi, Axis.NAMESPACE);
        break;
      case OpCodes.FROM_CHILDREN:
        ai = new AxesWalker(lpi, Axis.CHILD);
        break;
      case OpCodes.FROM_DESCENDANTS:
        ai = new AxesWalker(lpi, Axis.DESCENDANT);
        break;
      case OpCodes.FROM_DESCENDANTS_OR_SELF:
        ai = new AxesWalker(lpi, Axis.DESCENDANTORSELF);
        break;
      case OpCodes.FROM_FOLLOWING:
        ai = new AxesWalker(lpi, Axis.FOLLOWING);
        break;
      case OpCodes.FROM_FOLLOWING_SIBLINGS:
        ai = new AxesWalker(lpi, Axis.FOLLOWINGSIBLING);
        break;
      case OpCodes.FROM_PRECEDING:
        ai = new ReverseAxesWalker(lpi, Axis.PRECEDING);
        break;
      case OpCodes.FROM_PRECEDING_SIBLINGS:
        ai = new ReverseAxesWalker(lpi, Axis.PRECEDINGSIBLING);
        break;
      case OpCodes.FROM_PARENT:
        ai = new ReverseAxesWalker(lpi, Axis.PARENT);
        break;
      case OpCodes.FROM_SELF:
        ai = new AxesWalker(lpi, Axis.SELF);
        break;
      default:
        throw new RuntimeException(
            XPATHMessages.createXPATHMessage(
                XPATHErrorResources.ER_NULL_ERROR_HANDLER,
                new Object[] {Integer.toString(stepType)}));
    }

    if (simpleInit) {
      ai.initNodeTest(DTMFilter.SHOW_ALL);
    }
    else {
      final int whatToShow = compiler.getWhatToShow(opPos);

      if ((0
              == (whatToShow
                  & (DTMFilter.SHOW_ATTRIBUTE
                      | DTMFilter.SHOW_NAMESPACE
                      | DTMFilter.SHOW_ELEMENT
                      | DTMFilter.SHOW_PROCESSING_INSTRUCTION)))
          || (whatToShow == DTMFilter.SHOW_ALL)) {
          ai.initNodeTest(whatToShow);
      }
      else {
        ai.initNodeTest(whatToShow, compiler.getStepNS(opPos), compiler.getStepLocalName(opPos));
      }
    }

    return ai;
  }

  public static String getAnalysisString(final int analysis) {
    final StringBuilder buf = new StringBuilder();
    buf.append("count: ").append(getStepCount(analysis)).append(" ");
    if ((analysis & BIT_NODETEST_ANY) != 0) {
      buf.append("NTANY|");
    }
    if ((analysis & BIT_PREDICATE) != 0) {
      buf.append("PRED|");
    }
    if ((analysis & BIT_ANCESTOR) != 0) {
      buf.append("ANC|");
    }
    if ((analysis & BIT_ANCESTOR_OR_SELF) != 0) {
      buf.append("ANCOS|");
    }
    if ((analysis & BIT_ATTRIBUTE) != 0) {
      buf.append("ATTR|");
    }
    if ((analysis & BIT_CHILD) != 0) {
      buf.append("CH|");
    }
    if ((analysis & BIT_DESCENDANT) != 0) {
      buf.append("DESC|");
    }
    if ((analysis & BIT_DESCENDANT_OR_SELF) != 0) {
      buf.append("DESCOS|");
    }
    if ((analysis & BIT_FOLLOWING) != 0) {
      buf.append("FOL|");
    }
    if ((analysis & BIT_FOLLOWING_SIBLING) != 0) {
      buf.append("FOLS|");
    }
    if ((analysis & BIT_NAMESPACE) != 0) {
      buf.append("NS|");
    }
    if ((analysis & BIT_PARENT) != 0) {
      buf.append("P|");
    }
    if ((analysis & BIT_PRECEDING) != 0) {
      buf.append("PREC|");
    }
    if ((analysis & BIT_PRECEDING_SIBLING) != 0) {
      buf.append("PRECS|");
    }
    if ((analysis & BIT_SELF) != 0) {
      buf.append(".|");
    }
    if ((analysis & BIT_FILTER) != 0) {
      buf.append("FLT|");
    }
    if ((analysis & BIT_ROOT) != 0) {
      buf.append("R|");
    }
    return buf.toString();
  }

  /** Set to true for diagnostics about walker creation */
  static final boolean DEBUG_PATTERN_CREATION = false;

  /** Set to true for diagnostics about walker creation */
  static final boolean DEBUG_WALKER_CREATION = false;

  /** Set to true for diagnostics about iterator creation */
  static final boolean DEBUG_ITERATOR_CREATION = false;

  public static boolean hasPredicate(final int analysis) {
    return 0 != (analysis & BIT_PREDICATE);
  }

  public static boolean isWild(final int analysis) {
    return 0 != (analysis & BIT_NODETEST_ANY);
  }

  public static boolean walksAttributes(final int analysis) {
    return 0 != (analysis & BIT_ATTRIBUTE);
  }

  public static boolean walksNamespaces(final int analysis) {
    return 0 != (analysis & BIT_NAMESPACE);
  }

  public static boolean walksChildren(final int analysis) {
    return 0 != (analysis & BIT_CHILD);
  }

  public static boolean walksDescendants(final int analysis) {
    return isSet(analysis, BIT_DESCENDANT | BIT_DESCENDANT_OR_SELF);
  }

  public static boolean walksSubtree(final int analysis) {
    return isSet(analysis, BIT_DESCENDANT | BIT_DESCENDANT_OR_SELF | BIT_CHILD);
  }

  public static boolean walksSubtreeOnlyMaybeAbsolute(final int analysis) {
    return walksSubtree(analysis)
        && !walksExtraNodes(analysis)
        && !walksUp(analysis)
        && !walksSideways(analysis);
  }

  public static boolean walksFilteredList(final int analysis) {
    return isSet(analysis, BIT_FILTER);
  }

  public static boolean walksInDocOrder(final int analysis) {
    return (walksSubtreeOnlyMaybeAbsolute(analysis)
            || walksExtraNodesOnly(analysis)
            || walksFollowingOnlyMaybeAbsolute(analysis))
        && !isSet(analysis, BIT_FILTER);
  }

  public static boolean walksFollowingOnlyMaybeAbsolute(final int analysis) {
    return isSet(analysis, BIT_SELF | BIT_FOLLOWING_SIBLING | BIT_FOLLOWING)
        && !walksSubtree(analysis)
        && !walksUp(analysis)
        && !walksSideways(analysis);
  }

  public static boolean walksUp(final int analysis) {
    return isSet(analysis, BIT_PARENT | BIT_ANCESTOR | BIT_ANCESTOR_OR_SELF);
  }

  public static boolean walksSideways(final int analysis) {
    return isSet(
        analysis, BIT_FOLLOWING | BIT_FOLLOWING_SIBLING | BIT_PRECEDING | BIT_PRECEDING_SIBLING);
  }

  public static boolean walksExtraNodes(final int analysis) {
    return isSet(analysis, BIT_NAMESPACE | BIT_ATTRIBUTE);
  }

  public static boolean walksExtraNodesOnly(final int analysis) {
    return walksExtraNodes(analysis)
        && !isSet(analysis, BIT_SELF)
        && !walksSubtree(analysis)
        && !walksUp(analysis)
        && !walksSideways(analysis)
        && !isAbsolute(analysis);
  }

  public static boolean isAbsolute(final int analysis) {
    return isSet(analysis, BIT_ROOT | BIT_FILTER);
  }

  public static boolean walksChildrenOnly(final int analysis) {
    return walksChildren(analysis)
        && !isSet(analysis, BIT_SELF)
        && !walksExtraNodes(analysis)
        && !walksDescendants(analysis)
        && !walksUp(analysis)
        && !walksSideways(analysis)
        && (!isAbsolute(analysis) || isSet(analysis, BIT_ROOT));
  }

  public static boolean walksChildrenAndExtraAndSelfOnly(final int analysis) {
    return walksChildren(analysis)
        && !walksDescendants(analysis)
        && !walksUp(analysis)
        && !walksSideways(analysis)
        && (!isAbsolute(analysis) || isSet(analysis, BIT_ROOT));
  }

  public static boolean walksDescendantsAndExtraAndSelfOnly(final int analysis) {
    return !walksChildren(analysis)
        && walksDescendants(analysis)
        && !walksUp(analysis)
        && !walksSideways(analysis)
        && (!isAbsolute(analysis) || isSet(analysis, BIT_ROOT));
  }

  public static boolean walksSelfOnly(final int analysis) {
    return isSet(analysis, BIT_SELF)
        && !walksSubtree(analysis)
        && !walksUp(analysis)
        && !walksSideways(analysis)
        && !isAbsolute(analysis);
  }

  public static boolean walksUpOnly(final int analysis) {
    return !walksSubtree(analysis)
        && walksUp(analysis)
        && !walksSideways(analysis)
        && !isAbsolute(analysis);
  }

  public static boolean walksDownOnly(final int analysis) {
    return walksSubtree(analysis)
        && !walksUp(analysis)
        && !walksSideways(analysis)
        && !isAbsolute(analysis);
  }

  public static boolean canSkipSubtrees(final int analysis) {
    return isSet(analysis, BIT_CHILD) | walksSideways(analysis);
  }

  public static boolean canCrissCross(final int analysis) {
    // This could be done faster. Coded for clarity.
    if (walksSelfOnly(analysis)) {
        return false;
    }
    else if (walksDownOnly(analysis) && !canSkipSubtrees(analysis)) {
        return false;
    }
    else if (walksChildrenAndExtraAndSelfOnly(analysis)) {
        return false;
    }
    else if (walksDescendantsAndExtraAndSelfOnly(analysis)) {
        return false;
    }
    else if (walksUpOnly(analysis)) {
        return false;
    }
    else if (walksExtraNodesOnly(analysis)) {
        return false;
    }
    else if (walksSubtree(analysis)
        && (walksSideways(analysis) || walksUp(analysis) || canSkipSubtrees(analysis))) {
        return true;
    }
    else {
        return false;
    }
  }

  /**
   * Tell if the pattern can be 'walked' with the iteration steps in natural document order, without
   * duplicates.
   *
   * @param compiler non-null reference to compiler object that has processed the XPath operations
   *     into an opcode map.
   * @param stepOpCodePos The opcode position for the step.
   * @param analysis The general analysis of the pattern.
   * @return true if the walk can be done in natural order.
   */
  private static boolean isNaturalDocOrder(
      final Compiler compiler, int stepOpCodePos, final int analysis) {

    // Namespaces can present some problems, so just punt if we're looking for
    // these.
    // The following, preceding, following-sibling, and preceding sibling can
    // be found in doc order if we get to this point, but if they occur
    // together, they produce
    // duplicates, so it's better for us to eliminate this case so we don't
    // have to check for duplicates during runtime if we're using a
    // WalkingIterator.
    if (canCrissCross(analysis)
        || isSet(analysis, BIT_NAMESPACE)
        || (isSet(analysis, BIT_FOLLOWING | BIT_FOLLOWING_SIBLING)
            && isSet(analysis, BIT_PRECEDING | BIT_PRECEDING_SIBLING))) {
        return false;
    }

    // OK, now we have to check for select="@*/axis::*" patterns, which
    // can also cause duplicates to happen. But select="axis*/@::*" patterns
    // are OK, as are select="@foo/axis::*" patterns.
    // Unfortunately, we can't do this just via the analysis bits.

    int stepType;
    boolean foundWildAttribute = false;

    // Steps that can traverse anything other than down a
    // subtree or that can produce duplicates when used in
    // combination are counted with this variable.
    int potentialDuplicateMakingStepCount = 0;

    while (OpCodes.ENDOP != (stepType = compiler.getOp(stepOpCodePos))) {
      switch (stepType) {
        case OpCodes.FROM_ATTRIBUTES:
        case OpCodes.MATCH_ATTRIBUTE:
          if (foundWildAttribute) {
            // Maybe not needed, but be safe.
            return false;
          }

          // This doesn't seem to work as a test for wild card. Hmph.
          // int nodeTestType = compiler.getStepTestType(stepOpCodePos);

          final String localName = compiler.getStepLocalName(stepOpCodePos);
          // System.err.println("localName: "+localName);
          if ("*".equals(localName)) {
            foundWildAttribute = true;
          }
          break;
        case OpCodes.FROM_FOLLOWING:
        case OpCodes.FROM_FOLLOWING_SIBLINGS:
        case OpCodes.FROM_PRECEDING:
        case OpCodes.FROM_PRECEDING_SIBLINGS:
        case OpCodes.FROM_PARENT:
        case OpCodes.OP_VARIABLE:
        case OpCodes.OP_FUNCTION:
        case OpCodes.OP_GROUP:
        case OpCodes.FROM_NAMESPACE:
        case OpCodes.FROM_ANCESTORS:
        case OpCodes.FROM_ANCESTORS_OR_SELF:
        case OpCodes.MATCH_ANY_ANCESTOR:
        case OpCodes.MATCH_IMMEDIATE_ANCESTOR:
        case OpCodes.FROM_DESCENDANTS_OR_SELF:
        case OpCodes.FROM_DESCENDANTS:
          if (potentialDuplicateMakingStepCount > 0) {
              return false;
          }
          potentialDuplicateMakingStepCount++;
        case OpCodes.FROM_ROOT:
        case OpCodes.FROM_CHILDREN:
        case OpCodes.FROM_SELF:
          if (foundWildAttribute) {
              return false;
          }
          break;
        default:
          throw new RuntimeException(
              XPATHMessages.createXPATHMessage(
                  XPATHErrorResources.ER_NULL_ERROR_HANDLER,
                  new Object[] {Integer.toString(stepType)}));
      }

      final int nextStepOpCodePos = compiler.getNextStepPos(stepOpCodePos);

      if (nextStepOpCodePos < 0) {
          break;
      }

      stepOpCodePos = nextStepOpCodePos;
    }

    return true;
  }

  public static boolean isOneStep(final int analysis) {
    return (analysis & BITS_COUNT) == 0x00000001;
  }

  public static int getStepCount(final int analysis) {
    return analysis & BITS_COUNT;
  }

  /**
   * First 8 bits are the number of top-level location steps. Hopefully there will never be more
   * that 255 location steps!!!
   */
  public static final int BITS_COUNT = 0x000000FF;

  /** Bit is on if the expression contains a top-level predicate. */
  public static final int BIT_PREDICATE = 0x00001000;

  /** Bit is on if any of the walkers contain an ancestor step. */
  public static final int BIT_ANCESTOR = 0x00001000 << 1;

  /** Bit is on if any of the walkers contain an ancestor-or-self step. */
  public static final int BIT_ANCESTOR_OR_SELF = 0x00001000 << 2;

  /** Bit is on if any of the walkers contain an attribute step. */
  public static final int BIT_ATTRIBUTE = 0x00001000 << 3;

  /** Bit is on if any of the walkers contain a child step. */
  public static final int BIT_CHILD = 0x00001000 << 4;

  /** Bit is on if any of the walkers contain a descendant step. */
  public static final int BIT_DESCENDANT = 0x00001000 << 5;

  /** Bit is on if any of the walkers contain a descendant-or-self step. */
  public static final int BIT_DESCENDANT_OR_SELF = 0x00001000 << 6;

  /** Bit is on if any of the walkers contain a following step. */
  public static final int BIT_FOLLOWING = 0x00001000 << 7;

  /** Bit is on if any of the walkers contain a following-sibiling step. */
  public static final int BIT_FOLLOWING_SIBLING = 0x00001000 << 8;

  /** Bit is on if any of the walkers contain a namespace step. */
  public static final int BIT_NAMESPACE = 0x00001000 << 9;

  /** Bit is on if any of the walkers contain a parent step. */
  public static final int BIT_PARENT = 0x00001000 << 10;

  /** Bit is on if any of the walkers contain a preceding step. */
  public static final int BIT_PRECEDING = 0x00001000 << 11;

  /** Bit is on if any of the walkers contain a preceding-sibling step. */
  public static final int BIT_PRECEDING_SIBLING = 0x00001000 << 12;

  /** Bit is on if any of the walkers contain a self step. */
  public static final int BIT_SELF = 0x00001000 << 13;

  /**
   * Bit is on if any of the walkers contain a filter (i.e. id(), extension function, etc.) step.
   */
  public static final int BIT_FILTER = 0x00001000 << 14;

  /** Bit is on if any of the walkers contain a root step. */
  public static final int BIT_ROOT = 0x00001000 << 15;

  /** Found "//foo" pattern */
  public static final int BIT_ANY_DESCENDANT_FROM_ROOT = 0x00001000 << 17;

  /**
   * Bit is on if any of the walkers contain an node() test. This is really only useful if the count
   * is 1.
   */
  public static final int BIT_NODETEST_ANY = 0x00001000 << 18;

  // can't go higher than 18!

  /** Bit is on if the expression is a match pattern. */
  public static final int BIT_MATCH_PATTERN = 0x00001000 << 19;
}
