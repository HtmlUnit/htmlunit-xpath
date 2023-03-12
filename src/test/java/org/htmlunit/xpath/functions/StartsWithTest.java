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

/** Unit test for starts-with() function. */
public class StartsWithTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void startsWithNumber() throws Exception {
    List<?> hits = getByXpath("starts-with(33, '3')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithNumberAll() throws Exception {
    List<?> hits = getByXpath("starts-with(12.34, '12.34')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithNumberNot() throws Exception {
    List<?> hits = getByXpath("starts-with(34, '4')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithString() throws Exception {
    List<?> hits = getByXpath("starts-with('test', 'te')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithStringAll() throws Exception {
    List<?> hits = getByXpath("starts-with('xpath', 'xpath')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithStringNot() throws Exception {
    List<?> hits = getByXpath("starts-with('xpath', 'y')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyStringStartsWithNonEmptyString() throws Exception {
    List<?> hits = getByXpath("starts-with('', 'y')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyStringStartsWithEmptyString() throws Exception {
    List<?> hits = getByXpath("starts-with('', '')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithEmptyString() throws Exception {
    List<?> hits = getByXpath("starts-with('xpath', '')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithFunctionRequiresAtLeastTwoArguments() throws Exception {
    assertGetByXpathException(
        "starts-with('a')",
        "Could not retrieve XPath >starts-with('a')< on [#document: null]",
        "FuncStartsWith only allows 2 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithFunctionRequiresAtMostTwoArguments() throws Exception {
    assertGetByXpathException(
        "starts-with('a', 'b', '')",
        "Could not retrieve XPath >starts-with('a', 'b', '')< on [#document: null]",
        "FuncStartsWith only allows 2 arguments");
  }
}
