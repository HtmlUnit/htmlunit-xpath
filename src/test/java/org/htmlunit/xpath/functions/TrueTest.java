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
package org.htmlunit.xpath.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.htmlunit.xpath.AbstractXPathTest;
import org.junit.jupiter.api.Test;

/** Unit test for true() function. */
public class TrueTest extends AbstractXPathTest {

  /** @throws Exception in case of problems */
  @Test
  public void trueLessThanOrEqualToTrue() throws Exception {
    final List<?> hits = getByXpath("true() <= true()");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyNodeSetLessThanOrEqualToTrue() throws Exception {
    final List<?> hits = getByXpath("/nonexistent<=true()");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void emptyNodeSetLessThanTrue() throws Exception {
    final List<?> hits = getByXpath("/nonexistent<true()");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void trueLessThanOrEqualToEmptyNodeSet() throws Exception {
    final List<?> hits = getByXpath("true()<=/nonexistent");
    assertEquals(1, hits.size());
    assertEquals(Boolean.FALSE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void trueGreaterThanOrEqualToEmptyNodeSet() throws Exception {
    final List<?> hits = getByXpath("true()>=/nonexistent");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void trueGreaterThaEmptyNodeSet() throws Exception {
    final List<?> hits = getByXpath("true()>/nonexistent");
    assertEquals(1, hits.size());
    assertEquals(Boolean.TRUE, hits.get(0));
  }

  /** @throws Exception in case of problems */
  @Test
  public void trueFunctionRequiresNoArgument() throws Exception {
    assertGetByXpathException(
        "true(1)",
        "Could not retrieve XPath >true(1)< on [#document: null]",
        "FuncTrue only allows 0 arguments");
  }
}
