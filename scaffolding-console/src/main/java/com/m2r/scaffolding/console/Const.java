package com.m2r.scaffolding.console;

public class Const {

    public static Prompt SETUP = Prompt.builder()
            .title("Scaffolding configuration folder not found in the project. Do you want create it?")
            .option("yes")
            .option("no")
            .defaultValue("no")
            .build();

    public static Prompt ARCHETYPE = Prompt.builder()
            .title("Which artifact do you want to use?")
            .required(true)
            .build();

    public static Prompt PROJECT_NAME = Prompt.builder()
            .title("What is the project name?")
            .required(true)
            .build();

    public static Prompt BASE_PACKAGE = Prompt.builder()
            .title("What is the base package?")
            .required(true)
            .build();

    public static Prompt WHAT_SCRIPT = Prompt.builder()
            .title("What scripts do you want to execute [all]?")
            .defaultValue("")
            .build();

    public static Prompt WHAT_TEMPLATE = Prompt.builder()
            .title("What templates do you want to generate [all]?")
            .defaultValue("")
            .build();

    public static Prompt INITIAL_STRUCTURE = Prompt.builder()
            .title("Do you want to generate the base structure [no]?")
            .option("yes")
            .option("no")
            .defaultValue("no")
            .build();

    public static Prompt OVERRIDE_FILE = Prompt.builder()
            .title("Override %s [no]?")
            .option("yes")
            .option("no")
            .defaultValue("no")
            .build();




}

