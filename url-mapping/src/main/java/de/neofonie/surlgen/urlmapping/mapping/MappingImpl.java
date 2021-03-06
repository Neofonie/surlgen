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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MappingImpl implements Mapping {

    private static final Logger logger = LoggerFactory.getLogger(MappingImpl.class);
    private final TernarySearchTree<String> reverseMap = new TernarySearchTree<>();
    private final Map<String, String> map = new HashMap<>();

    public MappingImpl(Map<String, String> map) {
        Preconditions.checkNotNull(map);
        this.map.putAll(map);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String old = reverseMap.put(entry.getValue(), entry.getKey());
            if (old != null) {
                logger.warn(String.format("Duplicate keys %s / %s for value %s", old, entry.getKey(), entry.getValue()));
            }
        }
    }

    @Override
    public Collection<Map.Entry<String, String>> getMatches(String string) {
        return reverseMap.headMap(string);
    }

    @Override
    public String getValue(String key) {
        return map.get(key);
    }
}
