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

import java.util.HashMap;

/** Table of strings to operation code lookups. */
public class Keywords {

  /** Table of keywords to opcode associations. */
  private static final HashMap<String, Integer> m_keywords = new HashMap<>();

  /** Table of axes names to opcode associations. */
  private static final HashMap<String, Integer> m_axisnames = new HashMap<>();

  /** Table of function name to function ID associations. */
  private static final HashMap<String, Integer> m_nodetests = new HashMap<>();

  /** Table of node type strings to opcode associations. */
  private static final HashMap<String, Integer> m_nodetypes = new HashMap<>();

  /** ancestor axes string. */
  private static final String FROM_ANCESTORS_STRING = "ancestor";

  /** ancestor-or-self axes string. */
  private static final String FROM_ANCESTORS_OR_SELF_STRING = "ancestor-or-self";

  /** attribute axes string. */
  private static final String FROM_ATTRIBUTES_STRING = "attribute";

  /** child axes string. */
  private static final String FROM_CHILDREN_STRING = "child";

  /** descendant-or-self axes string. */
  private static final String FROM_DESCENDANTS_STRING = "descendant";

  /** ancestor axes string. */
  private static final String FROM_DESCENDANTS_OR_SELF_STRING = "descendant-or-self";

  /** following axes string. */
  private static final String FROM_FOLLOWING_STRING = "following";

  /** following-sibling axes string. */
  private static final String FROM_FOLLOWING_SIBLINGS_STRING = "following-sibling";

  /** parent axes string. */
  private static final String FROM_PARENT_STRING = "parent";

  /** preceding axes string. */
  private static final String FROM_PRECEDING_STRING = "preceding";

  /** preceding-sibling axes string. */
  private static final String FROM_PRECEDING_SIBLINGS_STRING = "preceding-sibling";

  /** self axes string. */
  private static final String FROM_SELF_STRING = "self";

  /** namespace axes string. */
  private static final String FROM_NAMESPACE_STRING = "namespace";

  /** self axes abreviated string. */
  private static final String FROM_SELF_ABBREVIATED_STRING = ".";

  /** comment node test string. */
  private static final String NODETYPE_COMMENT_STRING = "comment";

  /** text node test string. */
  private static final String NODETYPE_TEXT_STRING = "text";

  /** processing-instruction node test string. */
  private static final String NODETYPE_PI_STRING = "processing-instruction";

  /** Any node test string. */
  private static final String NODETYPE_NODE_STRING = "node";

  /** Wildcard element string. */
  private static final String NODETYPE_ANYELEMENT_STRING = "*";

  /** current function string. */
  public static final String FUNC_CURRENT_STRING = "current";

  /** last function string. */
  public static final String FUNC_LAST_STRING = "last";

  /** position function string. */
  public static final String FUNC_POSITION_STRING = "position";

  /** count function string. */
  public static final String FUNC_COUNT_STRING = "count";

  /** id function string. */
  static final String FUNC_ID_STRING = "id";

  /** local-name function string. */
  public static final String FUNC_LOCAL_PART_STRING = "local-name";

  /** namespace-uri function string. */
  public static final String FUNC_NAMESPACE_STRING = "namespace-uri";

  /** name function string. */
  public static final String FUNC_NAME_STRING = "name";

  /** not function string. */
  public static final String FUNC_NOT_STRING = "not";

  /** true function string. */
  public static final String FUNC_TRUE_STRING = "true";

  /** false function string. */
  public static final String FUNC_FALSE_STRING = "false";

  /** boolean function string. */
  public static final String FUNC_BOOLEAN_STRING = "boolean";

  /** lang function string. */
  public static final String FUNC_LANG_STRING = "lang";

  /** number function string. */
  public static final String FUNC_NUMBER_STRING = "number";

  /** floor function string. */
  public static final String FUNC_FLOOR_STRING = "floor";

  /** ceiling function string. */
  public static final String FUNC_CEILING_STRING = "ceiling";

  /** round function string. */
  public static final String FUNC_ROUND_STRING = "round";

  /** sum function string. */
  public static final String FUNC_SUM_STRING = "sum";

  /** string function string. */
  public static final String FUNC_STRING_STRING = "string";

  /** starts-with function string. */
  public static final String FUNC_STARTS_WITH_STRING = "starts-with";

  /** contains function string. */
  public static final String FUNC_CONTAINS_STRING = "contains";

  /** substring-before function string. */
  public static final String FUNC_SUBSTRING_BEFORE_STRING = "substring-before";

  /** substring-after function string. */
  public static final String FUNC_SUBSTRING_AFTER_STRING = "substring-after";

  /** normalize-space function string. */
  public static final String FUNC_NORMALIZE_SPACE_STRING = "normalize-space";

  /** translate function string. */
  public static final String FUNC_TRANSLATE_STRING = "translate";

  /** concat function string. */
  public static final String FUNC_CONCAT_STRING = "concat";

  /** substring function string. */
  public static final String FUNC_SUBSTRING_STRING = "substring";

  /** string-length function string. */
  public static final String FUNC_STRING_LENGTH_STRING = "string-length";

  static {
    m_axisnames.put(FROM_ANCESTORS_STRING, Integer.valueOf(OpCodes.FROM_ANCESTORS));
    m_axisnames.put(FROM_ANCESTORS_OR_SELF_STRING, Integer.valueOf(OpCodes.FROM_ANCESTORS_OR_SELF));
    m_axisnames.put(FROM_ATTRIBUTES_STRING, Integer.valueOf(OpCodes.FROM_ATTRIBUTES));
    m_axisnames.put(FROM_CHILDREN_STRING, Integer.valueOf(OpCodes.FROM_CHILDREN));
    m_axisnames.put(FROM_DESCENDANTS_STRING, Integer.valueOf(OpCodes.FROM_DESCENDANTS));
    m_axisnames.put(
        FROM_DESCENDANTS_OR_SELF_STRING, Integer.valueOf(OpCodes.FROM_DESCENDANTS_OR_SELF));
    m_axisnames.put(FROM_FOLLOWING_STRING, Integer.valueOf(OpCodes.FROM_FOLLOWING));
    m_axisnames.put(
        FROM_FOLLOWING_SIBLINGS_STRING, Integer.valueOf(OpCodes.FROM_FOLLOWING_SIBLINGS));
    m_axisnames.put(FROM_PARENT_STRING, Integer.valueOf(OpCodes.FROM_PARENT));
    m_axisnames.put(FROM_PRECEDING_STRING, Integer.valueOf(OpCodes.FROM_PRECEDING));
    m_axisnames.put(
        FROM_PRECEDING_SIBLINGS_STRING, Integer.valueOf(OpCodes.FROM_PRECEDING_SIBLINGS));
    m_axisnames.put(FROM_SELF_STRING, Integer.valueOf(OpCodes.FROM_SELF));
    m_axisnames.put(FROM_NAMESPACE_STRING, Integer.valueOf(OpCodes.FROM_NAMESPACE));
    m_nodetypes.put(NODETYPE_COMMENT_STRING, Integer.valueOf(OpCodes.NODETYPE_COMMENT));
    m_nodetypes.put(NODETYPE_TEXT_STRING, Integer.valueOf(OpCodes.NODETYPE_TEXT));
    m_nodetypes.put(NODETYPE_PI_STRING, Integer.valueOf(OpCodes.NODETYPE_PI));
    m_nodetypes.put(NODETYPE_NODE_STRING, Integer.valueOf(OpCodes.NODETYPE_NODE));
    m_nodetypes.put(NODETYPE_ANYELEMENT_STRING, Integer.valueOf(OpCodes.NODETYPE_ANYELEMENT));
    m_keywords.put(FROM_SELF_ABBREVIATED_STRING, Integer.valueOf(OpCodes.FROM_SELF));
    m_keywords.put(FUNC_ID_STRING, Integer.valueOf(FunctionTable.FUNC_ID));

    m_nodetests.put(NODETYPE_COMMENT_STRING, Integer.valueOf(OpCodes.NODETYPE_COMMENT));
    m_nodetests.put(NODETYPE_TEXT_STRING, Integer.valueOf(OpCodes.NODETYPE_TEXT));
    m_nodetests.put(NODETYPE_PI_STRING, Integer.valueOf(OpCodes.NODETYPE_PI));
    m_nodetests.put(NODETYPE_NODE_STRING, Integer.valueOf(OpCodes.NODETYPE_NODE));
  }

  static Object getAxisName(final String key) {
    return m_axisnames.get(key);
  }

  static Object lookupNodeTest(final String key) {
    return m_nodetests.get(key);
  }

  static Object getKeyWord(final String key) {
    return m_keywords.get(key);
  }

  static Object getNodeType(final String key) {
    return m_nodetypes.get(key);
  }
}
