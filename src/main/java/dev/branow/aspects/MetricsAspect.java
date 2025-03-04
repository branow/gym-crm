package dev.branow.aspects;

import dev.branow.dtos.service.CreateTrainingDto;
import dev.branow.monitoring.metrics.TraineeRegistrationCounter;
import dev.branow.monitoring.metrics.TrainerRegistrationCounter;
import dev.branow.monitoring.metrics.TrainingDurationSummary;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {

    private final TraineeRegistrationCounter traineeRegistrationCounter;
    private final TrainerRegistrationCounter trainerRegistrationCounter;
    private final TrainingDurationSummary trainingDurationSummary;

    @AfterReturning("execution(* dev.branow.services.TraineeService.create(*))")
    public void afterTraineeRegistration(JoinPoint joinPoint) {
        traineeRegistrationCounter.increment();
    }

    @AfterReturning("execution(* dev.branow.services.TrainerService.create(*))")
    public void afterTrainerRegistration(JoinPoint joinPoint) {
        trainerRegistrationCounter.increment();
    }

    @AfterReturning("execution(* dev.branow.services.TrainingService.create(*))")
    public void afterTrainingCreation(JoinPoint joinPoint) {
        var training = (CreateTrainingDto) joinPoint.getArgs()[0];
        trainingDurationSummary.recordTrainingDuration(training.getDuration());
    }

}
