# Scaffolding Generation Console

Console to execute the scaffolding generation based on [Codegen](https://github.com/rdabotelho/codegen) library.

## How to use

Configure repository and dependency in your maven pom file.

```
<repositories>
    <repository>
        <id>scaffolding-console</id>
        <url>https://raw.github.com/rdabotelho/mvn-repo/scaffolding-console/</url>
    </repository>
</repositories>
```

```
<dependency>
    <groupId>com.m2r.scaffolding</groupId>
    <artifactId>scaffolding-console</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Executing the generation console

To use just instance the ScaffoldingConsole class and call the execute method.

```
public static void main(String[] args) throws Exception {
    String baseDir = System.getProperty("user.dir");
    new ScaffoldingConsole(baseDir).execute();
}
```

## Running the first time

When the generation console run the first time, it will ask you if you want to create the scaffolding configuration folder. You answer yes to create an initial setup with multiples samples files.

```
File codegen.yml not found in the project. Do you want create it?
1: yes
2: no
Enter your choice yes
```

You will be asked for the name and the base package of the project.

```
What is the project name? example
What is the base package? com.m2r.scaffolding.example
```

Once answered, the initial scaffolding structure will be created in the `/scaffolding` folder.

```
File ~/myproject/scaffolding/codegen.yml created!
File ~/myproject/scaffolding/base/example/model/AbstractModel.java created!
File ~/myproject/scaffolding/base/example/enums/AbstractEnum.java created!
File ~/myproject/scaffolding/scripts/domains.gc created!
File ~/myproject/scaffolding/templates/template-class.vm created!
File ~/myproject/scaffolding/templates/template-enum.vm created!
bye!

Process finished with exit code 0
```

Scaffolding folder structure

```
-scaffolding
    -base
    -scripts
    -templates
```

- `base`: Folder that will be copied in the code generation. It is important to create an initial project structure.
- `scripts`: Folder to store the DSL [Codegen](https://github.com/rdabotelho/codegen) files to define the domains (class, enum and relationship).
- `templates`: Folder to store the [Apache Velocity](https://velocity.apache.org) template files for code generation.

# Samples files generated in the first time

In the first run, some sample files will be created inside the `base`, `scripts` and `templates` folders. Feel free to modify them.

- `scaffolding/base/examples/enums/AbstractEnum.java`: Sample of abstract java enum.
- `scaffolding/base/examples/model/AbstractModel.java`: Sample of abstract java class.
- `scaffolding/script/domains.gc`: Sample script with the definition of the domains (classes, enums and relationships).
- `scaffolding/templates/template-class.vm`: Velocity sample template to generate java class.
- `scaffolding/templates/template-enum.vm`: Velocity sample template to generate java enum.
- `scaffolding/codegen.yml`: Main gencode configuration file.

# File codegen.yml

This is the [Codegen](https://github.com/rdabotelho/codegen) configuration file. In this file that we will define the project name, package name and all templates that we will use in code generation.

```
projectName: example
basePackage: com.m2r.scaffolding.example
configDir: /${user.dir}/scaffolding
outputDir: /${user.dir}/src/main/java/com/m2r/scaffolding
templates:
- name: Model
  scope: class
  fileName: template-class.vm
  outputFileName: example/model/${domain.name}.java
- name: Enum
  scope: enum
  fileName: template-enum.vm
  outputFileName: example/enums/${domain.name}.java
```

- `projectName`: Project name.
- `basePackage`: Base package of the project.
- `configDir`: Folder of scaffolding configuration.
- `outputDir`: Folder where the files will be generated.
- `templates`: Templates configuration.

# Generation code

Once the initial structure has been created, whenever the `ScaffoldingConsole.execute()` method is called, the scaffolding code generator will go in action creating files based on templates (defined in the `scaffolding/templates` folder) and guieded by the script files (defined in the `scaffolding/scripts` folder).

In the samples of initial structure, there is a script file called `scaffolding/scripts/domains.gc`.

```
class User (label: 'User') {
    Long id;
    String name (label:'Name');
    String login (label:'Login');
    String password (label:'Password', type: 'password');
    RoleEnum role;
    List<Group> groups (label:'Groups', manyToMany: 'true');
}

class Group (label: 'Group') {
    Long id;
    String name (label: 'Name');
    String description (label: 'Description');
}

enum RoleEnum (fileName: 'Role.java') {
    ADMIN (id: '1', description: 'Administrator');
    USER (id: '2', description: 'User');
}
```

When the scaffolding code generator is executed, the following model will be created in the project.

![Example](example.png)

# Important consideration

For more details about the gencode DSL (domain specific language) used in the scripts files, access its [reference guide](https://github.com/rdabotelho/codegen).
