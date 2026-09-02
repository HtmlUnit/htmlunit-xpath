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

import java.util.Map;

/**
 * Table of strings to operation code lookups.
 *
 * @author Apache Xalan
 * @author Ronald Brill
 */
public final class Keywords {

    private Keywords() {
        // Utility class
    }

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

    /** Table of axes names to opcode associations. */
    private static final Map<String, Integer> AXIS_NAMES = Map.ofEntries(Map.entry("ancestor", OpCodes.FROM_ANCESTORS),
            Map.entry("ancestor-or-self", OpCodes.FROM_ANCESTORS_OR_SELF),
            Map.entry("attribute", OpCodes.FROM_ATTRIBUTES), Map.entry("child", OpCodes.FROM_CHILDREN),
            Map.entry("descendant", OpCodes.FROM_DESCENDANTS),
            Map.entry("descendant-or-self", OpCodes.FROM_DESCENDANTS_OR_SELF),
            Map.entry("following", OpCodes.FROM_FOLLOWING),
            Map.entry("following-sibling", OpCodes.FROM_FOLLOWING_SIBLINGS), Map.entry("parent", OpCodes.FROM_PARENT),
            Map.entry("preceding", OpCodes.FROM_PRECEDING),
            Map.entry("preceding-sibling", OpCodes.FROM_PRECEDING_SIBLINGS), Map.entry("self", OpCodes.FROM_SELF),
            Map.entry("namespace", OpCodes.FROM_NAMESPACE));

    /** Table of node type strings to opcode associations. */
    private static final Map<String, Integer> NODE_TYPES = Map.of("comment", OpCodes.NODETYPE_COMMENT, "text",
            OpCodes.NODETYPE_TEXT, "processing-instruction", OpCodes.NODETYPE_PI, "node", OpCodes.NODETYPE_NODE, "*",
            OpCodes.NODETYPE_ANYELEMENT);

    /** Table of function name to function ID associations. */
    private static final Map<String, Integer> NODE_TESTS = Map.of("comment", OpCodes.NODETYPE_COMMENT, "text",
            OpCodes.NODETYPE_TEXT, "processing-instruction", OpCodes.NODETYPE_PI, "node", OpCodes.NODETYPE_NODE);

    /** Table of keywords to opcode associations. */
    private static final Map<String, Integer> KEYWORDS = Map.of(".", OpCodes.FROM_SELF, FUNC_ID_STRING,
            FunctionTable.FUNC_ID);

    static Integer getAxisName(final String key) {
        return AXIS_NAMES.get(key);
    }

    static Integer lookupNodeTest(final String key) {
        return NODE_TESTS.get(key);
    }

    static Integer getKeyWord(final String key) {
        return KEYWORDS.get(key);
    }

    static Integer getNodeType(final String key) {
        return NODE_TYPES.get(key);
    }
}