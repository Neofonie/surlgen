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

import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.classwriter.ClassWriter;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.logging.Logger;

public enum TypeEnum {
    URL_RELEVANT {
        @Override
        public void handleUriComponentsInvocation(VariableElement variableElement, JMethod urlMethod, JArray varArgArray, JVar uriComponentsBuilder) {

            AbstractJType type = ClassWriter.parseType(variableElement.asType().toString());
            JVar param = urlMethod.param(type, variableElement.getSimpleName().toString());
            varArgArray.add(param);
        }
    },
    MODEL_ATTRIBUTE {
        @Override
        public void handleUriComponentsInvocation(VariableElement variableElement, JMethod urlMethod, JArray varArgArray, JVar uriComponentsBuilder) {

            varArgArray.add(JExpr._null());
            TypeMirror typeMirror = variableElement.asType();
            if (typeMirror.getKind() == TypeKind.DECLARED) {
                DeclaredType declaredType = (DeclaredType) typeMirror;
                JVar param = urlMethod.param(ClassWriter.ref(declaredType.asElement().toString()), variableElement.getSimpleName().toString());
                JBlock block = urlMethod.body()._if(param.neNull())._then();
                List<? extends Element> elements = declaredType.asElement().getEnclosedElements();
                JVar tempValue = block.decl(ClassWriter.ref(Object.class), "__tempValue");
                for (Element element : elements) {
                    handleElement(tempValue, block, uriComponentsBuilder, element, param);
                }
            }
        }

        private void handleElement(JVar tempValue, JBlock body, JVar uriComponentsBuilder, Element element, JVar param) {
            if (!(element instanceof ExecutableElement)
                    || !element.getSimpleName().toString().startsWith("get")) {
                return;
            }
            ExecutableElement executableElement = (ExecutableElement) element;
            if (!executableElement.getParameters().isEmpty()
                    || executableElement.getReturnType().getKind() == TypeKind.VOID
                    || !executableElement.getModifiers().contains(Modifier.PUBLIC)
                    || executableElement.getModifiers().contains(Modifier.STATIC)) {
                return;
            }

            String name = CamelCaseUtils.firstCharLowerCased(element.getSimpleName().toString().substring(3));

            body.assign(tempValue, param.invoke(executableElement.getSimpleName().toString()));
            body._if(tempValue.neNull())._then()
                    .invoke(uriComponentsBuilder, "queryParam").arg(name)
                    .arg(tempValue);
        }
    },
    NOT_SUPPORTED {
        @Override
        public void handleUriComponentsInvocation(VariableElement variableElement, JMethod urlMethod, JArray varArgArray, JVar uriComponentsBuilder) {
            varArgArray.add(JExpr._null());
        }
    },
    OTHER {
        @Override
        public void handleUriComponentsInvocation(VariableElement variableElement, JMethod urlMethod, JArray varArgArray, JVar uriComponentsBuilder) {
            varArgArray.add(JExpr._null());
        }
    };

    private static final Logger log = Logger.getLogger(TypeEnum.class.getCanonicalName());

    public static TypeEnum getType(VariableElement variableElement) {
        if (variableElement.getAnnotation(RequestParam.class) != null) {
            return URL_RELEVANT;
        }
        if (variableElement.getAnnotation(PathVariable.class) != null) {
            return URL_RELEVANT;
        }
        //ModelAttribute is currently not implemented in MvcUriComponentsBuilder - so we cant support this
        if (variableElement.getAnnotation(ModelAttribute.class) != null) {
            return MODEL_ATTRIBUTE;
        }
        //MatrixVariable is currently not implemented in MvcUriComponentsBuilder - so we cant support this
        if (variableElement.getAnnotation(MatrixVariable.class) != null) {
            log.info("MatrixVariable currently isnt supported in MvcUriComponentsBuilder");
            return NOT_SUPPORTED;
        }
        return OTHER;
    }

    public boolean isRelevantForUrl() {
        return this == URL_RELEVANT || this == MODEL_ATTRIBUTE;
    }

    public abstract void handleUriComponentsInvocation(VariableElement variableElement, JMethod urlMethod, JArray varArgArray, JVar uriComponentsBuilder);
}
