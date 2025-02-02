package dev.branow.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Consumer;

public class JsonStorage implements KeyValueStorage {

    private final Storage storage;
    private final ObjectMapper mapper;

    public JsonStorage(Storage storage) {
        this(storage, null);
    }

    public JsonStorage(Storage storage, Consumer<ObjectMapper> customizer) {
        this.storage = storage;
        this.mapper = new ObjectMapper();
        this.mapper.findAndRegisterModules();
        if (customizer != null) {
            customizer.accept(mapper);
        }
    }

    public<T> Optional<T> get(String key, Reference<T> reference) {
        try (var is = storage.read()) {
            var rootNode = safeRead(is);
            var node = rootNode.get(key);
            if (node == null || node.isNull()) {
                return Optional.empty();
            }
            return Optional.of(convert(node, reference));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read JSON storage", e);
        }
    }

    public<T> void save(String key, T value) {
        try {
            var rootNode = readOrCreateObjectNode();
            rootNode.set(key, mapper.valueToTree(value));
            writeToStorage(rootNode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to JSON storage", e);
        }
    }

    private<T> T convert(JsonNode node, Reference<T> reference) {
        return mapper.convertValue(node, new TypeReference<T>() {
            @Override
            public Type getType() {
                return reference.getType();
            }
        });
    }

    private ObjectNode readOrCreateObjectNode() throws IOException {
        try (var is = storage.read()) {
            var rootNode = safeRead(is);
            if (!(rootNode instanceof ObjectNode)) {
                throw new IllegalStateException("Invalid JSON structure. Expected ObjectNode at root");
            }
            return (ObjectNode) rootNode;
        }
    }

    private void writeToStorage(JsonNode rootNode) throws IOException {
        try (var baos = new ByteArrayOutputStream()) {
            mapper.writeValue(baos, rootNode);
            try (var bais = new ByteArrayInputStream(baos.toByteArray())) {
                storage.write(bais);
            }
        }
    }

    private JsonNode safeRead(InputStream is) throws IOException {
        return is.available() == 0 ? mapper.createObjectNode() : mapper.readTree(is);
    }

}
