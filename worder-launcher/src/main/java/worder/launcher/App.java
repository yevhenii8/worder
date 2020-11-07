/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <App.java>
 * Created: <04/08/2020, 07:03:59 PM>
 * Modified: <07/11/2020, 07:54:00 PM>
 * Version: <299>
 */

package worder.launcher;

import worder.launcher.model.Action;
import worder.launcher.model.DescriptorsHandler;
import worder.launcher.model.WorderRunner;
import worder.launcher.ui.SwingUI;
import worder.launcher.ui.UiHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

public class App {
    private static boolean useLocalDistribution;
    private static URL remoteWorderDistribution;
    private static Path localWorderDistribution;
    private static Path worderHome;


    public static void main(String[] args) throws MalformedURLException {
        initDefaults();
        processArguments(args);

        UiHandler uiHandler = new SwingUI();
        uiHandler.show();

//        DescriptorsHandler descriptorsHandler = new DescriptorsHandler(uiHandler);
//        descriptorsHandler.prepareWorderHome();
//
//        WorderRunner worderRunner = new WorderRunner(uiHandler, descriptorsHandler.getWorderHomePath());
//        worderRunner.runWorder();
    }


    private static void initDefaults() throws MalformedURLException {
        remoteWorderDistribution = new URL("https://dl.bintray.com/evgen8/generic");
        worderHome = Path.of(detectWorderHome());
    }

    private static void processArguments(String[] args) throws MalformedURLException {
        for (String argument : args) {
            var index = argument.indexOf("=");
            var argumentName = argument.substring(0, index);
            var launcherArgument = Arrays.stream(LauncherArgument.values())
                    .filter(it -> it.name.equals(argumentName))
                    .findFirst()
                    .orElseThrow();

            launcherArgument.value = argument.substring(index + 1);
            launcherArgument.action.execute();
        }
    }

    private static void setCustomWorderHome() {
        worderHome = Path.of(LauncherArgument.WORDER_DISTRIBUTION.value);
    }

    private static void setCustomWorderDistribution() {
        localWorderDistribution = Path.of(LauncherArgument.WORDER_DISTRIBUTION.value);
        useLocalDistribution = true;
    }

    private static String detectWorderHome() {
        var currentOs = System.getProperty("os.name");
        var userHomeCatalog = System.getProperty("user.home");

        if (currentOs.equals("Linux"))
            return userHomeCatalog + "/.worder-gui/";

        throw new IllegalStateException("There's no support of Worder-Launcher for your OS: " + currentOs);
    }


    enum LauncherArgument {
        WORDER_HOME(
                "--worder-home",
                "Sets specified path as a Worder Home Catalog.",
                App::setCustomWorderHome
        ),
        WORDER_DISTRIBUTION(
                "--worder-distribution",
                "Sets specified path as a Worder Distribution",
                App::setCustomWorderDistribution
        );


        private final String name;
        private final String description;
        private final Action action;
        private String value;


        LauncherArgument(String name, String description, Action action) {
            this.name = name;
            this.description = description;
            this.action = action;
        }
    }
}
