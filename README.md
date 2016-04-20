# Spring Url Generator (surlgen)

## Usage

### Create Service-Classes for Spring-RequestMapping Annotation 

To generate Spring Services for every [@RequestMapping](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestMapping.html) 
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
                <outputDirectory>${project.build.directory}/generated-sources/surlgen</outputDirectory>
                <processor>de.neofonie.surlgen.processor.spring.UrlFactoryServiceGenerator</processor>
            </configuration>
        </execution>
    </executions>
</plugin>
```

This will generate for each Controller methods to generate (compiler-safe) methods for url-construction. 
These are based on MvcUriComponentsBuilder.