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

package de.neofonie.surlgen.processor.core;

import java.util.HashMap;
import java.util.Map;

public abstract class Options {

    private static final Map<OptionEnum, String> values = new HashMap<>();

    public static void init(Map<String, String> options) {
        for (OptionEnum optionEnum : OptionEnum.values()) {
            String s = options.get(optionEnum.name);
            if (s == null) {
                s = optionEnum.defaultValue;
            }

            values.put(optionEnum, s);
        }
    }

    public static String getValue(OptionEnum optionEnum) {
        return values.get(optionEnum);
    }

    public static enum OptionEnum {
        ServiceClassName("service.class.name", "UrlFactoryGenerated"),
        FunctionClassName("function.class.name", "de.neofonie.surlgen.UrlFunction"),
        ServiceAddUriComponentsBuilder("service.addUriComponentsBuilder", "false"),
        TLD_FILE_NAME("tld.file", ""),
        TLD_URI("tld.uri", "http://de.neofonie.surlgen.uri/");

        private final String name;
        private final String defaultValue;

        OptionEnum(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }
    }
}
