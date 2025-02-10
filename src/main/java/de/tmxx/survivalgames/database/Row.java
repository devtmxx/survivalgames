package de.tmxx.survivalgames.database;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class Row {
    public static final boolean DEFAULT_BOOLEAN = false;
    public static final byte DEFAULT_BYTE = -1;
    public static final int DEFAULT_INT = -1;
    public static final float DEFAULT_FLOAT = -1F;
    public static final double DEFAULT_DOUBLE = -1D;
    public static final long DEFAULT_LONG = -1L;
    public static final String DEFAULT_STRING = "";

    private final Map<String, Object> data = new HashMap<>();

    protected void insertData(String name, Object data) {
        this.data.put(name, data);
    }

    public boolean hasData(String name) {
        return data.containsKey(name);
    }

    public Object getData(String name) {
        return data.get(name);
    }

    public boolean getBoolean(String name) {
        Object object = data.get(name);
        if (object instanceof Boolean value) return value;
        return DEFAULT_BOOLEAN;
    }

    public byte getByte(String name) {
        Object object = data.get(name);
        if (object instanceof Byte value) return value;
        return DEFAULT_BYTE;
    }

    public int getInt(String name) {
        Object object = data.get(name);
        if (object instanceof Integer value) return value;
        return DEFAULT_INT;
    }

    public float getFloat(String name) {
        Object object = data.get(name);
        if (object instanceof Float value) return value;
        return DEFAULT_FLOAT;
    }

    public double getDouble(String name) {
        Object object = data.get(name);
        if (object instanceof Double value) return value;
        return DEFAULT_DOUBLE;
    }

    public long getLong(String name) {
        Object object = data.get(name);
        return switch (object) {
            case Long value -> value;
            case Timestamp timestamp -> timestamp.getTime();
            case BigDecimal bigDecimal -> bigDecimal.longValue();
            case null, default -> DEFAULT_LONG;
        };
    }

    public String getString(String name) {
        Object object = data.get(name);
        if (object == null) return DEFAULT_STRING;
        if (object instanceof String value) return value;
        return DEFAULT_STRING;
    }
}
