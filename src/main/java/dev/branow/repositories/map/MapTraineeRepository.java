package dev.branow.repositories.map;

import dev.branow.model.Trainee;
import dev.branow.repositories.TraineeRepository;
import dev.branow.storage.KeyValueStorage;
import dev.branow.storage.Reference;
import org.springframework.stereotype.Repository;

@Repository
public class MapTraineeRepository extends MapRepository<Long, Trainee> implements TraineeRepository {

    public MapTraineeRepository(KeyValueStorage storage, IdGenerator<Long> idGenerator) {
        super("trainees", new Reference<>() {}, storage, idGenerator);
    }

    @Override
    protected Long getId(Trainee value) {
        return value.getUserId();
    }

    @Override
    protected void setId(Long id, Trainee value) {
        value.setUserId(id);
    }
}
