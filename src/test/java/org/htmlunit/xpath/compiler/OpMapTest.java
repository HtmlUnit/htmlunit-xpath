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
package org.htmlunit.xpath.compiler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.xml.transform.TransformerException;

import org.htmlunit.xpath.patterns.NodeTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link OpMap}.
 *
 * @author Ronald Brill
 */
public class OpMapTest {

    private OpMap opMap;

    @BeforeEach
    void setUp() {
        opMap = new OpMap();
        opMap.setOpMap(new OpMapVector(50, 10, OpMap.MAPINDEX_LENGTH));
    }

    @Test
    @DisplayName("Pattern string getters, setters, and toString output")
    void testPatternString() {
        opMap.setPatternString("/bookstore/book");
        assertEquals("/bookstore/book", opMap.getPatternString());
        assertEquals("/bookstore/book", opMap.toString());
    }

    @Test
    @DisplayName("Token queue management")
    void testTokenQueue() {
        opMap.getTokenQueue().add("token1");
        opMap.getTokenQueue().add("token2");

        assertEquals(2, opMap.getTokenQueueSize());
        assertEquals("token1", opMap.getTokenQueue().get(0));
    }

    @Test
    @DisplayName("Setting and getting operations")
    void testGetAndSetOp() {
        opMap.setOp(0, OpCodes.OP_XPATH);
        opMap.setOp(1, 10); // Length of opcode

        assertEquals(OpCodes.OP_XPATH, opMap.getOp(0));
        assertEquals(10, opMap.getNextOpPos(0));
    }

    @Test
    @DisplayName("Static offset position helpers")
    void testStaticPositionHelpers() {
        assertEquals(5, OpMap.getFirstChildPos(3));
        assertEquals(6, OpMap.getFirstChildPosOfStep(3));
    }

    @Test
    @DisplayName("Shrinking the OpMap trims op-map to logical size + 1 sentinel")
    void testShrink() {
        // Set logical length at MAPINDEX_LENGTH (1) to 5
        opMap.setOp(OpMap.MAPINDEX_LENGTH, 5);

        opMap.shrink();

        // op-map trimmed to n+1 = 6: logical content [0,5) plus one sentinel zero
        assertEquals(6, opMap.getOpMap().getMap().length);

        // sentinel slot is zero (ENDOP) — provided by zero-initialised new int[]
        assertEquals(0, opMap.getOpMap().elementAt(5));

        // token queue unchanged — no null sentinels appended
        assertEquals(0, opMap.getTokenQueueSize());
    }

    @Test
    @DisplayName("getNextStepPos correctly traverses axis steps")
    void testGetNextStepPosAxis() {
        // OpCodes.FROM_CHILDREN is within AXES_START_TYPES and AXES_END_TYPES
        opMap.setOp(0, OpCodes.FROM_CHILDREN);
        opMap.setOp(1, 4); // Step size = 4

        assertEquals(4, opMap.getNextStepPos(0));
    }

    @Test
    @DisplayName("getNextStepPos throws exception on unknown step type")
    void testGetNextStepPosUnknownType() {
        opMap.setOp(0, 9999); // Invalid step type

        assertThrows(RuntimeException.class, () -> opMap.getNextStepPos(0));
    }

    @Test
    @DisplayName("getFirstPredicateOpPos returns correct predicate offsets")
    void testGetFirstPredicateOpPos() throws TransformerException {
        // Axis step
        opMap.setOp(0, OpCodes.FROM_CHILDREN);
        opMap.setOp(2, 8); // Offset to predicate
        assertEquals(8, opMap.getFirstPredicateOpPos(0));

        // Special -2 opcode[cite: 2]
        opMap.setOp(10, -2);
        assertEquals(-2, opMap.getFirstPredicateOpPos(10));
    }

    @Test
    @DisplayName("getFirstPredicateOpPos throws TransformerException for invalid opcodes")
    void testGetFirstPredicateOpPosException() {
        opMap.setOp(0, -99);
        assertThrows(TransformerException.class, () -> opMap.getFirstPredicateOpPos(0));
    }

    @Test
    @DisplayName("Step namespace resolution for wildcard and token positions")
    void testGetStepNS() {
        // Setup step header
        opMap.setOp(0, OpCodes.FROM_CHILDREN);
        opMap.setOp(2, 6); // argLength = 6 - 3 = 3

        // Token Index lookup
        opMap.getTokenQueue().add("http://example.com/ns");
        opMap.setOp(4, 0); // Index 0 in token queue
        assertEquals("http://example.com/ns", opMap.getStepNS(0));

        // Wildcard lookup
        opMap.setOp(4, OpCodes.ELEMWILDCARD);
        assertEquals(NodeTest.WILD, opMap.getStepNS(0));
    }

    @Test
    @DisplayName("Step local name resolution across different argument lengths")
    void testGetStepLocalName() {
        opMap.getTokenQueue().add("title");

        // Case 1: Wildcard (argLen = 1 -> size = 4)
        opMap.setOp(0, OpCodes.FROM_CHILDREN);
        opMap.setOp(2, 4);
        assertEquals(NodeTest.WILD, opMap.getStepLocalName(0));

        // Case 2: Specific token name (argLen = 2 -> size = 5)
        opMap.setOp(2, 5);
        opMap.setOp(4, 0); // Token index 0 ("title")
        assertEquals("title", opMap.getStepLocalName(0));
    }

    @Test
    @DisplayName("error method throws TransformerException")
    void testError() {
        assertThrows(TransformerException.class, () -> opMap.error("ER_UNKNOWN_OPCODE", new Object[] { "123" }));
    }
}