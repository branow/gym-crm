package dev.branow.repositories;

import dev.branow.model.Training;

public interface TrainingRepository extends Repository<Long, Training> {
    void deleteAllByTraineeId(Long traineeId);
    void deleteAllByTrainerId(Long trainerId);
}
