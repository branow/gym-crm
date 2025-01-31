package dev.branow.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum TrainingType {
    CARDIO("cardio"),
    CROSSFIT("crossfit"),
    YOGA("yoga"),
    PILATES("pilates"),
    BODYBUILDING("bodybuilding");

    String name;
}
