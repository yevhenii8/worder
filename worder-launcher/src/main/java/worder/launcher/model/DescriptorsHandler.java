/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DescriptorsHandler.java>
 * Created: <28/10/2020, 10:50:39 PM>
 * Modified: <07/11/2020, 07:54:00 PM>
 * Version: <129>
 */

package worder.launcher.model;

import worder.commons.AppDescriptor;
import worder.launcher.ui.UiHandler;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class DescriptorsHandler {
    private final UiHandler uiHandler;
    private final URL worderDistribution;
    private final URL worderHome;


    public DescriptorsHandler(UiHandler uiHandler, URL worderDistribution, URL worderHome) {
        this.uiHandler = uiHandler;
        this.worderDistribution = worderDistribution;
        this.worderHome = worderHome;
    }


    public void prepareWorderHome() {
        uiHandler.status("Obtaining local & remote descriptors ...");

        String requiredDescriptorName = AppDescriptor.obtainNameForCurrentOS();
        AppDescriptor localDescriptor = obtainLocalDescriptor(requiredDescriptorName);
        AppDescriptor remoteDescriptor = obtainRemoteDescriptor(requiredDescriptorName);

        if (localDescriptor == null && remoteDescriptor == null)
            uiHandler.criticalError("Neither local nor remote descriptor is accessible!");
        if (localDescriptor != null && remoteDescriptor == null)
            return;
//        if (localDescriptor != null && localDescriptor.getVersion() == remoteDescriptor.getVersion())
//            return;

        if (localDescriptor == null)
            //noinspection ConstantConditions
            syncWorderHome(remoteDescriptor);
        else
            syncWorderHome(localDescriptor, remoteDescriptor);
    }


    private void syncWorderHome(AppDescriptor remoteDescriptor, AppDescriptor localDescriptor) {
//        var toRemove = new LinkedList<>(localDescriptor.getAllArtifacts());
//        var toDownload = new LinkedList<>(remoteDescriptor.getAllArtifacts());
//
//        toRemove.removeAll(remoteDescriptor.getAllArtifacts());
//        toDownload.removeAll(localDescriptor.getAllArtifacts());
//
//        toRemove.forEach(artifacts -> {
//            uiHandler.status("Removing " + artifacts.getName() + " ...");
//            try {
//                Files.delete(localArtifactsPath.resolve(artifacts.getName()));
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        });
//
//        toDownload.forEach(artifact -> {
//            uiHandler.status("Downloading " + artifact.getName() + " ...");
//            try {
//                Files.write(
//                        localArtifactsPath.resolve(artifact.getName()),
//                        Objects.requireNonNull(downloadFile("artifacts/" + artifact.getName()))
//                );
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        });
    }

    private void syncWorderHome(AppDescriptor remoteDescriptor) {
//        try {
//            Files.createDirectories(localArtifactsPath);
//            Files.write(worderHomePath.resolve(remoteDescriptor.getName()), remoteDescriptor.toByteArray());
//
//            for (AppDescriptor.Artifact artifact : remoteDescriptor.getAllArtifacts()) {
//                uiHandler.status("Downloading " + artifact.getName() + " ...");
//                Files.write(
//                        localArtifactsPath.resolve(artifact.getName()),
//                        Objects.requireNonNull(downloadFile("artifacts/" + artifact.getName()))
//                );
//            }
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        }
    }

    private AppDescriptor obtainLocalDescriptor(String requiredDescriptorName) {
        var requiredFile = worderHome.resolve(requiredDescriptorName);

        if (Files.notExists(requiredFile))
            return null;

        try {
            return AppDescriptor.fromByteArray(Files.readAllBytes(requiredFile));
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

    private AppDescriptor obtainRemoteDescriptor(String requiredDescriptorName) {
        var remoteDescriptorBytes = downloadFile(requiredDescriptorName);

        if (remoteDescriptorBytes == null)
            return null;

        return AppDescriptor.fromByteArray(remoteDescriptorBytes);
    }

    private byte[] downloadFile(String path) {
        try {
            var url = new URL(worderDistribution + path);
            var inputStream = url.openStream();
            var res = inputStream.readAllBytes();
            inputStream.close();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
