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
package net.sourceforge.htmlunit.xpath.xml.res;

import java.util.ListResourceBundle;

/**
 * Set up error messages. We build a two dimensional array of message keys and message strings. In
 * order to add a new message here, you need to first add a String constant. And you need to enter
 * key, value pair as part of the contents array. You also need to update MAX_CODE for error strings
 * and MAX_WARNING for warnings ( Needed for only information purpose )
 */
public class XMLErrorResources extends ListResourceBundle {

  /*
   * This file contains error and warning messages related to Xalan Error
   * Handling.
   *
   * General notes to translators:
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
   */

  /*
   * Message keys
   */
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

  /*
   * Now fill in the message text. Then fill in the message text for that message
   * code in the array. Use the new error code as the index into the array.
   */

  // Error messages...

  /** {@inheritDoc} */
  @Override
  public Object[][] getContents() {
    return new Object[][] {

      /* Error message ID that has a null message, but takes in a single object. */
      {"ER0000", "{0}"},
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
      {"column", "Column #"}
    };
  }
}
