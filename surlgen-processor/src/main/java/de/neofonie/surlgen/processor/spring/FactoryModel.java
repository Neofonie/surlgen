package de.neofonie.surlgen.processor.spring;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.writer.FileCodeWriter;
import de.neofonie.surlgen.processor.core.ClassWriter;
import de.neofonie.surlgen.processor.core.Options;
import de.neofonie.surlgen.processor.core.UrlMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.lang.model.element.ExecutableElement;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FactoryModel {

    private static final Map<String, FactoryModel> map = new HashMap<>();
    private final String name;
    private final ClassWriter classWriter;
    private final JDefinedClass definedClass;
    private final JMethod baseMvcUriComponentsMethod;

    public static FactoryModel create(String name, Options options, ClassWriter classWriter) {
        return map.computeIfAbsent(name, key -> new FactoryModel(key, options, classWriter));
    }

    private FactoryModel(String name, Options options, ClassWriter classWriter) {
        this.name = name;
        this.classWriter = classWriter;
//        codeModel = new JCodeModel();
        try {
            definedClass = classWriter.createClass(name + options.getValue(Options.OptionEnum.ServiceClassName));
            definedClass.annotate(Service.class);
            definedClass.javadoc().add("Generated with " + UrlFactoryServiceGenerator.class.getCanonicalName());
            baseMvcUriComponentsMethod = appendBaseMvcUriComponentsBuilderMethod();
        } catch (JClassAlreadyExistsException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void writeSourceCodes(File outputDir) throws IOException, JClassAlreadyExistsException {
        for (FactoryModel factoryModel : map.values()) {
            factoryModel.writeSourceCode(outputDir);
        }
    }

    private void writeSourceCode(File outputDir) throws JClassAlreadyExistsException, IOException {
        classWriter.build(new FileCodeWriter(outputDir));
    }

    private JMethod appendBaseMvcUriComponentsBuilderMethod() {
        JMethod getBaseMvcUriComponentsBuilder = definedClass.method(JMod.PROTECTED, UriComponentsBuilder.class, "getBaseUriComponentsBuilder");
        getBaseMvcUriComponentsBuilder.body()._return(JExpr._null());
        getBaseMvcUriComponentsBuilder.javadoc().add("Extension-Point to use another Base-UriComponentsBuilder than the default one");
        return getBaseMvcUriComponentsBuilder;
    }

    void appendMethod(ExecutableElement method, ClassWriter classWriter) {
        UrlMethod parameters = new UrlMethod(method, classWriter);

        JMethod mvcUriComponentsBuilderMethod = appendMvcUriComponentsBuilderMethod(parameters.getMethodName(), parameters);
        appendUriStringMethod(parameters.getMethodName(), parameters, mvcUriComponentsBuilderMethod);
    }

    private void appendUriStringMethod(String methodName, UrlMethod parameters, JMethod mvcUriComponentsBuilderMethod) {
        JMethod uriStringMethod = definedClass.method(JMod.PUBLIC, String.class, methodName + "UriString");
//        checkSupport(parameters, uriStringMethod);

        JInvocation invocation = JExpr.invoke(mvcUriComponentsBuilderMethod);
        uriStringMethod.body()._return(invocation.invoke("toUriString"));
        parameters.appendParams(uriStringMethod, invocation);
    }

    private JMethod appendMvcUriComponentsBuilderMethod(String methodName, UrlMethod parameters) {
        JMethod urlMethod = definedClass.method(JMod.PUBLIC, UriComponentsBuilder.class, methodName);
//        checkSupport(parameters, urlMethod);
        JBlock body = urlMethod.body();
        JInvocation fromMethodName = createMvcUriComponentsBuilderInvocation(parameters, methodName, urlMethod);
        body._return(fromMethodName);
        return urlMethod;
    }

//    private void checkSupport(List<? extends VariableElement> parameters, JMethod urlMethod) {
//        boolean notSupported = TypeEnum.isNotSupported(parameters);
//        if (notSupported) {
//            urlMethod.annotate(Deprecated.class);
//            JDocComment javadoc = urlMethod.javadoc();
//            javadoc.addDeprecated();
//            javadoc.add("Some params are not supported by MvcUriComponentsBuilder");
//        }
//    }

    private JInvocation createMvcUriComponentsBuilderInvocation(UrlMethod parameters, String methodName, JMethod urlMethod) {
        AbstractJClass mvcUriComponentsBuilder = classWriter.ref(MvcUriComponentsBuilder.class);
        JInvocation fromMethodName = mvcUriComponentsBuilder.staticInvoke("fromMethodName");
        fromMethodName.arg(JExpr.invoke(baseMvcUriComponentsMethod));
        fromMethodName.arg(classWriter.ref(name).dotclass());
        fromMethodName.arg(methodName);
        fromMethodName.arg(parameters.createVarArgArray(urlMethod));
        return fromMethodName;
    }
}
