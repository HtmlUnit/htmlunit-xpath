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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/** Unit tests for the NodeSet class. */
public class NodeSetTest {

  private Document parseXml(final String xml) throws Exception {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(IOUtils.toInputStream(xml, StandardCharsets.UTF_8));
  }

  /** @throws Exception in case of problems */
  @Test
  public void getLengthEmpty() throws Exception {
    final NodeSet nodeSet = new NodeSet(new ArrayList<>());
    assertEquals(0, nodeSet.getLength());
  }

  /** @throws Exception in case of problems */
  @Test
  public void getLengthPopulated() throws Exception {
    final Document doc = parseXml("<root><a/><b/><c/></root>");
    final List<Node> nodes = new ArrayList<>();
    nodes.add(doc.getDocumentElement().getChildNodes().item(0));
    nodes.add(doc.getDocumentElement().getChildNodes().item(1));
    nodes.add(doc.getDocumentElement().getChildNodes().item(2));

    final NodeSet nodeSet = new NodeSet(nodes);
    assertEquals(3, nodeSet.getLength());
  }

  /** @throws Exception in case of problems */
  @Test
  public void itemValidIndex() throws Exception {
    final Document doc = parseXml("<root><a/><b/></root>");
    final List<Node> nodes = new ArrayList<>();
    nodes.add(doc.getDocumentElement().getChildNodes().item(0));
    nodes.add(doc.getDocumentElement().getChildNodes().item(1));

    final NodeSet nodeSet = new NodeSet(nodes);

    final Node first = nodeSet.item(0);
    assertNotNull(first);
    assertEquals("a", first.getNodeName());

    final Node second = nodeSet.item(1);
    assertNotNull(second);
    assertEquals("b", second.getNodeName());
  }

  /** @throws Exception in case of problems */
  @Test
  public void itemOutOfRangeThrows() throws Exception {
    final NodeSet nodeSet = new NodeSet(new ArrayList<>());
    assertNull(nodeSet.item(0));
    assertNull(nodeSet.item(10));
    assertNull(nodeSet.item(-1));
  }

  /** @throws Exception in case of problems */
  @Test
  public void nodeSetIsDefensiveCopy() throws Exception {
    final Document doc = parseXml("<root><a/></root>");
    final List<Node> nodes = new ArrayList<>();
    nodes.add(doc.getDocumentElement().getChildNodes().item(0));

    final NodeSet nodeSet = new NodeSet(nodes);
    assertEquals(1, nodeSet.getLength());

    // Modifying the original list should not affect the NodeSet
    nodes.clear();
    assertEquals(1, nodeSet.getLength());
  }
}