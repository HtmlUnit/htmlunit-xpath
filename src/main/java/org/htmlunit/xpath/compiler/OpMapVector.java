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

import java.util.Arrays;

/**
 * High-performance primitive int array vector for OpMap operations.
 *
 * @author Apache Xalan
 * @author Ronald Brill
 */
public class OpMapVector {

    private final int blockSize_;
    private final int lengthPos;

    // Package-private so IntStack can access directly without overhead
    int[] map;

    /**
     * Construct a OpMapVector, using the given block size.
     *
     * @param blocksize Size of block to allocate
     */
    public OpMapVector(final int initialCapacity, final int incrementSize, final int lengthPos) {
        this.blockSize_ = incrementSize;
        this.lengthPos = lengthPos;
        this.map = new int[initialCapacity];
    }

    /**
     * Get the nth element.
     *
     * @param i index of object to get
     * @return object at given index
     */
    public final int elementAt(final int i) {
        return map[i];
    }

    /**
     * Sets the component at the specified index of this vector to be the specified
     * object. The previous component at that position is discarded.
     *
     * <p>
     * The index must be a value greater than or equal to 0 and less than the
     * current size of the vector.
     *
     * @param value object to set
     * @param index Index of where to set the object
     */
    public final void setElementAt(final int value, final int index) {
        if (index >= map.length) {
            // Calculate growth to ensure index is safely accommodated
            int newSize = map.length + blockSize_;
            if (index >= newSize) {
                newSize = index + blockSize_;
            }
            map = Arrays.copyOf(map, newSize);
        }
        map[index] = value;
    }

    /*
     * Reset the array to the supplied size. No checking is done.
     *
     * @param size The size to trim to.
     */
    public final void setToSize(final int size) {
        final int[] newMap = new int[size];

        // Guard against out-of-bounds if map[lengthPos] exceeds the target size or
        // array bounds
        final int copyLength = Math.min(size, map[lengthPos]);
        if (copyLength > 0) {
            System.arraycopy(map, 0, newMap, 0, copyLength);
        }

        map = newMap;
    }

    public int[] getMap() {
        return map;
    }
}