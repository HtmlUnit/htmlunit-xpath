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

import org.htmlunit.xpath.AbstractXPathTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LowerCaseTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void stringLowerCase() throws Exception {
    final List<?> hits = getByXpath("lower-case('ABc!D')");
    assertEquals(1, hits.size());
    assertEquals("abc!d", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLowerCaseOfNumbers() throws Exception {
    final List<?> hits = getByXpath("lower-case(1234)");
    assertEquals(1, hits.size());
    assertEquals("1234", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLowerCaseOfEmptyInput() throws Exception {
    final List<?> hits = getByXpath("lower-case('')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLowerCaseFunctionOperatesOnContextNode() throws Exception {
    final List<?> hits = getByXpath("lower-case()");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLowerCaseFunctionRequiresAtMostOneArguments() throws Exception {
    assertGetByXpathException(
        "lower-case('a', 'b')",
        "Could not retrieve XPath >lower-case('a', 'b')< on [#document: null]",
        "FuncLowerCase only allows 0 or 1 arguments");
  }
}
