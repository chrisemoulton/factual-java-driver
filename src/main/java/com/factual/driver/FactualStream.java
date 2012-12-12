package com.factual.driver;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.common.io.Closeables;
/**
 * Represents a streaming request where the response is provided through the a callback
 * @author brandon
 *
 */
public class FactualStream {

  private final BufferedReader br;
  private final LineCallback cb;
  public FactualStream(BufferedReader br, LineCallback cb) {
    this.br = br;
    this.cb = cb;
  }

  /**
   * Start reading the response line by line.  This is a blocking call.
   */
  public void start() {
    String line = null;
    StringBuffer sb = new StringBuffer();
    try {
      while ((line = br.readLine()) != null) {
        if (cb != null)
          cb.onLine(line);
        sb.append(line);
      }
    } catch (IOException e) {
    }
  }

  /**
   * Terminate the request
   */
  public void end() {
    Closeables.closeQuietly(br);
  }
}
