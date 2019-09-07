package com.netease.tools.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.netease.tools.NEConsole;
import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zyl06 on 7/25/16.
 */
public class CommandUtil {
    public static final String QUOT = "\"";
    public static final String RETURN = "\n ";
    public static final String DOT = ".";

    public static void runCommand(Project project, String cmd) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd, null, new File(project.getBasePath()));
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage() + "   \n  " + e.getCause();

            for (StackTraceElement stackTrace : e.getStackTrace()) {
                error += stackTrace.toString() + "\n";
            }

            Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
        }

        NEConsole.show(project, process, cmd);
    }

    public static void runSytemCommand(Project project, String cmd, boolean isWait) {
        String[] cmds = CommandUtil.getSystemCmds(cmd);

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmds, null, new File(project.getBasePath()));
            if (isWait) {
                process.waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = e.getMessage() + "   \n  " + e.getCause();

            for (StackTraceElement stackTrace : e.getStackTrace()) {
                error += stackTrace.toString() + "\n";
            }

            Messages.showMessageDialog(error, "Error", Messages.getErrorIcon());
        }

        NEConsole.show(project, process, cmds.toString());
    }

    public static String[] getSystemCmds(String cmd) {
        return isWindows() ? getSystemCmds_Windows(cmd) : getSystemCmds_Mac(cmd);
    }

    private static String[] getSystemCmds_Windows(String cmd) {
        return new String[] {"cmd.exe", "/c", cmd};
    }

    private static String[] getSystemCmds_Mac(String cmd) {
        return new String[] { "/bin/sh", "-c", cmd };
    }

    public static String getShCmdPath(String path) {
        return isWindows() ? getShCmdPath_Windows(path) : getShPath_Mac(path);
    }

    public static String getShPath(String path) {
        return isWindows() ? getShPath_Windows(path) : getShPath_Mac(path);
    }

    public static String getPythonCommandPath(Project project) {
        String python = getInstallPath(project, "python");
        if (TextUtils.isEmpty(python)) {
            python = ConfigUtil.getConfig("python");
        }
        return python;
    }

    public static String getGitCommandPath(Project project) {
        String git = getInstallPath(project, "git");
        if (TextUtils.isEmpty(git)) {
            git = ConfigUtil.getConfig("git");
        }
        return git;
    }

    private static String getShCmdPath_Windows(String path) {
        return path.replaceAll(" ","\" \"");
    }

    private static String getShPath_Windows(String path) {
        return QUOT + path + QUOT;
    }

    private static String getShPath_Mac(String path) {
        return path.replaceAll("\\s", "\\\\ ");
    }

    private static String getInstallPath(Project project, String name) {
        return isWindows() ? getInstallPath_Windows(project, name) : getInstallPath_Mac(project, name);
    }

    private static String getInstallPath_Mac(Project project, String name) {
        String path = getCommandOutput(project, "which " + name);
        if (TextUtils.isEmpty(path)) {
            path = getCommandOutput(project, "which /usr/local/bin/" + name);
        }
        if (TextUtils.isEmpty(path)) {
            path = getCommandOutput(project, "which /usr/bin/" + name);
        }
        if (TextUtils.isEmpty(path)) {
            path = getCommandOutput(project, "which /bin/" + name);
        }
        if (TextUtils.isEmpty(path)) {
            path = getCommandOutput(project, "which /usr/sbin/" + name);
        }
        if (TextUtils.isEmpty(path)) {
            path = getCommandOutput(project, "which /sbin/" + name);
        }
        if (TextUtils.isEmpty(path)) {
            path = ConfigUtil.getConfig(name);
        }
        if (path != null) {
            path = path.trim();
        }
        return path;
    }

    private static String getInstallPath_Windows(Project project, String name) {
        String path = getInstallPathFromCmd_Windows(project, name);
        if (TextUtils.isEmpty(path)) {
            path = ConfigUtil.getConfig(name);
        }
        if (path != null) {
            path = path.trim();
        }
        return path;
    }

    private static String getInstallPathFromCmd_Windows(Project project, String name) {
        List<String> lines = getCommandOutputEachLine(project, "where " + name);
        if (lines.isEmpty())
            return null;

        String line = lines.get(0);
        File file = new File(line);
        if (!file.exists()) {
            return null;
        }

        String fileName = file.getName();
        int index = fileName.lastIndexOf(DOT);
        if (index == -1)
            return line;

        return line.substring(0, line.length() - fileName.length() + index);
    }

    private static String getCommandOutput(Project project, String cmd) {
        List<String> lines = getCommandOutputEachLine(project, cmd);
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(line);
            result.append(RETURN);
        }
        return result.toString();
    }

    private static List<String> getCommandOutputEachLine(Project project, String cmd) {
        Process process;

        List<String> outLines = new ArrayList<String>();
        try {
            String[] cmds = getSystemCmds(cmd);
            process = Runtime.getRuntime().exec(cmds);

            int exitValue = process.waitFor();
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            while ((line = input.readLine()) != null) {
                outLines.add(line);
            }

            System.out.print("process exitValue = " + exitValue);

//            NEConsole.show(project, process, cmd);
        } catch (Exception e) {
            e.printStackTrace();
            Messages.showMessageDialog(e.toString(), "Error", Messages.getErrorIcon());
            outLines.clear();
        }

        return outLines;
    }

    public static String getGitCloneCmds(String url, String clonePath) {
        return isWindows() ?
                getGitCloneCmds_Windows(url, clonePath) :
                getGitCloneCmds_Mac(url, clonePath);
    }

    private static String getGitCloneCmds_Mac(String url, String clonePath) {
        String shClonePath = getShPath(clonePath);
        return "git clone " + url + " " + shClonePath + RETURN +
                "ls "  + shClonePath + RETURN;
    }

    private static String getGitCloneCmds_Windows(String url, String clonePath) {
        String gitPath = PathUtil.getGitPath();
        String cmd = TextUtils.isEmpty(gitPath) ? "git" : gitPath;
        cmd += " clone " + url + " " + getShPath(clonePath) + RETURN + RETURN;
//        cmd += "dir " + getShPath(clonePath) + RETURN;
        return cmd;
    }

    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        if (osName.contains("windows") || osName.contains("Windows")
                || osName.contains("WINDOWS")) {
//            Messages.showInfoMessage("windows","OS Type");
            return true;
        }

        else return false;
    }
}
