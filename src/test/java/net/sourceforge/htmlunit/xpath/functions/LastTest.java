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
package net.sourceforge.htmlunit.xpath.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.sourceforge.htmlunit.xpath.AbstractXPathTest;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Node;

/** Unit test for last() function. */
public class LastTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void last() throws Exception {
    final String xml =
        "<root>"
            + "<a>"
            + "<x>2</x>"
            + "</a>"
            + "<b>"
            + "<x>3</x>"
            + "<x>4</x>"
            + "</b>"
            + "</root>";
    List<?> hits = getByXpath(xml, "//x[position()=last()]");
    assertEquals(2, hits.size());
    assertEquals("2", ((Node) hits.get(0)).getTextContent());
    assertEquals("4", ((Node) hits.get(1)).getTextContent());
  }

  /** @throws Exception in case of problems */
  @Test
  public void lastEmptyList() throws Exception {
    List<?> hits = getByXpath("//x[position()=last()]");
    assertEquals(0, hits.size());
  }

  /** @throws Exception in case of problems */
  @Test
  public void falseFunctionRequiresNoArgument() throws Exception {
    assertGetByXpathException(
        "//x[position()=last(.)]",
        "Could not retrieve XPath >//x[position()=last(.)]< on [#document: null]",
        "FuncLast only allows 0 arguments");
  }
}
