package com.m2r.scaffolding.console.archetype;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;

public class ArchetypeRepo {

    private static final String URL = "https://github.com/rdabotelho/scaffolding-archetype.git";
    private static final String SCAFFOLDING_DIR = "scaffolding";

    public static void cloneBranch(String branch, File destDir) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir") + UUID.randomUUID().toString());
        try {
            Git.cloneRepository()
                    .setURI(URL)
                    .setDirectory(tmpDir)
                    .setBranchesToClone(Arrays.asList(branch))
                    .setBranch(branch)
                    .call();
            File sourceDir = new File(tmpDir, SCAFFOLDING_DIR);
            FileUtils.copyDirectory(sourceDir, destDir);
            FileUtils.deleteDirectory(tmpDir);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getBranches() {
        List<String> branches = new ArrayList<>();
        try {
            Collection<Ref> refs = Git.lsRemoteRepository()
                    .setHeads(true)
                    .setRemote(URL)
                    .call();
            for (Ref ref : refs) {
                branches.add(ref.getName());
            }
            Collections.sort(branches);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return branches;
    }

    public static String extractBranchName(String branch) {
        return branch.substring(11);
    }

}
