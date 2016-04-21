# Spring Url Generator (surlgen)

## Usage

### Create Service-Classes for Spring-RequestMapping Annotation 

To generate Spring Services for every [@RequestMapping](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html) 
add following to your pom.xml in the build/plugins section.

```XML
<plugin>
    <groupId>com.mysema.maven</groupId>
    <artifactId>apt-maven-plugin</artifactId>
    <version>1.1.3</version>
    <executions>
        <execution>
            <goals>
                <goal>process</goal>
            </goals>
            <configuration>
                <processor>de.neofonie.surlgen.processor.spring.UrlFactoryServiceGenerator</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This will generate for each Controller methods to generate (compiler-safe) methods for url-construction. 
These are based on [MvcUriComponentsBuilder](http://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/MvcUriComponentsBuilder.html).

### Create Static-Accessors/TLD for Url-Factory class 

Adding the UrlFunctionGenerator as processor will generate a UrlFunction-Class. Applying a additional tld-filename, a TLD-File will also be generated. 

```XML
<processors>
    <processor>de.neofonie.surlgen.processor.spring.UrlFactoryServiceGenerator</processor>
    <processor>de.neofonie.surlgen.processor.spring.UrlFunctionGenerator</processor>
</processors>
<options>
    <tld.file>${project.build.directory}/${project.build.finalName}/WEB-INF/tld/urls.tld</tld.file>
</options>
```