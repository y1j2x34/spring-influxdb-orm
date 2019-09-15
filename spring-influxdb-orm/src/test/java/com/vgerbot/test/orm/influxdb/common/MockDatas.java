package com.vgerbot.test.orm.influxdb.common;

import org.influxdb.dto.QueryResult;

import java.util.Arrays;
import java.util.List;

public class MockDatas {
    public static QueryResult.Series createSeries(String measurementName, List<String> columns, List<List<Object>> values) {
        QueryResult.Series series = new QueryResult.Series();
        series.setColumns(columns);
        series.setName(measurementName);
        series.setValues(values);
        return series;
    }
    public static QueryResult createSingleMeasurementQueryResult(String measurementName, List<String> columns, List<List<Object>> values) {
        QueryResult.Result queryResultItem = new QueryResult.Result();
        queryResultItem.setSeries(
                Arrays.asList(createSeries(measurementName, columns, values))
        );
        QueryResult result = new QueryResult();
        result.setResults(Arrays.asList(
                queryResultItem
        ));
        return result;
    }
}
