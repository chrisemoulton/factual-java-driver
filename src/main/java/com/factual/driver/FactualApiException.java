package com.factual.driver;

import com.google.api.client.http.HttpResponse;

/**
 * Represents an Exception that happened while communicating with Factual.
 * Includes information about the request that triggered the problem.
 * 
 * @author aaron
 */
public class FactualApiException extends RuntimeException {
  private String requestUrl;
  private String requestMethod;
  private HttpResponse response;
  private int statusCode;
  private String statusMessage;

  public FactualApiException(Exception e) {
    super(e);
  }

  public FactualApiException(String msg) {
    super(msg);
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
    this.statusCode = response.getStatusCode();
    this.statusMessage = response.getStatusMessage();
    return this;
  }

  /**
   * @return the status code.
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * @return the status code.
   */
  public String getStatusMessage() {
    return statusMessage;
  }

  /**
   * @return the URL used to make the offending request to Factual.
   */
  public String getRequestUrl() {
    return requestUrl;
  }

  /**
   * @return the HTTP request method used to make the offending request to
   *         Factual.
   */
  public String getRequestMethod() {
    return requestMethod;
  }

  /**
   * @deprecated remove dependency on api specific to google client api. Use
   *             getStatusMessage() and getStatusCode() instead to find
   *             information on the response. Access to HttpResponse is
   *             unavailable from google-client-api version 1.9.0-beta and
   *             later.
   * @return the full HttpResponse object, representing the problematic response
   *         from Factual.
   */
  @Deprecated
  public HttpResponse getResponse() {
    return response;
  }
}
