package com.dragonlayout.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProblemMethodUtils {

    public static String getSolutionMethodReturnType(String methodStructureStr) {
        return methodStructureStr.split(" ")[1];
    }

    public static String getSolutionMethodName(String methodStructureStr) {
        return methodStructureStr.split(" ")[2].split("\\(")[0];
    }

    public static String getSolutionMethodParamList(String methodStructureStr) {
        return methodStructureStr.split("\\(")[1].split("\\)")[0];
    }

    public static Map<String, String> getSolutionMethodParamMap(String methodStructureStr) {
        String paramList = getSolutionMethodParamList(methodStructureStr);
        Map<String, String> paramMap = new LinkedHashMap<>();
        for (String param : paramList.split(",")) {
            paramMap.put(param.trim().split(" ")[0], param.trim().split(" ")[1]);
        }
        return paramMap;
    }

    public static String getSolutionMethodParamNameList(String methodStructureStr) {
        String paramList = getSolutionMethodParamList(methodStructureStr);
        StringBuilder paramNameList = new StringBuilder();
        for (String param : paramList.split(",")) {
            paramNameList.append(param.trim().split(" ")[1]).append(", ");
        }
        return paramNameList.toString().substring(0, paramNameList.length() - 2);
    }

    public static Map<String, String> getSolutionFieldMap(int solutionFileCount) {
        Map<String, String> solutionMap = new LinkedHashMap<>();
        for (int i = 0; i < solutionFileCount; i++) {
            if (i == 0) {
                solutionMap.put("Solution", "solution");
            } else {
                solutionMap.put("Solution" + i, "solution" + i);
            }
        }
        return solutionMap;
    }
}
