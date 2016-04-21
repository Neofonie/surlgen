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
