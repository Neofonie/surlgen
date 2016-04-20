package de.neofonie.surlgen.processor.core;

import junit.framework.TestCase;

public class CamelCaseUtilsTest extends TestCase {

    public void testFirstCharLowerCased() throws Exception {
        assertEquals(null, CamelCaseUtils.firstCharLowerCased(null));
        assertEquals("", CamelCaseUtils.firstCharLowerCased(""));
        assertEquals("fooBar", CamelCaseUtils.firstCharLowerCased("FooBar"));
        assertEquals("fooBar", CamelCaseUtils.firstCharLowerCased("fooBar"));
        assertEquals("f", CamelCaseUtils.firstCharLowerCased("F"));
        assertEquals("f", CamelCaseUtils.firstCharLowerCased("f"));
        assertEquals("fo", CamelCaseUtils.firstCharLowerCased("Fo"));
        assertEquals("fo", CamelCaseUtils.firstCharLowerCased("fo"));
        assertEquals("fO", CamelCaseUtils.firstCharLowerCased("FO"));
        assertEquals("fO", CamelCaseUtils.firstCharLowerCased("fO"));
    }

    public void testFirstCharUpperCased() throws Exception {
        assertEquals(null, CamelCaseUtils.firstCharUpperCased(null));
        assertEquals("", CamelCaseUtils.firstCharUpperCased(""));
        assertEquals("FooBar", CamelCaseUtils.firstCharUpperCased("FooBar"));
        assertEquals("FooBar", CamelCaseUtils.firstCharUpperCased("fooBar"));
        assertEquals("F", CamelCaseUtils.firstCharUpperCased("F"));
        assertEquals("F", CamelCaseUtils.firstCharUpperCased("f"));
        assertEquals("Fo", CamelCaseUtils.firstCharUpperCased("Fo"));
        assertEquals("Fo", CamelCaseUtils.firstCharUpperCased("fo"));
        assertEquals("FO", CamelCaseUtils.firstCharUpperCased("FO"));
        assertEquals("FO", CamelCaseUtils.firstCharUpperCased("fO"));

    }
}