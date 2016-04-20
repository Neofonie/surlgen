package de.neofonie.surlgen.processor.core;

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

    public static void init() {
        Preconditions.checkArgument(codeModel == null);
        codeModel = new JCodeModel();
    }

    private ClassWriter() {
    }

    @Nonnull
    public static JDefinedClass createClass(@Nonnull String sFullyQualifiedClassName) throws JClassAlreadyExistsException {
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
}
