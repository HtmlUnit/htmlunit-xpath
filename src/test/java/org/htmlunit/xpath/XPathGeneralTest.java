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
package org.htmlunit.xpath;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

/** Unit tests for XPath expressions evaluated from the document root. */
public class XPathGeneralTest extends AbstractXPathTest {

  // -------------------------------------------------------------------------
  // Basic navigation
  // -------------------------------------------------------------------------

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
  public void pathWithParentheses() throws Exception {
    final List<?> hits = getByXpath("<root><child></child></root>", "(/root)/child");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void parentOfSelection() throws Exception {
    final String xml =
        """
        <html>
        <a id='a1'><img src='1.gif'></img></a>\
        <a id='a2'><img src='1.gif'></img></a>\
        </html>""";

    final List<?> hits = getByXpath(xml, "(/html/a/img[contains(@src,'gif')])[2]/..");
    assertEquals(1, hits.size());
    assertEquals("a2", ((Element) hits.get(0)).getAttribute("id"));
  }

  // -------------------------------------------------------------------------
  // Axes
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void childAxis() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "/root/child::p");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void descendantAxis() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/></div><p/></root>", "/root/descendant::p");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void descendantOrSelfAxis() throws Exception {
    final List<?> hits = getByXpath("<root><p/><div><p/></div></root>", "/root/descendant-or-self::p");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void selfAxis() throws Exception {
    final List<?> hits = getByXpath("<root><p/></root>", "/root/self::root");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void followingSiblingAxis() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/><p/></root>", "//p[1]/following-sibling::p");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void followingSiblingAxisWithIndex() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//a/following-sibling::*[1]");
    assertEquals(1, hits.size());
    assertEquals("b", ((Element) hits.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxis() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/><p/></root>", "//p[3]/preceding-sibling::p");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisWithIndex() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c/preceding-sibling::*[1]");
    assertEquals(1, hits.size());
    assertEquals("b", ((Element) hits.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void followingAxis() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//a/following::*");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxis() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c/preceding::*");
    assertEquals(2, hits.size());
  }

  // -------------------------------------------------------------------------
  // Wildcards
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void wildcardElement() throws Exception {
    final List<?> hits = getByXpath("<root><p/><div/><span/></root>", "/root/*");
    assertEquals(3, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void wildcardAttribute() throws Exception {
    final List<?> hits = getByXpath("<root><p a='1'/><p/><p b='2'/></root>", "//p[@*]");
    assertEquals(2, hits.size());
  }

  // -------------------------------------------------------------------------
  // Node type tests
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void textNodeTest() throws Exception {
    final List<?> hits = getByXpath("<root><p>text</p><p/></root>", "//p/text()");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void commentNodeTest() throws Exception {
    final List<?> hits = getByXpath("<root><!-- comment --><p/></root>", "//comment()");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void nodeTest() throws Exception {
    final List<?> hits = getByXpath("<root><p/><div/></root>", "/root/node()");
    assertEquals(2, hits.size());
  }

  // -------------------------------------------------------------------------
  // Position / comparison operators
  // -------------------------------------------------------------------------

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

  // -------------------------------------------------------------------------
  // Logical operators
  // -------------------------------------------------------------------------

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

  // -------------------------------------------------------------------------
  // Attribute predicates
  // -------------------------------------------------------------------------

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

  // -------------------------------------------------------------------------
  // Boolean functions
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void notFunction() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p name='test'/><p/></root>", "//p[not(@name)]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void booleanFunction() throws Exception {
    final List<?> hits = getByXpath("<root><p a='1'/><p/></root>", "//p[boolean(@a)]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void trueFunction() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "//p[true()]");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void falseFunction() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/></root>", "//p[false()]");
    assertEquals(0, hits.size());
  }

  // -------------------------------------------------------------------------
  // Position functions
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void lastFunction() throws Exception {
    final List<?> hits = getByXpath("<root><p/><p/><p/></root>", "//p[last()]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void countFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><div><p/><p/></div><div><p/></div></root>", "//div[count(p)=2]");
    assertEquals(1, hits.size());
  }

  // -------------------------------------------------------------------------
  // String functions
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void containsFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='test'/><p name='other'/></root>", "//p[contains(@name,'es')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void startsWithFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='test'/><p name='other'/></root>", "//p[starts-with(@name,'te')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void stringLengthFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='ab'/><p name='abcd'/></root>", "//p[string-length(@name)>3]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void normalizeSpaceFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='  test  '/><p name='other'/></root>",
            "//p[normalize-space(@name)='test']");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void concatFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='hello world'/></root>",
            "//p[@name=concat('hello',' ','world')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='testing'/><p name='other'/></root>",
            "//p[substring(@name,1,4)='test']");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringBeforeFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='test-val'/><p name='other'/></root>",
            "//p[substring-before(@name,'-')='test']");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringAfterFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='test-val'/><p name='other'/></root>",
            "//p[substring-after(@name,'-')='val']");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void translateFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p name='TEST'/><p name='other'/></root>",
            "//p[translate(@name,'TEST','test')='test']");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void evaluateString() throws Exception {
    final List<?> hits = getByXpath("string(/*)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  // -------------------------------------------------------------------------
  // Name functions
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void nameFunction() throws Exception {
    final List<?> hits = getByXpath("<root><p/><div/><p/></root>", "//*[name()='p']");
    assertEquals(2, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void localNameFunction() throws Exception {
    final List<?> hits = getByXpath("<root><p/><div/><p/></root>", "//*[local-name()='p']");
    assertEquals(2, hits.size());
  }

  // -------------------------------------------------------------------------
  // Numeric functions
  // -------------------------------------------------------------------------

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
  public void sumFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><div><p a='3'/><p a='4'/></div><div><p a='1'/></div></root>",
            "//div[sum(p/@a) > 5]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void floorFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p a='2.7'/><p a='1.1'/></root>", "//p[floor(@a) = 2]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void ceilingFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p a='2.3'/><p a='1.9'/></root>", "//p[ceiling(@a) = 3]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void roundFunction() throws Exception {
    final List<?> hits =
        getByXpath("<root><p a='2.7'/><p a='2.3'/></root>", "//p[round(@a) = 3]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisOrder() throws Exception {
    // verify the result order: nearest first
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c/preceding-sibling::*");
    assertEquals(2, hits.size());

    // as of version 5.0.0 this is returned in wrong order
    // assertEquals("b", ((Element) hits.get(0)).getNodeName());
    // assertEquals("a", ((Element) hits.get(1)).getNodeName());
    assertEquals("a", ((Element) hits.get(0)).getNodeName());
    assertEquals("b", ((Element) hits.get(1)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisOrder() throws Exception {
    // verify the result order: nearest first
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c/preceding::*");
    assertEquals(2, hits.size());

    // as of version 5.0.0 this is returned in wrong order
    // assertEquals("b", ((Element) hits.get(0)).getNodeName());
    // assertEquals("a", ((Element) hits.get(1)).getNodeName());
    assertEquals("a", ((Element) hits.get(0)).getNodeName());
    assertEquals("b", ((Element) hits.get(1)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisWithIndex() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c/preceding::*[1]");
    assertEquals(1, hits.size());
    assertEquals("b", ((Element) hits.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void ancestorAxisFromRootedPath() throws Exception {
    final List<?> hits = getByXpath("<root><div><p><span/></p></div></root>", "//span/ancestor::*");
    // ancestors: p, div, root — 3 elements, nearest first
    assertEquals(3, hits.size());

    // as of version 5.0.0 this is returned in wrong order
    // assertEquals("p",    ((Element) hits.get(0)).getNodeName());
    // assertEquals("div",  ((Element) hits.get(1)).getNodeName());
    // assertEquals("root", ((Element) hits.get(2)).getNodeName());
    assertEquals("root", ((Element) hits.get(0)).getNodeName());
    assertEquals("div",  ((Element) hits.get(1)).getNodeName());
    assertEquals("p",    ((Element) hits.get(2)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void ancestorAxisWithIndexFromRootedPath() throws Exception {
    // [1] = nearest ancestor = p
    final List<?> hits = getByXpath("<root><div><p><span/></p></div></root>", "//span/ancestor::*[1]");
    assertEquals(1, hits.size());
    assertEquals("p", ((Element) hits.get(0)).getNodeName());
  }

  // -------------------------------------------------------------------------
  // Multi-node / special results
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void evaluateWithMultiNodeAnswer() throws Exception {
    final List<?> hits = getByXpath("(/descendant-or-self::node())");
    assertEquals(2, hits.size());
  }

  // -------------------------------------------------------------------------
  // Error cases
  // -------------------------------------------------------------------------

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
