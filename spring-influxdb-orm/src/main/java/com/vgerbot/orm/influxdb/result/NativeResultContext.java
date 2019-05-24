package com.vgerbot.orm.influxdb.result;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import com.vgerbot.orm.influxdb.InfluxDBException;
import com.vgerbot.orm.influxdb.utils.StringUtils;

public class NativeResultContext implements ResultContext {

	private static final Map<String, List<Map<String, Object>>> EMPTY_SERIES_MAP = Collections.unmodifiableMap(new HashMap<>());
	private static final Set<String> EMPTY_SET = Collections.emptySet();

	private String errorMessage;
	private boolean hasResult;
	private Set<String> measurementNames;
	private Map<String, List<Map<String, Object>>> seriesMap;

	public NativeResultContext(QueryResult queryResult) {
		this.errorMessage = queryResult.getError();
		List<Result> results = queryResult.getResults();
		if (isNotEmpty(results)) {
			if (results.size() > 1) {
				throw new InfluxDBException("Multiple results is not suppported!");
			}

			Result result = results.get(0);
			if (StringUtils.isNotBlank(result.getError())) {
				errorMessage = result.getError();
				hasResult = false;
			} else if (isNotEmpty(result.getSeries())) {
				hasResult = true;
				seriesMap = transformResult(result);
			}
		}
		if (!hasResult) {
			measurementNames = EMPTY_SET;
			seriesMap = EMPTY_SERIES_MAP;
		}
	}

	public boolean hasResult() {
		return hasResult;
	}

	public boolean hasError() {
		return StringUtils.isNotBlank(errorMessage);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public Object getValue() {
		return seriesMap;
	}

	public Set<String> getMeasurementNames() {
		return measurementNames;
	}

	private Map<String, List<Map<String, Object>>> transformResult(Result result) {
		List<Series> seriesList = result.getSeries();
		if (isNotEmpty(seriesList)) {
			Set<String> measurementNames = new HashSet<>();
			Map<String, List<Map<String, Object>>> seriesMap = new HashMap<>();
			for (Series series : seriesList) {
				measurementNames.add(series.getName());
				List<Map<String, Object>> dataList = seriesMap.get(series.getName());
				if (dataList == null) {
					dataList = new LinkedList<>();
					seriesMap.put(series.getName(), dataList);
				}
				transformTo(series, dataList);
			}
			return seriesMap;
		}
		return EMPTY_SERIES_MAP;
	}

	private void transformTo(Series series, List<Map<String, Object>> dataList) {
		List<String> columns = series.getColumns();
		List<List<Object>> valuesList = series.getValues();
		if (isNotEmpty(columns) && isNotEmpty(valuesList)) {
			int columnCount = columns.size();
			for (List<Object> values : valuesList) {
				Map<String, Object> data = new HashMap<String, Object>();
				for (int i = 0; i < columnCount; i++) {
					String columnName = columns.get(i);
					Object value = values.get(i);
					data.put(columnName, value);
				}
				dataList.add(data);
			}
		}
	}

	private static final boolean isNotEmpty(Collection<?> coll) {
		return !isEmpty(coll);
	}

	private static final boolean isEmpty(Collection<?> col) {
		return col == null || col.isEmpty();
	}
}
