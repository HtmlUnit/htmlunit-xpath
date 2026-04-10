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
package org.htmlunit.xpath.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.htmlunit.xpath.AbstractXPathTest;
import org.junit.jupiter.api.Test;

/** Unit tests for XPath comparison and logical operators. */
public class ComparisonOperationsTest extends AbstractXPathTest {

  private static final java.lang.String XML =
      "<root>"
          + "<item val='1' name='a'/>"
          + "<item val='2' name='b'/>"
          + "<item val='3' name='c'/>"
          + "<item val='4' name='d'/>"
          + "<item val='5' name='e'/>"
          + "</root>";

  // ============ Greater than ============

  /** @throws Exception in case of problems */
  @Test
  public void greaterThanNumber() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val > 3]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void greaterThanNone() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val > 5]");
    assertEquals(0, hits.size());
  }

  // ============ Greater than or equal ============

  /** @throws Exception in case of problems */
  @Test
  public void greaterThanOrEqualNumber() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val >= 3]");
    assertEquals(3, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void greaterThanOrEqualAll() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val >= 1]");
    assertEquals(5, hits.size());
  }

  // ============ Less than ============

  /** @throws Exception in case of problems */
  @Test
  public void lessThanNumber() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val < 3]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void lessThanNone() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val < 1]");
    assertEquals(0, hits.size());
  }

  // ============ Less than or equal ============

  /** @throws Exception in case of problems */
  @Test
  public void lessThanOrEqualNumber() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val <= 3]");
    assertEquals(3, hits.size());
  }

  // ============ Equals ============

  /** @throws Exception in case of problems */
  @Test
  public void equalsNumber() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val = 3]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void equalsString() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@name = 'c']");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void equalsNoMatch() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@name = 'z']");
    assertEquals(0, hits.size());
  }

  // ============ Not equals ============

  /** @throws Exception in case of problems */
  @Test
  public void notEqualsNumber() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val != 3]");
    assertEquals(4, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void notEqualsString() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@name != 'a']");
    assertEquals(4, hits.size());
  }

  // ============ Logical AND ============

  /** @throws Exception in case of problems */
  @Test
  public void andBothTrue() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val > 1 and @val < 5]");
    assertEquals(3, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void andOneFalse() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val > 1 and @val > 10]");
    assertEquals(0, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void andStringAndNumber() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@name = 'c' and @val = 3]");
    assertEquals(1, hits.size());
  }

  // ============ Logical OR ============

  /** @throws Exception in case of problems */
  @Test
  public void orBothFalse() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val > 10 or @val < 0]");
    assertEquals(0, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void orOneTrue() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val = 1 or @val = 5]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void orAllMatch() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val >= 1 or @val < 1]");
    assertEquals(5, hits.size());
  }

  // ============ Combined operators ============

  /** @throws Exception in case of problems */
  @Test
  public void combinedAndOrWithParentheses() throws Exception {
    final List<?> hits =
        getByXpath(XML, "//item[(@val = 1 or @val = 5) and @name != 'z']");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void comparisonWithArithmetic() throws Exception {
    final List<?> hits = getByXpath(XML, "//item[@val = (1 + 2)]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void notEqualsWithBoolean() throws Exception {
    final java.lang.String xml = "<root><p a='true'/><p a='false'/></root>";
    final List<?> hits = getByXpath(xml, "//p[@a != 'true']");
    assertEquals(1, hits.size());
  }
}