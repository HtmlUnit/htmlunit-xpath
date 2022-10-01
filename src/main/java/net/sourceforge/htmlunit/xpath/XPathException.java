/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.htmlunit.xpath;

import javax.xml.transform.TransformerException;

/**
 * This class implements an exception object that all XPath classes will throw in case of an error.
 * This class extends TransformerException, and may hold other exceptions. In the case of nested
 * exceptions, printStackTrace will dump all the traces of the nested exceptions, not just the trace
 * of this object.
 */
public class XPathException extends TransformerException {

  /**
   * A nested exception.
   *
   * @serial
   */
  protected Exception m_exception;

  /**
   * Create an XPathException object that holds an error message.
   *
   * @param message The error message.
   */
  public XPathException(String message, ExpressionNode ex) {
    super(message);
    this.setLocator(ex);
  }

  /**
   * Create an XPathException object that holds an error message.
   *
   * @param message The error message.
   */
  public XPathException(String message) {
    super(message);
  }

  /**
   * Get the first non-Expression parent of this node.
   *
   * @return null or first ancestor that is not an Expression.
   */
  protected ExpressionNode getExpressionOwner(ExpressionNode ex) {
    ExpressionNode parent = ex.exprGetParent();
    while ((null != parent) && (parent instanceof Expression)) parent = parent.exprGetParent();
    return parent;
  }

  /**
   * Create an XPathException object that holds an error message, and another exception that caused
   * this exception.
   *
   * @param message The error message.
   * @param e The exception that caused this exception.
   */
  public XPathException(String message, Exception e) {

    super(message);

    this.m_exception = e;
  }

  /**
   * Print the the trace of methods from where the error originated. This will trace all nested
   * exception objects, as well as this object.
   *
   * @param s The stream where the dump will be sent to.
   */
  @Override
  public void printStackTrace(java.io.PrintStream s) {

    if (s == null) s = System.err;

    try {
      super.printStackTrace(s);
    } catch (Exception e) {
    }

    Throwable exception = m_exception;

    for (int i = 0; (i < 10) && (null != exception); i++) {
      s.println("---------");
      exception.printStackTrace(s);

      if (exception instanceof TransformerException) {
        TransformerException se = (TransformerException) exception;
        Throwable prev = exception;

        exception = se.getException();

        if (prev == exception) break;
      } else {
        exception = null;
      }
    }
  }

  /**
   * Find the most contained message.
   *
   * @return The error message of the originating exception.
   */
  @Override
  public String getMessage() {

    String lastMessage = super.getMessage();
    Throwable exception = m_exception;

    while (null != exception) {
      String nextMessage = exception.getMessage();

      if (null != nextMessage) lastMessage = nextMessage;

      if (exception instanceof TransformerException) {
        TransformerException se = (TransformerException) exception;
        Throwable prev = exception;

        exception = se.getException();

        if (prev == exception) break;
      } else {
        exception = null;
      }
    }

    return (null != lastMessage) ? lastMessage : "";
  }

  /**
   * Print the the trace of methods from where the error originated. This will trace all nested
   * exception objects, as well as this object.
   *
   * @param s The writer where the dump will be sent to.
   */
  @Override
  public void printStackTrace(java.io.PrintWriter s) {

    if (s == null) s = new java.io.PrintWriter(System.err);

    try {
      super.printStackTrace(s);
    } catch (Exception e) {
    }
  }

  /**
   * Return the embedded exception, if any. Overrides
   * javax.xml.transform.TransformerException.getException().
   *
   * @return The embedded exception, or null if there is none.
   */
  @Override
  public Throwable getException() {
    return m_exception;
  }
}
