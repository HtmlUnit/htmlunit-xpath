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
package org.htmlunit.xpath.res;

import java.util.ListResourceBundle;

/**
 * Set up error messages. We build a two dimensional array of message keys and message strings. In
 * order to add a new message here, you need to first add a Static string constant for the Key and
 * update the contents array with Key, Value pair Also you need to update the count of
 * messages(MAX_CODE)or the count of warnings(MAX_WARNING) [ Information purpose only]
 */
public class XPATHErrorResources extends ListResourceBundle {

  /*
   * General notes to translators:
   *
   * This file contains error and warning messages related to XPath Error
   * Handling.
   *
   * 1) Xalan (or more properly, Xalan-interpretive) and XSLTC are names of
   * components. XSLT is an acronym for
   * "XML Stylesheet Language: Transformations". XSLTC is an acronym for XSLT
   * Compiler.
   *
   * 2) A stylesheet is a description of how to transform an input XML document
   * into a resultant XML document (or HTML document or text). The stylesheet
   * itself is described in the form of an XML document.
   *
   * 3) A template is a component of a stylesheet that is used to match a
   * particular portion of an input document and specifies the form of the
   * corresponding portion of the output document.
   *
   * 4) An element is a mark-up tag in an XML document; an attribute is a modifier
   * on the tag. For example, in <elem attr='val' attr2='val2'> "elem" is an
   * element name, "attr" and "attr2" are attribute names with the values "val"
   * and "val2", respectively.
   *
   * 5) A namespace declaration is a special attribute that is used to associate a
   * prefix with a URI (the namespace). The meanings of element names and
   * attribute names that use that prefix are defined with respect to that
   * namespace.
   *
   * 6) "Translet" is an invented term that describes the class file that results
   * from compiling an XML stylesheet into a Java class.
   *
   * 7) XPath is a specification that describes a notation for identifying nodes
   * in a tree-structured representation of an XML document. An instance of that
   * notation is referred to as an XPath expression.
   *
   * 8) The context node is the node in the document with respect to which an
   * XPath expression is being evaluated.
   *
   * 9) An iterator is an object that traverses nodes in the tree, one at a time.
   *
   * 10) NCName is an XML term used to describe a name that does not contain a
   * colon (a "no-colon name").
   *
   * 11) QName is an XML term meaning "qualified name".
   */

  /*
   * static variables
   */
  public static final String ER_CONTEXT_HAS_NO_OWNERDOC = "ER_CONTEXT_HAS_NO_OWNERDOC";
  public static final String ER_UNKNOWN_MATCH_OPERATION = "ER_UNKNOWN_MATCH_OPERATION";
  public static final String ER_CANT_CONVERT_TO_NUMBER = "ER_CANT_CONVERT_TO_NUMBER";
  public static final String ER_CANT_CONVERT_TO_NODELIST = "ER_CANT_CONVERT_TO_NODELIST";
  public static final String ER_CANT_CONVERT_TO_MUTABLENODELIST =
      "ER_CANT_CONVERT_TO_MUTABLENODELIST";
  public static final String ER_UNKNOWN_OPCODE = "ER_UNKNOWN_OPCODE";
  public static final String ER_EXTRA_ILLEGAL_TOKENS = "ER_EXTRA_ILLEGAL_TOKENS";
  public static final String ER_EXPECTED_DOUBLE_QUOTE = "ER_EXPECTED_DOUBLE_QUOTE";
  public static final String ER_EXPECTED_SINGLE_QUOTE = "ER_EXPECTED_SINGLE_QUOTE";
  public static final String ER_EMPTY_EXPRESSION = "ER_EMPTY_EXPRESSION";
  public static final String ER_EXPECTED_BUT_FOUND = "ER_EXPECTED_BUT_FOUND";
  public static final String ER_INCORRECT_PROGRAMMER_ASSERTION =
      "ER_INCORRECT_PROGRAMMER_ASSERTION";
  public static final String ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL =
      "ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL";
  public static final String ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG =
      "ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG";
  public static final String ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG =
      "ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG";
  public static final String ER_PREDICATE_ILLEGAL_SYNTAX = "ER_PREDICATE_ILLEGAL_SYNTAX";
  public static final String ER_ILLEGAL_AXIS_NAME = "ER_ILLEGAL_AXIS_NAME";
  public static final String ER_UNKNOWN_NODETYPE = "ER_UNKNOWN_NODETYPE";
  public static final String ER_PATTERN_LITERAL_NEEDS_BE_QUOTED =
      "ER_PATTERN_LITERAL_NEEDS_BE_QUOTED";
  public static final String ER_COULDNOT_BE_FORMATTED_TO_NUMBER =
      "ER_COULDNOT_BE_FORMATTED_TO_NUMBER";
  public static final String ER_AXES_NOT_ALLOWED = "ER_AXES_NOT_ALLOWED";
  public static final String ER_COULDNOT_FIND_FUNCTION = "ER_COULDNOT_FIND_FUNCTION";
  public static final String ER_PREFIX_MUST_RESOLVE = "ER_PREFIX_MUST_RESOLVE";
  public static final String ER_FUNCTION_TOKEN_NOT_FOUND = "ER_FUNCTION_TOKEN_NOT_FOUND";
  public static final String ER_CANNOT_DEAL_XPATH_TYPE = "ER_CANNOT_DEAL_XPATH_TYPE";
  public static final String ER_NODESET_NOT_MUTABLE = "ER_NODESET_NOT_MUTABLE";
  /** Null error handler */
  public static final String ER_NULL_ERROR_HANDLER = "ER_NULL_ERROR_HANDLER";
  /** 0 or 1 */
  public static final String ER_ZERO_OR_ONE = "ER_ZERO_OR_ONE";
  /** 2 or 3 */
  public static final String ER_TWO_OR_THREE = "ER_TWO_OR_THREE";
  /** Error! Setting the root of a walker to null! */
  public static final String ER_SETTING_WALKER_ROOT_TO_NULL = "ER_SETTING_WALKER_ROOT_TO_NULL";
  /** This NodeSetDTM can not iterate to a previous node! */
  public static final String ER_NODESETDTM_CANNOT_ITERATE = "ER_NODESETDTM_CANNOT_ITERATE";
  /** This NodeSet can not iterate to a previous node! */
  public static final String ER_NODESET_CANNOT_ITERATE = "ER_NODESET_CANNOT_ITERATE";
  /** This NodeSetDTM can not do indexing or counting functions! */
  public static final String ER_NODESETDTM_CANNOT_INDEX = "ER_NODESETDTM_CANNOT_INDEX";
  /** This NodeSet can not do indexing or counting functions! */
  public static final String ER_NODESET_CANNOT_INDEX = "ER_NODESET_CANNOT_INDEX";
  /** Can not call setShouldCacheNodes after nextNode has been called! */
  public static final String ER_CANNOT_CALL_SETSHOULDCACHENODE =
      "ER_CANNOT_CALL_SETSHOULDCACHENODE";
  /** {0} only allows {1} arguments */
  public static final String ER_ONLY_ALLOWS = "ER_ONLY_ALLOWS";
  /** Programmer's assertion in getNextStepPos: unknown stepType: {0} */
  public static final String ER_UNKNOWN_STEP = "ER_UNKNOWN_STEP";
  /** Problem with RelativeLocationPath */
  public static final String ER_EXPECTED_REL_LOC_PATH = "ER_EXPECTED_REL_LOC_PATH";
  /** Problem with LocationPath */
  public static final String ER_EXPECTED_LOC_PATH = "ER_EXPECTED_LOC_PATH";

  public static final String ER_EXPECTED_LOC_PATH_AT_END_EXPR = "ER_EXPECTED_LOC_PATH_AT_END_EXPR";
  /** Problem with Step */
  public static final String ER_EXPECTED_LOC_STEP = "ER_EXPECTED_LOC_STEP";
  /** Problem with NodeTest */
  public static final String ER_EXPECTED_NODE_TEST = "ER_EXPECTED_NODE_TEST";
  /** Expected step pattern */
  public static final String ER_EXPECTED_STEP_PATTERN = "ER_EXPECTED_STEP_PATTERN";
  /** Expected relative path pattern */
  public static final String ER_EXPECTED_REL_PATH_PATTERN = "ER_EXPECTED_REL_PATH_PATTERN";

  public static final String ER_XPATH_ERROR = "ER_XPATH_ERROR";

  public static final String ER_CANNOT_OVERWRITE_CAUSE = "ER_CANNOT_OVERWRITE_CAUSE";
  public static final String ER_ITERATOR_AXIS_NOT_IMPLEMENTED = "ER_ITERATOR_AXIS_NOT_IMPLEMENTED";
  public static final String ER_ITERATOR_CLONE_NOT_SUPPORTED = "ER_ITERATOR_CLONE_NOT_SUPPORTED";
  public static final String ER_UNKNOWN_AXIS_TYPE = "ER_UNKNOWN_AXIS_TYPE";
  public static final String ER_NO_DTMIDS_AVAIL = "ER_NO_DTMIDS_AVAIL";
  public static final String ER_NOT_SUPPORTED = "ER_NOT_SUPPORTED";
  public static final String ER_NODE_NON_NULL = "ER_NODE_NON_NULL";
  public static final String ER_COULD_NOT_RESOLVE_NODE = "ER_COULD_NOT_RESOLVE_NODE";
  public static final String ER_SELF_CAUSATION_NOT_PERMITTED = "ER_SELF_CAUSATION_NOT_PERMITTED";
  public static final String ER_METHOD_NOT_SUPPORTED = "ER_METHOD_NOT_SUPPORTED";
  public static final String ER_AXIS_TRAVERSER_NOT_SUPPORTED = "ER_AXIS_TRAVERSER_NOT_SUPPORTED";

  // Error messages...

  /** {@inheritDoc} */
  @Override
  public Object[][] getContents() {
    return new Object[][] {
      {ER_CONTEXT_HAS_NO_OWNERDOC, "context does not have an owner document!"},
      {ER_UNKNOWN_MATCH_OPERATION, "unknown match operation!"},
      {ER_CANT_CONVERT_TO_NUMBER, "Can not convert {0} to a number"},
      {ER_CANT_CONVERT_TO_NODELIST, "Can not convert {0} to a NodeList!"},
      {ER_CANT_CONVERT_TO_MUTABLENODELIST, "Can not convert {0} to a NodeSetDTM!"},
      {ER_UNKNOWN_OPCODE, "ERROR! Unknown op code: {0}"},
      {ER_EXTRA_ILLEGAL_TOKENS, "Extra illegal tokens: {0}"},
      {ER_EXPECTED_DOUBLE_QUOTE, "misquoted literal... expected double quote!"},
      {ER_EXPECTED_SINGLE_QUOTE, "misquoted literal... expected single quote!"},
      {ER_EMPTY_EXPRESSION, "Empty expression!"},
      {ER_EXPECTED_BUT_FOUND, "Expected {0}, but found: {1}"},
      {ER_INCORRECT_PROGRAMMER_ASSERTION, "Programmer assertion is incorrect! - {0}"},
      {
        ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL,
        "boolean(...) argument is no longer optional with 19990709 XPath draft."
      },
      {ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG, "Found ',' but no preceding argument!"},
      {ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG, "Found ',' but no following argument!"},
      {
        ER_PREDICATE_ILLEGAL_SYNTAX,
        "'..[predicate]' or '.[predicate]' is illegal syntax.  Use 'self::node()[predicate]' instead."
      },
      {ER_ILLEGAL_AXIS_NAME, "illegal axis name: {0}"},
      {ER_UNKNOWN_NODETYPE, "Unknown nodetype: {0}"},
      {ER_PATTERN_LITERAL_NEEDS_BE_QUOTED, "Pattern literal ({0}) needs to be quoted!"},
      {ER_COULDNOT_BE_FORMATTED_TO_NUMBER, "{0} could not be formatted to a number!"},
      {
        ER_AXES_NOT_ALLOWED,
        "Only child:: and attribute:: axes are allowed in match patterns!  Offending axes = {0}"
      },
      {ER_COULDNOT_FIND_FUNCTION, "Could not find function: {0}"},
      {ER_PREFIX_MUST_RESOLVE, "Prefix must resolve to a namespace: {0}"},
      {ER_FUNCTION_TOKEN_NOT_FOUND, "function token not found."},
      {ER_CANNOT_DEAL_XPATH_TYPE, "Can not deal with XPath type: {0}"},
      {ER_NODESET_NOT_MUTABLE, "This NodeSet is not mutable"},
      {ER_NULL_ERROR_HANDLER, "Null error handler"},
      {ER_ZERO_OR_ONE, "0 or 1"},
      {ER_TWO_OR_THREE, "2 or 3"},
      {ER_SETTING_WALKER_ROOT_TO_NULL, "\n !!!! Error! Setting the root of a walker to null!!!"},
      {ER_NODESETDTM_CANNOT_ITERATE, "This NodeSetDTM can not iterate to a previous node!"},
      {ER_NODESET_CANNOT_ITERATE, "This NodeSet can not iterate to a previous node!"},
      {ER_NODESETDTM_CANNOT_INDEX, "This NodeSetDTM can not do indexing or counting functions!"},
      {ER_NODESET_CANNOT_INDEX, "This NodeSet can not do indexing or counting functions!"},
      {
        ER_CANNOT_CALL_SETSHOULDCACHENODE,
        "Can not call setShouldCacheNodes after nextNode has been called!"
      },
      {ER_ONLY_ALLOWS, "{0} only allows {1} arguments"},
      {ER_UNKNOWN_STEP, "Programmer''s assertion in getNextStepPos: unknown stepType: {0}"},

      // Note to translators: A relative location path is a form of XPath expression.
      // The message indicates that such an expression was expected following the
      // characters '/' or '//', but was not found.
      {
        ER_EXPECTED_REL_LOC_PATH,
        "A relative location path was expected following the '/' or '//' token."
      },

      // Note to translators: A location path is a form of XPath expression.
      // The message indicates that syntactically such an expression was expected,but
      // the characters specified by the substitution text were encountered instead.
      {
        ER_EXPECTED_LOC_PATH,
        "A location path was expected, but the following token was encountered\u003a  {0}"
      },

      // Note to translators: A location path is a form of XPath expression.
      // The message indicates that syntactically such a subexpression was expected,
      // but no more characters were found in the expression.
      {
        ER_EXPECTED_LOC_PATH_AT_END_EXPR,
        "A location path was expected, but the end of the XPath expression was found instead."
      },

      // Note to translators: A location step is part of an XPath expression.
      // The message indicates that syntactically such an expression was expected
      // following the specified characters.
      {ER_EXPECTED_LOC_STEP, "A location step was expected following the '/' or '//' token."},

      // Note to translators: A node test is part of an XPath expression that is
      // used to test for particular kinds of nodes. In this case, a node test that
      // consists of an NCName followed by a colon and an asterisk or that consists
      // of a QName was expected, but was not found.
      {ER_EXPECTED_NODE_TEST, "A node test that matches either NCName:* or QName was expected."},

      // Note to translators: A step pattern is part of an XPath expression.
      // The message indicates that syntactically such an expression was expected,
      // but the specified character was found in the expression instead.
      {ER_EXPECTED_STEP_PATTERN, "A step pattern was expected, but '/' was encountered."},

      // Note to translators: A relative path pattern is part of an XPath expression.
      // The message indicates that syntactically such an expression was expected,
      // but was not found.
      {ER_EXPECTED_REL_PATH_PATTERN, "A relative path pattern was expected."},
      {ER_CANNOT_OVERWRITE_CAUSE, "Cannot overwrite cause"},
      {ER_ITERATOR_AXIS_NOT_IMPLEMENTED, "Error: iterator for axis {0} not implemented "},
      {ER_ITERATOR_CLONE_NOT_SUPPORTED, "Iterator clone not supported"},
      {ER_UNKNOWN_AXIS_TYPE, "Unknown axis traversal type: {0}"},
      {ER_NO_DTMIDS_AVAIL, "No more DTM IDs are available"},
      {ER_NOT_SUPPORTED, "Not supported: {0}"},
      {ER_NODE_NON_NULL, "Node must be non-null for getDTMHandleFromNode"},
      {ER_COULD_NOT_RESOLVE_NODE, "Could not resolve the node to a handle"},
      {ER_SELF_CAUSATION_NOT_PERMITTED, "Self-causation not permitted"},
      {ER_METHOD_NOT_SUPPORTED, "Method not yet supported "},
      {ER_AXIS_TRAVERSER_NOT_SUPPORTED, "Axis traverser not supported: {0}"},
      {"BAD_CODE", "Parameter to createMessage was out of bounds"},
      {"FORMAT_FAILED", "Exception thrown during messageFormat call"},
      {"line", "Line #"},
      {"column", "Column #"},

      // Other miscellaneous text used inside the code...
      {"BAD_CODE", "Parameter to createMessage was out of bounds"},
      {"FORMAT_FAILED", "Exception thrown during messageFormat call"},
      {"yes", "yes"},
      {"line", "Line #"},
      {"column", "Column #"},
      {"gtone", ">1"},
      {"zero", "0"},
      {"one", "1"},
      {"two", "2"},
      {"three", "3"}
    };
  }

  // ================= INFRASTRUCTURE ======================

  /** Field BAD_CODE */
  public static final String BAD_CODE = "BAD_CODE";

  /** Field FORMAT_FAILED */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";
}
