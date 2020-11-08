package worder.commons.impl;

import worder.commons.IOExchanger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class LocalExchanger implements IOExchanger {
    private final Path root;


    public LocalExchanger(Path root) {
        this.root = root;
    }


    @Override
    public List<String> listCatalog(String path) throws IOException {
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        var requiredPath = root.resolve(path);

        if (Files.notExists(requiredPath))
            return null;

        return Files.list(requiredPath)
                .map(it -> it.getFileName().toString())
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadFile(String path) throws IOException {
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        var requestedFile = root.resolve(path);

        if (Files.notExists(requestedFile))
            return null;

        return Files.readAllBytes(requestedFile);
    }

    @Override
    public void uploadFile(String path, byte[] bytes, boolean override) throws IOException {
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        var pathToUpload = root.resolve(path);

        if (Files.exists(pathToUpload) && !override)
            throw new IllegalArgumentException("File " + path + " already exists!");

        Files.createDirectories(pathToUpload.subpath(0, pathToUpload.getNameCount() - 1));
        Files.write(pathToUpload, bytes);
    }

    @Override
    public void deleteFile(String path) throws IOException {
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        Files.delete(root.resolve(path));
    }
}
