package worder.commons.impl;

import worder.commons.IOExchanger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BintrayExchanger implements IOExchanger {
    private static final Pattern downstreamFilter = Pattern.compile("<pre>.*</pre>");
    private static final Pattern downstreamPattern = Pattern.compile("<.*?>");

    private final String downstream;
    private HttpClient httpClient;
    private String upstream;
    private String api;


    public BintrayExchanger(String bintrayUser, String bintrayKey, String bintrayRepository, String bintrayPackage, String bintrayVersion) {
        this(bintrayUser, bintrayRepository);

        upstream = String.format("https://api.bintray.com/content/%s/%s/%s/%s/", bintrayUser, bintrayRepository, bintrayPackage, bintrayVersion);
        api = String.format("https://api.bintray.com/content/%s/%s/", bintrayUser, bintrayRepository);

        httpClient = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(bintrayUser, bintrayKey.toCharArray());
                    }
                })
                .build();
    }

    public BintrayExchanger(String bintrayUser, String repository) {
        downstream = String.format("https://dl.bintray.com/%s/%s/", bintrayUser, repository);
    }


    @Override
    public List<String> listCatalog(String path) throws IOException {
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        byte[] bytes;
        try {
            var inputStream = new URL(downstream + path).openStream();
            bytes = inputStream.readAllBytes();
            inputStream.close();
        } catch (FileNotFoundException exception) {
            return null;
        }

        return new String(bytes)
                .lines()
                .filter(downstreamFilter.asMatchPredicate())
                .map(it -> downstreamPattern.matcher(it).replaceAll(""))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadFile(String path) throws IOException {
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        var inputStream = new URL(downstream + path).openStream();
        var res = inputStream.readAllBytes();
        inputStream.close();

        return res;
    }

    @Override
    public void uploadFile(String path, byte[] bytes, boolean override) throws IOException, InterruptedException {
        if (upstream == null)
            throw new IllegalStateException("You can't perform files uploading without API key.");
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        var request = HttpRequest.newBuilder(URI.create(upstream + path + "?publish=1&override=" + (override ? 1 : 0)))
                .PUT(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201)
            throw new IOException("BintrayAPI response code - " + response.statusCode() + ": \n" + response.body());
    }

    @Override
    public void deleteFile(String path) throws IOException, InterruptedException {
        if (upstream == null)
            throw new IllegalStateException("You can't perform files deleting without API key.");
        if (path.startsWith("/"))
            throw new IllegalArgumentException("You can't use absolute path here, passed value: " + path);

        var request = HttpRequest.newBuilder(URI.create(api + path))
                .DELETE()
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            throw new IOException("BintrayAPI response code - " + response.statusCode() + ": \n" + response.body());
    }
}
