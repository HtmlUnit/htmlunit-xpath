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
package org.htmlunit.xpath.compiler;

import java.util.ArrayList;
import javax.xml.transform.TransformerException;
import org.htmlunit.xpath.patterns.NodeTest;
import org.htmlunit.xpath.res.XPATHErrorResources;
import org.htmlunit.xpath.res.XPATHMessages;

/** This class represents the data structure basics of the XPath object. */
public class OpMap {

  /** The current pattern string, for diagnostics purposes */
  protected String m_currentPattern;

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return m_currentPattern;
  }

  /**
   * Return the expression as a string for diagnostics.
   *
   * @return The expression string.
   */
  public String getPatternString() {
    return m_currentPattern;
  }

  /** The starting size of the token queue. */
  static final int MAXTOKENQUEUESIZE = 500;

  /*
   * Amount to grow token queue when it becomes full
   */
  static final int BLOCKTOKENQUEUESIZE = 500;

  /**
   * TokenStack is the queue of used tokens. The current token is the token at the end of the
   * m_tokenQueue. The idea is that the queue can be marked and a sequence of tokens can be reused.
   */
  final ArrayList<Object> m_tokenQueue = new ArrayList<>();

  /**
   * Get the XPath as a list of tokens.
   *
   * @return ObjectVector of tokens.
   */
  public ArrayList<Object> getTokenQueue() {
    return m_tokenQueue;
  }

  /**
   * Get size of the token queue.
   *
   * @return The size of the token queue.
   */
  public int getTokenQueueSize() {
    return m_tokenQueue.size();
  }

  /**
   * An operations map is used instead of a proper parse tree. It contains operations codes and
   * indexes into the m_tokenQueue. I use an array instead of a full parse tree in order to cut down
   * on the number of objects created.
   */
  OpMapVector m_opMap = null;

  // Position indexes

  /**
   * The length is always the opcode position + 1. Length is always expressed as the opcode+length
   * bytes, so it is always 2 or greater.
   */
  public static final int MAPINDEX_LENGTH = 1;

  /** Replace the large arrays with a small array. */
  void shrink() {

    final int n = m_opMap.elementAt(MAPINDEX_LENGTH);
    m_opMap.setToSize(n + 4);

    m_opMap.setElementAt(0, n);
    m_opMap.setElementAt(0, n + 1);
    m_opMap.setElementAt(0, n + 2);

    m_tokenQueue.add(null);
    m_tokenQueue.add(null);
    m_tokenQueue.add(null);
  }

  /**
   * Given an operation position, return the current op.
   *
   * @param opPos index into op map.
   * @return the op that corresponds to the opPos argument.
   */
  public int getOp(final int opPos) {
    return m_opMap.elementAt(opPos);
  }

  /**
   * Set the op at index to the given int.
   *
   * @param opPos index into op map.
   * @param value Value to set
   */
  public void setOp(final int opPos, final int value) {
    m_opMap.setElementAt(value, opPos);
  }

  /**
   * Given an operation position, return the end position, i.e. the beginning of the next operation.
   *
   * @param opPos An op position of an operation for which there is a size entry following.
   * @return position of next operation in m_opMap.
   */
  public int getNextOpPos(final int opPos) {
    return opPos + m_opMap.elementAt(opPos + 1);
  }

  /**
   * Given a location step position, return the end position, i.e. the beginning of the next step.
   *
   * @param opPos the position of a location step.
   * @return the position of the next location step.
   */
  public int getNextStepPos(final int opPos) {

    int stepType = getOp(opPos);

    if ((stepType >= OpCodes.AXES_START_TYPES) && (stepType <= OpCodes.AXES_END_TYPES)) {
      return getNextOpPos(opPos);
    }
    else if ((stepType >= OpCodes.FIRST_NODESET_OP) && (stepType <= OpCodes.LAST_NODESET_OP)) {
      int newOpPos = getNextOpPos(opPos);

      while (OpCodes.OP_PREDICATE == getOp(newOpPos)) {
        newOpPos = getNextOpPos(newOpPos);
      }

      stepType = getOp(newOpPos);

      if (!((stepType >= OpCodes.AXES_START_TYPES) && (stepType <= OpCodes.AXES_END_TYPES))) {
        return OpCodes.ENDOP;
      }

      return newOpPos;
    }
    else {
      throw new RuntimeException(
          XPATHMessages.createXPATHMessage(
              XPATHErrorResources.ER_UNKNOWN_STEP, new Object[] {String.valueOf(stepType)}));
    }
  }

  /**
   * Given an FROM_stepType position, return the position of the first predicate, if there is one,
   * or else this will point to the end of the FROM_stepType. Example: int posOfPredicate =
   * xpath.getNextOpPos(stepPos); boolean hasPredicates = OpCodes.OP_PREDICATE ==
   * xpath.getOp(posOfPredicate);
   *
   * @param opPos position of FROM_stepType op.
   * @return position of predicate in FROM_stepType structure.
   */
  public int getFirstPredicateOpPos(final int opPos)
      throws javax.xml.transform.TransformerException {

    final int stepType = m_opMap.elementAt(opPos);

    if ((stepType >= OpCodes.AXES_START_TYPES) && (stepType <= OpCodes.AXES_END_TYPES)) {
      return opPos + m_opMap.elementAt(opPos + 2);
    }
    else if ((stepType >= OpCodes.FIRST_NODESET_OP) && (stepType <= OpCodes.LAST_NODESET_OP)) {
      return opPos + m_opMap.elementAt(opPos + 1);
    }
    else if (-2 == stepType) {
      return -2;
    }
    else {
      error(
          org.htmlunit.xpath.res.XPATHErrorResources.ER_UNKNOWN_OPCODE,
          new Object[] {String.valueOf(stepType)});
      return -1;
    }
  }

  /**
   * Tell the user of an error, and probably throw an exception.
   *
   * @param msg An error msgkey that corresponds to one of the constants found in {@link
   *     org.htmlunit.xpath.res.XPATHErrorResources}, which is a key for a format string.
   * @param args An array of arguments represented in the format string, which may be null.
   * @throws TransformerException if the current ErrorListoner determines to throw an exception.
   */
  public void error(final String msg, final Object[] args)
      throws javax.xml.transform.TransformerException {
    final String fmsg = XPATHMessages.createXPATHMessage(msg, args);

    throw new javax.xml.transform.TransformerException(fmsg);
  }

  /**
   * Go to the first child of a given operation.
   *
   * @param opPos position of operation.
   * @return The position of the first child of the operation.
   */
  public static int getFirstChildPos(final int opPos) {
    return opPos + 2;
  }

  /**
   * Given a location step, get the length of that step.
   *
   * @param opPos Position of location step in op map.
   * @return The length of the step.
   */
  public int getArgLengthOfStep(final int opPos) {
    return m_opMap.elementAt(opPos + MAPINDEX_LENGTH + 1) - 3;
  }

  /**
   * Get the first child position of a given location step.
   *
   * @param opPos Position of location step in the location map.
   * @return The first child position of the step.
   */
  public static int getFirstChildPosOfStep(final int opPos) {
    return opPos + 3;
  }

  /**
   * Get the test type of the step, i.e. NODETYPE_XXX value.
   *
   * @param opPosOfStep The position of the FROM_XXX step.
   * @return NODETYPE_XXX value.
   */
  public int getStepTestType(final int opPosOfStep) {
    return m_opMap.elementAt(opPosOfStep + 3); // skip past op, len, len without predicates
  }

  /**
   * Get the namespace of the step.
   *
   * @param opPosOfStep The position of the FROM_XXX step.
   * @return The step's namespace, NodeTest.WILD, or null for null namespace.
   */
  public String getStepNS(final int opPosOfStep) {

    final int argLenOfStep = getArgLengthOfStep(opPosOfStep);

    // System.out.println("getStepNS.argLenOfStep: "+argLenOfStep);
    if (argLenOfStep == 3) {
      final int index = m_opMap.elementAt(opPosOfStep + 4);

      if (index >= 0) {
          return (String) m_tokenQueue.get(index);
      }
      else if (OpCodes.ELEMWILDCARD == index) {
          return NodeTest.WILD;
      }
      else {
          return null;
      }
    }
    return null;
  }

  /**
   * Get the local name of the step.
   *
   * @param opPosOfStep The position of the FROM_XXX step.
   * @return OpCodes.EMPTY, OpCodes.ELEMWILDCARD, or the local name.
   */
  public String getStepLocalName(final int opPosOfStep) {

    final int argLenOfStep = getArgLengthOfStep(opPosOfStep);

    // System.out.println("getStepLocalName.argLenOfStep: "+argLenOfStep);
    final int index;

    switch (argLenOfStep) {
      case 0:
        index = OpCodes.EMPTY;
        break;
      case 1:
        index = OpCodes.ELEMWILDCARD;
        break;
      case 2:
        index = m_opMap.elementAt(opPosOfStep + 4);
        break;
      case 3:
        index = m_opMap.elementAt(opPosOfStep + 5);
        break;
      default:
        index = OpCodes.EMPTY;
        break; // Should assert error
    }

    if (index >= 0) {
        return m_tokenQueue.get(index).toString();
    }
    else if (OpCodes.ELEMWILDCARD == index) {
        return NodeTest.WILD;
    }
    else {
        return null;
    }
  }
}
