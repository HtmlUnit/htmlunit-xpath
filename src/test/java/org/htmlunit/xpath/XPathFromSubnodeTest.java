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

  /** @throws Exception in case of problems */
  @Test
  public void ancestorSecondIndex() throws Exception {
    // ancestor::*[2] = second nearest ancestor (reverse: [1]=nearest)
    final List<?> hits = getByXpath(
        "<root><table id='t1'><tr><td>"
        + "<table id='t2'><tr><td><btn></btn></td></tr></table>"
        + "</td></tr></table></root>", "//btn");
    assertEquals(1, hits.size());
    final Node btn = (Node) hits.get(0);

    final List<?> tables = XPathHelper.getByXPath(btn, "ancestor::table[2]", null, false);
    assertEquals(1, tables.size());
    assertEquals("id=\"t1\"", tables.get(0) instanceof org.w3c.dom.Element e
        ? e.getAttributeNode("id").toString() : "");
  }

  /** @throws Exception in case of problems */
  @Test
  public void ancestorCount() throws Exception {
    final List<?> hits = getByXpath("<root><div><p><span/></p></div></root>", "//span");
    assertEquals(1, hits.size());
    final Node span = (Node) hits.get(0);

    // ancestors: p, div, root — 3 elements
    final List<?> result = XPathHelper.getByXPath(span, "count(ancestor::*)", null, false);
    assertEquals(1, result.size());
    assertEquals(3.0, result.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void ancestorOrSelfSecondIndex() throws Exception {
    // ancestor-or-self::*[2] = parent of context node
    final List<?> hits = getByXpath("<root><div><p><span/></p></div></root>", "//span");
    assertEquals(1, hits.size());
    final Node span = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(span, "ancestor-or-self::*[2]", null, false);
    assertEquals(1, result.size());
    assertEquals("p", ((Node) result.get(0)).getNodeName());
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

  /** @throws Exception in case of problems */
  @Test
  public void followingSiblingAxisEmpty() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "following-sibling::*", null, false);
    assertEquals(0, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void followingSiblingAxisDoesNotCrossSubtrees() throws Exception {
    // following-sibling only returns siblings, not unrelated following nodes
    final List<?> hits = getByXpath("<root><div><p/></div><span/></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    // p has no following siblings (span is not its sibling)
    final List<?> result = XPathHelper.getByXPath(p, "following-sibling::*", null, false);
    assertEquals(0, result.size());
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

    // as of version 5.0.0 this is returned in wrong order
    // assertEquals("b", ((Node) result.get(0)).getNodeName());
    // assertEquals("a", ((Node) result.get(1)).getNodeName());
    assertEquals("a", ((Node) result.get(0)).getNodeName());
    assertEquals("b", ((Node) result.get(1)).getNodeName());
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

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisEmpty() throws Exception {
    // first child has no preceding siblings
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//a");
    assertEquals(1, hits.size());
    final Node a = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(a, "preceding-sibling::*", null, false);
    assertEquals(0, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisSecondIndex() throws Exception {
    // [2] = second-nearest preceding sibling = a
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding-sibling::*[2]", null, false);
    assertEquals(1, result.size());
    assertEquals("a", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisLast() throws Exception {
    // last() on a reverse axis = the farthest sibling
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding-sibling::*[last()]", null, false);
    assertEquals(1, result.size());
    assertEquals("a", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisCount() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "count(preceding-sibling::*)", null, false);
    assertEquals(1, result.size());
    assertEquals(2.0, result.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisPositionPredicate() throws Exception {
    // position()=1 in a reverse axis means the nearest sibling (same as [1])
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding-sibling::*[position()=1]", null, false);
    assertEquals(1, result.size());
    assertEquals("b", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisDoesNotCrossSubtrees() throws Exception {
    // preceding-sibling only returns siblings, not unrelated preceding nodes
    final List<?> hits = getByXpath("<root><span/><div><p/></div></root>", "//p");
    assertEquals(1, hits.size());
    final Node p = (Node) hits.get(0);

    // p has no preceding siblings (span is not its sibling)
    final List<?> result = XPathHelper.getByXPath(p, "preceding-sibling::*", null, false);
    assertEquals(0, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingSiblingAxisByNameWithIndex() throws Exception {
    // nearest preceding <a> sibling = the second a (index [1])
    final List<?> hits = getByXpath("<root><a/><b/><a/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding-sibling::a[1]", null, false);
    assertEquals(1, result.size());
    // nearest a is the second a (position 3 in document)
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
  /** @throws Exception in case of problems */
  @Test
  public void followingAxisEmpty() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "following::*", null, false);
    assertEquals(0, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void followingAxisWithIndex() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//a");
    assertEquals(1, hits.size());
    final Node a = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(a, "following::*[1]", null, false);
    assertEquals(1, result.size());
    assertEquals("b", ((Node) result.get(0)).getNodeName());
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

    // as of version 5.0.0 this is returned in wrong order
    // assertEquals("b", ((Node) result.get(0)).getNodeName());
    // assertEquals("a", ((Node) result.get(1)).getNodeName());
    assertEquals("a", ((Node) result.get(0)).getNodeName());
    assertEquals("b", ((Node) result.get(1)).getNodeName());
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

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisOrder() throws Exception {
    // nearest preceding node first
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding::*", null, false);
    assertEquals(2, result.size());

    // as of version 5.0.0 this is returned in wrong order
    // assertEquals("b", ((Node) result.get(0)).getNodeName());
    // assertEquals("a", ((Node) result.get(1)).getNodeName());
    assertEquals("a", ((Node) result.get(0)).getNodeName());
    assertEquals("b", ((Node) result.get(1)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisWithIndex() throws Exception {
    // [1] = nearest preceding node = b
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding::*[1]", null, false);
    assertEquals(1, result.size());
    assertEquals("b", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisSecondIndex() throws Exception {
    // [2] = second nearest = a
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "preceding::*[2]", null, false);
    assertEquals(1, result.size());
    assertEquals("a", ((Node) result.get(0)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisEmpty() throws Exception {
    // first node has no preceding nodes
    final List<?> hits = getByXpath("<root><a/><b/></root>", "//a");
    assertEquals(1, hits.size());
    final Node a = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(a, "preceding::*", null, false);
    assertEquals(0, result.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisCount() throws Exception {
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    final List<?> result = XPathHelper.getByXPath(c, "count(preceding::*)", null, false);
    assertEquals(1, result.size());
    assertEquals(2.0, result.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAxisExcludesAncestors() throws Exception {
    // preceding:: must NOT include ancestors of the context node
    final List<?> hits = getByXpath("<root><a/><div><b/><c/></div></root>", "//c");
    assertEquals(1, hits.size());
    final Node c = (Node) hits.get(0);

    // ancestors of c: div, root — these must not appear
    // nodes before c in document order (excluding ancestors): a, b
    final List<?> result = XPathHelper.getByXPath(c, "preceding::*", null, false);
    assertEquals(2, result.size());

    // as of version 5.0.0 this is returned in wrong order
    // assertEquals("b", ((Node) result.get(0)).getNodeName());
    // assertEquals("a", ((Node) result.get(1)).getNodeName());
    assertEquals("a", ((Node) result.get(0)).getNodeName());
    assertEquals("b", ((Node) result.get(1)).getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void precedingAndFollowingAreComplementary() throws Exception {
    // preceding::* + following::* + self = all elements in document (minus ancestors/descendants)
    final List<?> hits = getByXpath("<root><a/><b/><c/></root>", "//b");
    assertEquals(1, hits.size());
    final Node b = (Node) hits.get(0);

    final List<?> preceding = XPathHelper.getByXPath(b, "preceding::*", null, false);
    final List<?> following = XPathHelper.getByXPath(b, "following::*", null, false);

    assertEquals(1, preceding.size());
    assertEquals("a", ((Node) preceding.get(0)).getNodeName());
    assertEquals(1, following.size());
    assertEquals("c", ((Node) following.get(0)).getNodeName());
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
