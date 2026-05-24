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
import org.w3c.dom.Node;

/**
 * Unit tests for XPath expressions evaluated from a subnode context (not the document root).
 * Each test first locates a specific node using a root-based XPath, then evaluates
 * a relative or axis-based XPath from that node as context.
 */
public class XPathFromSubnodeTest extends AbstractXPathTest {

  // -------------------------------------------------------------------------
  // ancestor axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void ancestor() throws Exception {
    final List<?> hits = getByXpath(
        "<root><table id='t1'><tr><td>"
        + "<table id='t2'><tr><td><btn></btn></td></tr></table>"
        + "</td></tr></table></root>", "//btn");
    assertEquals(1, hits.size());
    final Node btn = (Node) hits.get(0);

    final List<?> tables = XPathHelper.getByXPath(btn, "ancestor::table", null, false);
    assertEquals(2, tables.size());

    Node tbl = (Node) tables.get(0);
    assertEquals("id=\"t1\"", tbl.getAttributes().getNamedItem("id").toString());

    tbl = (Node) tables.get(1);
    assertEquals("id=\"t2\"", tbl.getAttributes().getNamedItem("id").toString());
  }

  /** @throws Exception in case of problems */
  @Test
  public void ancestorWithIndex() throws Exception {
    final List<?> hits = getByXpath(
        "<root><table id='t1'><tr><td>"
        + "<table id='t2'><tr><td><btn></btn></td></tr></table>"
        + "</td></tr></table></root>", "//btn");
    assertEquals(1, hits.size());
    final Node btn = (Node) hits.get(0);

    final List<?> tables = XPathHelper.getByXPath(btn, "ancestor::table[1]", null, false);
    assertEquals(1, tables.size());

    final Node tbl = (Node) tables.get(0);
    assertEquals("id=\"t2\"", tbl.getAttributes().getNamedItem("id").toString());
  }

  // -------------------------------------------------------------------------
  // ancestor-or-self axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void ancestorOrSelf() throws Exception {
    final List<?> hits = getByXpath(
        "<root><div><p><span/></p></div></root>", "//span");
    assertEquals(1, hits.size());
    final Node span = (Node) hits.get(0);

    // ancestors-or-self of span: span, p, div, root  (4 elements)
    final List<?> result = XPathHelper.getByXPath(span, "ancestor-or-self::*", null, false);
    assertEquals(4, result.size());
    assertEquals("root", ((Node) result.get(0)).getNodeName());
    assertEquals("div",  ((Node) result.get(1)).getNodeName());
    assertEquals("p",    ((Node) result.get(2)).getNodeName());
    assertEquals("span", ((Node) result.get(3)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void ancestorOrSelfWithIndex() throws Exception {
    final List<?> hits = getByXpath(
        "<root><div><p><span/></p></div></root>", "//span");
    assertEquals(1, hits.size());
    final Node span = (Node) hits.get(0);

    // ancestor-or-self::*[1] = the context node itself (span)
    final List<?> result = XPathHelper.getByXPath(span, "ancestor-or-self::*[1]", null, false);
    assertEquals(1, result.size());
    assertEquals("span", ((Node) result.get(0)).getNodeName());
  }

  // -------------------------------------------------------------------------
  // parent axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void parentAxis() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/></div></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(p, "parent::div", null, false);
    assertEquals(1, result.size());
    assertEquals("div", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void parentAxisNoMatch() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/></div></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    // parent is div, not span — expect no results
    final List<?> result = XPathHelper.getByXPath(p, "parent::span", null, false);
    assertEquals(0, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void parentShorthand() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/></div></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(p, "..", null, false);
    assertEquals(1, result.size());
    assertEquals("div", ((Node) result.get(0)).getNodeName());
  }

  // -------------------------------------------------------------------------
  // self axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void selfAxis() throws Exception {
    final List<?> hits = getByXpath("<root><p/></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(p, "self::p", null, false);
    assertEquals(1, result.size());
    assertEquals("p", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void selfAxisNoMatch() throws Exception {
    final List<?> hits = getByXpath("<root><p/></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    // self is p, not div
    final List<?> result = XPathHelper.getByXPath(p, "self::div", null, false);
    assertEquals(0, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void selfShorthand() throws Exception {
    final List<?> hits = getByXpath("<root><p/></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(p, ".", null, false);
    assertEquals(1, result.size());
    assertEquals("p", ((Node) result.get(0)).getNodeName());
  }

  // -------------------------------------------------------------------------
  // child axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void childAxis() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/><p/><span/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, "child::p", null, false);
    assertEquals(2, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void childAxisAll() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/><p/><span/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, "child::*", null, false);
    assertEquals(3, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void relativeChildPath() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/><p/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    // implicit child axis
    final List<?> result = XPathHelper.getByXPath(div, "p", null, false);
    assertEquals(2, result.size());
  }

  // -------------------------------------------------------------------------
  // descendant axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void descendantAxis() throws Exception {
    final List<?> hits = getByXpath("<root><div><p><span/></p><span/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, "descendant::span", null, false);
    assertEquals(2, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void descendantAxisAll() throws Exception {
    final List<?> hits = getByXpath("<root><div><p><span/></p></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, "descendant::*", null, false);
    assertEquals(2, result.size()); // p and span
  }

  /** @throws Exception in case of problems */
  @Test
  public void relativeDescendantPath() throws Exception {
    final List<?> hits = getByXpath("<root><div><p><span/></p><span/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, ".//span", null, false);
    assertEquals(2, result.size());
  }

  // -------------------------------------------------------------------------
  // descendant-or-self axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void descendantOrSelfAxis() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/><p/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    // div itself + 2 p children
    final List<?> result = XPathHelper.getByXPath(div, "descendant-or-self::*", null, false);
    assertEquals(3, result.size());
  }

  // -------------------------------------------------------------------------
  // following-sibling axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void followingSiblingAxis() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//a");
    assertEquals(1, hits.size());
    final Node a = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(a, "following-sibling::*", null, false);
    assertEquals(2, result.size());
    assertEquals("b", ((Node) result.get(0)).getNodeName());
    assertEquals("c", ((Node) result.get(1)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void followingSiblingAxisWithIndex() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//a");
    assertEquals(1, hits.size());
    final Node a = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(a, "following-sibling::*[1]", null, false);
    assertEquals(1, result.size());
    assertEquals("b", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void followingSiblingAxisByName() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><a/><c/></root>", "//a[1]");
    assertEquals(1, hits.size());
    final Node a = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(a, "following-sibling::a", null, false);
    assertEquals(1, result.size());
  }

  // -------------------------------------------------------------------------
  // preceding-sibling axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxis() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding-sibling::*", null, false);
    assertEquals(2, result.size());
    // preceding-sibling is reverse axis: nearest sibling first
    assertEquals("b", ((Node) result.get(0)).getNodeName());
    assertEquals("a", ((Node) result.get(1)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisWithIndex() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    // [1] = nearest preceding sibling = b
    final List<?> result = XPathHelper.getByXPath(c, "preceding-sibling::*[1]", null, false);
    assertEquals(1, result.size());
    assertEquals("b", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisByName() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><a/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding-sibling::a", null, false);
    assertEquals(2, result.size());
  }

  // -------------------------------------------------------------------------
  // following axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void followingAxis() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//a");
    assertEquals(1, hits.size());
    final Node a = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(a, "following::*", null, false);
    assertEquals(2, result.size());
    assertEquals("b", ((Node) result.get(0)).getNodeName());
    assertEquals("c", ((Node) result.get(1)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void followingAxisNested() throws Exception {
    // following:: crosses subtree boundaries, unlike following-sibling::
    final List<?> hits = getByXpath("<root><div><p/></div><span/></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    // p's following nodes: span (sibling of div, but following p in document order)
    final List<?> result = XPathHelper.getByXPath(p, "following::*", null, false);
    assertEquals(1, result.size());
    assertEquals("span", ((Node) result.get(0)).getNodeName());
  }

  // -------------------------------------------------------------------------
  // preceding axis
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxis() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding::*", null, false);
    assertEquals(2, result.size());
    assertEquals("b", ((Node) result.get(0)).getNodeName());
    assertEquals("a", ((Node) result.get(1)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisNested() throws Exception {
    // preceding:: crosses subtree boundaries, unlike preceding-sibling::
    final List<?> hits = getByXpath("<root><span/><div><p/></div></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    // p's preceding nodes: span (sibling of div, but preceding p in document order)
    final List<?> result = XPathHelper.getByXPath(p, "preceding::*", null, false);
    assertEquals(1, result.size());
    assertEquals("span", ((Node) result.get(0)).getNodeName());
  }

  // -------------------------------------------------------------------------
  // Predicates from subnode context
  // -------------------------------------------------------------------------

  /** @throws Exception in case of problems */
  @Test
  public void childWithPositionPredicate() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/><p/><p/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, "p[2]", null, false);
    assertEquals(1, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void childWithAttributePredicate() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/><p name='x'/><p/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, "p[@name='x']", null, false);
    assertEquals(1, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void childWithLastPredicate() throws Exception {
    final List<?> hits = getByXpath("<root><div><p/><p/><p/></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, "p[last()]", null, false);
    assertEquals(1, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void deepRelativePath() throws Exception {
    final List<?> hits = getByXpath("<root><div><ul><li><a/></li></ul></div></root>", "//div");
    assertEquals(1, hits.size());
    final Node div = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(div, "ul/li/a", null, false);
    assertEquals(1, result.size());
  }
}
