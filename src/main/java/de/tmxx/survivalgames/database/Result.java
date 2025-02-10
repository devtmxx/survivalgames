package de.tmxx.survivalgames.database;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Getter
public class Result {
    private final List<Row> rows = new ArrayList<>();

    public void addRow(Row row) {
        rows.add(row);
    }

    public static Result from(ResultSet resultSet) {
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
}