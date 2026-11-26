package com.smartcity.search;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class SearchServiceTest {

    private static SearchService sample() {
        SearchStrategy strategy = new SearchStrategy() {
            @Override
            public List search(String query) {
                return List.of();
            }
        };

        return new SearchService(strategy);
    }

    @Test
    void constructsValidInstance() {
        SearchService service = sample();
        assertNotNull(service);
    }
}
