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

package de.neofonie.surlgen.urlmapping.parser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ParamsTest {

    @Test
    public void testAdd() throws Exception {
        Params params = new Params().add("A", "12");

        assertEquals("Params{params={A=[12]}}", params.toString());
        assertEquals("12", params.getValue("A"));
        assertEquals(null, params.getValue("a"));

        assertEquals("[12]", params.getValues("A").toString());
        assertEquals("[]", params.getValues("a").toString());
    }

    @Test
    public void testAdd2() throws Exception {
        Params params = new Params().add("A", "12").add("A", "13");

        assertEquals("Params{params={A=[12, 13]}}", params.toString());
        assertEquals("12", params.getValue("A"));
        assertEquals(null, params.getValue("a"));

        assertEquals("[12, 13]", params.getValues("A").toString());
        assertEquals("[]", params.getValues("a").toString());
    }

    @Test
    public void testAddAll() throws Exception {
        Params params = new Params().add("A", "12").add("C", "2").add("A", "13").addAll(new Params().add("A", "14").add("B", "1"));

        assertEquals("Params{params={A=[12, 13, 14], B=[1], C=[2]}}", params.toString());
    }

    @Test
    public void testCopy() throws Exception {
        Params params = new Params().add("A", "12");

        assertEquals("Params{params={A=[12]}}", params.toString());
        assertEquals("Params{params={A=[12], C=[13]}}", params.copy().add("C", "13").toString());
        assertEquals("Params{params={A=[12]}}", params.toString());
    }

    @Test
    public void testCreateQueryString() throws Exception {
        Params params = new Params().add("A", "12");

        assertEquals("?A=12", params.createQueryString());
        assertEquals("?A=12&C=13", params.add("C", "13").createQueryString());
        assertEquals("?A=12&A=1&C=13", params.add("A", "1").createQueryString());
        assertEquals("?a=%C3%B6%C3%BC%26%21%25%2F%26%28%29%21%22%C2%A7%27&A=12&A=1&C=13",
                params.add("a", "öü&!%/&()!\"§\'").createQueryString());

    }

    @Test
    public void testRemoveFirst() throws Exception {
        Params params = new Params().add("A", "12").add("C", "13").add("A", "1");

        assertEquals("Params{params={A=[12, 1], C=[13]}}", params.toString());
        assertEquals(null, params.removeFirst("B"));
        assertEquals("Params{params={A=[12, 1], C=[13]}}", params.toString());
        assertEquals("12", params.removeFirst("A"));
        assertEquals("Params{params={A=[1], C=[13]}}", params.toString());
        assertEquals("13", params.removeFirst("C"));
        assertEquals("Params{params={A=[1]}}", params.toString());
        assertEquals("1", params.removeFirst("A"));
        assertEquals("Params{params={}}", params.toString());

    }
}