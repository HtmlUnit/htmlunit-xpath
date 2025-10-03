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

/** Unit test for sum() function. */
public class SumTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void sum() throws Exception {
    final List<?> hits = getByXpath("<root>4</root>", "sum(/*)");
    assertEquals(1, hits.size());
    assertEquals(4.0, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void sumMany() throws Exception {
    final List<?> hits = getByXpath("<root><a>4</a><a>3</a></root>", "sum(/*/a)");
    assertEquals(1, hits.size());
    assertEquals(7.0, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void sumEmpty() throws Exception {
    final List<?> hits = getByXpath("sum(/*)");
    assertEquals(1, hits.size());
    assertEquals(Double.NaN, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void sumOfNumber() throws Exception {
    assertGetByXpathException(
        "sum(7)",
        "Could not retrieve XPath >sum(7)< on [#document: null]",
        "Can not convert #NUMBER to a NodeList!");
  }

  /** @throws Exception in case of problems */
  @Test
  public void sumNoArguments() throws Exception {
    assertGetByXpathException(
        "sum()",
        "Could not retrieve XPath >sum()< on [#document: null]",
        "FuncSum only allows 1 arguments");
  }
}
