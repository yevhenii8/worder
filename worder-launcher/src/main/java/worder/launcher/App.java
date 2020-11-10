/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <App.java>
 * Created: <04/08/2020, 07:03:59 PM>
 * Modified: <11/11/2020, 12:53:29 AM>
 * Version: <423>
 */

package worder.launcher;

import worder.commons.AppDescriptor;
import worder.commons.IOExchanger;
import worder.commons.impl.BintrayExchanger;
import worder.commons.impl.LocalExchanger;
import worder.launcher.model.DescriptorsHandler;
import worder.launcher.model.WorderRunner;
import worder.launcher.ui.UiHandler;
import worder.launcher.ui.impl.swing.SwingUI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class App {
    private static IOExchanger worderDistribution = new BintrayExchanger("evgen8", "generic");
    private static IOExchanger worderHome = new LocalExchanger(Path.of(detectWorderHome()));
    private static WorderRunner.RunningType runningType = WorderRunner.RunningType.IN_PLACE;


    public static void main(String[] args) throws Exception {
        processArguments(args);

        UiHandler uiHandler = new SwingUI();
        uiHandler.show();

        DescriptorsHandler descriptorsHandler = new DescriptorsHandler(uiHandler, worderDistribution, worderHome);
        descriptorsHandler.prepareWorderHome();

        WorderRunner worderRunner = new WorderRunner(uiHandler, worderHome, descriptorsHandler.getHomeDescriptor(), runningType);
        worderRunner.runWorder();
    }


    private static void processArguments(String[] args) {
        for (String argument : args) {
            var index = argument.indexOf("=");
            var argumentName = index > 0 ? argument.substring(0, index) : argument;
            var launcherArgument = Arrays.stream(LauncherArgument.values())
                    .filter(it -> it.name.equals(argumentName))
                    .findFirst()
                    .orElseThrow();

            launcherArgument.value = argument.substring(index + 1);
            launcherArgument.action.run();
        }
    }

    private static void setCustomWorderHome() {
        worderHome = new LocalExchanger(Path.of(LauncherArgument.WORDER_HOME.value));
    }

    private static void setCustomWorderDistribution() {
        worderDistribution = new LocalExchanger(Path.of(LauncherArgument.WORDER_DISTRIBUTION.value));
    }

    private static void setSeparatedRunning() {
        runningType = WorderRunner.RunningType.SEPARATED;
    }

    private static void printHelp() {
        var maxLength = Arrays.stream(LauncherArgument.values())
                .mapToInt(argument -> argument.name.length())
                .max()
                .orElseThrow();

        for (LauncherArgument argument : LauncherArgument.values())
            System.out.println(argument.name + " ".repeat(maxLength - argument.name.length() + 3) + argument.description);

        System.exit(0);
    }

    private static void printDistributionDescriptors() {
        try {
            for (String name : worderDistribution.listAsStrings("")) {
                if (name.startsWith("WorderAppDescriptor")) {
                    AppDescriptor appDescriptor = AppDescriptor.fromByteArray(worderHome.downloadFile(name));
                    printDescriptor(appDescriptor);
                    System.out.println();
                    System.out.println();
                }
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    private static void printHomeDescriptor() {
        try {
            printDescriptor(
                    AppDescriptor.fromByteArray(
                            worderHome.downloadFile(AppDescriptor.obtainNameForCurrentOS())
                    )
            );
        } catch (IOException | ClassNotFoundException exception) {
            System.out.println("Error when processing " + worderHome + "/" + AppDescriptor.obtainNameForCurrentOS());
            exception.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }

    private static void printChannels() {
        System.out.println("Worder Home:          " + worderHome);
        System.out.println("Worder Distribution:  " + worderDistribution);
        System.exit(0);
    }

    private static void printDescriptor(AppDescriptor descriptor) {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("Generated", descriptor.getGenerated());
        values.put("AppVersion", descriptor.getAppVersion());
        values.put("AppMainClass", descriptor.getAppMainClass());
        values.put("UsedModules", descriptor.getUsedModules());
        values.put("EnvArguments", descriptor.getEnvArguments().toString());

        var modulePath = descriptor.getArtifacts().stream()
                .filter(it -> it.getType() == AppDescriptor.Artifact.Type.MODULEPATH)
                .toArray(AppDescriptor.Artifact[]::new);
        var classPath = descriptor.getArtifacts().stream()
                .filter(it -> it.getType() == AppDescriptor.Artifact.Type.CLASSPATH)
                .toArray(AppDescriptor.Artifact[]::new);
        var maxLength = values.keySet().stream()
                .map(String::length)
                .max(Integer::compareTo)
                .orElseThrow();

        System.out.println(descriptor.getName());
        System.out.println();
        values.forEach((k, v) -> System.out.println("    " + k + ":  " + " ".repeat(maxLength - k.length()) + v));
        
        System.out.println();
        System.out.println("    Module Path Artifacts:");
        for (int i = 0; i < modulePath.length; i++)
            System.out.println("        " + (i + 1) + ") " + modulePath[i].getName());

        System.out.println();
        System.out.println("    Class Path Artifacts:");
        for (int i = 0; i < classPath.length; i++)
            System.out.println("        " + (i + 1) + ") " + classPath[i].getName());

        System.exit(0);
    }

    private static String detectWorderHome() {
        var currentOs = System.getProperty("os.name");
        var userHomeCatalog = System.getProperty("user.home");

        if (currentOs.equals("Linux"))
            return userHomeCatalog + "/.worder-gui/";

        throw new IllegalStateException("There's no support of Worder-Launcher for your OS: " + currentOs);
    }


    enum LauncherArgument {
        HELP(
                "--help",
                "Prints all possible arguments and exits.",
                App::printHelp
        ),
        PRINT_DISTRIBUTION_DESCRIPTORS(
                "--print-distribution-descriptors",
                "Prints all descriptors from distribution and exits.",
                App::printDistributionDescriptors
        ),
        PRINT_HOME_DESCRIPTOR(
                "--print-home-descriptor",
                "Prints the descriptor from home catalog and exits.",
                App::printHomeDescriptor
        ),
        PRINT_CHANNELS(
                "--print-channels",
                "Prints current Worder Home && Worder Distribution and exits.",
                App::printChannels
        ),
        WORDER_HOME(
                "--worder-home",
                "Sets specified path as a Worder Home Catalog.",
                App::setCustomWorderHome
        ),
        WORDER_DISTRIBUTION(
                "--worder-distribution",
                "Sets specified path as a Worder Distribution",
                App::setCustomWorderDistribution
        ),
        RUN_SEPARATED(
                "--run-separated",
                "Launches the application as a separate process.",
                App::setSeparatedRunning
        );


        private final String name;
        private final String description;
        private final Runnable action;
        private String value;


        LauncherArgument(String name, String description, Runnable action) {
            this.name = name;
            this.description = description;
            this.action = action;
        }
    }
}
