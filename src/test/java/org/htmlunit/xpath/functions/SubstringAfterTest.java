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

/** Unit test for substring-after() function. */
public class SubstringAfterTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterNumber() throws Exception {
    final List<?> hits = getByXpath("substring-after(1234, 3)");
    assertEquals(1, hits.size());
    assertEquals("4", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterNumberFirst() throws Exception {
    final List<?> hits = getByXpath("substring-after(12444, 4)");
    assertEquals(1, hits.size());
    assertEquals("44", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterNumberUnknown() throws Exception {
    final List<?> hits = getByXpath("substring-after(1234, 7)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterString() throws Exception {
    final List<?> hits = getByXpath("substring-after('test', 'es')");
    assertEquals(1, hits.size());
    assertEquals("t", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterStringWhole() throws Exception {
    final List<?> hits = getByXpath("substring-after('test', 'test')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterStringNotFound() throws Exception {
    final List<?> hits = getByXpath("substring-after('test', 'tex')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterStringEmpty() throws Exception {
    final List<?> hits = getByXpath("substring-after('', 'o')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterStringEmptyEmpty() throws Exception {
    final List<?> hits = getByXpath("substring-after('', '')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterStringEmptySearch() throws Exception {
    final List<?> hits = getByXpath("substring-after('text', '')");
    assertEquals(1, hits.size());
    assertEquals("text", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterFunctionRequiresAtLeastTwoArguments() throws Exception {
    assertGetByXpathException(
        "substring-after('a')",
        "Could not retrieve XPath >substring-after('a')< on [#document: null]",
        "FuncSubstringAfter only allows 2 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterFunctionRequiresAtMostThreeArguments() throws Exception {
    assertGetByXpathException(
        "substring-after('a', 1, 1, 4)",
        "Could not retrieve XPath >substring-after('a', 1, 1, 4)< on [#document: null]",
        "FuncSubstringAfter only allows 2 arguments");
  }
}
