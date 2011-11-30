package com.factual;

import com.google.api.client.http.HttpResponse;


public class FactualApiException extends RuntimeException {
  private String requestUrl;
  private String requestMethod;
  private HttpResponse response;


  public FactualApiException(Exception e) {
    super(e);
  }

  public FactualApiException requestUrl(String url) {
    this.requestUrl = url;
    return this;
  }

  public FactualApiException requestMethod(String method) {
    this.requestMethod = method;
    return this;
  }

  public FactualApiException response(HttpResponse response) {
    this.response = response;
    return this;
  }

  public String getRequestUrl() {
    return requestUrl;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public HttpResponse getResponse() {
    return response;
  }
}
