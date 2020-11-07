/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WorderRunner.java>
 * Created: <05/11/2020, 08:36:34 PM>
 * Modified: <07/11/2020, 07:54:00 PM>
 * Version: <35>
 */

package worder.launcher.model;

import worder.launcher.App;
import worder.launcher.ui.UiHandler;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class WorderRunner {
    public static RunningType defaultRunningType = RunningType.IN_PLACE;
    private final UiHandler uiHandler;
    private final Path worderHomeCatalog;


    public WorderRunner(UiHandler uiHandler, Path worderHomeCatalog) {
        this.uiHandler = uiHandler;
        this.worderHomeCatalog = worderHomeCatalog;
    }


    public static void setDefaultRunningType(RunningType defaultRunningType) {
        WorderRunner.defaultRunningType = defaultRunningType;
    }

    public void runWorder() {
        uiHandler.status("Running worder ...");

        switch (defaultRunningType) {
            case IN_PLACE -> runInPlace();
            case SEPARATE_PID -> runSeparated();
        }
    }


    private void runInPlace() {
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
                uiHandler.dispose();
            });
            method.invoke(null, mainClass, new String[0]);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void runSeparated() {

    }


    public enum RunningType {
        IN_PLACE, SEPARATE_PID
    }
}
