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

package de.neofonie.surlgen.processor.core.data;

import com.helger.jcodemodel.JArray;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JVar;
import de.neofonie.surlgen.processor.util.LangModelUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.ArrayList;

public class UrlMethod {

    private final ArrayList<Parameter> parameters;
    private final ExecutableElement method;

    public UrlMethod(ExecutableElement method, LangModelUtil langModelUtil) {
        this.method = method;
        parameters = Parameter.createParameters(method.getParameters(), langModelUtil);
//        parameters = method.getParameters();
    }

    public Element getClazz() {
        return method.getEnclosingElement();
    }

    public String getMethodName() {
        return method.getSimpleName().toString();
    }

    public void appendParams(JMethod uriStringMethod, JInvocation invocation) {
        for (Parameter parameter : parameters) {
            if (parameter.isRelevantForUrl()) {
                JVar param = parameter.appendParamToMethod(uriStringMethod);
                invocation.arg(param);
            }
        }
    }

    public void handleUriComponentsInvocation(JMethod urlMethod, JArray varArgArray, JVar uriComponentsBuilder) {
        for (Parameter parameter : parameters) {
            parameter.handleUriComponentsInvocation(urlMethod, varArgArray, uriComponentsBuilder);
        }
    }
}
