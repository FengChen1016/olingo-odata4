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
package org.apache.olingo.odata4.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEnumType;
import org.apache.olingo.odata4.commons.api.edm.EdmMember;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.odata4.commons.api.edm.provider.EnumType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmInt64;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;

public class EdmEnumImpl extends EdmNamedImpl implements EdmEnumType {

  private final FullQualifiedName enumName;
  private final EdmPrimitiveType edmPrimitiveTypeInstance;
  private final EnumType enumType;
  private final String uriPrefix;
  private final String uriSuffix;
  private List<String> memberNames;

  public EdmEnumImpl(final EdmProviderImpl edm, final FullQualifiedName enumName, final EnumType enumType) {
    super(edm, enumName.getName());
    this.enumName = enumName;
    this.enumType = enumType;
    uriPrefix = enumName.getFullQualifiedNameAsString() + '\'';
    uriSuffix = "'";
    FullQualifiedName underlyingTypeName = enumType.getUnderlyingType();
    if (underlyingTypeName == null) {
      edmPrimitiveTypeInstance = EdmPrimitiveTypeKind.Int32.getEdmPrimitiveTypeInstance();
    } else {
      edmPrimitiveTypeInstance =
          EdmPrimitiveTypeKind.valueOf(underlyingTypeName.getName()).getEdmPrimitiveTypeInstance();
      // TODO: Should we validate that the underlying type is of byte, sbyte, in16, int32 or int64?
    }

  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return this.equals(primitiveType);
  }

  @Override
  public Class<?> getDefaultType() {
    return edmPrimitiveTypeInstance.getDefaultType();
  }

  @Override
  public boolean validate(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) {
    try {
      valueOfString(value, isNullable, maxLength, precision, scale, isUnicode, getDefaultType());
      return true;
    } catch (final EdmPrimitiveTypeException e) {
      return false;
    }
  }

  @Override
  public <T> T valueOfString(String value, Boolean isNullable, Integer maxLength, Integer precision, Integer scale,
      Boolean isUnicode, Class<T> returnType) throws EdmPrimitiveTypeException {
    if (value == null) {
      if (isNullable != null && !isNullable) {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_NULL_NOT_ALLOWED");
      }
      return null;
    }
    return internalValueOfString(value, isNullable, maxLength, precision, scale, isUnicode, returnType);
  }

  @Override
  public String valueToString(Object value, Boolean isNullable, Integer maxLength, Integer precision, Integer scale,
      Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value == null) {
      if (isNullable != null && !isNullable) {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.VALUE_NULL_NOT_ALLOWED");
      }
      return null;
    }
    return internalValueToString(value, isNullable, maxLength, precision, scale, isUnicode);
  }

  @Override
  public String toUriLiteral(final String literal) {
    return literal == null ? null :
        uriPrefix.isEmpty() && uriSuffix.isEmpty() ? literal : uriPrefix + literal + uriSuffix;
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    if (literal == null) {
      return null;
    } else if (uriPrefix.isEmpty() && uriSuffix.isEmpty()) {
      return literal;
    } else if (literal.length() >= uriPrefix.length() + uriSuffix.length()
        && literal.startsWith(uriPrefix) && literal.endsWith(uriSuffix)) {
      return literal.substring(uriPrefix.length(), literal.length() - uriSuffix.length());
    } else {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(literal)");
    }
  }

  @Override
  public String getNamespace() {
    return enumName.getNamespace();
  }

  @Override
  public EdmTypeKind getKind() {
    return EdmTypeKind.ENUM;
  }

  @Override
  public EdmMember getMember(final String name) {
    for (EdmMember member : enumType.getMembers()) {
      if (member.getName().equals(name)) {
        return member;
      }
    }
    return null;
  }

  @Override
  public List<String> getMemberNames() {
    if (memberNames == null) {
      memberNames = new ArrayList<String>();
      for (final EdmMember member : enumType.getMembers()) {
        memberNames.add(member.getName());
      }
    }
    return memberNames;
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
    return edmPrimitiveTypeInstance;
  }

  private <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    try {
      return EdmInt64.convertNumber(parseEnumValue(value), returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType), e");
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType), e");
    }
  }

  private Long parseEnumValue(final String value) throws EdmPrimitiveTypeException {
    Long result = null;
    for (final String memberValue : value.split(",", enumType.isFlags() ? -1 : 1)) {
      Long memberValueLong = null;
      for (final EdmMember member : enumType.getMembers()) {
        if (member.getName().equals(memberValue) || member.getValue().equals(memberValue)) {
          memberValueLong = Long.decode(member.getValue());
        }
      }
      if (memberValueLong == null) {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
      }
      result = result == null ? memberValueLong : result | memberValueLong;
    }
    return result;
  }

  protected String internalValueToString(final Object value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
      return constructEnumValue(((Number) value).longValue());
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }

  protected String constructEnumValue(final long value) throws EdmPrimitiveTypeException {
    long remaining = value;
    StringBuilder result = new StringBuilder();

    for (final EdmMember member : enumType.getMembers()) {
      final long memberValue = Long.parseLong(member.getValue());
      if ((memberValue & remaining) == memberValue) {
        if (result.length() > 0) {
          result.append(',');
        }
        result.append(member.getName());
        remaining ^= memberValue;
      }
    }

    if (remaining != 0) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
    }
    return result.toString();
  }
}
