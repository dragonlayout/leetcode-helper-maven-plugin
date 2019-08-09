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
import java.util.List;
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
            getLog().error("src directory not exist.");
            return;
        }
        int solutionImplementationFileCount = PathUtils.getSolutionImplementationFileCount(packageSrcPath.toPath());
        if (solutionImplementationFileCount == 0) {
            getLog().error("No Solution file to delete.");
            return;
        }
        String solutionFileName = "Solution" + solutionImplementationFileCount + ".java";
        File solutionToDelete = new File(packageSrcPath, solutionFileName);
        boolean solutionDeleteResult = solutionToDelete.delete();
        if (solutionDeleteResult) {
            getLog().info("Delete Solution file successfully.");
        } else {
            getLog().info("Delete Solution file result failed.");
        }

        // 1. create test directory
        File packageTestPath = new File(Paths.get(testDirPath,
                packageName.replace(".", File.separator),
                problemCategory,
                "_" + problemNo + "_" + problemName
        ).toAbsolutePath().toString());
        boolean packageTestPathMkdirResult = packageTestPath.mkdirs();
        if (packageTestPathMkdirResult) {
            getLog().info("test directory not exist, create directory successfully.");
        } else {
            getLog().info("test directory already exist.");
        }
        // 2. generate test file
        solutionImplementationFileCount = PathUtils.getSolutionImplementationFileCount(packageSrcPath.toPath());

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
        List<String> solutionDefinitionList = ProblemMethodUtils.getSolutionDefinitionList(solutionImplementationFileCount);
        velocityContext.put("methodReturnType", returnType);
        velocityContext.put("methodName", methodName);
        velocityContext.put("paramKvStr", paramStr);
        velocityContext.put("paramKvMap", paramMap);
        velocityContext.put("paramNameStr", paramNameList);
        velocityContext.put("solutionDefinitionList", solutionDefinitionList);

        StringWriter stringWriter = new StringWriter();
        FileWriter fileWriter;
        try {
            File solutionTestFile = new File(packageTestPath.getAbsolutePath() + File.separator + "SolutionTest.java");
            String testCasesStr = "// todo add your test cases here";
            boolean noTestCases = true;
            if (solutionTestFile.exists()) {
                noTestCases = false;
                testCasesStr = TemplateUtils.getSolutionTestCases(solutionTestFile);
            }
            velocityContext.put("testCasesStr", testCasesStr);
            velocityContext.put("noTestCases", noTestCases);
            template.merge(velocityContext, stringWriter);
            fileWriter = new FileWriter(solutionTestFile, false);
            fileWriter.write(stringWriter.toString());
            fileWriter.flush();
            fileWriter.close();
            stringWriter.close();
        } catch (IOException e) {
            getLog().error("Generate SolutionTest.java failed.", e);
        }
        getLog().info("Generate SolutionTest.java successfully.");
    }
}
