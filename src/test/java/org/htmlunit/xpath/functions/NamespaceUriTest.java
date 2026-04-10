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
package org.htmlunit.xpath.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.htmlunit.xpath.AbstractXPathTest;
import org.junit.jupiter.api.Test;

/** Unit tests for the XPath namespace-uri() function. */
public class NamespaceUriTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void namespaceUriNoNamespace() throws Exception {
    final String xml = "<root><child/></root>";
    final List<?> hits = getByXpath(xml, "string(namespace-uri(/root))");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void namespaceUriEmptyNodeSet() throws Exception {
    final String xml = "<root/>";
    final List<?> hits = getByXpath(xml, "string(namespace-uri(/nonexistent))");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void namespaceUriNoArgOnContextNode() throws Exception {
    final String xml = "<root><child/></root>";
    final List<?> hits = getByXpath(xml, "//child[namespace-uri()='']");
    assertEquals(1, hits.size());
  }
}