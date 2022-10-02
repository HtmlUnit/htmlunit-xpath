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
package net.sourceforge.htmlunit.xpath.xml.utils;

import java.io.PrintWriter;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

/** Implement SAX error handler for default reporting. */
public class DefaultErrorHandler implements ErrorListener {
  PrintWriter m_pw;

  /**
   * if this flag is set to true, we will rethrow the exception on the error() and fatalError()
   * methods. If it is false, the errors are reported to System.err.
   */
  boolean m_throwExceptionOnError;

  /** Constructor DefaultErrorHandler */
  public DefaultErrorHandler() {
    this(true);
  }

  /** Constructor DefaultErrorHandler */
  public DefaultErrorHandler(boolean throwExceptionOnError) {
    // Defer creation of a PrintWriter until it's actually needed
    m_throwExceptionOnError = throwExceptionOnError;
  }

  /**
   * Retrieve <code>java.io.PrintWriter</code> to which errors are being directed.
   *
   * @return The <code>PrintWriter</code> installed via the constructor or the default <code>
   *     PrintWriter</code>
   */
  public PrintWriter getErrorWriter() {
    // Defer creating the java.io.PrintWriter until an error needs to be
    // reported.
    if (m_pw == null) {
      m_pw = new PrintWriter(System.err, true);
    }
    return m_pw;
  }

  /** {@inheritDoc} */
  @Override
  public void warning(TransformerException exception) throws TransformerException {
    PrintWriter pw = getErrorWriter();
    pw.println(exception.getMessage());
  }

  /** {@inheritDoc} */
  @Override
  public void error(TransformerException exception) throws TransformerException {
    // If the m_throwExceptionOnError flag is true, rethrow the exception.
    // Otherwise report the error to System.err.
    if (m_throwExceptionOnError) throw exception;
    else {
      PrintWriter pw = getErrorWriter();
      pw.println(exception.getMessage());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void fatalError(TransformerException exception) throws TransformerException {
    // If the m_throwExceptionOnError flag is true, rethrow the exception.
    // Otherwise report the error to System.err.
    if (m_throwExceptionOnError) throw exception;
    else {
      PrintWriter pw = getErrorWriter();
      pw.println(exception.getMessage());
    }
  }
}
