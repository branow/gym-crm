package dev.branow.storage;

import java.io.*;

public class FileStorage implements Storage {

    private final File file;

    public FileStorage(String path) {
        this.file = new File(path);
    }

    @Override
    public InputStream read() {
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + file.getPath(), e);
        }
    }

    @Override
    public void write(InputStream inputStream) {
        try (var fos = new FileOutputStream(file);
             var bis = new BufferedInputStream(inputStream)) {
            bis.transferTo(fos);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write to file: " + file.getPath(), e);
        }
    }
}
