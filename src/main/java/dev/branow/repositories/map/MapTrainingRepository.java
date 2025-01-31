package dev.branow.repositories.map;

import dev.branow.model.Training;
import dev.branow.repositories.TrainingRepository;
import dev.branow.storage.KeyValueStorage;
import dev.branow.storage.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class MapTrainingRepository extends MapRepository<Long, Training> implements TrainingRepository {

    public MapTrainingRepository(KeyValueStorage storage, IdGenerator<Long> idGenerator) {
        super("trainings", new Reference<>() {}, storage, idGenerator);
    }

    @Override
    public void deleteAllByTraineeId(Long traineeId) {
        log.debug("Deleting all trainings by traineeId {}", traineeId);
        deleteAllByCondition(entry -> entry.getValue().getTraineeId().equals(traineeId));
    }

    @Override
    public void deleteAllByTrainerId(Long trainerId) {
        log.debug("Deleting all trainings by trainerId {}", trainerId);
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
