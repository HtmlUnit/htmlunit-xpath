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

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Unit test for simple App.
 */
public class XPathTest
{
    /**
     * @throws Exception in case of problems
     */
    @Test
    public void simpleSearch() throws Exception {
        final String input = "<root><element/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//element", null, false);
        assertEquals(1, hits.size());
    }

    /**
     * @throws Exception in case of problems
     */
    @Test
    public void pipeSearch() throws Exception {
        final String input = "<root><element/><element2/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//element | //element2", null, false);
        assertEquals(2, hits.size());
    }

    /**
     * @throws Exception in case of problems
     */
    @Test
    public void mathSearch() throws Exception {
        final String input = "<root><p/><p/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//p[position()=(1+5-(2*2))div 2]", null, false);
        assertEquals(1, hits.size());
    }

    /**
     * @throws Exception in case of problems
     */
    @Test
    public void gtSearch() throws Exception {
        final String input = "<root><p/><p/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//p[position()>1]", null, false);
        assertEquals(1, hits.size());
    }

    /**
     * @throws Exception in case of problems
     */
    @Test
    public void gteSearch() throws Exception {
        final String input = "<root><p/><p/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//p[position()>=1]", null, false);
        assertEquals(2, hits.size());
    }


    /**
     * @throws Exception in case of problems
     */
    @Test
    public void ltSearch() throws Exception {
        final String input = "<root><p/><p/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//p[position()<2]", null, false);
        assertEquals(1, hits.size());
    }

    /**
     * @throws Exception in case of problems
     */
    @Test
    public void lteSearch() throws Exception {
        final String input = "<root><p/><p/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//p[position()<=2]", null, false);
        assertEquals(2, hits.size());
    }

    /**
     * @throws Exception in case of problems
     */
    @Test
    public void eqSearch() throws Exception {
        final String input = "<root><p/><p/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//p[position()=2]", null, false);
        assertEquals(1, hits.size());
    }

    /**
     * @throws Exception in case of problems
     */
    @Test
    public void neqSearch() throws Exception {
        final String input = "<root><p/><p/><p/><p/></root>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(IOUtils.toInputStream(input, StandardCharsets.UTF_8));

        List<?> hits = XPathHelper.getByXPath(doc, "//p[position()!=2]", null, false);
        assertEquals(3, hits.size());
    }
}
