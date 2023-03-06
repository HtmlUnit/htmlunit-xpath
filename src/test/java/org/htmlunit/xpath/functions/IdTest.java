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
package org.htmlunit.xpath.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.htmlunit.xpath.AbstractXPathTest;
import org.junit.jupiter.api.Test;

/** Unit test for id() function. */
public class IdTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void idFunctionSelectsNothingInDocumentWithNoIds() throws Exception {
    List<?> hits = getByXpath("id('rootId')");
    assertEquals(0, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void id() throws Exception {
    List<?> hits =
        getByXpath(
            "<!DOCTYPE root [<!ATTLIST a id ID #REQUIRED>]><root><a id='myId'/></root>",
            "id('myId')");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void findMultipleElementsByMultipleIDs() throws Exception {
    List<?> hits =
        getByXpath(
            "<!DOCTYPE root [<!ATTLIST a id ID #REQUIRED>]><root><id>p1</id><id>p2</id><id>p3</id><a id='p1'/><a id='p2'/></root>",
            "id(//id)");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void returnsFirstElementWithMatchingId() throws Exception {
    List<?> hits =
        getByXpath(
            "<!DOCTYPE root [<!ATTLIST a id ID #REQUIRED><!ATTLIST b id ID #REQUIRED>]><root><a id='p1'/><b id='p1'/></root>",
            "id('p1')");
    assertEquals(1, hits.size());
    assertEquals("[a: null]", hits.get(0).toString());
  }

  /** @throws Exception in case of problems */
  @Test
  public void idFunctionRequiresAtLeastOneArgument() throws Exception {
    assertGetByXpathException(
        "id()",
        "Could not retrieve XPath >id()< on [#document: null]",
        "FuncId only allows 1 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void floorFunctionRequiresExactlyOneArgument() throws Exception {
    assertGetByXpathException(
        "id('a', 'b')",
        "Could not retrieve XPath >id('a', 'b')< on [#document: null]",
        "FuncId only allows 1 arguments");
  }
}
