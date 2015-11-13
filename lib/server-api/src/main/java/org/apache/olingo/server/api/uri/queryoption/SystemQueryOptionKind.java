/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.api.uri.queryoption;

/**
 * Defines the supported system query options.
 */

public enum SystemQueryOptionKind {

  /**
   * @see FilterOption
   */
  FILTER("$filter"),

  /**
   * @see FormatOption
   */
  FORMAT("$format"),

  /**
   * @see ExpandOption
   */
  EXPAND("$expand"),

  /**
   * @see IdOption
   */
  ID("$id"),

  /**
   * @see CountOption
   */
  COUNT("$count"),

  /**
   * @see OrderByOption
   */
  ORDERBY("$orderby"),

  /**
   * @see SearchOption
   */
  SEARCH("$search"),

  /**
   * @see SelectOption
   */
  SELECT("$select"),

  /**
   * @see SkipOption
   */
  SKIP("$skip"),

  /**
   * @see SkipTokenOption
   */
  SKIPTOKEN("$skiptoken"),

  /**
   * @see TopOption
   */
  TOP("$top"),

  /**
   * @see LevelsExpandOption
   */
  LEVELS("$levels");

  private String syntax;

  SystemQueryOptionKind(final String syntax) {
    this.syntax = syntax;
  }

  @Override
  public String toString() {
    return syntax;
  }
}
