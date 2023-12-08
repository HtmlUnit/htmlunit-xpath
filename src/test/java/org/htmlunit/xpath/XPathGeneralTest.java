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
package org.htmlunit.xpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

/** Unit test for simple App. */
public class XPathGeneralTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void simpleSearch() throws Exception {
    final List<?> hits = getByXpath("<root><element/></root>", "//element");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void pipeSearch() throws Exception {
    final List<?> hits = getByXpath("<root><element/><element2/></root>", "//element | //element2");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void mathSearch() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()=(1+5-(2*2))div 2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void gtSearch() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()>1]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void gteSearch() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()>=1]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void ltSearch() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()<2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void lteSearch() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()<=2]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void eqSearch() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()=2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void neqSearch() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/><p/><p/></root>", "//p[position()!=2]");
    assertEquals(3, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void andSearch() throws Exception {
    final List<?> hits =
        getByXpath("<root><p a='1' b='2'/><p/><p/><p/></root>", "//p[@a=1 and @b=2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void orSearch() throws Exception {
    final List<?> hits =
        getByXpath("<root><p a='1'/><p b='2'/><p/><p/></root>", "//p[@a=1 or @b=2]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void modSearch() throws Exception {
    final List<?> hits =
        getByXpath("<root><p a='1'/><p a='2'/><p a='3'/><p a='4'/></root>", "//p[@a mod 2 = 0]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void numberSearch() throws Exception {
    final List<?> hits =
        getByXpath(
            "<root><p a='1'/><p a='2'/><p a='3'/><p a='4'/></root>", "//p[@a=number('  4\t')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void attributeSearch() throws Exception {
    final List<?> hits =
        getByXpath("<root><p/><p name='test'/><p/><p/></root>", "//p[@name='test']");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void attributeSearchDoubleQuotes() throws Exception {
    final List<?> hits =
        getByXpath("<root><p/><p name='test'/><p/><p/></root>", "//p[@name=\"test\"]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void parentOfSelection() throws Exception {
    final String xml =
        "<html>\n"
            + "<a id='a1'><img src='1.gif'></img></a>"
            + "<a id='a2'><img src='1.gif'></img></a>"
            + "</html>";

    final List<?> hits = getByXpath(xml, "(/html/a/img[contains(@src,'gif')])[2]/..");
    assertEquals(1, hits.size());
    assertEquals("a2", ((Element) hits.get(0)).getAttribute("id"));
  }

  /** @throws Exception in case of problems */
  @Test
  public void pathWithParentheses() throws Exception {
    final List<?> hits = getByXpath("<root><child></child></root>", "(/root)/child");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void evaluateWithMultiNodeAnswer() throws Exception {
    final List<?> hits = getByXpath("(/descendant-or-self::node())");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void evaluateString() throws Exception {
    final List<?> hits = getByXpath("string(/*)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void errorMissingDoubleQuotes() throws Exception {
    assertGetByXpathException(
        "<root><p/><p name='test'/><p/><p/></root>",
        "//p[@name=\"test]",
        "Could not retrieve XPath >//p[@name=\"test]< on [#document: null]",
        "misquoted literal... expected double quote!");
  }

  /** @throws Exception in case of problems */
  @Test
  public void errorMissingSingleQuotes() throws Exception {
    assertGetByXpathException(
        "<root><p/><p name='test'/><p/><p/></root>",
        "//p[@name=test']",
        "Could not retrieve XPath >//p[@name=test']< on [#document: null]",
        "misquoted literal... expected single quote!");
  }
}
