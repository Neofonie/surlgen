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

import java.util.List;

class PatternList extends AbstractUrlPattern implements UrlPattern {

    private static final Logger logger = LoggerFactory.getLogger(PatternList.class);
    private final List<AbstractUrlPattern> list;

    PatternList(List<AbstractUrlPattern> list) {
        this.list = list;
    }

    public static AbstractUrlPattern create(List<AbstractUrlPattern> list) {
        if (list.size() == 1) {
            return list.get(0);
        }

        return new PatternList(list);
    }

    @Override
    public String toString() {
        return "PatternList{" +
                "list=" + list +
                '}';
    }

    @Override
    protected Matcher matches(Matcher matcher) {
        Matcher current = matcher;
        for (AbstractUrlPattern abstractUrlPattern : list) {
            current = abstractUrlPattern.matches(current);
            if (current == null) {
                return null;
            }
        }
        return current;
    }
}