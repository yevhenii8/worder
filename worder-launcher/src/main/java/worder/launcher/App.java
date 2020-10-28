/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <App.java>
 * Created: <04/08/2020, 07:03:59 PM>
 * Modified: <29/10/2020, 12:40:15 AM>
 * Version: <205>
 */

package worder.launcher;

import worder.launcher.ui.UI;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class App {
    public static void main(String[] args) {
        UI ui = new UI();
        ui.show();

        runWorder(Path.of("/home/yevhenii/WorderDeployTest/"));
    }

    public static void runWorder(Path worderHomeCatalog) {
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
            Class<?> javafxPlatform = Class.forName("javafx.application.Platform", true, loader);

            javafxPlatform.getMethod("startup", Runnable.class).invoke(null, (Runnable) () -> {
                Thread.currentThread().setContextClassLoader(loader);
                try {
                    Class<?> javafxApplication = Class.forName("javafx.application.Application", true, loader);
                    Class<?> worderMainClass = Class.forName("worder.gui.AppEntry", true, loader);
                    javafxApplication.getMethod("launch", Class.class, String[].class).invoke(null, worderMainClass, new String[0]);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
