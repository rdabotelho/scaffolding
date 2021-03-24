package com.m2r.scaffolding.console;

public class Main {

    public static void main(String[] args) throws Exception {
        String baseDir = System.getProperty("user.dir") + "/scaffolding-console";
        new ScaffoldingConsole().execute(baseDir);
    }

}
