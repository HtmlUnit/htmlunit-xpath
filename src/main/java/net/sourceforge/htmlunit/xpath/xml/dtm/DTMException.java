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
  static final long serialVersionUID = -775576419181334734L;

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

  /**
   * Returns the cause of this throwable or <code>null</code> if the cause is nonexistent or
   * unknown. (The cause is the throwable that caused this throwable to get thrown.)
   */
  @Override
  public Throwable getCause() {

    return (containedException == this) ? null : containedException;
  }

  /**
   * Initializes the <i>cause</i> of this throwable to the specified value. (The cause is the
   * throwable that caused this throwable to get thrown.)
   *
   * <p>This method can be called at most once. It is generally called from within the constructor,
   * or immediately after creating the throwable. If this throwable was created with {@link
   * #DTMException(Throwable)} or {@link #DTMException(String,Throwable)}, this method cannot be
   * called even once.
   *
   * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
   *     (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent or
   *     unknown.)
   * @return a reference to this <code>Throwable</code> instance.
   * @throws IllegalArgumentException if <code>cause</code> is this throwable. (A throwable cannot
   *     be its own cause.)
   * @throws IllegalStateException if this throwable was created with {@link
   *     #DTMException(Throwable)} or {@link #DTMException(String,Throwable)}, or this method has
   *     already been called on this throwable.
   */
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

  /**
   * Print the the trace of methods from where the error originated. This will trace all nested
   * exception objects, as well as this object.
   */
  @Override
  public void printStackTrace() {
    printStackTrace(new java.io.PrintWriter(System.err, true));
  }

  /**
   * Print the the trace of methods from where the error originated. This will trace all nested
   * exception objects, as well as this object.
   *
   * @param s The stream where the dump will be sent to.
   */
  @Override
  public void printStackTrace(java.io.PrintStream s) {
    printStackTrace(new java.io.PrintWriter(s));
  }
}
