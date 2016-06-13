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

class MatcherResult<T> {

    private final T value;
    private final Params params;

    static <T> MatcherResult<T> create(T value, Params params) {
        if (value != null) {
            return new MatcherResult<>(value, params);
        } else {
            return null;
        }
    }

    MatcherResult(T value, Params params) {
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(params);
        this.value = value;
        this.params = params.copy();
    }

    public T getValue() {
        return value;
    }

    public Params getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "MatcherResult{" +
                "value=" + value +
                ", params=" + params +
                '}';
    }
}
