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

/** Unit tests for the XPath position() function. */
public class PositionTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void positionFirst() throws Exception {
    final String xml = "<root><p/><p/><p/></root>";
    final List<?> hits = getByXpath(xml, "//p[position()=1]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void positionLast() throws Exception {
    final String xml = "<root><p/><p/><p/></root>";
    final List<?> hits = getByXpath(xml, "//p[position()=last()]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void positionGreaterThanOne() throws Exception {
    final String xml = "<root><p/><p/><p/></root>";
    final List<?> hits = getByXpath(xml, "//p[position()>1]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void positionLessThanLast() throws Exception {
    final String xml = "<root><p/><p/><p/></root>";
    final List<?> hits = getByXpath(xml, "//p[position()<last()]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void positionMiddle() throws Exception {
    final String xml = "<root><p/><p/><p/></root>";
    final List<?> hits = getByXpath(xml, "//p[position()=2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void positionSingleElement() throws Exception {
    final String xml = "<root><p/></root>";
    final List<?> hits = getByXpath(xml, "//p[position()=1 and position()=last()]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void positionInExpression() throws Exception {
    final String xml = "<root><p/><p/><p/><p/></root>";
    final List<?> hits = getByXpath(xml, "//p[position() mod 2 = 0]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void positionNotEquals() throws Exception {
    final String xml = "<root><p/><p/><p/></root>";
    final List<?> hits = getByXpath(xml, "//p[position()!=2]");
    assertEquals(2, hits.size());
  }
}