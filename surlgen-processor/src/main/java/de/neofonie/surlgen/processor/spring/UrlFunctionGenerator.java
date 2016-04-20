package de.neofonie.surlgen.processor.spring;

import de.neofonie.surlgen.processor.core.AbstractGenerator;
import de.neofonie.surlgen.processor.core.ClassWriter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class UrlFunctionGenerator extends AbstractGenerator {

    @Override
    protected void handleElement(Element elem, ClassWriter classWriter) {
        FunctionModel.write((ExecutableElement) elem, getOptions(), classWriter);
//        FunctionModel functionModel = FunctionModel.create(elem.getEnclosingElement().toString(), getOptions(), classWriter);
//        functionModel.appendMethod((ExecutableElement) elem);
    }
}
