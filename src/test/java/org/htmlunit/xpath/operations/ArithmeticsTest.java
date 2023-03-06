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
package org.htmlunit.xpath.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.htmlunit.xpath.AbstractXPathTest;
import org.junit.jupiter.api.Test;

/** Unit test for arithmetic functions. */
public class ArithmeticsTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void numbersThatBeginWithADecimalPoint2() throws Exception {
    List<?> hits = getByXpath(".5 > .4");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void numbersThatBeginWithADecimalPoint() throws Exception {
    List<?> hits = getByXpath(".3 <= .4 <= 1.1");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void leftAssociativityOfLessThanOrEqual() throws Exception {
    List<?> hits = getByXpath(".3 <= .4 <= 0.9");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void negativeZeroNotEqualsZero() throws Exception {
    List<?> hits = getByXpath("0 != -0");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void negativeZeroEqualsZero() throws Exception {
    List<?> hits = getByXpath("0 = -0");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void zeroGreaterThanOrEqualsToNegativeZero() throws Exception {
    List<?> hits = getByXpath("0 >= -0");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void zeroLessThanOrEqualToNegativeZero() throws Exception {
    List<?> hits = getByXpath("0 <= -0");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void negativeZeroNotLessThanZero() throws Exception {
    List<?> hits = getByXpath("-0 < 0");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void naNNotEqualsString() throws Exception {
    List<?> hits = getByXpath("(0.0 div 0.0) != 'foo'");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void naNEqualsString() throws Exception {
    List<?> hits = getByXpath("(0.0 div 0.0) = 'foo'");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void equalityPrecedence() throws Exception {
    List<?> hits = getByXpath("1.5 = 2.3 = 2.3");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }
}
