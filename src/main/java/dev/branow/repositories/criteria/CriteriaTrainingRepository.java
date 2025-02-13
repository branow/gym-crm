package dev.branow.repositories.criteria;

import dev.branow.model.Training;
import dev.branow.repositories.TrainingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
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

        List<Predicate> predicates = new ArrayList<>();

        ifPresent(traineeUsername, username ->
                predicates.add(builder.equal(root.get("trainee").get("username"), username)));
        ifPresent(trainerUsername, username ->
                predicates.add(builder.equal(root.get("trainer").get("username"), username)));
        ifPresent(from, date ->
                predicates.add(builder.greaterThanOrEqualTo(root.get("date"), date)));
        ifPresent(to, date ->
                predicates.add(builder.lessThanOrEqualTo(root.get("date"), date)));
        ifPresent(typeId, id ->
                predicates.add(builder.equal(root.get("type").get("id"), id)));

        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(new Predicate[0]));
        }

        var query = entityManager.createQuery(cq);
        return query.getResultList();
    }

    private static<T> void ifPresent(T entity, Consumer<T> consumer) {
        Optional.ofNullable(entity).ifPresent(consumer);
    }

}
