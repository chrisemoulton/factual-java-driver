package com.factual.driver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Maps;

public class ResolveQuery {
  Map<String, Object> values = Maps.newHashMap();


  public ResolveQuery add(String key, Object val) {
    values.put(key, val);
    return this;
  }

  protected String toUrlQuery() {
    return urlPair("values", toJsonStr(values));
  }

  private String toJsonStr(Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (JsonGenerationException e) {
      throw new RuntimeException(e);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String urlPair(String name, Object val) {
    if(val != null) {
      try {
        return name + "=" + (val instanceof String ? URLEncoder.encode(val.toString(), "UTF-8") : val);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    } else {
      return null;
    }
  }

}
