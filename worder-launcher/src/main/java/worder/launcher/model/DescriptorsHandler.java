/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DescriptorsHandler.java>
 * Created: <28/10/2020, 10:50:39 PM>
 * Modified: <14/11/2020, 10:13:32 PM>
 * Version: <455>
 */

package worder.launcher.model;

import worder.commons.AppDescriptor;
import worder.commons.IOExchanger;
import worder.launcher.ui.UiHandler;

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DescriptorsHandler {
    private final IOExchanger worderDistribution;
    private final IOExchanger worderHome;
    private final UiHandler uiHandler;
    private AppDescriptor homeDescriptor;


    public DescriptorsHandler(UiHandler uiHandler, IOExchanger worderDistribution, IOExchanger worderHome) {
        this.worderDistribution = worderDistribution;
        this.worderHome = worderHome;
        this.uiHandler = uiHandler;
    }


    public static String detectWorderHome() {
        var currentOs = System.getProperty("os.name");
        var userHomeCatalog = System.getProperty("user.home");

        if (currentOs.equals("Linux"))
            return userHomeCatalog + "/.worder-gui/";

        throw new IllegalStateException("There's no support of Worder-Launcher for your OS: " + currentOs);
    }


    public void prepareWorderHome() throws IOException, ClassNotFoundException, InterruptedException {
        uiHandler.status("Obtaining local & remote descriptors ...");

        String requiredDescriptorName = AppDescriptor.obtainNameForCurrentOS();
        byte[] distributionDescriptorRaw = worderDistribution.downloadFile(requiredDescriptorName);
        AppDescriptor distributionDescriptor = AppDescriptor.fromByteArray(distributionDescriptorRaw);
        AppDescriptor localDescriptor;

        try {
            localDescriptor = AppDescriptor.fromByteArray(worderHome.downloadFile(requiredDescriptorName));
        } catch (InvalidClassException exception) {
            localDescriptor = null;
        }

        if (localDescriptor == null && distributionDescriptor == null)
            throw new IllegalStateException("Neither local nor distribution descriptor is accessible!");

        if (distributionDescriptor != null && (localDescriptor == null || !localDescriptor.equals(distributionDescriptor))) {
            syncWorderHome(distributionDescriptor, distributionDescriptorRaw);
            homeDescriptor = distributionDescriptor;
        } else {
            homeDescriptor = localDescriptor;
        }
    }

    public AppDescriptor getHomeDescriptor() {
        return homeDescriptor;
    }


    private void syncWorderHome(AppDescriptor distributionDescriptor, byte[] distributionDescriptorRaw) throws IOException, InterruptedException {
        List<String> actualArtifactNames = worderHome.listAsStrings("artifacts");
        Map<String, Integer> actualArtifacts = Objects.requireNonNullElse(actualArtifactNames, Collections.<String>emptyList())
                .stream()
                .collect(Collectors.toMap(Function.identity(), __ -> -1));

        distributionDescriptor.getArtifacts()
                .forEach(artifact -> actualArtifacts.compute(artifact.getName(), (key, value) -> (value == null) ? 1 : 0));

        for (Map.Entry<String, Integer> entry : actualArtifacts.entrySet()) {
            var name = entry.getKey();
            var value = entry.getValue();

            if (value == -1) {
                uiHandler.status("Removing '" + name + "' ...");
                worderHome.deleteFile("artifacts/" + name);
            }
            if (value == 1) {
                uiHandler.status("Downloading '" + name + "' ...");
                worderHome.uploadFile(
                        "artifacts/" + name,
                        worderDistribution.downloadFile("artifacts/" + name),
                        false
                );
            }
        }

        uiHandler.status("Uploading most relevant " + distributionDescriptor.getName() + " ...");
        worderHome.uploadFile(distributionDescriptor.getName(), distributionDescriptorRaw, true);
    }
}
