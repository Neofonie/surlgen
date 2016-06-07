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

package de.neofonie.surlgen.processor.util;

import de.neofonie.surlgen.processor.core.CamelCaseUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LangModelUtil {

    private final ProcessingEnvironment processingEnvironment;

    public LangModelUtil(ProcessingEnvironment processingEnvironment) {
        this.processingEnvironment = processingEnvironment;
    }

    public List<Element> getEnclosedElementsInHierarchy(TypeMirror typeMirror) {
        List<Element> result = new ArrayList<>();
        final List<Element> classHierarchy = getClassHierarchy(typeMirror);
        for (Element clazz : classHierarchy) {
            List<? extends Element> elements = clazz.getEnclosedElements();
            result.addAll(elements);
        }
        return result;
    }

    private List<Element> getClassHierarchy(TypeMirror typeMirror) {
        List<Element> result = new ArrayList<>();
        fillClassHierarchy(result, typeMirror);
        return result;
    }

    private void fillClassHierarchy(List<Element> result, final TypeMirror typeMirror) {
        result.add(convert(typeMirror));
        final List<? extends TypeMirror> typeMirrors = processingEnvironment.getTypeUtils().directSupertypes(typeMirror);
        for (TypeMirror typeMirror1 : typeMirrors) {
            fillClassHierarchy(result, typeMirror1);
        }
    }

    public static Element convert(TypeMirror typeMirror) {
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            throw new IllegalArgumentException(String.format("Unknown type %s", typeMirror));
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        return declaredType.asElement();
    }

    public static <T extends Element> List<T> filter(List<T> list, ElementKind kind) {
        return filter(list, new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return t.getKind() == kind;
            }
        });
    }

    private static <T extends Element> List<T> filter(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    public List<? extends Element> extractGetterForFields(TypeMirror typeMirror) {
        List<? extends Element> elements = getEnclosedElementsInHierarchy(typeMirror);
        final List<Element> result = new ArrayList<>();
        final List<? extends Element> fields = LangModelUtil.filter(elements, ElementKind.FIELD);
        for (Element field : fields) {
            final Element method = getMethod(elements, field);
            elements.remove(method);
            result.add(method);
        }
        return result;
    }

    private Element getMethod(List<? extends Element> elements, Element field) {
        for (Element method : elements) {
            if (isMethodForField(method, field)) {
                return method;
            }
        }
        throw new IllegalArgumentException(String.format("No getter for field %s in %s found (or its not public, has params, type dismatch, static, ...)", field, field.getEnclosingElement()));
    }

    private boolean isMethodForField(Element method, Element field) {
        if (method.getKind() != ElementKind.METHOD) {
            return false;
        }

        if (!method.getSimpleName().toString().equals("get" + CamelCaseUtils.firstCharUpperCased(field.getSimpleName().toString()))) {
            return false;
        }

        ExecutableElement executableElement = (ExecutableElement) method;
        if (!executableElement.getParameters().isEmpty()
                || executableElement.getReturnType().getKind() == TypeKind.VOID
                || !executableElement.getModifiers().contains(Modifier.PUBLIC)
                || executableElement.getModifiers().contains(Modifier.STATIC)) {
            return false;
        }

        return executableElement.getReturnType().equals(field.asType());
    }
}
