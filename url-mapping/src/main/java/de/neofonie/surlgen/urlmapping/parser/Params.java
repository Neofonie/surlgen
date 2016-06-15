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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class Params {

    private static final Logger logger = LoggerFactory.getLogger(Params.class);
    private final Map<String, List<String>> params = new HashMap<>();

    public Params() {
    }

    public Params(Params params) {
        addAll(params);
    }

    public Params add(String key, String value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        params.computeIfAbsent(key, t -> new ArrayList<>()).add(value);
        return this;
    }

    public Params addAll(String key, Collection<String> value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);

        params.computeIfAbsent(key, t -> new ArrayList<>()).addAll(value);
        return this;
    }

    public Params addAll(Params params) {
        for (Map.Entry<String, List<String>> s : params.params.entrySet()) {
            addAll(s.getKey(), s.getValue());
        }
        return this;
    }

    @Override
    public String toString() {
        return "Params{" +
                "params=" + params +
                '}';
    }

    public String getValue(String key) {
        final List<String> values = params.get(key);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    public List<String> getValues(String key) {
        final List<String> values = params.get(key);
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(values);
    }

    public Params copy() {
        return new Params(this);
    }

    public static Params parseFromQueryString(String s) {
        return null;
    }

    public String createQueryString() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                for (String list : entry.getValue()) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append('&');
                    } else {
                        stringBuilder.append('?');
                    }

                    stringBuilder
                            .append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                            .append('=')
                            .append(URLEncoder.encode(list, "UTF-8"));
                }
            }

            return stringBuilder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Exception occured - this happens if UTF-8 isnt supported", e);
        }
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(params.keySet());
    }

    public String removeFirst(String name) {

        List<String> strings = params.get(name);
        if (strings == null || strings.isEmpty()) {
            return null;
        }

        String result = strings.remove(0);
        if (strings.isEmpty()) {
            params.remove(name);
        }
        return result;
    }
}
