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

/** Unit test for string() function. */
public class StringTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void stringFunctionOperatesOnFirstNodeInDocumentOrder() throws Exception {
    final List<?> hits =
        getByXpath("<root><a><b><x>2</x><x>3</x></b><x>4</x></a></root>", "string(//x)");
    assertEquals(1, hits.size());
    assertEquals("2", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringOfInfinity() throws Exception {
    final List<?> hits = getByXpath("string(1 div 0)");
    assertEquals(1, hits.size());
    assertEquals("Infinity", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringOfNegativeInfinity() throws Exception {
    final List<?> hits = getByXpath("string(-1 div 0)");
    assertEquals(1, hits.size());
    assertEquals("-Infinity", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringOfNegativeZero() throws Exception {
    final List<?> hits = getByXpath("string(-0)");
    assertEquals(1, hits.size());
    assertEquals("0", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void integersAreFormattedAsInts() throws Exception {
    final List<?> hits = getByXpath("string(12)");
    assertEquals(1, hits.size());
    assertEquals("12", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringWithoutParam() throws Exception {
    final List<?> hits = getByXpath("string()");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringFunctionRequiresExactlyOneArgument() throws Exception {
    assertGetByXpathException(
        "string('', '')",
        "Could not retrieve XPath >string('', '')< on [#document: null]",
        "FuncString only allows 0 or 1 arguments");
  }
}
