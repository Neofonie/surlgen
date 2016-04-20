package de.neofonie.surlgen.processor.core;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractGenerator extends AbstractProcessor {

    private final Logger log = Logger.getLogger(getClass().getCanonicalName());
    private Options options;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        options = new Options(processingEnv.getOptions());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<>();
        annotataions.add(RequestMapping.class.getCanonicalName());
        return annotataions;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }

        for (Element elem : roundEnv.getElementsAnnotatedWith(RequestMapping.class)) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("annotation found in " + elem);
            }

            ElementKind elementKind = elem.getKind();

            if (elementKind == ElementKind.METHOD) {
                handleElement(elem);
            }
        }

        finished();
        return true; // no further processing of this annotation type
    }

    protected abstract void finished();

    protected abstract void handleElement(Element elem);

    protected File getOutputDir() throws IOException {
        URI tempFile = processingEnv.getFiler().createSourceFile("a").toUri();
        File outputDir = new File(tempFile).getParentFile();
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Couldnt create " + outputDir.getAbsolutePath());
        }
        if (log.isLoggable(Level.FINE)) {
            log.fine("writing source code to: " + outputDir.getAbsolutePath());
        }
        return outputDir;
    }

    public Options getOptions() {
        return options;
    }
}