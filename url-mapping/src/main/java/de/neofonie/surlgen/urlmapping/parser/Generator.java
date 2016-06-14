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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Generator implements Comparable<Generator> {

    private static final Logger logger = LoggerFactory.getLogger(Generator.class);

    private final List<Matcher> matcherList;
    private final Set<String> params = new HashSet<>();

    public Generator(List<Matcher> matcherList) {
        this.matcherList = matcherList;
        for (Matcher matcher : matcherList) {
            String param = matcher.getParam();
            if (param != null) {
                params.add(param);
            }
        }
    }

    @Override
    public int compareTo(Generator o) {
        int compare = Integer.compare(o.params.size(), params.size());
        if (compare != 0) {
            return compare;
        }
        return Integer.compare(hashCode(), o.hashCode());

    }

    @Override
    public String toString() {
        return "Generator{" +
                "matcherList=" + matcherList +
                ", params=" + params +
                '}';
    }
}
