# Scaffolding Maven Plugin

Maven plugin to use the [scaffolding-console](../scaffolding-console/README.md) within a maven project.

## How to use

Configure repository and plugin in your maven pom file.

```
<pluginRepositories>
    <pluginRepository>
        <id>scaffolding-maven</id>
        <url>https://raw.github.com/rdabotelho/mvn-repo/scaffolding-maven/</url>
    </pluginRepository>
</pluginRepositories>
```

```
<profile>
    <id>scaffolding</id>
    <activation>
        <activeByDefault>false</activeByDefault>
    </activation>
    <build>
        <plugins>
            <plugin>
                <groupId>com.m2r</groupId>
                <artifactId>scaffolding-maven</artifactId>
                <version>1.0.0</version>
                <executions>
                    <execution>
                        <phase>compile</phase>
                        <configuration>
                            <baseDir>${project.basedir}</baseDir>
                        </configuration>
                        <goals>
                            <goal>run</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</profile>
```

Use the maven command to call the console:

```
mvn compile -Pscaffolding
```

Then just follow the [scaffolding-console](../scaffolding-console/README.md) instructions.