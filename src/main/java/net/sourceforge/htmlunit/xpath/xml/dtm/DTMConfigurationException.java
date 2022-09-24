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

/** Indicates a serious configuration error. */
public class DTMConfigurationException extends DTMException {
  static final long serialVersionUID = -4607874078818418046L;

  /** Create a new <code>DTMConfigurationException</code> with no detail mesage. */
  public DTMConfigurationException() {
    super("Configuration Error");
  }

  /**
   * Create a new <code>DTMConfigurationException</code> with the <code>String </code> specified as
   * an error message.
   *
   * @param msg The error message for the exception.
   */
  public DTMConfigurationException(String msg) {
    super(msg);
  }

  /**
   * Create a new <code>DTMConfigurationException</code> with a given <code>Exception</code> base
   * cause of the error.
   *
   * @param e The exception to be encapsulated in a DTMConfigurationException.
   */
  public DTMConfigurationException(Throwable e) {
    super(e);
  }

  /**
   * Create a new <code>DTMConfigurationException</code> with the given <code>Exception</code> base
   * cause and detail message.
   *
   * @param msg The detail message.
   * @param e The exception to be wrapped in a DTMConfigurationException
   */
  public DTMConfigurationException(String msg, Throwable e) {
    super(msg, e);
  }
}
