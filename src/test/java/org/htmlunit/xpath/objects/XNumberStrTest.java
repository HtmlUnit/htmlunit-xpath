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
package org.htmlunit.xpath.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Unit tests for XNumber.str() — number-to-string conversion. */
public class XNumberStrTest {

  /** @throws Exception in case of problems */
  @Test
  public void strNaN() {
    final XNumber xn = new XNumber(Double.NaN);
    assertEquals("NaN", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strPositiveInfinity() {
    final XNumber xn = new XNumber(Double.POSITIVE_INFINITY);
    assertEquals("Infinity", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strNegativeInfinity() {
    final XNumber xn = new XNumber(Double.NEGATIVE_INFINITY);
    assertEquals("-Infinity", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strZero() {
    final XNumber xn = new XNumber(0.0);
    assertEquals("0", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strNegativeZero() {
    final XNumber xn = new XNumber(-0.0);
    assertEquals("0", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strPositiveInteger() {
    final XNumber xn = new XNumber(5.0);
    assertEquals("5", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strNegativeInteger() {
    final XNumber xn = new XNumber(-42.0);
    assertEquals("-42", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strDecimal() {
    final XNumber xn = new XNumber(3.14);
    assertEquals("3.14", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strSmallDecimal() {
    final XNumber xn = new XNumber(0.5);
    assertEquals("0.5", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strOne() {
    final XNumber xn = new XNumber(1.0);
    assertEquals("1", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strLargeNumber() {
    final XNumber xn = new XNumber(1e20);
    assertEquals("100000000000000000000", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strSmallPositiveNumber() {
    final XNumber xn = new XNumber(0.001);
    assertEquals("0.001", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strVerySmallNumber() {
    final XNumber xn = new XNumber(1e-20);
    assertEquals("0.00000000000000000001", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strNegativeDecimal() {
    final XNumber xn = new XNumber(-0.25);
    assertEquals("-0.25", xn.str());
  }

  /** @throws Exception in case of problems */
  @Test
  public void strTrailingZeros() {
    // 1.5 should render as "1.5", not "1.50"
    final XNumber xn = new XNumber(1.5);
    assertEquals("1.5", xn.str());
  }
}