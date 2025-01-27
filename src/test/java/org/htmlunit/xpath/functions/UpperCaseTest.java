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

public class UpperCaseTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void stringUpperCase() throws Exception {
    final List<?> hits = getByXpath("upper-case('abCd0')");
    assertEquals(1, hits.size());
    assertEquals("ABCD0", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringUpperCaseOfNumbers() throws Exception {
    final List<?> hits = getByXpath("upper-case(1234)");
    assertEquals(1, hits.size());
    assertEquals("1234", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringUpperCaseOfEmptyInput() throws Exception {
    final List<?> hits = getByXpath("upper-case('')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringUpperCaseFunctionOperatesOnContextNode() throws Exception {
   final List<?> hits = getByXpath("upper-case()");
   assertEquals(1, hits.size());
   assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringUpperCaseFunctionRequiresAtMostOneArguments() throws Exception {
    assertGetByXpathException("upper-case('a', 'b')",
        "Could not retrieve XPath >upper-case('a', 'b')< on [#document: null]",
        "FuncUpperCase only allows 0 or 1 arguments");
  }
}
