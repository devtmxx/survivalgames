package de.tmxx.survivalgames.stats.database;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * <p>
 *     Compiles the result of a database query in to a better accessible result.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Getter
public class Result {
    private final List<Row> rows = new ArrayList<>();

    private Result() {}

    /**
     * Adds a row to the data set.
     *
     * @param row the row to add
     */
    public void addRow(Row row) {
        rows.add(row);
    }

    /**
     * Creates a result from a specified {@link ResultSet}. The generated result will never be null but may be empty.
     * Throws a {@link RuntimeException} if an error occurs while reading the result set.
     *
     * @param resultSet the result set
     * @return the compiled result
     */
    public static @NotNull Result from(ResultSet resultSet) {
        Result result = new Result();

        if (resultSet == null) return result;
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();

            while (resultSet.next()) {
                Row row = new Row();

                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    String name = metaData.getColumnName(i + 1);
                    row.insertData(name, resultSet.getObject(name));
                }

                result.addRow(row);
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an empty result.
     *
     * @return empty result
     */
    public static @NotNull Result empty() {
        return new Result();
    }
}