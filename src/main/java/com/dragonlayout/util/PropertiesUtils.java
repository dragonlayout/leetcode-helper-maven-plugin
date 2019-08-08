package com.dragonlayout.util;

import com.dragonlayout.common.ProblemConstants;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtils {

    private static final String PROBLEM_PROPERTIES = "problem.properties";

    private static Properties properties = new Properties();

    public PropertiesUtils(String classpath) throws IOException {
        properties.load((new FileInputStream(classpath + File.separator + PROBLEM_PROPERTIES)));
    }

    public Map<String, String> getProblemPropertyMap() {
        Map<String, String> problemPropertyMap = new HashMap<>();
        String problemDescription = getProperty("problem.description");
        StringBuilder formattedDescription = new StringBuilder();
        for (String line : problemDescription.split("\n")) {
            if (!"".equals(line)) {
                formattedDescription.append("// ").append(line);
            }
            formattedDescription.append("\n");
        }
        problemDescription = formattedDescription.toString();

        problemPropertyMap.put(ProblemConstants.PACKAGE_NAME, getProperty("package.name"));
        problemPropertyMap.put(ProblemConstants.PROBLEM_NO, getProperty("problem.no"));
        problemPropertyMap.put(ProblemConstants.PROBLEM_CATEGORY, getProperty("problem.category"));
        problemPropertyMap.put(ProblemConstants.PROBLEM_NAME, getProperty("problem.name").replaceAll(" ", "_").toLowerCase());
        problemPropertyMap.put(ProblemConstants.PROBLEM_DESCRIPTION, problemDescription);
        problemPropertyMap.put(ProblemConstants.PROBLEM_METHOD_STRUCTURE, getProperty("problem.method.structure"));
        return problemPropertyMap;
    }

    private String getProperty(String key) {
        return properties == null ? "" : properties.getProperty(key);
    }
}
