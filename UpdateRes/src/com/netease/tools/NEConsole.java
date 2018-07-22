package com.netease.tools;

import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * Created by zyl06 on 7/25/16.
 */
public class NEConsole {
    private static final String ID = "Mipmap Console";

    private static ConsoleView sConsoleView;

    public static void show(Project project, Process process, String commandLine) {
        ToolWindowManager manager = ToolWindowManager.getInstance(project);
        ToolWindow sToolWindow = manager.getToolWindow(ID);

        if (sConsoleView == null) {
//            if (toolWindow != null) {
//                if (toolWindow.getComponent() != null) {
//                    for (Component component : toolWindow.getComponent().getComponents()
//                            ) {
//                        if (component instanceof ConsoleView)
//                            consoleView = (ConsoleView) component;
//                        break;
//                    }
//                }
//            }
            TextConsoleBuilderFactory factory = TextConsoleBuilderFactory.getInstance();
            TextConsoleBuilder builder = factory.createBuilder(project);
            sConsoleView = builder.getConsole();
        }

        OSProcessHandler handler = new OSProcessHandler(process, commandLine);
        sConsoleView.attachToProcess(handler);
//
//        handler.addProcessListener(new ProcessListener() {
//            @Override
//            public void startNotified(ProcessEvent event) {
//
//                System.out.println(event.getText() + " " + event.getExitCode() + " startNotified\n");
//            }
//
//            @Override
//            public void processTerminated(ProcessEvent event) {
//                System.out.println(event.getText() + " " + event.getExitCode() + "  processTerminated\n");
//            }
//
//            @Override
//            public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
//                System.out.println(event.getText() + " " + event.getExitCode() + " processWillTerminate\n");
//            }
//
//            @Override
//            public void onTextAvailable(ProcessEvent event, Key outputType) {
//                System.out.println(event.getText() + " " + event.getExitCode() + " onTextAvailable " + outputType.toString() + "\n");
//                if (event.getExitCode() == 0)
//                    Messages.showInfoMessage("nei-toolkit 安装成功", "Info");
//            }
//        });

        handler.startNotify();

        if (sToolWindow == null) {
            sToolWindow = manager.registerToolWindow(ID, false, ToolWindowAnchor.BOTTOM);

            try {
                sToolWindow.getComponent().add(sConsoleView.getComponent());
            } catch (Exception e) {
                e.printStackTrace();
            }

            sToolWindow.show(new Runnable() {
                public void run() {
                    //System.out.println("Do something here");
                }
            });
        }
    }
}
