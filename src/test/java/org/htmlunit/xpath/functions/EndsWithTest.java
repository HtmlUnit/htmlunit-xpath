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

/** Unit test for ends-with() function. */
public class EndsWithTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void endsWithNumber() throws Exception {
    final List<?> hits = getByXpath("ends-with(33, '3')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void endsWithNumberAll() throws Exception {
    final List<?> hits = getByXpath("ends-with(12.34, '12.34')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void endsWithNumberNot() throws Exception {
    final List<?> hits = getByXpath("ends-with(43, '4')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void endsWithString() throws Exception {
    final List<?> hits = getByXpath("ends-with('test', 'st')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void endsWithStringAll() throws Exception {
    final List<?> hits = getByXpath("ends-with('xpath', 'xpath')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void endsWithStringNot() throws Exception {
    final List<?> hits = getByXpath("ends-with('xpath', 'y')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyStringendsWithNonEmptyString() throws Exception {
    final List<?> hits = getByXpath("ends-with('', 'y')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyStringendsWithEmptyString() throws Exception {
    final List<?> hits = getByXpath("ends-with('', '')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void endsWithEmptyString() throws Exception {
    final List<?> hits = getByXpath("ends-with('xpath', '')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void endsWithFunctionRequiresAtLeastTwoArguments() throws Exception {
    assertGetByXpathException(
        "ends-with('a')",
        "Could not retrieve XPath >ends-with('a')< on [#document: null]",
        "FuncEndsWith only allows 2 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void endsWithFunctionRequiresAtMostTwoArguments() throws Exception {
    assertGetByXpathException(
        "ends-with('a', 'b', '')",
        "Could not retrieve XPath >ends-with('a', 'b', '')< on [#document: null]",
        "FuncEndsWith only allows 2 arguments");
  }
}
