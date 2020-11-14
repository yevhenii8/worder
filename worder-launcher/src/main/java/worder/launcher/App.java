/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <App.java>
 * Created: <04/08/2020, 07:03:59 PM>
 * Modified: <15/11/2020, 12:04:20 AM>
 * Version: <630>
 */

package worder.launcher;

import worder.commons.AppDescriptor;
import worder.commons.IOExchanger;
import worder.commons.impl.BintrayExchanger;
import worder.commons.impl.LocalExchanger;
import worder.launcher.logging.SimpleLogger;
import worder.launcher.model.DescriptorsHandler;
import worder.launcher.model.WorderRunner;
import worder.launcher.ui.UiHandler;
import worder.launcher.ui.UiHandlerLoggingDecorator;
import worder.launcher.ui.impl.swing.SwingUI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

public class App {
    private static WorderRunner.RunningType runningType = WorderRunner.RunningType.IN_PLACE;
    private static IOExchanger worderDistribution = new BintrayExchanger("evgen8", "generic");
    private static IOExchanger worderHome = new LocalExchanger(Path.of(DescriptorsHandler.detectWorderHome()));
    private static String worderArgs = null;


    public static void main(String[] args) throws Exception {
        var bytes = App.class.getResourceAsStream("/version").readAllBytes();;
        String launcherVersion = new String(bytes);

        ArgumentsHandler argumentsHandler = new ArgumentsHandler(args);
        argumentsHandler.applyExecutionArguments();

        SimpleLogger simpleLogger = new SimpleLogger(worderDistribution, worderHome, launcherVersion, args, runningType);
        simpleLogger.initLogging();

        if (argumentsHandler.hasCommandToExecute()) {
            argumentsHandler.executeCommandIfPresent();
            System.exit(0);
        }

        UiHandler uiHandler = new UiHandlerLoggingDecorator(new SwingUI(), simpleLogger);
        uiHandler.show();

        DescriptorsHandler descriptorsHandler = new DescriptorsHandler(uiHandler, worderDistribution, worderHome);
        descriptorsHandler.prepareWorderHome();

        WorderRunner worderRunner = new WorderRunner(uiHandler, worderHome, descriptorsHandler.getHomeDescriptor(), runningType, worderArgs);
        worderRunner.runWorder();
    }


    private static class ArgumentsHandler {
        private final List<LauncherArgument> launcherArgs = new LinkedList<>();
        private final LauncherCommand launcherCmd;


        private ArgumentsHandler(String[] rawArgs) {
            List<String> wrongArgs = new ArrayList<>();
            List<LauncherCommand> parsedCommands = new ArrayList<>();

            Arrays.stream(rawArgs)
                    .forEach(rawArg -> {
                        var index = rawArg.indexOf("=");
                        var rawArgName = index > 0 ? rawArg.substring(0, index) : rawArg;
                        var launcherArg = LauncherArgument.fromName(rawArgName);

                        if (launcherArg != null) {
                            launcherArg.value = rawArg.substring(index + 1);
                            launcherArgs.add(launcherArg);
                        } else {
                            var launcherCmd = LauncherCommand.fromName(rawArgName);

                            if (launcherCmd == null) {
                                wrongArgs.add(rawArg);
                                return;
                            }

                            launcherCmd.value = rawArg.substring(index + 1);
                            parsedCommands.add(launcherCmd);
                        }
                    });

            if (!wrongArgs.isEmpty())
                throw new IllegalArgumentException(
                        "Wrong argument(s) have been passed: "
                                + wrongArgs
                                + "\nPlease use --help."
                );

            if (parsedCommands.size() > 1)
                throw new IllegalArgumentException(
                        "Only one command can be passed. Several commands have been passed: "
                                + parsedCommands
                                + "\nPlease use --help."
                );

            launcherCmd = parsedCommands.isEmpty() ? null : parsedCommands.get(0);
        }


        private void applyExecutionArguments() {
            launcherArgs.forEach(it -> it.action.run());
        }

        private void executeCommandIfPresent() {
            if (launcherCmd != null)
                launcherCmd.action.run();
        }

        private boolean hasCommandToExecute() {
            return launcherCmd != null;
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

        private static void setWorderArgs() {
            worderArgs = LauncherArgument.ARGS.value;
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
        }

        private static void printHelp() {
            var maxLen = IntStream.concat(
                    Arrays.stream(LauncherArgument.values()).mapToInt(argument -> argument.name.length()),
                    Arrays.stream(LauncherCommand.values()).mapToInt(command -> command.name.length())
            ).max().orElseThrow();

            System.out.println("You can use one or more arguments at once, but only ONE command.");
            System.out.println("------------------------------------------------------------------------");
            System.out.println();

            System.out.println("Possible execution arguments: ");
            for (LauncherArgument argument : LauncherArgument.values())
                System.out.println("    " + argument.name + " ".repeat(maxLen - argument.name.length() + 3) + argument.description);

            System.out.println();

            System.out.println("Possible commands: ");
            for (LauncherCommand command : LauncherCommand.values())
                System.out.println("    " + command.name + " ".repeat(maxLen - command.name.length() + 3) + command.description);
        }

        private static void printDistributionDescriptors() {
            try {
                for (String name : worderDistribution.listAsStrings("")) {
                    if (name.startsWith("WorderAppDescriptor")) {
                        AppDescriptor appDescriptor = AppDescriptor.fromByteArray(worderDistribution.downloadFile(name));
                        printDescriptor(appDescriptor);
                        System.out.println();
                        System.out.println();
                    }
                }
            } catch (IOException | ClassNotFoundException exception) {
                exception.printStackTrace();
                System.exit(1);
            }
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
        }

        private static void printChannels() {
            System.out.println("Worder Home:          " + worderHome);
            System.out.println("Worder Distribution:  " + worderDistribution);
        }


        private enum LauncherArgument {
            ARGS(
                    "--args",
                    "Passes its value to Worder as arguments.",
                    ArgumentsHandler::setWorderArgs
            ),
            WORDER_HOME(
                    "--worder-home",
                    "Sets specified path as a Worder Home Catalog.",
                    ArgumentsHandler::setCustomWorderHome
            ),
            WORDER_DISTRIBUTION(
                    "--worder-distribution",
                    "Sets specified path as a Worder Distribution",
                    ArgumentsHandler::setCustomWorderDistribution
            ),
            RUN_SEPARATED(
                    "--run-separated",
                    "Launches the application as a separate process.",
                    ArgumentsHandler::setSeparatedRunning
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


            static LauncherArgument fromName(String name) {
                for (LauncherArgument arg : values())
                    if (arg.name.equals(name))
                        return arg;

                return null;
            }
        }

        private enum LauncherCommand {
            HELP(
                    "--help",
                    "Prints all possible arguments and exits.",
                    ArgumentsHandler::printHelp
            ),
            PRINT_DISTRIBUTION_DESCRIPTORS(
                    "--print-distribution-descriptors",
                    "Prints all descriptors from distribution and exits.",
                    ArgumentsHandler::printDistributionDescriptors
            ),
            PRINT_HOME_DESCRIPTOR(
                    "--print-home-descriptor",
                    "Prints the descriptor from home catalog and exits.",
                    ArgumentsHandler::printHomeDescriptor
            ),
            PRINT_CHANNELS(
                    "--print-channels",
                    "Prints current Worder Home && Worder Distribution and exits.",
                    ArgumentsHandler::printChannels
            );


            private final String name;
            private final String description;
            private final Runnable action;
            private String value;


            LauncherCommand(String name, String description, Runnable action) {
                this.name = name;
                this.description = description;
                this.action = action;
            }


            static LauncherCommand fromName(String name) {
                for (LauncherCommand cmd : values())
                    if (cmd.name.equals(name))
                        return cmd;

                return null;
            }
        }
    }
}
