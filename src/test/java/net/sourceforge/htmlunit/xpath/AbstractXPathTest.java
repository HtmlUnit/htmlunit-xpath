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

import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.w3c.dom.Document;

/** Parent for our tests */
public abstract class AbstractXPathTest {

  public <T> List<T> getByXpath(final String xml, final String xPath) throws Exception {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(IOUtils.toInputStream(xml, StandardCharsets.UTF_8));

    return XPathHelper.getByXPath(doc, xPath, null, false);
  }

  public <T> List<T> getByXpath(final String xPath) throws Exception {
    return getByXpath("<root></root>", xPath);
  }

  public void assertGetByXpathException(
      final String xml, final String xPath, final String exMsg, final String exCauseMsg)
      throws Exception {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(IOUtils.toInputStream(xml, StandardCharsets.UTF_8));

    Exception exception =
        Assertions.assertThrows(
            RuntimeException.class,
            () -> {
              XPathHelper.getByXPath(doc, xPath, null, false);
            });
    Assertions.assertEquals(exMsg, exception.getMessage());
    Assertions.assertEquals(exCauseMsg, exception.getCause().getMessage());
  }

  public void assertGetByXpathException(
      final String xPath, final String exMsg, final String exCauseMsg) throws Exception {
    assertGetByXpathException("<root></root>", xPath, exMsg, exCauseMsg);
  }
}
