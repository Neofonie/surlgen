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

package de.neofonie.surlgen.processor.classwriter;

import com.helger.jcodemodel.*;
import de.neofonie.surlgen.processor.util.LangModelUtil;
import de.neofonie.surlgen.processor.util.SingletonInstance;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.function.Supplier;

public class URLConversionServiceWriter extends ClassWriter {

    private static SingletonInstance<URLConversionServiceWriter> instance = new SingletonInstance<>(new Supplier<URLConversionServiceWriter>() {
        @Override
        public URLConversionServiceWriter get() {
            try {
                JDefinedClass definedClass = ClassWriter.createClass("de.neofonie.surlgen.generated.URLConversionService");
                definedClass.annotate(Service.class);
                definedClass.javadoc().add("Generated with " + URLConversionServiceWriter.class.getCanonicalName());
                return new URLConversionServiceWriter(definedClass);
            } catch (JClassAlreadyExistsException e) {
                throw new IllegalStateException(e);
            }
        }
    });
    //    private final JFieldVar conversionService;
    private final JMethod addQueryParamMethodObject;
    private final JMethod addQueryParamMethodCollection;
//    private addQueryParamMethod;

    private URLConversionServiceWriter(JDefinedClass definedClass) {
        super(definedClass);
//        conversionService = definedClass.field(JMod.PRIVATE, ConversionService.class, "conversionService");
//        conversionService.annotate(Autowired.class);
        addQueryParamMethodObject = createAddQueryParamMethodObject(definedClass);
        addQueryParamMethodCollection = createAddQueryParamMethodCollection(definedClass, addQueryParamMethodObject);
    }

    public static URLConversionServiceWriter getInstance() {
        return instance.getInstance();
    }

    public void addQueryParam(JBlock body, JFieldVar urlConversionService,
                              IJExpression uriComponentsBuilder, String name, IJExpression var, TypeMirror returnType, LangModelUtil langModelUtil) {

        if (returnType.getKind().isPrimitive()) {
            body.invoke(urlConversionService, addQueryParamMethodObject).arg(uriComponentsBuilder).arg(name).arg(var);
        } else if (returnType.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) returnType;
            if (langModelUtil.isOfType(declaredType, Collection.class)) {
                body.invoke(urlConversionService, addQueryParamMethodCollection).arg(uriComponentsBuilder).arg(name).arg(var);
            } else {
                body.invoke(urlConversionService, addQueryParamMethodObject).arg(uriComponentsBuilder).arg(name).arg(var);
            }
        } else {
            throw new IllegalArgumentException("Unknown type " + returnType);
        }
    }

    private static JMethod createAddQueryParamMethodObject(JDefinedClass definedClass) {
        JMethod addQueryParamMethod = definedClass.method(JMod.PUBLIC, JPrimitiveType.VOID, "addQueryParamObject");
        final JVar uriComponentsBuilder1 = addQueryParamMethod.param(UriComponentsBuilder.class, "uriComponentsBuilder");
        final JVar name = addQueryParamMethod.param(String.class, "name");
        final JVar value = addQueryParamMethod.param(Object.class, "value");
        final JBlock mbody = addQueryParamMethod.body();
        mbody._if(value.eqNull())._then()._return();

//            final JBlock thenBlock = body._if(tempValue.neNull())._then();
        mbody.invoke(uriComponentsBuilder1, "queryParam").arg(name).arg(value);
        return addQueryParamMethod;
    }

    private static JMethod createAddQueryParamMethodCollection(JDefinedClass definedClass, JMethod addQueryParamMethodObject) {
        JMethod addQueryParamMethod = definedClass.method(JMod.PUBLIC, JPrimitiveType.VOID, "addQueryParamCollection");
        final JVar uriComponentsBuilder1 = addQueryParamMethod.param(UriComponentsBuilder.class, "uriComponentsBuilder");
        final JVar name = addQueryParamMethod.param(String.class, "name");
        final JVar collection = addQueryParamMethod.param(Collection.class, "collection");
        final JBlock mbody = addQueryParamMethod.body();
        mbody._if(collection.eqNull())._then()._return();

        final JForEach forEach = mbody.forEach(ref(Object.class), "value", collection);
        forEach.body().invoke(addQueryParamMethodObject).arg(uriComponentsBuilder1).arg(name).arg(forEach.var());

        return addQueryParamMethod;
    }
}
