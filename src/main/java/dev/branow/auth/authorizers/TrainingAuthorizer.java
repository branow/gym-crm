package dev.branow.auth.authorizers;

import dev.branow.auth.Authorizer;
import dev.branow.dtos.CriteriaTrainingTraineeDto;
import dev.branow.dtos.CriteriaTrainingTrainerDto;

public interface TrainingAuthorizer<T> extends Authorizer<T> {

    interface CriteriaTraineeDto extends TrainingAuthorizer<CriteriaTrainingTraineeDto> {}

    interface CriteriaTrainerDto extends TrainingAuthorizer<CriteriaTrainingTrainerDto> {}

}
