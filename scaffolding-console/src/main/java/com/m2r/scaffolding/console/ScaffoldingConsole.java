package com.m2r.scaffolding.console;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.m2r.codegen.Codegen;
import com.m2r.codegen.parser.Domain;
import com.m2r.codegen.parser.StringWrapper;
import com.m2r.codegen.parser.Template;
import com.m2r.scaffolding.console.archetype.ArchetypeRepo;
import com.m2r.scaffolding.console.dto.ScaffoldingProject;
import com.m2r.scaffolding.console.dto.ScaffoldingTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ScaffoldingConsole {

    private static final String SCRIPT_EXT = ".gc";
    private static final String CODEGEN_YML = "codegen.yml";
    private static final String CODEGEN_DIR = "scaffolding";
    public static final String FILE_CREATED = "File %s created!";
    public static final String ARCHETYPE_CREATED = "The %s archetype was created with success!";
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

            // create initial gencode structure
            List<String> branches = ArchetypeRepo.getBranches();
            if (branches.isEmpty()) {
                throw new RuntimeException("It was not possible to fetch the architypes list, check your connection.");
            }
            Prompt arcPrompt = Const.ARCHETYPE;
            arcPrompt.getOptions().clear();
            branches.removeIf(it -> it.equals("refs/heads/master"));
            for (String archetype : branches) {
                arcPrompt.getOptions().add(ArchetypeRepo.extractBranchName(archetype));
            }
            int arcResult = this.console.read(arcPrompt).map(it -> Integer.parseInt(it)).get() - 1;
            String branch = branches.get(arcResult);
            File scaffoldingDir = new File(baseDir, CODEGEN_DIR);
            ArchetypeRepo.cloneBranch(branch, scaffoldingDir);

            // change de project name, basePackage, configDir and outputDir
            String projectName = this.console.read(Const.PROJECT_NAME).get();
            String basePackage = this.console.read(Const.BASE_PACKAGE).get();

            File codegenFile = new File(scaffoldingDir, CODEGEN_YML);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            project = mapper.readValue(codegenFile, ScaffoldingProject.class);
            project.setProjectName(projectName);
            project.setBasePackage(basePackage);
            mapper.writeValue(codegenFile, project);

            this.console.writeln(String.format(ARCHETYPE_CREATED, ArchetypeRepo.extractBranchName(branch)));
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
        project.setConfigDir(project.getConfigDir().replace("${user.dir}", System.getProperty("user.dir")));

        if (project.getOutputDir() == null) {
            throw new RuntimeException("Parameter outputDir doesn't defined!");
        }
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
