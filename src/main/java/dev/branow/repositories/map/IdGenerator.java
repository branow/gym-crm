package dev.branow.repositories.map;

import dev.branow.repositories.Repository;
import org.springframework.stereotype.Component;

public interface IdGenerator<ID> {
    ID generate(Repository<ID, ?> repository);

    @Component
    class IncrementIdGenerator implements IdGenerator<Long> {

        public Long generate(Repository<Long, ?> repository) {
            var max = repository.findIdAll()
                    .max(Long::compareTo)
                    .orElse(0L);
            return max + 1;
        }

    }
}

