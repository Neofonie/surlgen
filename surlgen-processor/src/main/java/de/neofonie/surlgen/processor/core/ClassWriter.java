package de.neofonie.surlgen.processor.core;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.writer.FileCodeWriter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClassWriter {

    private static final Map<String, JDefinedClass> createdClasses = new HashMap<>();
    private final JCodeModel codeModel;

    public ClassWriter(JCodeModel codeModel) {
        this.codeModel = codeModel;
    }

    @Nonnull
    public JDefinedClass createClass(@Nonnull String sFullyQualifiedClassName) throws JClassAlreadyExistsException {
        JDefinedClass result = createdClasses.get(sFullyQualifiedClassName);
        if (result != null) {
            return result;
        }
        result = codeModel._class(sFullyQualifiedClassName);
        createdClasses.put(sFullyQualifiedClassName, result);
        return result;
    }

    public static interface Init {
        void init(JDefinedClass definedClass);
    }

    @Nonnull
    public AbstractJClass ref(@Nonnull Class<?> clazz) {
        return codeModel.ref(clazz);
    }

    public void build(@Nonnull AbstractCodeWriter out) throws IOException {
        codeModel.build(out);
    }

    @Nonnull
    public AbstractJType parseType(@Nonnull String name) {
        return codeModel.parseType(name);
    }

    @Nonnull
    public AbstractJClass ref(@Nonnull String sFullyQualifiedClassName) {
        return codeModel.ref(sFullyQualifiedClassName);
    }

    public void writeSourceCodes(File outputDir) throws IOException, JClassAlreadyExistsException {
//        for (FunctionModel functionModel : map.values()) {
        writeSourceCode(outputDir);
//        }
    }

    private void writeSourceCode(File outputDir) throws JClassAlreadyExistsException, IOException {
        build(new FileCodeWriter(outputDir));
    }
}
