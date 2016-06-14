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

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MappingTree<T> {

    private final List<Node<T>> root = new ArrayList<>();
    private T rootValue;

    public void addEntry(UrlPattern urlPattern, T value) {
        final List<List<Matcher>> completeHierarchy = urlPattern.getCompleteHierarchy();
        for (List<Matcher> matcherList : completeHierarchy) {
            addEntry(matcherList, value);
        }
    }

    private void addEntry(List<Matcher> matcherList, T value) {
        Preconditions.checkNotNull(value);
        if (matcherList.isEmpty()) {
            Preconditions.checkArgument(rootValue == null, "Root-Node has already a value");
            rootValue = value;
            return;
        }
        Preconditions.checkArgument(!matcherList.isEmpty());
        final Iterator<Matcher> iterator = matcherList.iterator();
        List<Node<T>> nodes = root;
        Preconditions.checkArgument(iterator.hasNext());
        Node<T> node = null;
        while (iterator.hasNext()) {
            final Matcher next = iterator.next();
            node = getNode(next, nodes);
            Preconditions.checkNotNull(node);
            nodes = node.childs;
        }
        Preconditions.checkNotNull(node);
        Preconditions.checkArgument(node.value == null,
                String.format("Duplicate URL-Patterns %s found %s - %s", node.urlPattern, node.value, value));
        node.value = value;
    }

    private static <T> Node<T> getNode(final Matcher next, List<Node<T>> nodes) {
        for (Node<T> node : nodes) {
            if (node.urlPattern.equals(next)) {
                return node;
            }
        }
        final Node<T> result = new Node<>(next);
        nodes.add(result);
        return result;
    }

    public String toStringHierarchy() {
        StringBuilder stringBuilder = new StringBuilder();
        if (rootValue != null) {
            stringBuilder.append('<').append(rootValue).append('>').append('\n');
        }
        for (Node<T> node : root) {
            node.appendStringHierarchy(stringBuilder, 1);
        }
        return stringBuilder.toString();
    }

    public MatcherResult<T> resolve(String value) {
        if (value.isEmpty()) {
            return MatcherResult.create(rootValue, new Params());
        }

        for (Node<T> node : root) {
            final MatcherProcessingCommand matcherProcessingCommand = new MatcherProcessingCommand(value, new Params());
            MatcherResult<T> t = node.resolve(matcherProcessingCommand);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    private static class Node<T> {

        private final Matcher urlPattern;
        private final List<Node<T>> childs = new ArrayList<>();
        private T value;

        private Node(Matcher urlPattern) {
            this.urlPattern = urlPattern;
        }

        private void appendStringHierarchy(StringBuilder stringBuilder, int deep) {
            stringBuilder.append(urlPattern);
            if (value != null) {
                stringBuilder.append('<').append(value).append('>');
            }
            if (childs.isEmpty()) {
                stringBuilder.append('\n');
                return;
            }
            if (childs.size() == 1) {
                if (value != null) {
                    if (stringBuilder.charAt(stringBuilder.length() - 1) != '\n') {
                        stringBuilder.append('\n');
                    }
                    stringBuilder.append(StringUtils.repeat(" ", deep * 4));
                    deep++;
                }
                for (Node<T> node : childs) {
                    node.appendStringHierarchy(stringBuilder, deep);
                }
                return;
            }

            for (Node<T> node : childs) {
                if (stringBuilder.charAt(stringBuilder.length() - 1) != '\n') {
                    stringBuilder.append('\n');
                }
                stringBuilder.append(StringUtils.repeat(" ", deep * 4));
                node.appendStringHierarchy(stringBuilder, deep + 1);
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "urlPattern=" + urlPattern +
//                    ", childs=" + childs +
                    ", value=" + value +
                    '}';
        }

        public MatcherResult<T> resolve(MatcherProcessingCommand matcherProcessingCommand) {
            final List<MatcherProcessingCommand> matches = urlPattern.matches(matcherProcessingCommand);
            if (matches == null || matches.isEmpty()) {
                return null;
            }
            for (MatcherProcessingCommand match : matches) {
                if (match.allConsumed()) {
                    return MatcherResult.create(value, match.getParams());
                }
                for (Node<T> node : childs) {
                    MatcherResult<T> t = node.resolve(match);
                    if (t != null) {
                        return t;
                    }
                }
            }
            return null;
        }
    }

}
