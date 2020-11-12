package worder.commons.impl;

import worder.commons.IOExchanger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LocalExchanger implements IOExchanger {
    private final Path root;


    public LocalExchanger(Path root) {
        this.root = root;
    }


    @Override
    public List<String> listAsStrings(String path) throws IOException {
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
    public List<URL> listAsUrls(String path) throws IOException {
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        var requiredPath = root.resolve(path);
        if (Files.notExists(requiredPath))
            return null;

        var files = Files.list(requiredPath).toArray(Path[]::new);
        var res = new ArrayList<URL>(files.length);

        for (Path file : files)
            res.add(file.toUri().toURL());

        return res;
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

        Files.createDirectories(pathToUpload.getParent());
        Files.write(pathToUpload, bytes);
    }

    @Override
    public void deleteFile(String path) throws IOException {
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        Files.delete(root.resolve(path));
    }

    @Override
    public URI getRootURI() {
        return root.toUri();
    }

    @Override
    public String toString() {
        return "LocalExchanger{" +
                "root=" + root +
                '}';
    }
}
