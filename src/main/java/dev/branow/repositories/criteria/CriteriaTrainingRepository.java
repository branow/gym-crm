package dev.branow.repositories.criteria;

import dev.branow.model.Training;
import dev.branow.repositories.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Repository
public class CriteriaTrainingRepository extends SimpleJpaRepository<Training, Long> implements TrainingRepository {

    private final EntityManager entityManager;

    public CriteriaTrainingRepository(EntityManager entityManager) {
        super(Training.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<Training> findAllByCriteria(
            String traineeUsername,
            String trainerUsername,
            LocalDate from,
            LocalDate to,
            Long typeId
    ) {
        var builder = entityManager.getCriteriaBuilder();
        var cq = builder.createQuery(Training.class);

        Root<Training> root = cq.from(Training.class);
        cq.select(root);

        ifPresent(traineeUsername, username -> cq.where(builder.equal(root.get("trainee").get("username"), username)));
        ifPresent(trainerUsername, username -> cq.where(builder.equal(root.get("trainer").get("username"), username)));
        ifPresent(from, date -> cq.where(builder.greaterThanOrEqualTo(root.get("date"), date)));
        ifPresent(to, date -> cq.where(builder.lessThanOrEqualTo(root.get("date"), date)));
        ifPresent(typeId, id -> cq.where(builder.equal(root.get("type").get("id"), id)));

        var query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    private static<T> void ifPresent(T entity, Consumer<T> consumer) {
        Optional.ofNullable(entity).ifPresent(consumer);
    }

}
