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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link OpMapVector} class.
 *
 * @author Ronald Brill
 */
public class OpMapVectorTest {

    private static final int INITIAL_CAPACITY = 10;
    private static final int BLOCK_SIZE = 5;
    private static final int LENGTH_POS = 0;

    private OpMapVector vector;

    @BeforeEach
    void setUp() {
        vector = new OpMapVector(INITIAL_CAPACITY, BLOCK_SIZE, LENGTH_POS);
    }

    @Test
    @DisplayName("Initialization creates backing map with target initial capacity")
    void testInitialCapacity() {
        assertEquals(INITIAL_CAPACITY, vector.getMap().length);
        assertEquals(INITIAL_CAPACITY, vector.map.length); // Direct package-private access test
    }

    @Test
    @DisplayName("Setting and retrieving elements within existing bounds")
    void testSetAndElementAtWithinBounds() {
        vector.setElementAt(42, 3);
        assertEquals(42, vector.elementAt(3));
        assertEquals(42, vector.map[3]);
    }

    @Test
    @DisplayName("Setting an element past capacity triggers block expansion")
    void testSetElementAtStandardGrowth() {
        // Accessing index 10 requires expanding initial capacity of 10
        vector.setElementAt(99, 10);

        assertEquals(99, vector.elementAt(10));
        assertEquals(15, vector.map.length); // Expanded by BLOCK_SIZE (10 + 5)
    }

    @Test
    @DisplayName("Setting an element far past capacity resizes to fit index + block size")
    void testSetElementAtLargeIndexGrowth() {
        // Setting index 25 exceeds both initial capacity and single block expansion
        vector.setElementAt(77, 25);

        assertEquals(77, vector.elementAt(25));
        assertEquals(30, vector.map.length); // Calculated as 25 + BLOCK_SIZE
    }

    @Test
    @DisplayName("setToSize allocates an array of length 'size' and copies elements up to map[lengthPos]")
    void testSetToSize() {
        // Store logical length (3) at lengthPos (index 0)
        vector.setElementAt(3, LENGTH_POS);
        vector.setElementAt(100, 1);
        vector.setElementAt(200, 2);
        vector.setElementAt(999, 3); // Beyond logical length (3)

        // Resize backing array to size 8
        vector.setToSize(8);

        // Array capacity must match target size
        assertEquals(8, vector.getMap().length);

        // Elements up to logical size (3) are preserved
        assertEquals(3, vector.elementAt(0));
        assertEquals(100, vector.elementAt(1));
        assertEquals(200, vector.elementAt(2));

        // Trailing elements beyond logical size remain 0
        assertEquals(0, vector.elementAt(3));
    }
}