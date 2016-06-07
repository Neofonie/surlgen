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

package de.neofonie.surlgen.processor.classwriter;

import com.google.common.base.Preconditions;
import com.helger.jcodemodel.*;
import com.helger.jcodemodel.writer.FileCodeWriter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ClassWriter {

    private static final Map<String, JDefinedClass> createdClasses = new HashMap<>();
    private static JCodeModel codeModel;
    final JDefinedClass definedClass;

    public static void init() {
        Preconditions.checkArgument(codeModel == null);
        codeModel = new JCodeModel();
    }

    ClassWriter(JDefinedClass definedClass) {
        Preconditions.checkNotNull(definedClass);
        this.definedClass = definedClass;
    }

    public static JDefinedClass createClass(String sFullyQualifiedClassName) throws JClassAlreadyExistsException {
        JDefinedClass result = createdClasses.get(sFullyQualifiedClassName);
        if (result != null) {
            return result;
        }
        result = codeModel._class(sFullyQualifiedClassName);
        createdClasses.put(sFullyQualifiedClassName, result);
        return result;
    }

    @Nonnull
    public static AbstractJClass ref(@Nonnull Class<?> clazz) {
        return codeModel.ref(clazz);
    }

    static void build(@Nonnull AbstractCodeWriter out) throws IOException {
        codeModel.build(out);
    }

    @Nonnull
    public static AbstractJType parseType(@Nonnull String name) {
        return codeModel.parseType(name);
    }

    @Nonnull
    public static AbstractJClass ref(@Nonnull String sFullyQualifiedClassName) {
        return codeModel.ref(sFullyQualifiedClassName);
    }

    public static void writeSourceCodes(File outputDir) throws IOException {
        writeSourceCode(outputDir);
        codeModel = null;
    }

    private static void writeSourceCode(File outputDir) throws IOException {
        build(new FileCodeWriter(outputDir));
    }

    public JDefinedClass getDefinedClass() {
        return definedClass;
    }
}
