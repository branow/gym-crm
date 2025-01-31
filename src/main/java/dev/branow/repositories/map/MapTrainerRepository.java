package dev.branow.repositories.map;

import dev.branow.model.Trainer;
import dev.branow.repositories.TrainerRepository;
import dev.branow.storage.KeyValueStorage;
import dev.branow.storage.Reference;
import org.springframework.stereotype.Repository;

@Repository
public class MapTrainerRepository extends MapRepository<Long, Trainer> implements TrainerRepository {

    public MapTrainerRepository(KeyValueStorage storage, IdGenerator<Long> idGenerator) {
        super("trainers", new Reference<>() {}, storage, idGenerator);
    }

    @Override
    protected Long getId(Trainer value) {
        return value.getUserId();
    }

    @Override
    protected void setId(Long id, Trainer value) {
        value.setUserId(id);
    }

}
