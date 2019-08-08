package com.dragonlayout.util;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.*;

import static org.apache.velocity.app.Velocity.getLog;

public class TemplateUtils {

    public static VelocityEngine getVe() {
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        ve.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        ve.init();
        return ve;
    }

    public static String getSolutionTestCases(File solutionTestFile) {
        StringBuilder testCasesStringBuilder = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(solutionTestFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            boolean beginOfTestCaseMethodBody = false;
            String line;
            while((line = bufferedReader.readLine()) != null) {
                if(!beginOfTestCaseMethodBody && line.contains("Arrays.asList(new Object[][]{")){
                    beginOfTestCaseMethodBody = true;
                    continue;
                }
                if (line.contains("});")) {
                    break;
                }
                if (beginOfTestCaseMethodBody) {
                    testCasesStringBuilder.append(line).append("\n");
                }
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            getLog().error("Get SolutionTest.java's current test cases failed, using default comment instead.", e);
        }
        return testCasesStringBuilder.toString().substring(0, testCasesStringBuilder.length() - 1);
    }
}
