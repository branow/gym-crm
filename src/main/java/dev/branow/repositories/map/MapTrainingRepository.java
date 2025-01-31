package dev.branow.repositories.map;

import dev.branow.model.Training;
import dev.branow.repositories.TrainingRepository;
import dev.branow.storage.KeyValueStorage;
import dev.branow.storage.Reference;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MapTrainingRepository extends MapRepository<Long, Training> implements TrainingRepository {

    public MapTrainingRepository(KeyValueStorage storage, IdGenerator<Long> idGenerator) {
        super("trainings", new Reference<>() {}, storage, idGenerator);
    }

    @Override
    public void deleteAllByTraineeId(Long traineeId) {
        deleteAllByCondition(entry -> entry.getValue().getTraineeId().equals(traineeId));
    }

    @Override
    public void deleteAllByTrainerId(Long trainerId) {
        deleteAllByCondition(entry -> entry.getValue().getTrainerId().equals(trainerId));
   }

    @Override
    protected Long getId(Training training) {
        return training.getTrainingId();
    }

    @Override
    protected void setId(Long id, Training training) {
        training.setTrainingId(id);
    }

}
