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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.domain.ODataCollectionValue;
import org.apache.olingo.client.api.domain.ODataComplexValue;
import org.apache.olingo.client.api.domain.ODataPrimitiveValue;
import org.apache.olingo.client.api.domain.ODataProperty;
import org.apache.olingo.client.api.domain.ODataValue;
import org.apache.olingo.client.api.format.ODataFormat;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.junit.Test;

public abstract class AbstractPropertyTest extends AbstractTest {

  protected abstract ODataFormat getFormat();

  protected ODataServiceVersion getVersion() {
    return getClient().getServiceVersion();
  }

  @Test
  public void readPropertyValue() throws IOException {
    final InputStream input = getClass().getResourceAsStream(
            getVersion().name().toLowerCase() + File.separatorChar
            + "Customer_-10_CustomerId_value.txt");

    final ODataValue value = getClient().getPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.String).
            setText(IOUtils.toString(input)).
            build();
    assertNotNull(value);
    assertEquals("-10", value.toString());
  }

  private ODataProperty primitive() throws IOException {
    final InputStream input = getClass().getResourceAsStream(
            getVersion().name().toLowerCase() + File.separatorChar
            + "Customer_-10_CustomerId." + getSuffix(getFormat()));
    final ODataProperty property = getClient().getReader().readProperty(input, getFormat());
    assertNotNull(property);
    assertTrue(property.hasPrimitiveValue());
    assertTrue(-10 == property.getPrimitiveValue().<Integer>toCastValue());

    ODataProperty comparable;
    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, getFormat()), getFormat());
    if (getFormat() == ODataFormat.XML) {
      comparable = written;
    } else {
      // This is needed because type information gets lost with JSON serialization
      final ODataPrimitiveValue typedValue = getClient().getPrimitiveValueBuilder().
              setType(EdmPrimitiveTypeKind.valueOfFQN(
                              getClient().getServiceVersion(), property.getPrimitiveValue().getTypeName())).
              setText(written.getPrimitiveValue().toString()).
              build();
      comparable = getClient().getObjectFactory().newPrimitiveProperty(written.getName(), typedValue);
    }

    assertEquals(property, comparable);

    return property;
  }

  @Test
  public void readPrimitiveProperty() throws IOException {
    primitive();
  }

  private ODataProperty complex() throws IOException {
    final InputStream input = getClass().getResourceAsStream(
            getVersion().name().toLowerCase() + File.separatorChar
            + "Customer_-10_PrimaryContactInfo." + getSuffix(getFormat()));
    final ODataProperty property = getClient().getReader().readProperty(input, getFormat());
    assertNotNull(property);
    assertTrue(property.hasComplexValue());
    assertEquals(6, property.getComplexValue().size());

    ODataProperty comparable;
    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, getFormat()), getFormat());
    if (getFormat() == ODataFormat.XML) {
      comparable = written;
    } else {
      // This is needed because type information gets lost with JSON serialization
      final ODataComplexValue typedValue = new ODataComplexValue(property.getComplexValue().getTypeName());
      for (final Iterator<ODataProperty> itor = written.getComplexValue().iterator(); itor.hasNext();) {
        final ODataProperty prop = itor.next();
        typedValue.add(prop);
      }
      comparable = getClient().getObjectFactory().newComplexProperty(written.getName(), typedValue);
    }

    assertEquals(property, comparable);

    return property;
  }

  @Test
  public void readComplexProperty() throws IOException {
    complex();
  }

  private ODataProperty collection() throws IOException {
    final InputStream input = getClass().getResourceAsStream(
            getVersion().name().toLowerCase() + File.separatorChar
            + "Customer_-10_BackupContactInfo." + getSuffix(getFormat()));
    final ODataProperty property = getClient().getReader().readProperty(input, getFormat());
    assertNotNull(property);
    assertTrue(property.hasCollectionValue());
    assertEquals(9, property.getCollectionValue().size());

    ODataProperty comparable;
    final ODataProperty written = getClient().getReader().readProperty(
            getClient().getWriter().writeProperty(property, getFormat()), getFormat());
    if (getFormat() == ODataFormat.XML) {
      comparable = written;
    } else {
      // This is needed because type information gets lost with JSON serialization
      final ODataCollectionValue typedValue =
              new ODataCollectionValue(property.getCollectionValue().getTypeName());
      for (final Iterator<ODataValue> itor = written.getCollectionValue().iterator(); itor.hasNext();) {
        final ODataValue value = itor.next();
        if (value.isPrimitive()) {
          typedValue.add(value);
        }
        if (value.isComplex()) {
          final ODataComplexValue typedComplexValue =
                  new ODataComplexValue("Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails");
          for (final Iterator<ODataProperty> valueItor = value.asComplex().iterator(); valueItor.hasNext();) {
            final ODataProperty prop = valueItor.next();
            typedComplexValue.add(prop);
          }
          typedValue.add(typedComplexValue);
        }
      }
      comparable = getClient().getObjectFactory().newCollectionProperty(written.getName(), typedValue);
    }

    assertEquals(property, comparable);

    return property;
  }

  @Test
  public void readCollectionProperty() throws IOException {
    collection();
  }
}