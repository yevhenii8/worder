package worder.commons;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface IOExchanger {
    List<String> listAsStrings(String path) throws IOException;

    List<URL> listAsUrls(String path) throws IOException;

    byte[] downloadFile(String path) throws IOException;

    void uploadFile(String path, byte[] bytes, boolean override) throws IOException, InterruptedException;

    void deleteFile(String path) throws IOException, InterruptedException;
}
