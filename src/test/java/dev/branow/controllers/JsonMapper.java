package dev.branow.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;

public class JsonMapper {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
       mapper.findAndRegisterModules();
    }

    public static<T> T getNode(Path path, String key, Class<T> type) {
        try {
            var tree = mapper.readTree(path.toFile());
            var node = tree.get(key);
            return mapper.convertValue(node, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNode(Path path, String key) {
        try {
            var tree = mapper.readTree(path.toFile());
            var node = tree.get(key);
            return node.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object object) {
        try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, object);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            var reader = new StringReader(json);
            return mapper.readValue(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
