package com.smartcity.search;

import java.util.List;

/**
 * Strategy interface for searching city information.
 */
public interface SearchStrategy {

    List search(String query);
}
