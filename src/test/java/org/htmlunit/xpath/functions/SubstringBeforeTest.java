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

/** Unit test for substring-before() function. */
public class SubstringBeforeTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeNumber() throws Exception {
    List<?> hits = getByXpath("substring-before(1234, 3)");
    assertEquals(1, hits.size());
    assertEquals("12", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeNumberFirst() throws Exception {
    List<?> hits = getByXpath("substring-before(12444, 4)");
    assertEquals(1, hits.size());
    assertEquals("12", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeNumberUnknown() throws Exception {
    List<?> hits = getByXpath("substring-before(1234, 7)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeString() throws Exception {
    List<?> hits = getByXpath("substring-before('test', 'es')");
    assertEquals(1, hits.size());
    assertEquals("t", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeStringWhole() throws Exception {
    List<?> hits = getByXpath("substring-before('test', 'test')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeStringNotFound() throws Exception {
    List<?> hits = getByXpath("substring-before('test', 'tex')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeStringEmpty() throws Exception {
    List<?> hits = getByXpath("substring-before('', 'o')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeStringEmptyEmpty() throws Exception {
    List<?> hits = getByXpath("substring-before('', '')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeStringEmptySearch() throws Exception {
    List<?> hits = getByXpath("substring-before('text', '')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeFunctionRequiresAtLeastTwoArguments() throws Exception {
    assertGetByXpathException(
        "substring-before('a')",
        "Could not retrieve XPath >substring-before('a')< on [#document: null]",
        "FuncSubstringBefore only allows 2 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeFunctionRequiresAtMostThreeArguments() throws Exception {
    assertGetByXpathException(
        "substring-before('a', 1, 1, 4)",
        "Could not retrieve XPath >substring-before('a', 1, 1, 4)< on [#document: null]",
        "FuncSubstringBefore only allows 2 arguments");
  }
}
