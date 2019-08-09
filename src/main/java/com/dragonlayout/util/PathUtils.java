package com.dragonlayout.util;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class PathUtils {

    public static String getSrcDirPath(MavenProject project) {
        return getProjectPath(project, "src", "main", "java");
    }

    public static String getTestDirPath(MavenProject project) {
        return getProjectPath(project, "src", "test", "java");
    }

    public static String getResourcesDirPath(MavenProject project) {
        return getProjectPath(project, "src", "main", "resources");
    }

    public static int getSolutionImplementationFileCount(Path packageFile) {
        try {
            return (int) Files.list(packageFile)
                    .filter(path -> {
                        String[] paths = path.toString().split(File.separator);
                        String filename = paths[paths.length - 1];
                        return Pattern.matches("Solution[0-9]+.java", filename);
                    }).count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String getProjectPath(MavenProject project, String... paths) {
        return Paths.get(project.getBasedir().getAbsolutePath(), paths).toString();
    }
}
