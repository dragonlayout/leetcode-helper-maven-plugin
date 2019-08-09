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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mojo(name = "generateSolution", defaultPhase = LifecyclePhase.NONE)
public class GenerateSolutionMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String sourceDirPath = PathUtils.getSrcDirPath(project);
        String testDirPath = PathUtils.getTestDirPath(project);
        String resourceDirPath = PathUtils.getResourcesDirPath(project);
        getLog().info("sourceDirPath: " + sourceDirPath);
        getLog().info("testDirPath: " + testDirPath);
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
        String problemDescription = problemPropertyMap.get(ProblemConstants.PROBLEM_DESCRIPTION);
        String problemMethodStructure = problemPropertyMap.get(ProblemConstants.PROBLEM_METHOD_STRUCTURE);

        getLog().info("");
        getLog().info("------------------------------------------------------------------------");
        getLog().info("create src directory");
        getLog().info("------------------------------------------------------------------------");
        // 1. 建立 src 目录
        File packageSrcPath = new File(Paths.get(sourceDirPath,
                packageName.replace(".", File.separator),
                problemCategory, "_" + problemNo + "_" + problemName).toString());
        boolean srcPackagePathMkdirResult = packageSrcPath.mkdirs();
        if (srcPackagePathMkdirResult) {
            getLog().info("src directory not exist, create directory successfully.");
        } else {
            getLog().info("src directory already exist.");
        }
        VelocityEngine ve = TemplateUtils.getVe();
        Template template = ve.getTemplate("template/SolutionInterface.vm", "UTF-8");
        VelocityContext velocityContext = new VelocityContext();
        StringWriter stringWriter = new StringWriter();
        FileWriter fileWriter;
        // create Solution.java interface file
        File solutionInterfaceFile = new File(packageSrcPath.getAbsolutePath() + File.separator + "Solution.java");
        if (!solutionInterfaceFile.exists()) {
            velocityContext.put("packageName", packageName);
            velocityContext.put("problemCategory", problemCategory);
            velocityContext.put("problemNo", problemNo);
            velocityContext.put("problemName", problemName);
            velocityContext.put("interfaceName", "Solution");
            velocityContext.put("problemMethodStructure", problemMethodStructure.replaceAll("\\{", "").replaceAll("}", "").trim());
            template.merge(velocityContext, stringWriter);
            try {
                fileWriter = new FileWriter(solutionInterfaceFile);
                fileWriter.write(stringWriter.toString());
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                getLog().error("Generate Solution.java interface file failed.", e);
                return;
            }
        } else {
            getLog().info("Solution.java interface file already exist.");
        }

        // 2. 建立 Solution 文件
        int solutionImplementationFileCount = PathUtils.getSolutionImplementationFileCount(packageSrcPath.toPath());
        template = ve.getTemplate("template/Solution.vm", "UTF-8");
        velocityContext.put("packageName", packageName);
        velocityContext.put("problemCategory", problemCategory);
        velocityContext.put("problemNo", problemNo);
        velocityContext.put("problemName", problemName);
        velocityContext.put("generateDate", new SimpleDateFormat("yyyy/dd/MM HH:mm:ss").format(new Date()));
        velocityContext.put("problemDescription", problemDescription);
        velocityContext.put("className", "Solution" + (solutionImplementationFileCount + 1));
        velocityContext.put("problemMethodStructure", problemMethodStructure);

        stringWriter = new StringWriter();
        template.merge(velocityContext, stringWriter);
        String solutionFileName = "Solution" + (solutionImplementationFileCount + 1) + ".java";
        try {
            fileWriter = new FileWriter(new File(packageSrcPath.getAbsolutePath() + File.separator + solutionFileName));
            fileWriter.write(stringWriter.toString());
            fileWriter.flush();
            fileWriter.close();
            stringWriter.close();
        } catch (IOException e) {
            getLog().error("Generate " + solutionFileName + " failed.", e);
            return;
        }
        getLog().info("Generate " +solutionFileName + " successfully.");

        getLog().info("");
        getLog().info("------------------------------------------------------------------------");
        getLog().info("Create test directory");
        getLog().info("------------------------------------------------------------------------");
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

        template = ve.getTemplate("template/SolutionTest.vm", "UTF-8");
        velocityContext = new VelocityContext();
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

        stringWriter = new StringWriter();
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
