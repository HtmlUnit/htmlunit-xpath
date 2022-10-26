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
package net.sourceforge.htmlunit.xpath.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.sourceforge.htmlunit.xpath.XPathTest;
import org.junit.jupiter.api.Test;

/** Unit test for contains() function. */
public class ContainsTest extends XPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void containsNumber() throws Exception {
    List<?> hits = getByXpath("contains(33, '3')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsNumberAll() throws Exception {
    List<?> hits = getByXpath("contains(12.34, '12.34')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsNumberNot() throws Exception {
    List<?> hits = getByXpath("contains(33, '4')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsString() throws Exception {
    List<?> hits = getByXpath("contains('test', 'es')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsStringAll() throws Exception {
    List<?> hits = getByXpath("contains('xpath', 'xpath')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsStringNot() throws Exception {
    List<?> hits = getByXpath("contains('xpath', 'y')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyStringContainsNonEmptyString() throws Exception {
    List<?> hits = getByXpath("contains('', 'y')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyStringContainsEmptyString() throws Exception {
    List<?> hits = getByXpath("contains('', '')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsEmptyString() throws Exception {
    List<?> hits = getByXpath("contains('xpath', '')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsFunctionRequiresAtLeastTwoArguments() throws Exception {
    assertGetByXpathException(
        "contains('a')",
        "Could not retrieve XPath >contains('a')< on [#document: null]",
        "FuncContains only allows 2 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsFunctionRequiresAtMostTwoArguments() throws Exception {
    assertGetByXpathException(
        "contains('a', 'b', '')",
        "Could not retrieve XPath >contains('a', 'b', '')< on [#document: null]",
        "FuncContains only allows 2 arguments");
  }
}
