/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Neofonie GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.neofonie.surlgen.processor.core;

import junit.framework.TestCase;

public class CamelCaseUtilsTest extends TestCase {

    public void testFirstCharLowerCased() throws Exception {
        assertEquals(null, CamelCaseUtils.firstCharLowerCased(null));
        assertEquals("", CamelCaseUtils.firstCharLowerCased(""));
        assertEquals("fooBar", CamelCaseUtils.firstCharLowerCased("FooBar"));
        assertEquals("fooBar", CamelCaseUtils.firstCharLowerCased("fooBar"));
        assertEquals("f", CamelCaseUtils.firstCharLowerCased("F"));
        assertEquals("f", CamelCaseUtils.firstCharLowerCased("f"));
        assertEquals("fo", CamelCaseUtils.firstCharLowerCased("Fo"));
        assertEquals("fo", CamelCaseUtils.firstCharLowerCased("fo"));
        assertEquals("fO", CamelCaseUtils.firstCharLowerCased("FO"));
        assertEquals("fO", CamelCaseUtils.firstCharLowerCased("fO"));
    }

    public void testFirstCharUpperCased() throws Exception {
        assertEquals(null, CamelCaseUtils.firstCharUpperCased(null));
        assertEquals("", CamelCaseUtils.firstCharUpperCased(""));
        assertEquals("FooBar", CamelCaseUtils.firstCharUpperCased("FooBar"));
        assertEquals("FooBar", CamelCaseUtils.firstCharUpperCased("fooBar"));
        assertEquals("F", CamelCaseUtils.firstCharUpperCased("F"));
        assertEquals("F", CamelCaseUtils.firstCharUpperCased("f"));
        assertEquals("Fo", CamelCaseUtils.firstCharUpperCased("Fo"));
        assertEquals("Fo", CamelCaseUtils.firstCharUpperCased("fo"));
        assertEquals("FO", CamelCaseUtils.firstCharUpperCased("FO"));
        assertEquals("FO", CamelCaseUtils.firstCharUpperCased("fO"));

    }
}