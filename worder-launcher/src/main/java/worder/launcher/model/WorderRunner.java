/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WorderRunner.java>
 * Created: <05/11/2020, 08:36:34 PM>
 * Modified: <21/11/2020, 11:48:24 PM>
 * Version: <370>
 */

package worder.launcher.model;

import worder.commons.AppDescriptor;
import worder.commons.IOExchanger;
import worder.commons.OS;
import worder.launcher.ui.UiHandler;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WorderRunner {
    private static final long UI_DISPOSE_DELAY = 1500;
    private final RunningType runningType;
    private final UiHandler uiHandler;
    private final AppDescriptor descriptor;
    private final List<URL> artifactUrls;
    private final String[] worderArgs;


    public WorderRunner(
            UiHandler uiHandler,
            IOExchanger worderHome,
            AppDescriptor descriptor,
            RunningType runningType,
            String worderArgs
    ) throws Exception {
        this.uiHandler = uiHandler;
        this.runningType = runningType;
        this.descriptor = descriptor;
        this.artifactUrls = worderHome.listAsUrls("artifacts");
        this.worderArgs = (worderArgs == null) ? null : worderArgs.split("(?<!\") (?!\")");
    }


    public void runWorder() throws Exception {
        uiHandler.progress("Running Worder " + runningType + " ...");

        switch (runningType) {
            case IN_PLACE -> runInPlace();
            case SEPARATED -> runSeparated();
        }
    }


    private void runInPlace() throws Exception {
        var loader = new URLClassLoader(artifactUrls.toArray(URL[]::new), this.getClass().getClassLoader().getParent());
        var mainClass = Class.forName("worder.gui.AppEntry", false, loader);
        var mainMethod = mainClass.getMethod("launch", Class.class, String[].class);

        uiHandler.dispose(UI_DISPOSE_DELAY);

        Thread.currentThread().setContextClassLoader(loader);
        mainMethod.invoke(null, mainClass, worderArgs == null ? new String[0] : worderArgs);
    }

    private void runSeparated() throws Exception {
        var currentOs = OS.getCurrentOS();
        if (currentOs != OS.LINUX)
            throw new IllegalStateException("Separate run is not implemented on your OS: " + currentOs);

        var artifactPaths = artifactUrls.stream().map(URL::toString).collect(Collectors.toList());
        var modulePath = findArtifactsOfType(artifactPaths, AppDescriptor.Artifact.Type.MODULEPATH);
        var classPath = findArtifactsOfType(artifactPaths, AppDescriptor.Artifact.Type.CLASSPATH);

        var worderCommand = new LinkedList<String>();
        worderCommand.add("java");
        worderCommand.add("--add-modules");
        worderCommand.add(descriptor.getUsedModules());
        worderCommand.add("--module-path");
        worderCommand.add(String.join(":", modulePath));
        worderCommand.addAll(descriptor.getEnvArguments());
        worderCommand.add("--class-path");
        worderCommand.add(String.join(":", classPath));
        worderCommand.add(descriptor.getAppMainClass());

        if (worderArgs != null)
            worderCommand.addAll(Arrays.asList(worderArgs));

        uiHandler.dispose(UI_DISPOSE_DELAY);

        new ProcessBuilder()
                .command(worderCommand)
                .inheritIO()
                .start();
    }

    private List<String> findArtifactsOfType(List<String> artifactPaths, AppDescriptor.Artifact.Type type) {
        return descriptor.getArtifacts().stream()
                .filter(it -> it.getType() == type)
                .map(it -> {
                    for (String path : artifactPaths)
                        if (path.endsWith(it.getName()))
                            return path;

                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public enum RunningType {
        IN_PLACE, SEPARATED
    }
}
