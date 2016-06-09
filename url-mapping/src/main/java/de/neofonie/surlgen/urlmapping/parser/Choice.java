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

class Choice extends AbstractUrlPattern implements UrlPattern {

    private static final Logger logger = LoggerFactory.getLogger(Choice.class);
    private final AbstractUrlPattern choice;

    public Choice(AbstractUrlPattern choice) {
        this.choice = choice;
    }

    @Override
    public String toString() {
        return "Choice{" +
                "choice=" + choice +
                '}';
    }

    @Override
    protected Matcher matches(Matcher matcher) {
        final Matcher matches = choice.matches(matcher);
        if (matches != null) {
            return matches;
        }

        return matcher;
//        final String remaining = matcher.getRemaining();
//        if (!remaining.startsWith(string)) {
//            return false;
//        }
//        matcher.consume(string);
//        return true;
    }

}