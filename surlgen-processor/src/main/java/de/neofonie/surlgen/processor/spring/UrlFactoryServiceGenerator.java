package de.neofonie.surlgen.processor.spring;

import de.neofonie.surlgen.processor.core.AbstractGenerator;
import de.neofonie.surlgen.processor.core.ClassWriter;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class UrlFactoryServiceGenerator extends AbstractGenerator {

    @Override
    protected void handleElement(Element elem, ClassWriter classWriter) {
        Element clazz = elem.getEnclosingElement();
        FactoryModel factoryModel = FactoryModel.create(clazz.toString(), getOptions(), classWriter);
        factoryModel.appendMethod((ExecutableElement) elem, classWriter);

    }
//
//    @Override
//    protected void finished() {
//        try {
//            FactoryModel.writeSourceCodes(getOutputDir());
//        } catch (IOException | JClassAlreadyExistsException e) {
//            throw new IllegalArgumentException(e);
//        }
//    }
}
