package dev.branow.security.authorization;

public interface UserAuthorizer<T> extends Authorizer<T> {

    interface Username extends UserAuthorizer<String> {}

    interface CreateTrainingRequest extends UserAuthorizer<dev.branow.dtos.request.CreateTrainingRequest> {}

}
