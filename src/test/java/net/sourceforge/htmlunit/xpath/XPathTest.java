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
}
