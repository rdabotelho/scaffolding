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
<profiles>
    <profile>
        <id>scaffolding</id>
        <activation>
            <activeByDefault>false</activeByDefault>
        </activation>
        <build>
            <plugins>
                <plugin>
                    <groupId>com.m2r.scaffolding</groupId>
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
</profiles>
```

## Creating a new project with scaffolding archetype

Create a new directory

```
mkdir myproject
cd myproject
```

Create a pom.xml file

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example.myproject</groupId>
    <artifactId>myproject</artifactId>
    <version>1.0.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.target>1.8</maven.compiler.target>
        <maven.compiler.source>1.8</maven.compiler.source>
    </properties>

    <pluginRepositories>
        <pluginRepository>
            <id>scaffolding-maven</id>
            <url>https://raw.github.com/rdabotelho/mvn-repo/scaffolding-maven/</url>
        </pluginRepository>
    </pluginRepositories>

    <profiles>
        <profile>
            <id>scaffolding</id>
            <activation>
                <activeByDefault>false</activeByDefault>
            </activation>
            <build>
                <plugins>
                    <plugin>
                        <groupId>com.m2r.scaffolding</groupId>
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
    </profiles>

</project>
```

Execute the following maven command to call the scaffolding console:

```
mvn compile -Pscaffolding
```

Okay, now just follow the [scaffolding-console](../scaffolding-console/README.md) instructions.
