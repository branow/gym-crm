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
        this.storage = storage;
        this.mapper = new ObjectMapper();
    }

    public JsonStorage(Storage storage, Consumer<ObjectMapper> customizer) {
        this.storage = storage;
        this.mapper = new ObjectMapper();
        customizer.accept(mapper);
    }

    public<T> Optional<T> get(String key, Reference<T> reference) {
        try (var is = storage.read()) {
            var rootNode = safeRead(is);
            var node = rootNode.get(key);
            if (node == null || node.isNull()) {
                return Optional.empty();
            }
            var value = mapper.convertValue(node, new TypeReference<T>() {
                @Override
                public Type getType() {
                    return reference._type;
                }
            });
            return Optional.of(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public<T> void save(String key, T value) {
        try {
            JsonNode rootNode;

            try (var is = storage.read()) {
                rootNode = safeRead(is);
            }

            if (!(rootNode instanceof ObjectNode)) {
                throw new RuntimeException("Invalid JSON structure. Expected ObjectNode at root");
            }

            ObjectNode objectNode = (ObjectNode) rootNode;
            objectNode.set(key, mapper.valueToTree(value));

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                mapper.writeValue(baos, objectNode);
                try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
                    storage.write(bais);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode safeRead(InputStream is) throws IOException {
        return is.available() == 0 ? mapper.createObjectNode() : mapper.readTree(is);
    }

}
