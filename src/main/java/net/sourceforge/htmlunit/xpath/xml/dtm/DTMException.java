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

import net.sourceforge.htmlunit.xpath.res.XPATHErrorResources;
import net.sourceforge.htmlunit.xpath.res.XPATHMessages;

/** This class specifies an exceptional condition that occured in the DTM module. */
public class DTMException extends RuntimeException {

  /**
   * Field containedException specifies a wrapped exception. May be null.
   *
   * @serial
   */
  Throwable containedException;

  /** {@inheritDoc} */
  @Override
  public synchronized Throwable getCause() {
    return (containedException == this) ? null : containedException;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized Throwable initCause(Throwable cause) {

    if ((this.containedException == null) && (cause != null)) {
      throw new IllegalStateException(
          XPATHMessages.createXPATHMessage(XPATHErrorResources.ER_CANNOT_OVERWRITE_CAUSE, null));
    }

    if (cause == this) {
      throw new IllegalArgumentException(
          XPATHMessages.createXPATHMessage(
              XPATHErrorResources.ER_SELF_CAUSATION_NOT_PERMITTED, null));
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
}
