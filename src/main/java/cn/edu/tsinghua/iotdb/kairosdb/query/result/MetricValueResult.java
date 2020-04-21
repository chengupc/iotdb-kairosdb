package cn.edu.tsinghua.iotdb.kairosdb.query.result;

import cn.edu.tsinghua.iotdb.kairosdb.query.aggregator.QueryAggregatorAlign;
import cn.edu.tsinghua.iotdb.kairosdb.query.group_by.GroupBy;
import com.google.gson.annotations.SerializedName;
import java.sql.Types;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricValueResult {

  @SerializedName("name")
  private String name;

  @SerializedName("group_by")
  private List<GroupBy> groupBy;

  @SerializedName("tags")
  private Map<String, List<String>> tags;

  @SerializedName("values")
  private List<QueryDataPoint> values;

  public MetricValueResult(String name) {
    this.name = name;
    groupBy = Collections.synchronizedList(new LinkedList<>());
    tags = new ConcurrentHashMap<>();
    values = Collections.synchronizedList(new LinkedList<>());
  }

  public void sort() {
    values.sort(new Comparator<QueryDataPoint>() {
      @Override
      public int compare(QueryDataPoint o1, QueryDataPoint o2) {
        return (int) (o1.getTimestamp() - o2.getTimestamp());
      }
    });
  }

  public List<List<QueryDataPoint>> splitDataPoint(
      long startTimestamp, long step, QueryAggregatorAlign align) {
    if (align == QueryAggregatorAlign.ALIGN_SAMPLING) {
      return splitDataPoint(getDatapoints().get(0).getTimestamp(), step);
    }
    return splitDataPoint(startTimestamp, step);
  }

  public List<List<QueryDataPoint>> splitDataPoint(long startTimestamp, long step) {
    List<List<QueryDataPoint>> result = new LinkedList<>();

    List<QueryDataPoint> tmpList = new LinkedList<>();

    Long curTimestamp = startTimestamp + step;

    for (QueryDataPoint point : values) {
      if (point.getTimestamp() < startTimestamp) {
        continue;
      }
      if (point.getTimestamp() < curTimestamp) {
        tmpList.add(point);
      } else {
        if (!tmpList.isEmpty()) {
          result.add(tmpList);
          tmpList = new LinkedList<>();
        }
        curTimestamp += ((point.getTimestamp() - curTimestamp) / step + 1) * step;
        tmpList.add(point);
      }
    }
    if (!tmpList.isEmpty()) {
      result.add(tmpList);
    }

    return result;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void addGroupBy(GroupBy groupBy) {
    if (this.groupBy == null) {
      this.groupBy = Collections.synchronizedList(new LinkedList<>());
    }
    if (groupBy == null) {
      return;
    }
    this.groupBy.add(groupBy);
  }

  public List<GroupBy> getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(List<GroupBy> groupBy) {
    this.groupBy = groupBy;
  }

  public Map<String, List<String>> getTags() {
    return tags;
  }

  public void setTags(Map<String, List<String>> tags) {
    this.tags = tags;
  }

  public void addTag(String key, String value) {
    List<String> list = tags.get(key);
    if (list == null) {
      list = new LinkedList<>();
    }
    list.add(value);
    tags.put(key, list);
  }

  public void setTag(String key, List<String> tags) {
    this.tags.put(key, tags);
  }

  public void addDataPoint(QueryDataPoint point) {
    if (point == null) {
      return;
    }
    values.add(point);
  }

  public List<QueryDataPoint> getDatapoints() {
    return values;
  }

  public void setValues(List<QueryDataPoint> values) {
    this.values = values;
  }

  public boolean isTextType() {
    if (getDatapoints().isEmpty()) {
      return false;
    }
    return getDatapoints().get(0).getType() == Types.VARCHAR;
  }

}
