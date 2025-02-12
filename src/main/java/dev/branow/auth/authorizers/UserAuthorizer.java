package dev.branow.auth.authorizers;

import dev.branow.auth.Authorizer;

public interface UserAuthorizer<T> extends Authorizer<T> {

    interface Id extends UserAuthorizer<Long> {}

    interface Username extends UserAuthorizer<String> {}

    interface UpdateTraineeDto extends UserAuthorizer<dev.branow.dtos.UpdateTraineeDto> {}

    interface UpdateTrainerDto extends UserAuthorizer<dev.branow.dtos.UpdateTrainerDto> {}

}
