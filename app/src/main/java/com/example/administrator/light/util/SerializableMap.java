package com.example.administrator.light.util;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by JO on 2016/5/21.
 */
public class SerializableMap implements Serializable {
    private Map<String, Object> map;
    public Map<String, Object> getMap() {
        return  map;
    }
    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
