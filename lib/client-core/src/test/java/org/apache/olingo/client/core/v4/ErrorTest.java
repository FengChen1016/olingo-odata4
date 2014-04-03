/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.ODataError;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.client.core.AbstractTest;
import org.junit.Test;

public class ErrorTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  private ODataError error(final String name, final ODataPubFormat format) {
    final ODataError error = getClient().getDeserializer().toError(
            getClass().getResourceAsStream(name + "." + getSuffix(format)), format == ODataPubFormat.ATOM);
    assertNotNull(error);
    return error;
  }

  private void simple(final ODataPubFormat format) {
    final ODataError error = error("error", format);
    assertEquals("501", error.getCode());
    assertEquals("Unsupported functionality", error.getMessage());
    assertEquals("query", error.getTarget());
  }

  @Test
  public void jsonSimple() {
    simple(ODataPubFormat.JSON);
  }

  @Test
  public void atomSimple() {
    simple(ODataPubFormat.ATOM);
  }

}