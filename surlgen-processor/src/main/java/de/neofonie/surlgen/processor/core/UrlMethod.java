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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;

public class UrlMethod {

    private final List<? extends VariableElement> parameters;
    private final ExecutableElement method;

    public UrlMethod(ExecutableElement method) {
        this.method = method;
        parameters = method.getParameters();
    }

    public Element getClazz() {
        return method.getEnclosingElement();
    }

    public String getMethodName() {
        return method.getSimpleName().toString();
    }

    public void appendParams(JMethod uriStringMethod, JInvocation invocation) {
        for (VariableElement variableElement : parameters) {
            TypeEnum typeEnum = TypeEnum.getType(variableElement);
            if (typeEnum.isRelevantForUrl()) {
                AbstractJType type = ClassWriter.parseType(variableElement.asType().toString());
                JVar param = uriStringMethod.param(type, variableElement.getSimpleName().toString());
                invocation.arg(param);
            }
        }
    }

    public JArray createVarArgArray(JMethod urlMethod) {
        AbstractJType objectArray = ClassWriter.parseType("Object");
        JArray jArray = JExpr.newArray(objectArray);
        appendVarArgArray(urlMethod, jArray);
        return jArray;
    }

    void appendVarArgArray(JMethod urlMethod, JArray jArray) {
        for (VariableElement variableElement : parameters) {
            TypeEnum typeEnum = TypeEnum.getType(variableElement);
            if (typeEnum.isRelevantForUrl()) {
                AbstractJType type = ClassWriter.parseType(variableElement.asType().toString());
                JVar param = urlMethod.param(type, variableElement.getSimpleName().toString());
                jArray.add(param);
            } else {
                jArray.add(JExpr._null());
            }
        }
    }
}
