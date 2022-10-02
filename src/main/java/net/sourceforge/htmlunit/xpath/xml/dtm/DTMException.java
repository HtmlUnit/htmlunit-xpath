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
package net.sourceforge.htmlunit.xpath.xml.dtm;

import net.sourceforge.htmlunit.xpath.xml.res.XMLErrorResources;
import net.sourceforge.htmlunit.xpath.xml.res.XMLMessages;

/** This class specifies an exceptional condition that occured in the DTM module. */
public class DTMException extends RuntimeException {

  /**
   * Field containedException specifies a wrapped exception. May be null.
   *
   * @serial
   */
  Throwable containedException;

  /**
   * This method retrieves an exception that this exception wraps.
   *
   * @return An Throwable object, or null.
   * @see #getCause
   */
  public Throwable getException() {
    return containedException;
  }

  /** {@inheritDoc} */
  @Override
  public Throwable getCause() {

    return (containedException == this) ? null : containedException;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized Throwable initCause(Throwable cause) {

    if ((this.containedException == null) && (cause != null)) {
      throw new IllegalStateException(
          XMLMessages.createXMLMessage(
              XMLErrorResources.ER_CANNOT_OVERWRITE_CAUSE, null)); // "Can't
      // overwrite
      // cause");
    }

    if (cause == this) {
      throw new IllegalArgumentException(
          XMLMessages.createXMLMessage(
              XMLErrorResources.ER_SELF_CAUSATION_NOT_PERMITTED, null)); // "Self-causation
      // not
      // permitted");
    }

    this.containedException = cause;

    return this;
  }

  /**
   * Create a new DTMException.
   *
   * @param message The error or warning message.
   */
  public DTMException(String message) {

    super(message);

    this.containedException = null;
  }

  /**
   * Create a new DTMException wrapping an existing exception.
   *
   * @param e The exception to be wrapped.
   */
  public DTMException(Throwable e) {

    super(e.getMessage());

    this.containedException = e;
  }

  /**
   * Wrap an existing exception in a DTMException.
   *
   * <p>This is used for throwing processor exceptions before the processing has started.
   *
   * @param message The error or warning message, or null to use the message from the embedded
   *     exception.
   * @param e Any exception
   */
  public DTMException(String message, Throwable e) {

    super(((message == null) || (message.length() == 0)) ? e.getMessage() : message);

    this.containedException = e;
  }

  /** {@inheritDoc} */
  @Override
  public void printStackTrace() {
    printStackTrace(new java.io.PrintWriter(System.err, true));
  }

  /** {@inheritDoc} */
  @Override
  public void printStackTrace(java.io.PrintStream s) {
    printStackTrace(new java.io.PrintWriter(s));
  }
}
