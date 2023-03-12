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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.htmlunit.xpath.AbstractXPathTest;
import org.junit.jupiter.api.Test;

/** Unit test for ceiling() function. */
public class CeilingTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void ceiling() throws Exception {
    List<?> hits = getByXpath("ceiling(1.5)");
    assertEquals(1, hits.size());
    assertEquals(2, ((Double) hits.get(0)).doubleValue(), 0.0001);
  }

  /** @throws Exception in case of problems */
  @Test
  public void negativeCeiling() throws Exception {
    List<?> hits = getByXpath("ceiling(-1.5)");
    assertEquals(1, hits.size());
    assertEquals(-1, ((Double) hits.get(0)).doubleValue(), 0.0001);
  }

  /** @throws Exception in case of problems */
  @Test
  public void naNCeilingIsNaN() throws Exception {
    List<?> hits = getByXpath("ceiling(1.0 div 0.0 - 2.0 div 0.0)");
    assertEquals(1, hits.size());
    double result = ((Double) hits.get(0)).doubleValue();
    assertTrue(Double.isNaN(result));
  }

  /** @throws Exception in case of problems */
  @Test
  public void infCeilingIsInf() throws Exception {
    List<?> hits = getByXpath("ceiling(1.0 div 0.0)");
    assertEquals(1, hits.size());
    double result = ((Double) hits.get(0)).doubleValue();
    assertTrue(Double.isInfinite(result));
    assertTrue(result > 0);
  }

  /** @throws Exception in case of problems */
  @Test
  public void negativeInfCeilingIsNegativeInf() throws Exception {
    List<?> hits = getByXpath("ceiling(-11.0 div 0.0)");
    assertEquals(1, hits.size());
    double result = ((Double) hits.get(0)).doubleValue();
    assertTrue(Double.isInfinite(result));
    assertTrue(result < 0);
  }

  /** @throws Exception in case of problems */
  @Test
  public void ceilingFunctionRequiresAtLeastOneArgument() throws Exception {
    assertGetByXpathException(
        "ceiling()",
        "Could not retrieve XPath >ceiling()< on [#document: null]",
        "FuncCeiling only allows 1 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void ceilingFunctionRequiresExactlyOneArgument() throws Exception {
    assertGetByXpathException(
        "ceiling(2.2, 1.2)",
        "Could not retrieve XPath >ceiling(2.2, 1.2)< on [#document: null]",
        "FuncCeiling only allows 1 arguments");
  }
}
