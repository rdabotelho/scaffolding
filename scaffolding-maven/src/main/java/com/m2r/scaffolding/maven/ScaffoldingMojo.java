package com.m2r.scaffolding.maven;

import com.m2r.scaffolding.console.ScaffoldingConsole;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Mojo(name = "run")
public class ScaffoldingMojo extends AbstractMojo {

	@Parameter(property = "baseDir", required = true)
	private String baseDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (baseDir == null) {
			throw new MojoExecutionException("Parameter baseDir doesn't defined!");
		}
		ScaffoldingConsole scaffoldingConsole = new ScaffoldingConsole(baseDir);
		try {
			scaffoldingConsole.execute();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getBaseDir() {
		return baseDir;
	}

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}

}
