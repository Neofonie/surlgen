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

import com.google.common.base.Preconditions;

public class TernarySearchTree<T> {

    private Node<T> root;

    public void add(String key, T value) {
        final Node<T> node = createNode(key);
        node.value = value;
    }

    private Node<T> createNode(String key) {
        Preconditions.checkNotNull(key);
        if (root == null) {
            root = new Node<>(key.charAt(0));
        }
        Node node = root;
        char[] charArray = key.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            char c = charArray[i];
            if (i > 0) {
                if (node.middle == null) {
                    node.middle = new Node(c);
                }
                node = node.middle;
            }
            node = createNode(node, c);

        }
        return node;
    }

    public T getValue(String key) {
        Node<T> node = root;
        char[] charArray = key.toCharArray();
        for (int i = 0, charArrayLength = charArray.length; i < charArrayLength; i++) {
            if (node == null) {
                return null;
            }
            char c = charArray[i];
            if (i > 0) {
                node = node.middle;
            }
            if (node == null) {
                return null;
            }
            node = getNode(node, c);
        }
        if (node == null) {
            return null;
        }
        return node.value;
    }

    private Node<T> createNode(Node<T> node, char c) {
        Preconditions.checkNotNull(node);
        int compare = Character.compare(node.c, c);
        while (compare != 0) {
            if (compare < 0) {
                if (node.left == null) {
                    node.left = new Node(c);
                }
                node = node.left;
            } else if (compare > 0) {
                if (node.right == null) {
                    node.right = new Node(c);
                }
                node = node.right;
            }
            compare = Character.compare(node.c, c);
        }
        return node;
    }

    private Node<T> getNode(Node<T> node, char c) {
        Preconditions.checkNotNull(node);
        int compare = Character.compare(node.c, c);
        while (compare != 0) {
            if (compare < 0) {
                node = node.left;
            } else if (compare > 0) {
                node = node.right;
            }
            if (node == null) {
                return null;
            }
            compare = Character.compare(node.c, c);
        }
        return node;
    }

    @Override
    public String toString() {
        return "TernarySearchTree{" + root + '}';
    }

    private static class Node<T> {
        private Node<T> left;
        private Node<T> middle;
        private Node<T> right;
        private T value;
        private final char c;

        private Node(char c) {
            this.c = c;
        }

        @Override
        public String toString() {
            if (value != null) {
                return String.format("(%s|%s:%s{%s}|%s)", left, c, middle, value, right);
            } else {
                return String.format("(%s|%s:%s|%s)", left, c, middle, right);
            }
        }
    }
}
