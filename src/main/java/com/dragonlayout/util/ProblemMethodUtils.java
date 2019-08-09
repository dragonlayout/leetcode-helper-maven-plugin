package com.dragonlayout.util;

import java.util.*;

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

    public static List<String> getSolutionDefinitionList(int solutionFileCount) {
        List<String> solutionDefinitionList = new ArrayList<>();
        for (int i = 1; i <= solutionFileCount; i++) {
            solutionDefinitionList.add("// solution = new Solution" + i + "();");
        }
        if (solutionDefinitionList.size() != 0) {
            solutionDefinitionList.remove(solutionDefinitionList.size() - 1);
            solutionDefinitionList.add("solution = new Solution" + solutionFileCount + "();");
        }
        return solutionDefinitionList;
    }
}
