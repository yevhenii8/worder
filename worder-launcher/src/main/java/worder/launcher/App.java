/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <App.java>
 * Created: <04/08/2020, 07:03:59 PM>
 * Modified: <05/11/2020, 10:28:14 PM>
 * Version: <288>
 */

package worder.launcher;

import worder.launcher.model.DescriptorsHandler;
import worder.launcher.ui.LauncherUI;
import worder.launcher.model.Action;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class App {
    public static void main(String[] args) {
        processArguments(args);

        LauncherUI launcherUi = new LauncherUI();
        launcherUi.show();

        DescriptorsHandler descriptorsHandler = new DescriptorsHandler(launcherUi);
        descriptorsHandler.prepareWorderHome();

        runWorder(descriptorsHandler.getWorderHomePath(), launcherUi);
    }

    private static void runWorder(Path worderHomeCatalog, LauncherUI launcherUi) {
        URL[] urls = Arrays.stream(Objects.requireNonNull(worderHomeCatalog.resolve("artifacts").toFile().listFiles()))
                .map(file -> {
                    try {
                        return file.toURI().toURL();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .toArray(URL[]::new);

        ClassLoader loader = URLClassLoader.newInstance(urls, App.class.getClassLoader());
        try {
            Thread.currentThread().setContextClassLoader(loader);
            Class<?> mainClass = Class.forName("worder.gui.AppEntry", false, loader);
            var method = mainClass.getMethod("launch", Class.class, String[].class);
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                launcherUi.dispose();
            });
            method.invoke(null, mainClass, new String[0]);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void processArguments(String[] args) {
        Arrays.stream(args)
                .map(passedArgument -> {
                    var index = passedArgument.indexOf("=");
                    var launcherArgument = Arrays.stream(LauncherArgument.values())
                            .filter(it -> it.name.equals(passedArgument.substring(0, index)))
                            .findFirst()
                            .orElseThrow();
                    launcherArgument.value = passedArgument.substring(index + 1);
                    return launcherArgument;
                })
                .forEach(it -> it.action.execute());
    }

    private static void setCustomWorderHome() {
        DescriptorsHandler.setCustomWorderHome(LauncherArgument.WORDER_HOME.value);
    }


    enum LauncherArgument {
        WORDER_HOME(
                "--worder-home",
                "Uses specified catalog as a Worder Home Catalog.",
                App::setCustomWorderHome
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
