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
import net.sourceforge.htmlunit.xpath.XPathTest;
import org.junit.jupiter.api.Test;

/** Unit test for boolean() function. */
public class BooleanTest extends XPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void nonEmptyNodeSetsAreTrue() throws Exception {
    List<?> hits = getByXpath("<root><y/></root>", "boolean(//y)");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyNodeSetsAreFalse() throws Exception {
    List<?> hits = getByXpath("<root><z/></root>", "boolean(//y)");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void zeroIsFalse() throws Exception {
    List<?> hits = getByXpath("boolean(0)");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyStringIsFalse() throws Exception {
    List<?> hits = getByXpath("boolean('')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void naNIsFalse() throws Exception {
    List<?> hits = getByXpath("boolean(0 div 0)");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void nonEmptyStringIsTrue() throws Exception {
    List<?> hits = getByXpath("boolean('false')");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void booleanFunctionRequiresAtLeastOneArgument() throws Exception {
    assertGetByXpathException(
        "boolean()",
        "Could not retrieve XPath >boolean()< on [#document: null]",
        "FuncBoolean only allows 1 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void booleanFunctionRequiresExactlyOneArgument() throws Exception {
    assertGetByXpathException(
        "boolean('', '')",
        "Could not retrieve XPath >boolean('', '')< on [#document: null]",
        "FuncBoolean only allows 1 arguments");
  }
}
