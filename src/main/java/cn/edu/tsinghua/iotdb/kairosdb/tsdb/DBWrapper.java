package cn.edu.tsinghua.iotdb.kairosdb.tsdb;

import cn.edu.tsinghua.iotdb.kairosdb.http.rest.json.DataPointsParser.DataType;
import cn.edu.tsinghua.iotdb.kairosdb.query.QueryMetric;
import cn.edu.tsinghua.iotdb.kairosdb.query.result.MetricValueResult;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBWrapper implements IDatabase {
  private static final Logger LOGGER = LoggerFactory.getLogger(DBWrapper.class);
  private IDatabase db;

  public DBWrapper(String dbType, boolean isSession, String url) {
    DBFactory dbFactory = new DBFactory();
    try {
      db = dbFactory.getDatabase(dbType, isSession, url);
    } catch (Exception e) {
      LOGGER.error("Failed to get database because", e);
    }
  }

  @Override
  public void insert(String deviceId, long timestamp, List<String> measurements,
      List<String> values) {
    db.insert(deviceId, timestamp, measurements, values);
  }

  @Override
  public void rangeQuery(String sql, long metricCount, AtomicLong sampleSize,
      MetricValueResult metricValueResult, AtomicBoolean hasMetaData, QueryMetric metric) {
    db.rangeQuery(sql, metricCount, sampleSize, metricValueResult, hasMetaData, metric);
  }

  @Override
  public void createTimeSeries(Map<String, DataType> seriesPaths) throws SQLException {
    db.createTimeSeries(seriesPaths);
  }

  @Override
  public void executeSQL(String sql) throws SQLException {
    db.executeSQL(sql);
  }

  @Override
  public void addSaveFromData(MetricValueResult valueResult, String path, String metricName)
      throws SQLException {
    db.addSaveFromData(valueResult, path, metricName);
  }

  @Override
  public void deleteMetric(Map<String, Map<String, Integer>> tagOrder, String metricName)
      throws SQLException {
    db.deleteMetric(tagOrder, metricName);
  }

  @Override
  public void delete(String querySql) {
    db.delete(querySql);
  }

  @Override
  public long getValueResult(String sql, MetricValueResult metricValueResult) {
    return db.getValueResult(sql, metricValueResult);
  }
}