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

package de.neofonie.surlgen.processor.spring;

import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.classwriter.ClassWriter;
import de.neofonie.surlgen.processor.core.AbstractGenerator;
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
            try {
                MvcUriComponentsBuilder.class.getMethod("fromMethodName", UriComponentsBuilder.class, Class.class, String.class, Object[].class);

                JMethod getBaseMvcUriComponentsBuilder = definedClass.method(JMod.PROTECTED, UriComponentsBuilder.class, "getBaseUriComponentsBuilder");
                getBaseMvcUriComponentsBuilder.body()._return(JExpr._null());
                getBaseMvcUriComponentsBuilder.javadoc().add("Extension-Point to use another Base-UriComponentsBuilder than the default one");
                return getBaseMvcUriComponentsBuilder;
            } catch (NoSuchMethodException e) {
// prior to Spring 4.2
                return null;
            }
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
            JArray varArgArray = JExpr.newArray(ClassWriter.parseType("Object"));
            JVar uriComponentsBuilder = body.decl(ClassWriter.ref(UriComponentsBuilder.class), "uriComponentsBuilder")
                    .init(createMvcUriComponentsBuilderInvocation(methodName, varArgArray));

            parameters.handleUriComponentsInvocation(urlMethod, varArgArray, uriComponentsBuilder);

            body._return(uriComponentsBuilder);
            return urlMethod;
        }

        private JInvocation createMvcUriComponentsBuilderInvocation(String methodName, JArray varArgArray) {
            AbstractJClass mvcUriComponentsBuilder = ClassWriter.ref(MvcUriComponentsBuilder.class);
            JInvocation fromMethodName = mvcUriComponentsBuilder.staticInvoke("fromMethodName");
            if (baseMvcUriComponentsMethod != null) {
                fromMethodName.arg(JExpr.invoke(baseMvcUriComponentsMethod));
            }
            fromMethodName.arg(ClassWriter.ref(name).dotclass());
            fromMethodName.arg(methodName);
            fromMethodName.arg(varArgArray);
            return fromMethodName;
        }
    }
}
