package de.tmxx.survivalgames.database;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * <p>
 *     Displays a specified row in a database result.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
public class Row {
    // the default values to use if a requested data is not present
    public static final boolean DEFAULT_BOOLEAN = false;
    public static final byte DEFAULT_BYTE = -1;
    public static final int DEFAULT_INT = -1;
    public static final float DEFAULT_FLOAT = -1F;
    public static final double DEFAULT_DOUBLE = -1D;
    public static final long DEFAULT_LONG = -1L;
    public static final String DEFAULT_STRING = "";

    // a map of all queried data
    private final Map<String, Object> data = new HashMap<>();

    /**
     * Inserts data into the row by the name of the data.
     *
     * @param name the name
     * @param data the data
     */
    protected void insertData(String name, Object data) {
        this.data.put(name, data);
    }

    /**
     * Checks if the requested data is present within this row.
     *
     * @param name the name of the data
     * @return true, if the data is present
     */
    public boolean hasData(String name) {
        return data.containsKey(name);
    }

    /**
     * Retrieves the requested data from the row. Returns null if the data is not present.
     *
     * @param name the name of the data
     * @return the data or null
     */
    public @Nullable Object getData(String name) {
        return data.get(name);
    }

    /**
     * Retrieves the requested data from the row and casts it as {@link Boolean} if possible. Otherwise, returns
     * {@link #DEFAULT_BOOLEAN}.
     *
     * @param name the name of the data
     * @return the data
     */
    public boolean getBoolean(String name) {
        Object object = data.get(name);
        if (object instanceof Boolean value) return value;
        return DEFAULT_BOOLEAN;
    }

    /**
     * Retrieves the requested data from the row and casts it as {@link Byte} if possible. Otherwise, returns
     * {@link #DEFAULT_BYTE}.
     *
     * @param name the name of the data
     * @return the data
     */
    public byte getByte(String name) {
        Object object = data.get(name);
        if (object instanceof Byte value) return value;
        return DEFAULT_BYTE;
    }

    /**
     * Retrieves the requested data from the row and casts it as {@link Integer} if possible. Otherwise, returns
     * {@link #DEFAULT_INT}.
     *
     * @param name the name of the data
     * @return the data
     */
    public int getInt(String name) {
        Object object = data.get(name);
        if (object instanceof Integer value) return value;
        return DEFAULT_INT;
    }

    /**
     * Retrieves the requested data from the row and casts it as {@link Float} if possible. Otherwise, returns
     * {@link #DEFAULT_FLOAT}.
     *
     * @param name the name of the data
     * @return the data
     */
    public float getFloat(String name) {
        Object object = data.get(name);
        if (object instanceof Float value) return value;
        return DEFAULT_FLOAT;
    }

    /**
     * Retrieves the requested data from the row and casts it as {@link Double} if possible. Otherwise, returns
     * {@link #DEFAULT_DOUBLE}.
     *
     * @param name the name of the data
     * @return the data
     */
    public double getDouble(String name) {
        Object object = data.get(name);
        if (object instanceof Double value) return value;
        return DEFAULT_DOUBLE;
    }

    /**
     * Retrieves the requested data from the row and casts it as {@link Long} if possible. This also includes
     * {@link Timestamp} and {@link BigDecimal}. Otherwise, returns {@link #DEFAULT_LONG}.
     *
     * @param name the name of the data
     * @return the data
     */
    public long getLong(String name) {
        Object object = data.get(name);
        return switch (object) {
            case Long value -> value;
            case Timestamp timestamp -> timestamp.getTime();
            case BigDecimal bigDecimal -> bigDecimal.longValue();
            case null, default -> DEFAULT_LONG;
        };
    }

    /**
     * Retrieves the requested data from the row and casts it as {@link String} if possible. Otherwise, returns
     * {@link #DEFAULT_STRING}.
     *
     * @param name the name of the data
     * @return the data
     */
    public String getString(String name) {
        Object object = data.get(name);
        if (object == null) return DEFAULT_STRING;
        if (object instanceof String value) return value;
        return DEFAULT_STRING;
    }
}
