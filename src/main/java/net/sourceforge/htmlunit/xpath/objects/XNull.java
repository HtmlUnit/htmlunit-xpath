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
package net.sourceforge.htmlunit.xpath.objects;

/**
 * This class represents an XPath null object, and is capable of converting the null to other types,
 * such as a string.
 */
public class XNull extends XNodeSet {

  /** Create an XObject. */
  public XNull() {
    super();
  }

  /**
   * Tell what kind of class this is.
   *
   * @return type CLASS_NULL
   */
  @Override
  public int getType() {
    return CLASS_NULL;
  }

  /**
   * Given a request type, return the equivalent string. For diagnostic purposes.
   *
   * @return type string "#CLASS_NULL"
   */
  @Override
  public String getTypeString() {
    return "#CLASS_NULL";
  }

  /**
   * Cast result object to a number.
   *
   * @return 0.0
   */
  @Override
  public double num() {
    return 0.0;
  }

  /**
   * Cast result object to a boolean.
   *
   * @return false
   */
  @Override
  public boolean bool() {
    return false;
  }

  /**
   * Cast result object to a string.
   *
   * @return empty string ""
   */
  @Override
  public String str() {
    return "";
  }

  // /**
  // * Cast result object to a nodelist.
  // *
  // * @return null
  // */
  // public DTMIterator iter()
  // {
  // return null;
  // }

  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 Object to compare this to
   * @return True if the given object is of type CLASS_NULL
   */
  @Override
  public boolean equals(XObject obj2) {
    return obj2.getType() == CLASS_NULL;
  }
}
