package dev.branow.storage;

import java.io.*;

public class FileStorage implements Storage {

    private final String path;

    public FileStorage(String path) {
        this.path = path;
    }

    @Override
    public InputStream read() {
        var file = new File(path);
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(InputStream os) {
        var file = new File(path);
        try (var fos = new FileOutputStream(file);
             var bis = new BufferedInputStream(os)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
