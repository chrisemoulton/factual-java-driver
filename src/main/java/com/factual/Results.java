package com.factual;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * Represents the results of running a query against Factual.
 *
 * @author aaron
 */
public class Results {
  private String version;
  private String status;
  private int totalRowCount;
  private List<Map<String, Object>> data = Lists.newArrayList();


  public String getStatus() {
    return status;
  }

  public String getVersion() {
    return version;
  }

  public List<Map<String, Object>> getData() {
    return data;
  }

  /**
   * @return total underlying row count, or -1 if unknown.
   */
  public int getTotalRowCount() {
    return totalRowCount;
  }

  /**
   * @return a Collection of all String values found in these Results rows as
   *         the <tt>attr</tt> attribute.
   */
  public Collection<String> mapStrings(final String attr) {
    return Collections2.transform(data, new Function<Map<String, Object>, String>() {
      @Override
      public String apply(Map<String, Object> row) {
        Object val = row.get(attr);
        return val != null ? val.toString() : null;
      }});
  }

  public static Results fromJson(String json) {
    Results results = new Results();
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode root = mapper.readValue(json, JsonNode.class);
      results.version = root.get("version").toString();
      results.status = root.get("status").toString();

      results.totalRowCount = root.get("response").get("total_row_count").asInt(-1);

      results.data = data(root.get("response").get("data").getElements());
      return results;
    } catch (JsonParseException e) {
      throw new RuntimeException(e);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static List<Map<String, Object>> data(Iterator<JsonNode> rowNodes) {
    List<Map<String, Object>> data = Lists.newArrayList();
    while(rowNodes.hasNext()) {
      data.add(row(rowNodes.next()));
    }
    return data;
  }

  private static Map<String, Object> row(JsonNode rowNode) {
    Map<String, Object> row = Maps.newHashMap();
    Iterator<Map.Entry<String, JsonNode>> entries = rowNode.getFields();
    while(entries.hasNext()) {
      Map.Entry<String, JsonNode> entry = entries.next();
      row.put(entry.getKey(), entry.getValue());
    }
    return row;
  }

}
