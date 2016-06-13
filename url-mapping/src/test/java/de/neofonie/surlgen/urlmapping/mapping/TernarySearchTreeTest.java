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

import java.util.*;

import static org.junit.Assert.assertEquals;

public class TernarySearchTreeTest {

    @Test
    public void testAdd() throws Exception {
        TernarySearchTree<String> tree = new TernarySearchTree<>();
        assertEquals("TernarySearchTree{null}", tree.toString());
        tree.put("a", "1");
        assertEquals("TernarySearchTree{(null|a:null{1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));

        tree.put("aba", "2");
        assertEquals("TernarySearchTree{(null|a:(null|b:(null|a:null{2}|null)|null){1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("aba"));

        tree.put("abaa", "3");
        assertEquals("TernarySearchTree{(null|a:(null|b:(null|a:(null|a:null{3}|null){2}|null)|null){1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("aba"));
        assertEquals("3", tree.getValue("abaa"));
    }

    @Test
    public void testAdd2() throws Exception {
        TernarySearchTree<String> tree = new TernarySearchTree<>();
        assertEquals("TernarySearchTree{null}", tree.toString());
        tree.put("a", "1");
        assertEquals("TernarySearchTree{(null|a:null{1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));

        tree.put("b", "2");
        assertEquals("TernarySearchTree{((null|b:null{2}|null)|a:null{1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("b"));

        tree.put("ab", "3");
        assertEquals("TernarySearchTree{((null|b:null{2}|null)|a:(null|b:null{3}|null){1}|null)}", tree.toString());
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("b"));
    }

    @Test
    public void testRandom() throws Exception {
        Random random = new Random();
        HashMap<String, Integer> map = new HashMap<>();
        TernarySearchTree<Integer> tree = new TernarySearchTree<>();
        for (int i = 0; i < 1000000; i++) {
            final int length = random.nextInt(10) + 1;
            String key = randomString(random, length);
            map.put(key, i);
            tree.put(key, i);
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            assertEquals(entry.getValue(), tree.getValue(entry.getKey()));

            List<Map.Entry<String, Integer>> expect = new ArrayList<>();
            for (int i = 0; i < entry.getKey().length(); i++) {
                final String substring = entry.getKey().substring(0, i + 1);
                final Integer integer = map.get(substring);
                if (integer != null) {
                    expect.add(new AbstractMap.SimpleImmutableEntry<>(substring, integer));
                }
            }

            assertEquals(expect.toString(), tree.headMap(entry.getKey()).toString());
        }
    }

    private String randomString(Random random, int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    @Test
    public void testHeadMap() throws Exception {
        TernarySearchTree<String> tree = new TernarySearchTree<>();
        tree.put("a", "1");
        tree.put("b", "2");
        tree.put("abasdf", "3");
        tree.put("abasss", "4");
        assertEquals("1", tree.getValue("a"));
        assertEquals("2", tree.getValue("b"));
        assertEquals("3", tree.getValue("abasdf"));
        assertEquals("4", tree.getValue("abasss"));
        assertEquals("[a=1]", tree.headMap("a").toString());
        assertEquals("[b=2]", tree.headMap("b").toString());
        assertEquals("[a=1, abasdf=3]", tree.headMap("abasdf").toString());
        assertEquals("[a=1, abasss=4]", tree.headMap("abasss").toString());
    }
}