/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.core.edm.primitivetype;

import java.math.BigInteger;

import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Int16.
 */
public final class EdmInt16 extends SingletonPrimitiveType {

  private static final EdmInt16 instance = new EdmInt16();

  public static EdmInt16 getInstance() {
    return instance;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return primitiveType instanceof Uint7
        || primitiveType instanceof EdmByte
        || primitiveType instanceof EdmSByte
        || primitiveType instanceof EdmInt16;
  }

  @Override
  public Class<?> getDefaultType() {
    return Short.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    Short valueShort;
    try {
      valueShort = Short.parseShort(value);
    } catch (final NumberFormatException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)", e);
    }

    try {
      return EdmInt64.convertNumber(valueShort, returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType), e");
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType), e");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value instanceof Byte || value instanceof Short) {
      return value.toString();
    } else if (value instanceof Integer || value instanceof Long) {
      if (((Number) value).longValue() >= Short.MIN_VALUE
          && ((Number) value).longValue() <= Short.MAX_VALUE) {
        return value.toString();
      } else {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
      }
    } else if (value instanceof BigInteger) {
      if (((BigInteger) value).bitLength() < Short.SIZE) {
        return value.toString();
      } else {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
      }
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }
}
