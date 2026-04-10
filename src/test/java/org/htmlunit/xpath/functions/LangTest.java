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

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.htmlunit.xpath.AbstractXPathTest;
import org.htmlunit.xpath.XPathHelper;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

/** Unit tests for the XPath lang() function. */
public class LangTest extends AbstractXPathTest {

  /**
   * Parse XML with namespace awareness enabled, which is required for
   * xml:lang to be recognized in the http://www.w3.org/XML/1998/namespace.
   */
  private <T> List<T> getByXpathNsAware(final String xml, final String xPath) throws Exception {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    final DocumentBuilder builder = factory.newDocumentBuilder();
    final Document doc = builder.parse(IOUtils.toInputStream(xml, StandardCharsets.UTF_8));
    return XPathHelper.getByXPath(doc, xPath, null, false);
  }

  /** @throws Exception in case of problems */
  @Test
  public void langMatchesExact() throws Exception {
    final String xml = "<root xml:lang='en'><p/></root>";
    final List<?> hits = getByXpathNsAware(xml, "//root[lang('en')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void langNoMatch() throws Exception {
    final String xml = "<root xml:lang='fr'><p/></root>";
    final List<?> hits = getByXpathNsAware(xml, "//root[lang('en')]");
    assertEquals(0, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void langMatchesSublanguage() throws Exception {
    final String xml = "<root xml:lang='en-US'><p/></root>";
    final List<?> hits = getByXpathNsAware(xml, "//root[lang('en')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void langCaseInsensitive() throws Exception {
    final String xml = "<root xml:lang='EN'><p/></root>";
    final List<?> hits = getByXpathNsAware(xml, "//root[lang('en')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void langInheritedFromParent() throws Exception {
    final String xml = "<root xml:lang='en'><child><p/></child></root>";
    final List<?> hits = getByXpathNsAware(xml, "//p[lang('en')]");
    assertEquals(1, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void langNoAttribute() throws Exception {
    final String xml = "<root><p/></root>";
    final List<?> hits = getByXpathNsAware(xml, "//p[lang('en')]");
    assertEquals(0, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void langExactSubtagNoMatch() throws Exception {
    final String xml = "<root xml:lang='en-US'><p/></root>";
    final List<?> hits = getByXpathNsAware(xml, "//root[lang('en-GB')]");
    assertEquals(0, hits.size());
  }
}