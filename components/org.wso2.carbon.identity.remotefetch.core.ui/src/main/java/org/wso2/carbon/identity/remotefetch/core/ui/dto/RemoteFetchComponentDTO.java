/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.remotefetch.core.ui.dto;

/**
 *  DTO of remote fetch component.
 */
public class RemoteFetchComponentDTO {

    /**
     * type of component.
     */
    public static enum ComponentType {
        REPOSITORY_MANAGER,
        ACTION_LISTENER,
        CONFIG_DEPLOYER
    }

    ComponentType type;
    String identifier;
    String name;

    public RemoteFetchComponentDTO(ComponentType type, String identifier, String name) {

        this.type = type;
        this.identifier = identifier;
        this.name = name;
    }

    public ComponentType getType() {

        return type;
    }

    public void setType(ComponentType type) {

        this.type = type;
    }

    public String getIdentifier() {

        return identifier;
    }

    public void setIdentifier(String identifier) {

        this.identifier = identifier;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }
}
