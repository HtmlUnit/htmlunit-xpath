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

/** Unit test for normalizeSpace() function. */
public class NormalizeSpaceTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void normalizeSpaceEmpty() throws Exception {
    final List<?> hits = getByXpath("normalize-space('')");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0).toString());
  }

  /** @throws Exception in case of problems */
  @Test
  public void normalizeSpaceNoParam() throws Exception {
    final List<?> hits = getByXpath("normalize-space()");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0).toString());
  }

  /** @throws Exception in case of problems */
  @Test
  public void normalizeSpaceRequiresAtMostOneArguments() throws Exception {
    assertGetByXpathException(
        "normalize-space('a', 'a')",
        "Could not retrieve XPath >normalize-space('a', 'a')< on [#document: null]",
        "FuncNormalizeSpace only allows 0 or 1 arguments");
  }
}
