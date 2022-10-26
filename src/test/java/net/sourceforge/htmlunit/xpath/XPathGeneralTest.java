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
package net.sourceforge.htmlunit.xpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Unit test for simple App. */
public class XPathGeneralTest extends XPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void simpleSearch() throws Exception {
    List<?> hits = getByXpath("<root><element/></root>", "//element");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void pipeSearch() throws Exception {
    List<?> hits = getByXpath("<root><element/><element2/></root>", "//element | //element2");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void mathSearch() throws Exception {
    List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()=(1+5-(2*2))div 2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void gtSearch() throws Exception {
    List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()>1]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void gteSearch() throws Exception {
    List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()>=1]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void ltSearch() throws Exception {
    List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()<2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void lteSearch() throws Exception {
    List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()<=2]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void eqSearch() throws Exception {
    List<?> hits = getByXpath("<root><p/><p/></root>", "//p[position()=2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void neqSearch() throws Exception {
    List<?> hits = getByXpath("<root><p/><p/><p/><p/></root>", "//p[position()!=2]");
    assertEquals(3, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void andSearch() throws Exception {
    List<?> hits = getByXpath("<root><p a='1' b='2'/><p/><p/><p/></root>", "//p[@a=1 and @b=2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void orSearch() throws Exception {
    List<?> hits = getByXpath("<root><p a='1'/><p b='2'/><p/><p/></root>", "//p[@a=1 or @b=2]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void modSearch() throws Exception {
    List<?> hits =
        getByXpath("<root><p a='1'/><p a='2'/><p a='3'/><p a='4'/></root>", "//p[@a mod 2 = 0]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void numberSearch() throws Exception {
    List<?> hits =
        getByXpath(
            "<root><p a='1'/><p a='2'/><p a='3'/><p a='4'/></root>", "//p[@a=number('  4\t')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void attributeSearch() throws Exception {
    List<?> hits = getByXpath("<root><p/><p name='test'/><p/><p/></root>", "//p[@name='test']");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void attributeSearchDoubleQuotes() throws Exception {
    List<?> hits = getByXpath("<root><p/><p name='test'/><p/><p/></root>", "//p[@name=\"test\"]");
    assertEquals(1, hits.size());
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
