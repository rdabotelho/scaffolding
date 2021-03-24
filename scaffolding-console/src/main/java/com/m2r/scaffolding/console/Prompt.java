package com.m2r.scaffolding.console;

import java.util.ArrayList;
import java.util.List;

public class Prompt {

    private String title;
    private String defaultValue;
    private boolean required = false;
    private List<String> options = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Prompt format(String ... params) {
        return Prompt.builder()
                .title(String.format(this.title, params))
                .defaultValue(this.defaultValue)
                .required(this.required)
                .options(this.options)
                .build();
    }

    public static PromptBuilder builder() {
        return new PromptBuilder();
    }

    public static class PromptBuilder {
        private Prompt prompt = new Prompt();
        public PromptBuilder title(String value) {
            this.prompt.title = value;
            return this;
        }
        public PromptBuilder defaultValue(String value) {
            this.prompt.defaultValue = value;
            return this;
        }
        public PromptBuilder required(boolean value) {
            this.prompt.required = value;
            return this;
        }
        public PromptBuilder option(String value) {
            this.prompt.options.add(value);
            return this;
        }
        public PromptBuilder options(List<String> value) {
            this.prompt.options = value;
            return this;
        }
        public Prompt build() {
            return this.prompt;
        }
    }

}
