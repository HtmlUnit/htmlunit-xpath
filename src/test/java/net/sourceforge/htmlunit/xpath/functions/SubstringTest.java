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

/** Unit test for substring() function. */
public class SubstringTest extends XPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void substringOfNumber() throws Exception {
    List<?> hits = getByXpath("substring(1234, 3)");
    assertEquals(1, hits.size());
    assertEquals("34", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringOfNumber2() throws Exception {
    List<?> hits = getByXpath("substring(1234, 2, 3)");
    assertEquals(1, hits.size());
    assertEquals("234", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void unusualSubstring1() throws Exception {
    List<?> hits = getByXpath("substring('12345', 1.5, 2.6)");
    assertEquals(1, hits.size());
    assertEquals("234", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void unusualSubstring2() throws Exception {
    List<?> hits = getByXpath("substring('12345', 0, 3)");
    assertEquals(1, hits.size());
    assertEquals("12", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void unusualSubstring3() throws Exception {
    List<?> hits = getByXpath("substring('12345', 0 div 0, 3)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void unusualSubstring4() throws Exception {
    List<?> hits = getByXpath("substring('12345', 1, 0 div 0)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void unusualSubstring5() throws Exception {
    List<?> hits = getByXpath("substring('12345', -42, 1 div 0)");
    assertEquals(1, hits.size());
    assertEquals("12345", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void unusualSubstring6() throws Exception {
    List<?> hits = getByXpath("substring('12345', -1 div 0, 1 div 0)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringOfNaN() throws Exception {
    List<?> hits = getByXpath("substring(0 div 0, 2)");
    assertEquals(1, hits.size());
    assertEquals("aN", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringOfEmptyString() throws Exception {
    List<?> hits = getByXpath("substring('', 2)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringLengthZero() throws Exception {
    List<?> hits = getByXpath("substring('12345', 2, 0)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringWithNegativeLength() throws Exception {
    List<?> hits = getByXpath("substring('12345', 2, -3)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringWithNegativeLength2() throws Exception {
    List<?> hits = getByXpath("substring('12345', 2, -1)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringWithExcessiveLength() throws Exception {
    List<?> hits = getByXpath("substring('12345', 2, 32)");
    assertEquals(1, hits.size());
    assertEquals("2345", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringNegativeStartNoLength() throws Exception {
    List<?> hits = getByXpath("substring('Hello', -50)");
    assertEquals(1, hits.size());
    assertEquals("Hello", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringNegativeStartWithLength() throws Exception {
    List<?> hits = getByXpath("substring('Hello', -50, 20)");
    assertEquals(1, hits.size());
    assertEquals("", hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringFunctionRequiresAtLeastTwoArguments() throws Exception {
    assertGetByXpathException(
        "substring('a')",
        "Could not retrieve XPath >substring('a')< on [#document: null]",
        "FuncSubstring only allows 2 or 3 arguments");
  }

  /** @throws Exception in case of problems */
  @Test
  public void substringFunctionRequiresAtMostThreeArguments() throws Exception {
    assertGetByXpathException(
        "substring('a', 1, 1, 4)",
        "Could not retrieve XPath >substring('a', 1, 1, 4)< on [#document: null]",
        "FuncSubstring only allows 2 or 3 arguments");
  }
}
