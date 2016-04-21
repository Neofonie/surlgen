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

import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.logging.Logger;

enum TypeEnum {
    URL_RELEVANT,
    NOT_SUPPORTED,
    OTHER;

    private static final Logger log = Logger.getLogger(TypeEnum.class.getCanonicalName());

    static TypeEnum getType(VariableElement variableElement) {
        if (variableElement.getAnnotation(RequestParam.class) != null) {
            return URL_RELEVANT;
        }
        if (variableElement.getAnnotation(PathVariable.class) != null) {
            return URL_RELEVANT;
        }
        //ModelAttribute is currently not implemented in MvcUriComponentsBuilder - so we cant support this
        if (variableElement.getAnnotation(ModelAttribute.class) != null) {
            log.info("ModelAttribute currently isnt supported in MvcUriComponentsBuilder");
            return NOT_SUPPORTED;
        }
        //MatrixVariable is currently not implemented in MvcUriComponentsBuilder - so we cant support this
        if (variableElement.getAnnotation(MatrixVariable.class) != null) {
            log.info("MatrixVariable currently isnt supported in MvcUriComponentsBuilder");
            return NOT_SUPPORTED;
        }
        return OTHER;
    }

    static boolean isNotSupported(List<? extends VariableElement> parameters) {
        for (VariableElement variableElement : parameters) {
            if (getType(variableElement) == NOT_SUPPORTED) {
                return true;
            }
        }
        return false;
    }

    public boolean isRelevantForUrl() {
        return this == URL_RELEVANT;
    }
}
