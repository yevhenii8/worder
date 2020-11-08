package worder.commons;

import java.io.IOException;
import java.util.List;

public interface IOExchanger {
    List<String> listCatalog(String path) throws IOException;

    byte[] downloadFile(String path) throws IOException;

    void uploadFile(String path, byte[] bytes, boolean override) throws IOException, InterruptedException;

    void deleteFile(String path) throws IOException, InterruptedException;
}
