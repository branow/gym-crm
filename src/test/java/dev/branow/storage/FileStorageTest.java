package dev.branow.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageTest {

    private Path dir;

    @BeforeEach
    void setUp() throws IOException {
        dir = Files.createTempDirectory("filestorage_test");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(dir).map(Path::toFile).forEach(File::delete);
    }

    @Test
    public void testRead() throws IOException {
        var path = dir.resolve("test.txt");
        assertFalse(Files.exists(path));
        var storage = new FileStorage(path.toString());
        assertThrows(RuntimeException.class, storage::read);

        Files.createFile(path);
        assertTrue(Files.exists(path));
        assertArrayEquals(Files.readAllBytes(path), storage.read().readAllBytes());

        Files.writeString(path, "Hello, World!");
        assertArrayEquals(Files.readAllBytes(path), storage.read().readAllBytes());
    }

    @Test
    public void testWrite() throws IOException {
        var path = dir.resolve("test.txt");
        assertFalse(Files.exists(path));
        var storage = new FileStorage(path.toString());
        Files.createFile(path);
        assertTrue(Files.exists(path));
        var expected = "Hello, World!".getBytes();
        storage.write(new ByteArrayInputStream(expected));
        assertArrayEquals(expected, Files.readAllBytes(path));
    }

}
