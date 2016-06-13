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

import static org.junit.Assert.assertEquals;

public class TernarySearchTreeTest {

    @Test
    public void testAdd() throws Exception {
        TernarySearchTree<String> tree = new TernarySearchTree<>();
        assertEquals("TernarySearchTree{null}", tree.toString());
        tree.add("a", "1");
        assertEquals("TernarySearchTree{(null|a:null{1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));

        tree.add("aba", "2");
        assertEquals("TernarySearchTree{(null|a:(null|b:(null|a:null{2}|null)|null){1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("aba"));

        tree.add("abaa", "3");
        assertEquals("TernarySearchTree{(null|a:(null|b:(null|a:(null|a:null{3}|null){2}|null)|null){1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("aba"));
        assertEquals("3", tree.getValue("abaa"));
    }

    @Test
    public void testAdd2() throws Exception {
        TernarySearchTree<String> tree = new TernarySearchTree<>();
        assertEquals("TernarySearchTree{null}", tree.toString());
        tree.add("a", "1");
        assertEquals("TernarySearchTree{(null|a:null{1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));

        tree.add("b", "2");
        assertEquals("TernarySearchTree{((null|b:null{2}|null)|a:null{1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("b"));

        tree.add("ab", "3");
        assertEquals("TernarySearchTree{((null|b:null{2}|null)|a:(null|b:null{3}|null){1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("b"));
    }
}