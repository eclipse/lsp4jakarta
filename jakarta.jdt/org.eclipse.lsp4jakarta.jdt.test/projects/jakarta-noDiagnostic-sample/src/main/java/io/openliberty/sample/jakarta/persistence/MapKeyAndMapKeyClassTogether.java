package io.openliberty.sample.jakarta.persistence;

import java.util.HashMap;
import java.util.Map;

import jakarta.fake.MapKey;
import jakarta.fake.MapKeyClass;

public class MapKeyAndMapKeyClassTogether {
    @MapKey()
    @MapKeyClass(Map.class)
    Map<Integer, String> testMap = new HashMap<>();
    
    
    @MapKey()
    @MapKeyClass(Map.class)
    public Map<Integer, String> getTestMap(){
    	return this.testMap;
    }
}
