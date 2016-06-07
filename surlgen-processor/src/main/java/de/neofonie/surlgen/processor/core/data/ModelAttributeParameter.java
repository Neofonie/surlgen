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

import com.google.common.base.Preconditions;
import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.classwriter.ClassWriter;
import de.neofonie.surlgen.processor.core.CamelCaseUtils;
import de.neofonie.surlgen.processor.util.LangModelUtil;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;

class ModelAttributeParameter extends Parameter {

    protected ModelAttributeParameter(VariableElement variableElement, LangModelUtil langModelUtil) {
        super(variableElement, langModelUtil);
    }

    @Override
    public void handleUriComponentsInvocation(JMethod urlMethod, JArray varArgArray, JVar uriComponentsBuilder) {

        varArgArray.add(JExpr._null());
        TypeMirror typeMirror = variableElement.asType();

        final Element elementType = LangModelUtil.convert(typeMirror);
        JVar param = urlMethod.param(ClassWriter.ref(elementType.toString()), variableElement.getSimpleName().toString());
        JBlock block = urlMethod.body()._if(param.neNull())._then();
        JVar tempValue = block.decl(ClassWriter.ref(Object.class), "__tempValue");

        final List<? extends Element> getter = langModelUtil.extractGetterForFields(typeMirror);

        for (Element element : getter) {
            handleElement(tempValue, block, uriComponentsBuilder, element, param);
        }
    }

    private void handleElement(JVar tempValue, JBlock body, JVar uriComponentsBuilder, Element element, JVar param) {
        Preconditions.checkArgument(element instanceof ExecutableElement);
        Preconditions.checkArgument(element.getSimpleName().toString().startsWith("get"));


        ExecutableElement executableElement = (ExecutableElement) element;

        Preconditions.checkArgument(executableElement.getParameters().isEmpty());
        Preconditions.checkArgument(executableElement.getReturnType().getKind() != TypeKind.VOID);
        Preconditions.checkArgument(executableElement.getModifiers().contains(Modifier.PUBLIC));
        Preconditions.checkArgument(!executableElement.getModifiers().contains(Modifier.STATIC));

        String name = CamelCaseUtils.firstCharLowerCased(element.getSimpleName().toString().substring(3));

        body.assign(tempValue, param.invoke(executableElement.getSimpleName().toString()));
        body._if(tempValue.neNull())._then()
                .invoke(uriComponentsBuilder, "queryParam").arg(name)
                .arg(tempValue);
    }

    private Element convert(TypeMirror typeMirror) {
        return LangModelUtil.convert(typeMirror);
    }


    @Override
    public boolean isRelevantForUrl() {
        return true;
    }

}
