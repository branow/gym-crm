package dev.branow.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import dev.branow.storage.FileStorage;
import dev.branow.storage.JsonStorage;
import dev.branow.storage.KeyValueStorage;
import dev.branow.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class StorageConfig {

    @Bean
    public FileStorage fileStorage(@Value("${storage.file}") String path) {
        return new FileStorage(path);
    }

    @Bean
    public KeyValueStorage keyValueStorage(Storage storage) {
        return new JsonStorage(storage,
                (mapper) -> mapper.enable(SerializationFeature.INDENT_OUTPUT));
    }

}
