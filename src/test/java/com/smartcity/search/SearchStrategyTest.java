package com.smartcity.search;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SearchStrategyTest {

    private static SearchStrategy sample() {
        return new SearchStrategy() {
            @Override
            public List search(String query) {
                return List.of("Example result");
            }
        };
    }

    @Test
    void canCreateConcreteImplementation() {
        SearchStrategy strategy = sample();

        assertNotNull(strategy);
    }

    @Test
    void searchReturnsExpectedResults() {
        SearchStrategy strategy = sample();

        assertEquals(
            List.of("Example result"),
            strategy.search("example")
        );
    }
}
