/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DemoHandler.java>
 * Created: <21/11/2020, 11:05:59 PM>
 * Modified: <22/11/2020, 12:38:35 AM>
 * Version: <2>
 */

package worder.launcher.model;

import worder.commons.IOExchanger;
import worder.launcher.ui.UiHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DemoHandler {
    private final IOExchanger worderDistribution;
    private final IOExchanger worderHome;
    private final UiHandler uiHandler;
    private final Path demoCatalog;


    public DemoHandler(UiHandler uiHandler, IOExchanger worderDistribution, IOExchanger worderHome) {
        demoCatalog = Path.of(worderHome.getRootURI()).resolve("demo");

        this.worderDistribution = worderDistribution;
        this.worderHome = worderHome;
        this.uiHandler = uiHandler;
    }


    public void prepareDemoFiles() throws IOException, InterruptedException {
        List<String> distributionDemoFiles = null;

        try {
            uiHandler.progress("Requesting demo files list ...");
            distributionDemoFiles = worderDistribution.listAsStrings("demo");
        } catch (IOException ignored) {
        }

        if (distributionDemoFiles != null) {
            List<String> localDemoFiles = Objects.requireNonNullElse(worderHome.listAsStrings("demo"), Collections.emptyList());

            var toRemove = new ArrayList<>(localDemoFiles);
            toRemove.removeAll(distributionDemoFiles);
            for (String fileName : toRemove) {
                uiHandler.progress("Deleting '" + fileName + "' ...");
                worderHome.deleteFile("demo/" + fileName);
            }

            var toDownload = new ArrayList<>(distributionDemoFiles);
            toDownload.removeAll(localDemoFiles);
            for (String fileName : toDownload) {
                uiHandler.progress("Downloading '" + fileName + "' ...");
                worderHome.uploadFile("demo/" + fileName, worderDistribution.downloadFile("demo/" + fileName), false);
            }
        }

        if (!Files.isDirectory(demoCatalog))
            throw new IllegalStateException("Launcher didn't detect demo-file and could not download them! Please check Internet connection and try again!");
    }

    public Path getDemoCatalog() {
        return demoCatalog;
    }
}
