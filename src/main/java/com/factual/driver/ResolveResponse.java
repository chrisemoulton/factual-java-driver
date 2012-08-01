package com.factual.driver;

import java.util.Map;

public class ResolveResponse extends ReadResponse {

  public ResolveResponse(String json) {
    super(json);
  }

  public Map<String, Object> getResolved() {
    if (isResolved())
      return getData().get(0);
    else
      return null;
  }

  public boolean isResolved() {
    return getData().size() > 0
        && Boolean.TRUE.equals(getData().get(0).get("resolved"));
  }
}
