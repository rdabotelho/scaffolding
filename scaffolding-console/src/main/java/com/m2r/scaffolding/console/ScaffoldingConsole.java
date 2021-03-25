package com.m2r.scaffolding.console;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.m2r.codegen.Codegen;
import com.m2r.codegen.parser.Domain;
import com.m2r.codegen.parser.StringWrapper;
import com.m2r.codegen.parser.Template;
import com.m2r.scaffolding.console.dto.ScaffoldingProject;
import com.m2r.scaffolding.console.dto.ScaffoldingTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ScaffoldingConsole {

    private static final String SCRIPT_EXT = ".gc";
    private static final String CODEGEN_YML = "codegen.yml";
    private static final String SCRIPT_GC = "domains.gc";
    private static final String BASE_MODEL = "AbstractModel.java";
    private static final String BASE_ENUM = "AbstractEnum.java";
    private static final String TEMPLATE_CLASS_VM = "template-class.vm";
    private static final String TEMPLATE_ENUM_VM = "template-enum.vm";
    private static final String CODEGEN_DIR = "scaffolding";
    private static final String SCRIPTS_DIR = "scripts";
    private static final String BASE_MODEL_DIR = "base/model";
    private static final String BASE_ENUMS_DIR = "base/enums";
    private static final String TEMPLATES_DIR = "templates";
    public static final String FILE_CREATED = "File %s created!";
    public static final String CODEGEN_CONFIG_DIR = "/${user.dir}/" + CODEGEN_DIR;
    public static final String GENCODE_OUTPUT_DIR = "/${user.dir}/src/main/java/";
    private static String BASE_FOLDER = "base";
    private static String SCRIPTS_FOLDER = "scripts";
    private static String TEMPLATES_FOLDER = "templates";

    private String baseDir;
    private ScaffoldingProject project;
    private boolean genInitialStructure;
    private Console console;

    public ScaffoldingConsole(String baseDir) {
        this(baseDir, null);
    }

    public ScaffoldingConsole(String baseDir, ConsoleAppender consoleAppender) {
        this.console = new Console(consoleAppender != null ? consoleAppender : new Console.ConsoleAppenderDefault());
        this.baseDir = baseDir;
    }

    public void execute() throws Exception {
        if (isReady()) {
            run();
        }
        this.console.write("bye!\n");
    }

    private boolean isReady() throws Exception {
        File dir = new File(baseDir, CODEGEN_DIR);
        File file = new File(dir, CODEGEN_YML);
        if (!file.exists()) {
            if (this.console.read(Const.SETUP).map(it -> it.equals("2")).get()) {
                return false;
            }

            // create gencode.yml
            if (!dir.exists()) dir.mkdirs();
            FileWriter writer = new FileWriter(file);
            writer.write(String.format("projectName: %s\n", this.console.read(Const.PROJECT_NAME).get()));
            String packg = this.console.read(Const.BASE_PACKAGE).get();
            writer.write(String.format("basePackage: %s\n", packg));
            writer.write(String.format("configDir: %s\n", CODEGEN_CONFIG_DIR));
            writer.write(String.format("outputDir: %s\n", GENCODE_OUTPUT_DIR + packg.replaceAll("\\.", "/")));
            writer.write("templates:\n");
            writer.write("- name: Model\n");
            writer.write("  scope: class\n");
            writer.write("  fileName: template-class.vm\n");
            writer.write("  outputFileName: model/${domain.name}.java\n");
            writer.write("- name: Enum\n");
            writer.write("  scope: enum\n");
            writer.write("  fileName: template-enum.vm\n");
            writer.write("  outputFileName: enums/${domain.name}.java\n");
            writer.close();
            this.console.writeln(String.format(FILE_CREATED, file.toString()));

            // create base model
            File dirBaseModel = new File(dir, BASE_MODEL_DIR);
            if (!dirBaseModel.exists()) dirBaseModel.mkdirs();

            InputStream baseModelIn = this.getClass().getResourceAsStream("/" + BASE_MODEL);
            File baseModelFile = new File(dirBaseModel, BASE_MODEL);
            Files.copy(baseModelIn, baseModelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.console.writeln(String.format(FILE_CREATED, baseModelFile.toString()));

            // create base enum
            File dirBaseEnum = new File(dir, BASE_ENUMS_DIR);
            if (!dirBaseEnum.exists()) dirBaseEnum.mkdirs();
            InputStream baseEnumIn = this.getClass().getResourceAsStream("/" + BASE_ENUM);
            File baseEnumFile = new File(dirBaseEnum, BASE_ENUM);
            Files.copy(baseEnumIn, baseEnumFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.console.writeln(String.format(FILE_CREATED, baseEnumFile.toString()));
            
            // create domains.gc
            File dirDomain = new File(dir, SCRIPTS_DIR);
            if (!dirDomain.exists()) dirDomain.mkdirs();
            InputStream scriptIn = this.getClass().getResourceAsStream("/" + SCRIPT_GC);
            File scriptFile = new File(dirDomain, SCRIPT_GC);
            Files.copy(scriptIn, scriptFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.console.writeln(String.format(FILE_CREATED, scriptFile.toString()));

            // create tamplates
            File dirTemplates = new File(dir, TEMPLATES_DIR);
            if (!dirTemplates.exists()) dirTemplates.mkdirs();
            InputStream templateClassIn = this.getClass().getResourceAsStream("/" + TEMPLATE_CLASS_VM);
            InputStream templateEnumIn = this.getClass().getResourceAsStream("/" + TEMPLATE_ENUM_VM);
            File templateClassFile = new File(dirTemplates, TEMPLATE_CLASS_VM);
            File templateEnumFile = new File(dirTemplates, TEMPLATE_ENUM_VM);
            Files.copy(templateClassIn, templateClassFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.copy(templateEnumIn, templateEnumFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            this.console.writeln(String.format(FILE_CREATED, templateClassFile.toString()));
            this.console.writeln(String.format(FILE_CREATED, templateEnumFile.toString()));

            return false;
        }
        else {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            project = mapper.readValue(file, ScaffoldingProject.class);

            return true;
        }
    }

    private void run() throws RuntimeException {
        if (project.getProjectName() == null) {
            throw new RuntimeException("Parameter projectName doesn't defined!");
        }

        if (project.getConfigDir() == null) {
            throw new RuntimeException("Parameter configDir doesn't defined!");
        }
        project.setConfigDir(project.getConfigDir().replace("/${user.dir}", System.getProperty("user.dir")));
        project.setConfigDir(project.getConfigDir().replace("${user.dir}", System.getProperty("user.dir")));

        if (project.getOutputDir() == null) {
            throw new RuntimeException("Parameter outputDir doesn't defined!");
        }
        project.setOutputDir(project.getOutputDir().replace("/${user.dir}", System.getProperty("user.dir")));
        project.setOutputDir(project.getOutputDir().replace("${user.dir}", System.getProperty("user.dir")));

        if (new File(project.getConfigDir()).exists() == false) {
            throw new RuntimeException(String.format("Config directory %s not found!", project.getConfigDir()));
        }

        String scp = this.console.read(Const.WHAT_SCRIPT).get();
        String tmp = this.console.read(Const.WHAT_TEMPLATE).get();
        genInitialStructure = this.console.read(Const.INITIAL_STRUCTURE).map(it -> it.equals("1")).get();

        File[] filteredFiles = new File(project.getConfigDir(), SCRIPTS_FOLDER).listFiles((dir2, name) -> name.endsWith(SCRIPT_EXT));
        if (filteredFiles == null) filteredFiles = new File[]{};
        if (!scp.equals("")) {
            List<File> list = new ArrayList<>();
            String[] names = scp.split(" ");
            for (String name : names) {
                for (File file : filteredFiles) {
                    if (file.toPath().getFileName().toString().equals(name)) {
                        list.add(file);
                        break;
                    }
                }
            }
            filteredFiles = list.toArray(new File[list.size()]);
        }

        List<ScaffoldingTemplate> filteredTemplates = new ArrayList();
        if (!tmp.equals("")) {
            String[] names = tmp.split(" ");
            for (String name : names) {
                for (ScaffoldingTemplate temp : project.getTemplates()) {
                    if (temp.getName().equals(name) || temp.getGroup().equalsIgnoreCase(name)) {
                        if (!filteredTemplates.stream().filter(it -> it.getName().equals(temp.getName())).findFirst().isPresent()) {
                            filteredTemplates.add(temp);
                        }
                    }
                }
            }
        }
        else {
            filteredTemplates = project.getTemplates();
        }

        processScript(filteredFiles, filteredTemplates);

        this.console.writeln("Process conclude with success!");

    }

    private void processScript(File[] filteredFiles, List<ScaffoldingTemplate> filteredTemplates) {
        List<Template> templatesParse = new ArrayList();
        if (genInitialStructure) {
            walk(templatesParse, new File(project.getConfigDir(), BASE_FOLDER));
        }

        for (ScaffoldingTemplate scftemplate : filteredTemplates) {
            Template template = new Template();
            template.setName(scftemplate.getName());
            template.setScope(scftemplate.getScope());
            template.setDirectory(new File(project.getConfigDir(), TEMPLATES_FOLDER).getAbsolutePath());
            template.setFileName(scftemplate.getFileName());
            template.setOutputDir(project.getOutputDir());
            template.setOutputFileName(scftemplate.getOutputFileName());
            template.setCreateIf(scftemplate.getCreateIf());
            template.setStartEvent((templ, domain, file) -> {
                if (file.exists()) {
                    return this.console.read(Const.OVERRIDE_FILE.format(file.toPath().getFileName().toString()))
                            .map(it -> it.equals("1"))
                            .get();
                }
                return true;
            });

            template.setEndEvent((templ, domain, file) -> {
                this.console.writeln(file.toPath().getFileName()+" generated with success!");
                return true;
            });

            templatesParse.add(template);
        }

        if (templatesParse.size() > 0 && filteredTemplates.size() > 0) {
            Codegen codegen = new Codegen();
            if (project.getCreateOneToManyClass()) {
                codegen.setStartEvent(context -> {
                    List<Domain> manies = new ArrayList<>();
                    context.getDomains().forEach(domain -> {
                        domain.getAttributes().forEach(attr -> {
                            if (attr.isList() && !attr.isComposition()) {
                                Domain dom = new Domain(context);
                                dom.setType(StringWrapper.of("oneToMany"));
                                dom.setName(StringWrapper.of(domain.getName().toString() + attr.getItemType().toString()));
                                dom.addAttribute(domain.getName().toString(), domain.getName().toCamelCase());
                                dom.addAttribute(attr.getItemType().toString(), attr.getItemType().toCamelCase());
                                manies.add(dom);
                            }
                        });
                    });
                    manies.forEach(it -> context.getDomains().add(it));
                });
            }
            codegen.generate(project.getProjectName(), project.getBasePackage(), templatesParse, filteredFiles);
        }
    }

    private void walk(List<Template> templatesParse, File root) {
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk(templatesParse, f);
            }
            else if (!f.isHidden()) {
                Template tm = new Template();
                tm.setName(f.getName());
                tm.setScope("global");
                tm.setDirectory(f.getParent());
                tm.setFileName(f.getName());
                File outputFile = new File(project.getOutputDir(), f.getAbsolutePath().substring(project.getConfigDir().length() + BASE_FOLDER.length() + 1));
                tm.setOutputDir(outputFile.getParent());
                tm.setOutputFileName(f.getName());
                templatesParse.add(tm);
            }
        }
    }

}
