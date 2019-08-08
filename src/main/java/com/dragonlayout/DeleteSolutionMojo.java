package com.dragonlayout;

import com.dragonlayout.common.ProblemConstants;
import com.dragonlayout.util.PathUtils;
import com.dragonlayout.util.ProblemMethodUtils;
import com.dragonlayout.util.PropertiesUtils;
import com.dragonlayout.util.TemplateUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.Map;

@Mojo(name = "deleteSolution", defaultPhase = LifecyclePhase.NONE)
public class DeleteSolutionMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String sourceDirPath = PathUtils.getSrcDirPath(project);
        String testDirPath = PathUtils.getTestDirPath(project);
        String resourceDirPath = PathUtils.getResourcesDirPath(project);
        getLog().info("sourceDirPath: " + sourceDirPath);
        getLog().info("resourceDirPath: " + resourceDirPath);
        PropertiesUtils propertiesUtils;
        try {
            propertiesUtils = new PropertiesUtils(resourceDirPath);
        } catch (IOException e) {
            getLog().error("Not found resources/problem.properties", e);
            return;
        }
        Map<String, String> problemPropertyMap = propertiesUtils.getProblemPropertyMap();
        String packageName = problemPropertyMap.get(ProblemConstants.PACKAGE_NAME);
        String problemNo = problemPropertyMap.get(ProblemConstants.PROBLEM_NO);
        String problemName = problemPropertyMap.get(ProblemConstants.PROBLEM_NAME);
        String problemCategory = problemPropertyMap.get(ProblemConstants.PROBLEM_CATEGORY);
        String problemMethodStructure = problemPropertyMap.get(ProblemConstants.PROBLEM_METHOD_STRUCTURE);

        File packageSrcPath = new File(Paths.get(sourceDirPath,
                packageName.replace(".", File.separator),
                problemCategory, "_" + problemNo + "_" + problemName).toAbsolutePath().toString());
        if (!packageSrcPath.exists()) {
            getLog().error("src directory not exist");
            return;
        }
        int solutionFileCount = PathUtils.getSolutionFileIndex(packageSrcPath.toPath());
        if (solutionFileCount == 0) {
            getLog().error("no Solution file to delete");
            return;
        }
        String solutionFileName = solutionFileCount == 1 ? "Solution.java" : "Solution" + (solutionFileCount - 1) + ".java";
        File solutionToDelete = new File(packageSrcPath, solutionFileName);
        boolean solutionDeleteResult = solutionToDelete.delete();
        if (solutionDeleteResult) {
            getLog().info("delete Solution file successfully");
        } else {
            getLog().info("delete Solution file result failed");
        }

        // 1. create test directory
        File packageTestPath = new File(Paths.get(testDirPath,
                packageName.replace(".", File.separator),
                problemCategory,
                "_" + problemNo + "_" + problemName
        ).toAbsolutePath().toString());
        boolean packageTestPathMkdirResult = packageTestPath.mkdirs();
        if (packageTestPathMkdirResult) {
            getLog().info("test directory not exist, create directory success");
        } else {
            getLog().info("test directory already exist");
        }
        // 2. generate test file
        solutionFileCount = PathUtils.getSolutionFileIndex(packageSrcPath.toPath());

        VelocityEngine ve = TemplateUtils.getVe();
        Template template = ve.getTemplate("template/SolutionTest.vm", "UTF-8");
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("packageName", packageName);
        velocityContext.put("problemCategory", problemCategory);
        velocityContext.put("problemNo", problemNo);
        velocityContext.put("problemName", problemName);

        // method return type
        String returnType = ProblemMethodUtils.getSolutionMethodReturnType(problemMethodStructure);
        // method name
        String methodName = ProblemMethodUtils.getSolutionMethodName(problemMethodStructure);
        // param list
        String paramStr = ProblemMethodUtils.getSolutionMethodParamList(problemMethodStructure);
        // map param type, param name k-v map
        Map<String, String> paramMap = ProblemMethodUtils.getSolutionMethodParamMap(problemMethodStructure);
        // param name list
        String paramNameList = ProblemMethodUtils.getSolutionMethodParamNameList(problemMethodStructure);
        // solution field
        Map<String, String> solutionMap = ProblemMethodUtils.getSolutionFieldMap(solutionFileCount);
        velocityContext.put("methodReturnType", returnType);
        velocityContext.put("methodName", methodName);
        velocityContext.put("paramKvStr", paramStr);
        velocityContext.put("paramKvMap", paramMap);
        velocityContext.put("paramNameStr", paramNameList);
        velocityContext.put("solutionMap", solutionMap);

        StringWriter stringWriter = new StringWriter();
        FileWriter fileWriter;
        template.merge(velocityContext, stringWriter);
        try {
            fileWriter = new FileWriter(new File(packageTestPath.getAbsolutePath() + File.separator + "SolutionTest.java"), false);
            fileWriter.write(stringWriter.toString());
            fileWriter.flush();
            fileWriter.close();
            stringWriter.close();
        } catch (IOException e) {
            getLog().error("generate SolutionTest.java failed", e);
        }
        getLog().info("generate SolutionTest.java successfully");
    }
}
