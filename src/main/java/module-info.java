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

/**
 * HtmlUnit XPath module.
 * <p>
 * This module provides XPath 1.0 functionality for HtmlUnit.
 * It is based on a fork of Apache Xalan's XPath engine, streamlined to include
 * only the XPath evaluation capabilities needed by HtmlUnit.
 * </p>
 *
 * @author Ronald Brill
 * @since 5.0.0
 */
module org.htmlunit.xpath {
    requires java.xml;

    exports org.htmlunit.xpath;
    exports org.htmlunit.xpath.axes;
    exports org.htmlunit.xpath.compiler;
    exports org.htmlunit.xpath.functions;
    exports org.htmlunit.xpath.objects;
    exports org.htmlunit.xpath.operations;
    exports org.htmlunit.xpath.patterns;
    exports org.htmlunit.xpath.res;
    exports org.htmlunit.xpath.xml.utils;
}
