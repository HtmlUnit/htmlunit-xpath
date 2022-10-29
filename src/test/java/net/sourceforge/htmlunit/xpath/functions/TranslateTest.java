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

/** Unit test for translate() function. */
public class TranslateTest extends XPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void translate() throws Exception {
    List<?> hits = getByXpath("translate('abc', 'b', 'd')");
    assertEquals(1, hits.size());
    assertEquals("adc", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void translateIgnoresExtraArguments() throws Exception {
    List<?> hits = getByXpath("translate('abc', 'b', 'dghf')");
    assertEquals(1, hits.size());
    assertEquals("adc", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void translateStringThatContainsNonBMPChars() throws Exception {
    List<?> hits = getByXpath("translate('ab\uD834\uDD00b', 'b', 'd')");
    assertEquals(1, hits.size());
    assertEquals("ad\uD834\uDD00d", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void translateWithExtraCharsInReplacementString() throws Exception {
    List<?> hits = getByXpath("translate('abc', 'c', 'def')");
    assertEquals(1, hits.size());
    assertEquals("abd", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void translateFunctionRequiresAtLeastThreeArguments() throws Exception {
    assertGetByXpathException(
        "translate('a', 'b')",
        "Could not retrieve XPath >translate('a', 'b')< on [#document: null]",
        "FuncTranslate only allows 3 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void translateRequiresAtMostThreeArguments() throws Exception {
    assertGetByXpathException(
        "translate('a', 'a', 'a', 'a')",
        "Could not retrieve XPath >translate('a', 'a', 'a', 'a')< on [#document: null]",
        "FuncTranslate only allows 3 arguments");
  }
}
