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

package de.neofonie.surlgen.urlmapping.mapping;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MappingImplTest {

    @Test
    public void testGetMatchesSimple() throws Exception {

        MappingImpl mapping;

        Map<String, String> map = new HashMap<>();
        mapping = new MappingImpl(map);
        assertEquals("[]", mapping.getMatches("foobar").toString());

        map = new HashMap<>();
        map.put("1", "a");
        map.put("2", "aa");
        map.put("3", "ab");
        map.put("4", "b");
        map.put("5", "ba");
        map.put("6", "bb");
        mapping = new MappingImpl(map);
        assertEquals("[a=1]", mapping.getMatches("a").toString());
        assertEquals("[a=1, ab=3]", mapping.getMatches("ab").toString());
        assertEquals("[a=1, aa=2]", mapping.getMatches("aa").toString());
        assertEquals("[b=4]", mapping.getMatches("b").toString());
        assertEquals("[b=4, ba=5]", mapping.getMatches("ba").toString());
        assertEquals("[b=4, bb=6]", mapping.getMatches("bb").toString());
        assertEquals("[]", mapping.getMatches("foobar").toString());

    }

    @Test
    public void testGetValue() throws Exception {

        Map<String, String> map = new HashMap<>();
        MappingImpl mapping = new MappingImpl(map);
        assertEquals(null, mapping.getValue("foobar"));

        map = new HashMap<>();
        map.put("1", "a");
        map.put("2", "aa");
        map.put("3", "ab");
        map.put("4", "b");
        map.put("5", "ba");
        map.put("6", "bb");
        mapping = new MappingImpl(map);
        assertEquals(null, mapping.getValue("a"));
        assertEquals("a", mapping.getValue("1"));
        assertEquals("ab", mapping.getValue("3"));
    }
}