package com.smartcity.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BaseEntityTest {

    private static class TestEntity extends BaseEntity {
        // Concrete subclass for testing inherited behavior
    }

    @Test
    void shouldInheritIdFromBaseEntity() {
        TestEntity entity = new TestEntity();

        entity.setId(1L);

        assertEquals(1L, entity.getId());
    }

    @Test
    void shouldHaveNullIdInitially() {
        TestEntity entity = new TestEntity();

        assertNull(entity.getId());
    }
}
