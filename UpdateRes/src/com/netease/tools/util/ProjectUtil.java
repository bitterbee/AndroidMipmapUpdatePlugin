package com.netease.tools.util;

import com.intellij.openapi.project.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 2019/2/25.
 */
public class ProjectUtil {
    private static final int MAX_DEEP = 2;

    public static List<String> findSubProjects(Project project) {
        String rootPath = project.getBasePath();
        File root = new File(rootPath);

        List<String> relativePaths = new ArrayList<String>();
        for (File sub : root.listFiles()) {
            if (sub.isDirectory()) {
                relativePaths.add(sub.getName());
            }
        }

        return findSubProjects(root, relativePaths, 0);
    }

    private static List<String> findSubProjects(File root, List<String> relativePaths, int deep) {
        List<String> result = new ArrayList<String>();
        if (relativePaths == null || relativePaths.isEmpty()) {
            return result;
        }
        for (String relativePath : relativePaths) {
            File file = new File(root, relativePath);
            if (file.isDirectory()) {
                StringBuilder sb = new StringBuilder(64);
                sb.append(File.separator).append("src")
                        .append(File.separator).append("main")
                        .append(File.separator).append("res");
                File res = new File(file, sb.toString());
                if (res.exists() && res.isDirectory()) {
                    result.add(relativePath);
                } else if (deep < MAX_DEEP) {
                    File[] subFiles = file.listFiles();
                    if (subFiles != null) {
                        List<String> subRelativePath = new ArrayList<String>();
                        for (File sub : subFiles) {
                            if (sub.isDirectory()) {
                                subRelativePath.add(relativePath + File.separator + sub.getName());
                            }
                        }
                        result.addAll(findSubProjects(root, subRelativePath, deep + 1));
                    }
                }
            }
        }
        return result;
    }
}
