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

/** Unit test for count() function. */
public class CountTest extends XPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void count() throws Exception {
    List<?> hits = getByXpath("count(/*)");
    assertEquals(1, hits.size());
    assertEquals(1, ((Double) hits.get(0)).doubleValue(), 0.0001);
  }

  /** @throws Exception in case of problems */
  @Test
  public void countFunctionRequiresNodeSet() throws Exception {
    assertGetByXpathException(
        "count(7)",
        "Could not retrieve XPath >count(7)< on [#document: null]",
        "Can not convert #NUMBER to a NodeList!");
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsFunctionRequiresAtLeastOneArguments() throws Exception {
    assertGetByXpathException(
        "count()",
        "Could not retrieve XPath >count()< on [#document: null]",
        "FuncCount only allows 1 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void containsFunctionRequiresAtMostIneArguments() throws Exception {
    assertGetByXpathException(
        "count('a', 7)",
        "Could not retrieve XPath >count('a', 7)< on [#document: null]",
        "FuncCount only allows 1 arguments");
  }
}
