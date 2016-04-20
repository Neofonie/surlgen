package de.neofonie.surlgen.processor.spring;

import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.core.AbstractGenerator;
import de.neofonie.surlgen.processor.core.ClassWriter;
import de.neofonie.surlgen.processor.core.Options;
import de.neofonie.surlgen.processor.core.UrlMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.lang.model.element.ExecutableElement;
import java.util.HashMap;
import java.util.Map;

public class UrlFactoryServiceGenerator extends AbstractGenerator {

    private static final Map<String, FactoryModel> map = new HashMap<>();

    @Override
    protected void handleElement(ExecutableElement elem) {
        FactoryModel factoryModel = map
                .computeIfAbsent(elem.getEnclosingElement().toString(), key -> new FactoryModel(key));
        factoryModel.appendMethod(elem);
    }

    private static class FactoryModel {

        private final String name;
        private final JDefinedClass definedClass;
        private final JMethod baseMvcUriComponentsMethod;

        private FactoryModel(String name) {
            this.name = name;
            try {
                definedClass = ClassWriter.createClass(name + Options.getValue(Options.OptionEnum.ServiceClassName));
                definedClass.annotate(Service.class);
                definedClass.javadoc().add("Generated with " + UrlFactoryServiceGenerator.class.getCanonicalName());
                baseMvcUriComponentsMethod = appendBaseMvcUriComponentsBuilderMethod();
            } catch (JClassAlreadyExistsException e) {
                throw new IllegalStateException(e);
            }
        }

        private JMethod appendBaseMvcUriComponentsBuilderMethod() {
            JMethod getBaseMvcUriComponentsBuilder = definedClass.method(JMod.PROTECTED, UriComponentsBuilder.class, "getBaseUriComponentsBuilder");
            getBaseMvcUriComponentsBuilder.body()._return(JExpr._null());
            getBaseMvcUriComponentsBuilder.javadoc().add("Extension-Point to use another Base-UriComponentsBuilder than the default one");
            return getBaseMvcUriComponentsBuilder;
        }

        void appendMethod(ExecutableElement method) {
            UrlMethod parameters = new UrlMethod(method);

            JMethod mvcUriComponentsBuilderMethod = appendMvcUriComponentsBuilderMethod(parameters.getMethodName(), parameters);
            appendUriStringMethod(parameters.getMethodName(), parameters, mvcUriComponentsBuilderMethod);
        }

        private void appendUriStringMethod(String methodName, UrlMethod parameters, JMethod mvcUriComponentsBuilderMethod) {
            JMethod uriStringMethod = definedClass.method(JMod.PUBLIC, String.class, methodName + "UriString");

            JInvocation invocation = JExpr.invoke(mvcUriComponentsBuilderMethod);
            uriStringMethod.body()._return(invocation.invoke("toUriString"));
            parameters.appendParams(uriStringMethod, invocation);
        }

        private JMethod appendMvcUriComponentsBuilderMethod(String methodName, UrlMethod parameters) {
            JMethod urlMethod = definedClass.method(JMod.PUBLIC, UriComponentsBuilder.class, methodName);
            JBlock body = urlMethod.body();
            JInvocation fromMethodName = createMvcUriComponentsBuilderInvocation(parameters, methodName, urlMethod);
            body._return(fromMethodName);
            return urlMethod;
        }

        private JInvocation createMvcUriComponentsBuilderInvocation(UrlMethod parameters, String methodName, JMethod urlMethod) {
            AbstractJClass mvcUriComponentsBuilder = ClassWriter.ref(MvcUriComponentsBuilder.class);
            JInvocation fromMethodName = mvcUriComponentsBuilder.staticInvoke("fromMethodName");
            fromMethodName.arg(JExpr.invoke(baseMvcUriComponentsMethod));
            fromMethodName.arg(ClassWriter.ref(name).dotclass());
            fromMethodName.arg(methodName);
            fromMethodName.arg(parameters.createVarArgArray(urlMethod));
            return fromMethodName;
        }
    }
}
