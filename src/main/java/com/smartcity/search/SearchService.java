package com.smartcity.search;

import com.smartcity.entity.BaseEntity;
import java.util.List;

/**
 * SearchService - generated from the UML diagram by diagram2app.
 */
public class SearchService {

    private SearchStrategy strategy;

    public SearchService(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public SearchStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(SearchStrategy strategy) {
        this.strategy = strategy;
    }

    public List<BaseEntity> search(String query) {
        // TODO(diagram2app): the behaviour of SearchService.search() is not specified in the diagram.
        throw new UnsupportedOperationException("SearchService.search() is not implemented: behaviour not specified");
    }
}
