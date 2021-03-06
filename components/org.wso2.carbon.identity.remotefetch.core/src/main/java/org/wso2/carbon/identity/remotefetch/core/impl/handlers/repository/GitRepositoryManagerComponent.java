/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.identity.remotefetch.core.impl.handlers.repository;

import org.wso2.carbon.identity.remotefetch.common.RemoteFetchConstants;
import org.wso2.carbon.identity.remotefetch.common.repomanager.RepositoryManagerBuilder;
import org.wso2.carbon.identity.remotefetch.common.repomanager.RepositoryManagerComponent;
import org.wso2.carbon.identity.remotefetch.common.ui.UIField;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds functions git repo for UI elements.
 */
public class GitRepositoryManagerComponent implements RepositoryManagerComponent {

    /**
     * Returns a new builder object for the component.
     *
     * @return RepositoryManagerBuilder
     */
    @Override
    public RepositoryManagerBuilder getRepositoryManagerBuilder() {

        return new GitRepositoryManagerBuilder();
    }

    /**
     * Returns a string of the unique identifier of the component.
     *
     * @return ID
     */
    @Override
    public String getIdentifier() {

        return RemoteFetchConstants.IDENTIFIER_GIT_REPOSITORY_MANAGER_COMPONENT;
    }

    /**
     * Returns screen name of the component.
     *
     * @return Name
     */
    @Override
    public String getName() {

        return "Standard Git Repository";
    }

    /**
     * Returns list of UIFields to configure UI.
     *
     * @return List of UI fields.
     */
    @Override
    public List<UIField> getUIFields() {

        ArrayList<UIField> fieldList = new ArrayList();

        fieldList.add(new UIField(
                RemoteFetchConstants.ID_UI_FIELD_URI, UIField.FieldTypes.TEXT_BOX, "Git Repository URI",
                "Https url of the repo",
                "((\\w+:\\/\\/)[-a-zA-Z0-9:@;?&=\\/%\\+\\.\\*!'\\(\\),\\$_\\{\\}\\^~\\[\\]`#|]+)",
                "", true, false, false

        ));
        fieldList.add(new UIField(
                RemoteFetchConstants.ID_UI_FIELD_BRANCH, UIField.FieldTypes.TEXT_BOX,
                "Branch", "Branch to be pulled",
                "",
                "master", true, false, false

        ));
        fieldList.add(new UIField(
                RemoteFetchConstants.ID_UI_FIELD_DIRECTORY, UIField.FieldTypes.TEXT_BOX, "Directory",
                "Directory of target files",
                "^(((\\/)|(\\.)\\/|(\\.\\.)\\/))*(((\\w+)|(\\.\\.)|(\\.))*\\/)*(\\w*)$",
                "", true, false, false

        ));
        fieldList.add(new UIField(
                "accessToken", UIField.FieldTypes.TEXT_BOX, "Personal Access Token",
                "Access token if repository is private",
                "",
                "", false, false, false

        ));
        fieldList.add(new UIField(
                "userName", UIField.FieldTypes.TEXT_BOX, "User Name",
                "User name if repository is private",
                "",
                "", false, false, false

        ));

        return fieldList;
    }
}
