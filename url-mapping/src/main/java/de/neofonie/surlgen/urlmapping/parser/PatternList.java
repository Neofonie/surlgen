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

import java.util.ArrayList;
import java.util.List;

class PatternList implements UrlPattern {

    private static final Logger logger = LoggerFactory.getLogger(PatternList.class);
    private final List<UrlPattern> list;

    PatternList(List<UrlPattern> list) {
        this.list = list;
    }

    public static UrlPattern create(List<UrlPattern> list) {
        if (list.size() == 1) {
            return list.get(0);
        }

        return new PatternList(list);
    }

//    @Override
//    protected MatcherResult matches(MatcherResult matcherResult) {
//        MatcherResult current = matcherResult;
//        for (UrlPattern abstractUrlPattern : list) {
//            current = abstractUrlPattern.matches(current);
//            if (current == null) {
//                return null;
//            }
//        }
//        return current;
//    }

    @Override
    public List<List<Matcher>> getCompleteHierarchy() {
        List<List<Matcher>> current = new ArrayList<>();
        current.add(new ArrayList<>());

        for (UrlPattern urlPattern : list) {
            List<List<Matcher>> newOne = new ArrayList<>();
            final List<List<Matcher>> completeHierarchy = urlPattern.getCompleteHierarchy();
            for (List<Matcher> c : current) {
                for (List<Matcher> comp : completeHierarchy) {
                    List<Matcher> tmp = new ArrayList<>();
                    tmp.addAll(c);
                    tmp.addAll(comp);
                    newOne.add(tmp);
                }
            }
            current = newOne;
        }
        return current;
    }

    @Override
    public String toString() {
        return "PatternList{" +
                "list=" + list +
                '}';
    }
}
