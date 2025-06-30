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

/** Unit test for sum() function. */
public class QnameTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void name() throws Exception {
    final List<?> hits = getByXpath("<root>4</root>", "name(/root)");
    assertEquals(1, hits.size());
    assertEquals("root", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void nameMany() throws Exception {
    final List<?> hits = getByXpath("<root><a>4</a><a>3</a></root>", "name(/*/a)");
    assertEquals(1, hits.size());
    assertEquals("a", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void nameEmpty() throws Exception {
    final List<?> hits = getByXpath("name(/o)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void nameOfNumber() throws Exception {
    assertGetByXpathException(
        "name(7)",
        "Could not retrieve XPath >name(7)< on [#document: null]",
        "Can not convert #NUMBER to a NodeList!");
  }

  /** @throws Exception in case of problems */
  @Test
  public void nameNoArguments() throws Exception {
    final List<?> hits = getByXpath("name()");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void nameOfComment() throws Exception {
    final List<?> hits = getByXpath("<root><!-- comment --></root>", "name(/root/comment())");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void nameOfAttribute() throws Exception {
    final List<?> hits = getByXpath("<root attr='abc'></root>", "name(/*/@*)");
    assertEquals(1, hits.size());
    assertEquals("attr", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void nameOfText() throws Exception {
    final List<?> hits = getByXpath("<root>abc</root>", "name(/*/text())");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }
}
