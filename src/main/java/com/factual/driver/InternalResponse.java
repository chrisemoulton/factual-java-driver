package com.factual.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.api.client.http.HttpResponse;

/**
 * Wrapper for a response from a Factual query.
 *
 * @author brandon
 */
public class InternalResponse {

  private String content = null;
  private final int statusCode;

  public InternalResponse(HttpResponse response, LineCallback cb) throws IOException {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new InputStreamReader(response
          .getContent()));
      String line = null;
      StringBuffer sb = new StringBuffer();
      while ((line = br.readLine()) != null) {
        if (cb != null)
          cb.onLine(line);
        sb.append(line);
      }
      this.content = sb.toString();
      this.statusCode = response.getStatusCode();
    } finally {
      if (br != null) {
        br.close();
      }
    }
  }

  public InternalResponse(String content) {
    this.content = content;
    this.statusCode = 200;
  }

  public String getContent() {
    return this.content;
  }

  public int getStatusCode() {
    return statusCode;
  }

}
