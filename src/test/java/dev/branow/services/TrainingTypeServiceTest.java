package dev.branow.services;

import dev.branow.DBTest;
import dev.branow.mappers.TrainingTypeMapper;
import dev.branow.model.TrainingType;
import dev.branow.repositories.TrainingTypeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig({
        TrainingTypeService.class,
        TrainingTypeRepository.class,
        TrainingTypeMapper.class,
})
@ExtendWith(MockitoExtension.class)
public class TrainingTypeServiceTest extends DBTest {

    @Autowired
    private EntityManager manager;
    @Autowired
    private TrainingTypeMapper mapper;
    @Autowired
    private TrainingTypeService service;

    @Test
    public void getAll() {
        var query = String.format("select t from %s t", TrainingType.class.getName());
        var expected = manager.createQuery(query, TrainingType.class).getResultList()
                .stream().map(mapper::toTrainingTypeDto).toList();
        var actual = service.getAll();
        assertEquals(expected, actual);
    }

}
