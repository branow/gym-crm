package dev.branow.storage;

import java.io.InputStream;

public interface Storage {
    InputStream read();
    void write(InputStream stream);
}
