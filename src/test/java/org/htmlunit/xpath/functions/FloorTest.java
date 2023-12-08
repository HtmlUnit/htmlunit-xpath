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

/** Unit test for floor() function. */
public class FloorTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void floor() throws Exception {
    final List<?> hits = getByXpath("floor(1.5)");
    assertEquals(1, hits.size());
    assertEquals(1, ((Double) hits.get(0)).doubleValue(), 0.0001);
  }

  /** @throws Exception in case of problems */
  @Test
  public void negativeFloor() throws Exception {
    final List<?> hits = getByXpath("floor(-1.5)");
    assertEquals(1, hits.size());
    assertEquals(-2, ((Double) hits.get(0)).doubleValue(), 0.0001);
  }

  /** @throws Exception in case of problems */
  @Test
  public void naNFloorIsNaN() throws Exception {
    final List<?> hits = getByXpath("floor(1.0 div 0.0 - 2.0 div 0.0)");
    assertEquals(1, hits.size());
    final double result = ((Double) hits.get(0)).doubleValue();
    assertTrue(Double.isNaN(result));
  }

  /** @throws Exception in case of problems */
  @Test
  public void infFloorIsInf() throws Exception {
    final List<?> hits = getByXpath("floor(1.0 div 0.0)");
    assertEquals(1, hits.size());
    final double result = ((Double) hits.get(0)).doubleValue();
    assertTrue(Double.isInfinite(result));
    assertTrue(result > 0);
  }

  /** @throws Exception in case of problems */
  @Test
  public void negativeInfFloorIsNegativeInf() throws Exception {
    final List<?> hits = getByXpath("floor(-11.0 div 0.0)");
    assertEquals(1, hits.size());
    final double result = ((Double) hits.get(0)).doubleValue();
    assertTrue(Double.isInfinite(result));
    assertTrue(result < 0);
  }

  /** @throws Exception in case of problems */
  @Test
  public void floorFunctionRequiresAtLeastOneArgument() throws Exception {
    assertGetByXpathException(
        "floor()",
        "Could not retrieve XPath >floor()< on [#document: null]",
        "FuncFloor only allows 1 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void floorFunctionRequiresExactlyOneArgument() throws Exception {
    assertGetByXpathException(
        "floor(2.2, 1.2)",
        "Could not retrieve XPath >floor(2.2, 1.2)< on [#document: null]",
        "FuncFloor only allows 1 arguments");
  }
}
