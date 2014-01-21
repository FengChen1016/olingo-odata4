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

import java.net.URI;

import org.apache.olingo.odata4.commons.api.edm.EdmEntitySetInfo;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityContainer;
import org.apache.olingo.odata4.commons.api.edm.provider.EntitySet;

public class EdmEntitySetInfoImpl implements EdmEntitySetInfo {

  private final EntityContainer entityContainer;
  private final EntitySet set;

  public EdmEntitySetInfoImpl(EntityContainer entityContainer, EntitySet set) {
    this.entityContainer = entityContainer;
    this.set = set;
  }

  @Override
  public String getEntityContainerName() {
    return entityContainer.getName();
  }

  @Override
  public String getEntitySetName() {
    return set.getName();
  }

  @Override
  public URI getEntitySetUri() {
    return null;
  }

}
