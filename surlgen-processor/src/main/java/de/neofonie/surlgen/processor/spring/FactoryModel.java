package de.neofonie.surlgen.processor.spring;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.writer.FileCodeWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FactoryModel {

    private static final Map<String, FactoryModel> map = new HashMap<>();
    private final String name;
    private final JCodeModel codeModel;
    private final JDefinedClass definedClass;
    private final JMethod baseMvcUriComponentsMethod;

    public static FactoryModel create(String name, Options options) {
        return map.computeIfAbsent(name, key -> new FactoryModel(key, options));
    }

    private FactoryModel(String name, Options options) {
        this.name = name;
        codeModel = new JCodeModel();
        try {
            definedClass = codeModel._class(name + options.getValue(Options.OptionEnum.ServiceClassName));
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
        codeModel.build(new FileCodeWriter(outputDir));
    }

    private JMethod appendBaseMvcUriComponentsBuilderMethod() {
        JMethod getBaseMvcUriComponentsBuilder = definedClass.method(JMod.PROTECTED, UriComponentsBuilder.class, "getBaseUriComponentsBuilder");
        getBaseMvcUriComponentsBuilder.body()._return(JExpr._null());
        getBaseMvcUriComponentsBuilder.javadoc().add("Extension-Point to use another Base-UriComponentsBuilder than the default one");
        return getBaseMvcUriComponentsBuilder;
    }

    void appendMethod(ExecutableElement method) {
        String methodName = method.getSimpleName().toString();
        List<? extends VariableElement> parameters = method.getParameters();

        JMethod mvcUriComponentsBuilderMethod = appendMvcUriComponentsBuilderMethod(methodName, parameters);
        appendUriStringMethod(methodName, parameters, mvcUriComponentsBuilderMethod);
    }

    private void appendUriStringMethod(String methodName, List<? extends VariableElement> parameters, JMethod mvcUriComponentsBuilderMethod) {
        JMethod uriStringMethod = definedClass.method(JMod.PUBLIC, String.class, methodName + "UriString");
//        checkSupport(parameters, uriStringMethod);

        JBlock body = uriStringMethod.body();
        JInvocation invocation = JExpr.invoke(mvcUriComponentsBuilderMethod);
        body._return(invocation.invoke("toUriString"));
        for (VariableElement variableElement : parameters) {
            TypeEnum typeEnum = TypeEnum.getType(variableElement);
            if (typeEnum.isRelevantForUrl()) {
                AbstractJType type = codeModel.parseType(variableElement.asType().toString());
                JVar param = uriStringMethod.param(type, variableElement.getSimpleName().toString());
                invocation.arg(param);
            }
        }
    }

    private JMethod appendMvcUriComponentsBuilderMethod(String methodName, List<? extends VariableElement> parameters) {
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

    private JInvocation createMvcUriComponentsBuilderInvocation(List<? extends VariableElement> parameters, String methodName, JMethod urlMethod) {
        AbstractJClass mvcUriComponentsBuilder = codeModel.ref(MvcUriComponentsBuilder.class);
        JInvocation fromMethodName = mvcUriComponentsBuilder.staticInvoke("fromMethodName");
        fromMethodName.arg(JExpr.invoke(baseMvcUriComponentsMethod));
        fromMethodName.arg(codeModel.ref(name).dotclass());
        fromMethodName.arg(methodName);

        AbstractJType objectArray = codeModel.parseType("Object");
        JArray jArray = JExpr.newArray(objectArray);
        for (VariableElement variableElement : parameters) {
            TypeEnum typeEnum = TypeEnum.getType(variableElement);
            if (typeEnum.isRelevantForUrl()) {
                AbstractJType type = codeModel.parseType(variableElement.asType().toString());
                JVar param = urlMethod.param(type, variableElement.getSimpleName().toString());
                jArray.add(param);
            } else {
                jArray.add(JExpr._null());
            }
        }
        fromMethodName.arg(jArray);
        return fromMethodName;
    }
}
