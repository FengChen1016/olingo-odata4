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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata4.commons.api.edm.EdmAction;
import org.apache.olingo.odata4.commons.api.edm.EdmComplexType;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.commons.api.edm.EdmEnumType;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmFunction;
import org.apache.olingo.odata4.commons.api.edm.EdmServiceMetadata;
import org.apache.olingo.odata4.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.odata4.commons.api.edm.provider.Action;
import org.apache.olingo.odata4.commons.api.edm.provider.AliasInfo;
import org.apache.olingo.odata4.commons.api.edm.provider.ComplexType;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityType;
import org.apache.olingo.odata4.commons.api.edm.provider.EnumType;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.Function;
import org.apache.olingo.odata4.commons.api.edm.provider.Parameter;
import org.apache.olingo.odata4.commons.api.edm.provider.TypeDefinition;
import org.apache.olingo.odata4.commons.api.exception.ODataException;
import org.apache.olingo.odata4.commons.core.edm.EdmImpl;

public class EdmProviderImpl extends EdmImpl {

  private final EdmProvider provider;

  public EdmProviderImpl(final EdmProvider provider) {
    this.provider = provider;

  }

  @Override
  public EdmEntityContainer createEntityContainer(final FullQualifiedName containerName) {
    try {
      EntityContainerInfo entityContainerInfo = provider.getEntityContainerInfo(containerName);
      if (entityContainerInfo != null) {
        return new EdmEntityContainerImpl(this, provider, entityContainerInfo);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  public EdmEnumType createEnumType(final FullQualifiedName enumName) {
    try {
      EnumType enumType = provider.getEnumType(enumName);
      if (enumType != null) {
        return new EdmEnumImpl(this, enumName, enumType);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  public EdmTypeDefinition createTypeDefinition(final FullQualifiedName typeDefinitionName) {
    try {
      TypeDefinition typeDefinition = provider.getTypeDefinition(typeDefinitionName);
      if (typeDefinition != null) {
        return new EdmTypeDefinitionImpl(this, typeDefinitionName, typeDefinition);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  public EdmEntityType createEntityType(final FullQualifiedName entityTypeName) {
    try {
      EntityType entityType = provider.getEntityType(entityTypeName);
      if (entityType != null) {
        return new EdmEntityTypeImpl(this, entityTypeName, entityType);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  public EdmComplexType createComplexType(final FullQualifiedName complexTypeName) {
    try {
      ComplexType complexType = provider.getComplexType(complexTypeName);
      if (complexType != null) {
        return new EdmComplexTypeImpl(this, complexTypeName, complexType);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  public EdmAction createAction(final FullQualifiedName actionName, final FullQualifiedName bindingParameterTypeName,
      final Boolean isBindingParameterCollection) {

    try {
      List<Action> actions = provider.getActions(actionName);
      if (actions != null) {
        EdmActionImpl actionImpl = null;
        if (bindingParameterTypeName == null) {
          // Search for first unbound action
          for (Action action : actions) {
            if (action.isBound() == false) {
              actionImpl = new EdmActionImpl(this, actionName, action);
              break;
            }
          }
        } else {
          // Search for bound action where binding parameter matches
          boolean isCollection = false;
          if (isBindingParameterCollection == null) {
            isCollection = false;
          } else {
            isCollection = isBindingParameterCollection;
          }
          for (Action action : actions) {
            if (action.isBound() == true) {
              List<Parameter> parameters = action.getParameters();
              Parameter parameter = parameters.get(0);
              if (bindingParameterTypeName.equals(parameter.getType()) && isCollection == parameter.isCollection()) {
                actionImpl = new EdmActionImpl(this, actionName, action);
                break;
              }

            }
          }
        }

        return actionImpl;
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  public EdmFunction createFunction(final FullQualifiedName functionName,
      final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection,
      final List<String> parameterNames) {
    try {
      List<Function> functions = provider.getFunctions(functionName);
      if (functions != null) {
        EdmFunctionImpl functionImpl = null;
        // TODO: Should we throw an edmexception when parameternames is null?
        if (bindingParameterTypeName == null) {
          // Search for matching unbound function
          for (Function function : functions) {
            if (function.isBound() == false) {
              List<Parameter> parameters = function.getParameters();
              if (parameterNames.size() == parameters.size()) {
                List<String> functionParameterNames = new ArrayList<String>();
                for (Parameter parameter : parameters) {
                  functionParameterNames.add(parameter.getName());
                }

                if (parameterNames.containsAll(functionParameterNames)) {
                  functionImpl = new EdmFunctionImpl(this, functionName, function);
                  break;
                }
              }
            }
          }
        } else {
          // Search for matching bound function
          boolean isCollection = false;
          if (isBindingParameterCollection == null) {
            isCollection = false;
          } else {
            isCollection = isBindingParameterCollection;
          }
          for (Function function : functions) {
            if (function.isBound() == true) {
              List<Parameter> parameters = function.getParameters();
              Parameter bindingParameter = parameters.get(0);
              if (bindingParameterTypeName.equals(bindingParameter.getType())
                  && isCollection == bindingParameter.isCollection()) {
                // bindingparameter type matches now only parameter names have to match
                List<String> functionParameterNames = new ArrayList<String>();
                for (int i = 1; i < parameters.size(); i++) {
                  functionParameterNames.add(parameters.get(i).getName());
                }

                if (parameterNames.containsAll(functionParameterNames)) {
                  functionImpl = new EdmFunctionImpl(this, functionName, function);
                  break;
                }
              }
            }
          }
        }

        return functionImpl;
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  public EdmServiceMetadata createServiceMetadata() {
    return new EdmServiceMetadataImpl(provider);
  }

  @Override
  protected Map<String, String> createAliasToNamespaceInfo() {
    Map<String, String> aliasToNamespaceInfos = new HashMap<String, String>();
    try {
      List<AliasInfo> aliasInfos = provider.getAliasInfos();
      if (aliasInfos != null) {
        for (AliasInfo info : aliasInfos) {
          aliasToNamespaceInfos.put(info.getAlias(), info.getNamespace());
        }
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }
    return aliasToNamespaceInfos;
  }

}
