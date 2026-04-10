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

/** Unit tests for the XPath number() function. */
public class NumberFuncTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void numberFromString() throws Exception {
    final String xml = "<root><p>42</p></root>";
    final List<?> hits = getByXpath(xml, "//p[number(.)=42]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void numberFromStringWithWhitespace() throws Exception {
    final String xml = "<root><p a='  4\t'/></root>";
    final List<?> hits = getByXpath(xml, "//p[@a=number('  4\t')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void numberFromTrue() throws Exception {
    final String xml = "<root><p a='1'/></root>";
    final List<?> hits = getByXpath(xml, "//p[@a=number(true())]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void numberFromFalse() throws Exception {
    final String xml = "<root><p a='0'/></root>";
    final List<?> hits = getByXpath(xml, "//p[@a=number(false())]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void numberFromNonNumericString() throws Exception {
    final String xml = "<root><p>abc</p></root>";
    // number('abc') returns NaN, NaN != NaN so no match
    final List<?> hits = getByXpath(xml, "//p[number(.)=number(.)]");
    assertEquals(0, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void numberFromNegative() throws Exception {
    final String xml = "<root><p a='-5'/></root>";
    final List<?> hits = getByXpath(xml, "//p[number(@a)=-5]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void numberFromDecimal() throws Exception {
    final String xml = "<root><p a='3.14'/></root>";
    final List<?> hits = getByXpath(xml, "//p[number(@a)=3.14]");
    assertEquals(1, hits.size());
  }
}