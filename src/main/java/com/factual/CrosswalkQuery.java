package com.factual;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class CrosswalkQuery {
  private String factualId;
  private int limit;
  private String namespace;
  private String namespaceId;
  private final Set<String> only = Sets.newHashSet();


  public CrosswalkQuery factualId(String factualId) {
    this.factualId = factualId;
    return this;
  }

  public CrosswalkQuery limit(int limit) {
    this.limit = limit;
    return this;
  }

  public CrosswalkQuery namespace(String namespace) {
    this.namespace = namespace;
    return this;
  }

  public CrosswalkQuery namespaceId(String namespaceId) {
    this.namespaceId = namespaceId;
    return this;
  }

  public CrosswalkQuery only(String... namespaces) {
    for(String ns : namespaces) {
      only.add(ns);
    }
    return this;
  }

  protected String toUrlQuery() {
    return Joiner.on("&").skipNulls().join(
        urlPair("factual_id", factualId),
        (limit > 0 ? urlPair("limit", limit) : null),
        urlPair("namespace", namespace),
        urlPair("namespace_id", namespaceId),
        urlPair("only", onlysOrNull()));
  }

  private String onlysOrNull() {
    if(!only.isEmpty()) {
      return Joiner.on(",").skipNulls().join(only);
    } else {
      return null;
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
