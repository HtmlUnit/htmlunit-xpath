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

/** Unit test for not() function. */
public class NotTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void notZero() throws Exception {
    List<?> hits = getByXpath("not(0)");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void notOne() throws Exception {
    List<?> hits = getByXpath("not(1)");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void notEmptyString() throws Exception {
    List<?> hits = getByXpath("not('')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void notString() throws Exception {
    List<?> hits = getByXpath("not('false')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void notFunctionRequiresAtLeastOneArgument() throws Exception {
    assertGetByXpathException(
        "not()",
        "Could not retrieve XPath >not()< on [#document: null]",
        "FuncNot only allows 1 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void notFunctionRequiresExactlyOneArgument() throws Exception {
    assertGetByXpathException(
        "not('', '')",
        "Could not retrieve XPath >not('', '')< on [#document: null]",
        "FuncNot only allows 1 arguments");
  }
}
