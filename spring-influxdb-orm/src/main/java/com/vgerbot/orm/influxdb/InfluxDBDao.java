package com.vgerbot.orm.influxdb;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface InfluxDBDao<Measurement extends Serializable> {
	/**
	 * 插入一条数据
	 * 
	 * @param measurement
	 */
	void insert(Measurement measurement);

	/**
	 * 批量插入
	 * 
	 * @param measurement
	 */
	void batchInsert(Collection<Measurement> measurement);

	/**
	 * 查询所有
	 * 
	 * @return
	 * @deprecated 使用{@link #select(Map)}方法查询
	 */
	@Deprecated
	List<Measurement> selectAll();

	/**
	 * 按条件查询
	 * 
	 * @param tagCondition
	 *            tagField查询条件
	 * @return
	 */
	List<Measurement> select(Map<String, Object> tagCondition);

	/**
	 * 按时间范围查询
	 * 
	 * @param timeStart
	 *            起始时间， 可以为空
	 * @param timeEnd
	 *            结束时间， 可以为空
	 * @param tagCondition
	 *            tagField查询条件
	 * @return
	 */
	List<Measurement> selectBetween(Date timeStart, Date timeEnd, Map<String, Object> tagCondition);

	/**
	 * 查询{@code time}之后的数据
	 * 
	 * @param time
	 * @param tagCondition
	 *            tagField查询条件
	 * @return
	 */
	List<Measurement> selectAfter(Date time, Map<String, Object> tagCondition);

	/**
	 * 查询{@code time}之前的数据
	 * 
	 * @param time
	 * @param tagCondition
	 *            tagField查询条件
	 * @return
	 */
	List<Measurement> selectBefore(Date time, Map<String, Object> tagCondition);
}
