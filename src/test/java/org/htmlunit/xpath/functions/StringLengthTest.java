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

/** Unit test for string-length() function. */
public class StringLengthTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void stringLengthOfNumber() throws Exception {
    final List<?> hits = getByXpath("string-length(3)");
    assertEquals(1, hits.size());
    assertEquals(1, ((Double) hits.get(0)).intValue());
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLengthOfEmptyString() throws Exception {
    final List<?> hits = getByXpath("string-length('')");
    assertEquals(1, hits.size());
    assertEquals(0, ((Double) hits.get(0)).intValue());
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLengthOfString() throws Exception {
    final List<?> hits = getByXpath("string-length('0123456789')");
    assertEquals(1, hits.size());
    assertEquals(10, ((Double) hits.get(0)).intValue());
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLengthFunctionOperatesOnContextNode() throws Exception {
    final List<?> hits = getByXpath("string-length()");
    assertEquals(1, hits.size());
    assertEquals(0, ((Double) hits.get(0)).intValue());
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLengthFunctionRequiresAtMostOneArguments() throws Exception {
    assertGetByXpathException(
        "string-length('a', 7)",
        "Could not retrieve XPath >string-length('a', 7)< on [#document: null]",
        "FuncStringLength only allows 0 or 1 arguments");
  }
}
