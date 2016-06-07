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

import de.neofonie.surlgen.processor.classwriter.ClassWriter;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
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
    private static File outputDir;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Options.init(processingEnv.getOptions());
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
        ClassWriter.init();

        for (Element elem : roundEnv.getElementsAnnotatedWith(RequestMapping.class)) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("annotation found in " + elem);
            }

            ElementKind elementKind = elem.getKind();

            if (elementKind == ElementKind.METHOD) {
                handleElement((ExecutableElement) elem);
            }
        }

        finished();
        finished(getOutputDir());
        return false; // no further processing of this annotation type
    }

    protected void finished(File outputDir) {

    }

    private void finished() {
        try {
            File outputDir1 = getOutputDir();
            ClassWriter.writeSourceCodes(outputDir1);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected abstract void handleElement(ExecutableElement elem);

    File getOutputDir() {
        try {

            if (outputDir != null) {
                return outputDir;
            }
            URI tempFile = processingEnv.getFiler().createSourceFile("a").toUri();
            outputDir = new File(tempFile).getParentFile();
            if (!outputDir.exists() && !outputDir.mkdirs()) {
                throw new IllegalArgumentException("Couldnt create " + outputDir.getAbsolutePath());
            }
            if (log.isLoggable(Level.FINE)) {
                log.fine("writing source code to: " + outputDir.getAbsolutePath());
            }
            return outputDir;
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}