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
package org.htmlunit.xpath;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.xml.transform.TransformerException;

import org.htmlunit.xpath.objects.XObject;
import org.htmlunit.xpath.xml.dtm.ref.DTMManagerDefault;
import org.junit.jupiter.api.Test;

/**
 * Test for error handling.
 *
 * @author Ronald Brill
 */
public class XPATHErrorTest {
    @Test
    void test_ER_CANT_CONVERT_TO_NUMBER() {
        XObject xo = new XObject(new Object());
        assertThrows(TransformerException.class, xo::num);
    }

    @Test
    void test_ER_CANT_CONVERT_TO_NODELIST() {
        XObject xo = new XObject(new Object());
        assertThrows(TransformerException.class, xo::iter);
    }

    @Test
    void test_ER_NULL_ERROR_HANDLER() {
        XPathContext ctx = new XPathContext(true);
        assertThrows(IllegalArgumentException.class, () -> {
            ctx.setErrorListener(null);
        });
    }

    @Test
    void test_ER_NOT_SUPPORTED() {
        DTMManagerDefault mgr = new DTMManagerDefault();
        assertThrows(RuntimeException.class, () -> {
            mgr.getDTM(null, false, false, false); // or with invalid Source type
        });
    }

    @Test
    void test_ER_NODE_NON_NULL() {
        DTMManagerDefault mgr = new DTMManagerDefault();
        assertThrows(IllegalArgumentException.class, () -> {
            mgr.getDTMHandleFromNode(null);
        });
    }
}
