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

package org.wso2.carbon.identity.remotefetch.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.remotefetch.common.BasicRemoteFetchConfiguration;
import org.wso2.carbon.identity.remotefetch.common.RemoteFetchConfiguration;
import org.wso2.carbon.identity.remotefetch.common.RemoteFetchConfigurationService;
import org.wso2.carbon.identity.remotefetch.common.ValidationReport;
import org.wso2.carbon.identity.remotefetch.common.exceptions.RemoteFetchCoreException;
import org.wso2.carbon.identity.remotefetch.core.dao.RemoteFetchConfigurationDAO;
import org.wso2.carbon.identity.remotefetch.core.dao.impl.RemoteFetchConfigurationDAOImpl;
import org.wso2.carbon.identity.remotefetch.core.executers.RemoteFetchTaskExecutor;
import org.wso2.carbon.identity.remotefetch.core.internal.RemoteFetchServiceComponentHolder;
import org.wso2.carbon.identity.remotefetch.core.util.RemoteFetchConfigurationUtils;
import org.wso2.carbon.identity.remotefetch.core.util.RemoteFetchConfigurationValidator;

import java.util.List;
import java.util.OptionalInt;

/**
 * Service to manage RemoteFetchConfigurations.
 */
public class RemoteFetchConfigurationServiceImpl implements RemoteFetchConfigurationService {

    private static final Log log = LogFactory.getLog(RemoteFetchConfigurationServiceImpl.class);

    private RemoteFetchConfigurationDAO fetchConfigurationDAO = new RemoteFetchConfigurationDAOImpl();
    private RemoteFetchTaskExecutor remoteFetchTaskExecutor;


    public RemoteFetchConfigurationServiceImpl(RemoteFetchTaskExecutor remoteFetchTaskExecutor) {
        this.remoteFetchTaskExecutor = remoteFetchTaskExecutor;
    }

    /**
     * @param fetchConfiguration
     * @return
     * @throws RemoteFetchCoreException
     */
    @Override
    public ValidationReport addRemoteFetchConfiguration(RemoteFetchConfiguration fetchConfiguration)
            throws RemoteFetchCoreException {

        RemoteFetchConfigurationValidator validator =
                new RemoteFetchConfigurationValidator(RemoteFetchServiceComponentHolder.getInstance()
                        .getRemoteFetchComponentRegistry(), fetchConfiguration);

        ValidationReport validationReport = validator.validate();

        if (validationReport.getValidationStatus() == ValidationReport.ValidationStatus.PASSED) {
            String remoteConfigurationId = RemoteFetchConfigurationUtils.generateUniqueID();
            if (log.isDebugEnabled()) {
                log.debug("Remote Configuration ID is  generated: " + remoteConfigurationId);
            }
            fetchConfiguration.setRemoteFetchConfigurationId(remoteConfigurationId);
            this.fetchConfigurationDAO.createRemoteFetchConfiguration(fetchConfiguration);
            validationReport.setId(remoteConfigurationId);
        }

        return validationReport;
    }

    /**
     * @param fetchConfiguration
     * @return
     * @throws RemoteFetchCoreException
     */
    @Override
    public ValidationReport updateRemoteFetchConfiguration(RemoteFetchConfiguration fetchConfiguration)
            throws RemoteFetchCoreException {

        RemoteFetchConfigurationValidator validator =
                new RemoteFetchConfigurationValidator(RemoteFetchServiceComponentHolder.getInstance()
                        .getRemoteFetchComponentRegistry(), fetchConfiguration);

        ValidationReport validationReport = validator.validate();

        if (validationReport.getValidationStatus() == ValidationReport.ValidationStatus.PASSED) {
            this.fetchConfigurationDAO.updateRemoteFetchConfiguration(fetchConfiguration);
            validationReport.setId(fetchConfiguration.getRemoteFetchConfigurationId());
        }

        return validationReport;
    }

    /**
     * @param fetchConfigurationId
     * @return Remote Fetch Configuration for id.
     * @throws RemoteFetchCoreException
     */
    @Override
    public RemoteFetchConfiguration getRemoteFetchConfiguration(String fetchConfigurationId)
            throws RemoteFetchCoreException {

        int tenantId = IdentityTenantUtil.getTenantId(CarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        return this.fetchConfigurationDAO.getRemoteFetchConfiguration(fetchConfigurationId, tenantId);
    }

    /**
     * @return
     * @throws RemoteFetchCoreException
     */
    @Override
    public List<BasicRemoteFetchConfiguration> getBasicRemoteFetchConfigurationList(OptionalInt limit,
                                                                                    OptionalInt offset)
            throws RemoteFetchCoreException {

        return this.fetchConfigurationDAO.getBasicRemoteFetchConfigurationsByTenant
                (CarbonContext.getThreadLocalCarbonContext().getTenantDomain(),
                        validateLimit(limit),
                        validateOffset(offset));
    }

    /**
     * @return All Enabled Remote Fetch Configurations.
     * @throws RemoteFetchCoreException
     */
    @Override
    public List<RemoteFetchConfiguration> getEnabledRemoteFetchConfigurationList() throws RemoteFetchCoreException {

        return this.fetchConfigurationDAO.getAllEnabledRemoteFetchConfigurations();
    }

    /**
     * @param fetchConfigurationId
     * @throws RemoteFetchCoreException
     */
    @Override
    public void deleteRemoteFetchConfiguration(String fetchConfigurationId)
            throws RemoteFetchCoreException {

        this.remoteFetchTaskExecutor.deleteRemoteFetchConfigurationFromBatchTask(fetchConfigurationId);

        int tenantId = IdentityTenantUtil.getTenantId(CarbonContext.getThreadLocalCarbonContext().getTenantDomain());
        this.fetchConfigurationDAO.deleteRemoteFetchConfiguration(fetchConfigurationId, tenantId);
    }

    /**
     * This method used to get remote fetch configuration for given id and start an Immediate task execution.
     * @param fetchConfiguration
     * @throws RemoteFetchCoreException
     */
    @Override
    public void triggerRemoteFetch(RemoteFetchConfiguration fetchConfiguration) throws RemoteFetchCoreException {

        this.remoteFetchTaskExecutor.startImmediateTaskExecution(fetchConfiguration);

        if (log.isDebugEnabled()) {
            log.debug("Immediate Task was created and executed for : " +
                    fetchConfiguration.getRemoteFetchConfigurationId());
        }
    }

    /**
     * Validate limit.
     * Check optionalLimit has a value, or else set to default value.
     * @param optionalLimit given limit value.
     * @return validated limit and offset value.
     */
    private int validateLimit(OptionalInt optionalLimit) throws RemoteFetchCoreException {

        int limit = optionalLimit.orElse(RemoteFetchConfigurationUtils.getDefaultItemsPerPage());
        if (log.isDebugEnabled()) {
            if (!(optionalLimit.isPresent())) {
                log.debug("Given limit is null. Therefore we get the default limit: " + limit +
                        " from identity.xml.");
            }
        }

        if (limit < 0) {
            String message = "Given limit: " + limit + " is a negative value.";
            throw new RemoteFetchCoreException("Unable to retrieve remote fetch configuration list " +
                    message);
        }

        int maximumItemsPerPage = RemoteFetchConfigurationUtils.getMaximumItemsPerPage();
        if (limit > maximumItemsPerPage) {
            if (log.isDebugEnabled()) {
                log.debug("Given limit exceed the maximum limit. Therefore we get the default limit from " +
                        "identity.xml. limit: " + maximumItemsPerPage);
            }
            limit = maximumItemsPerPage;
        }
        return limit;
    }

    /**
     * Validate offset.
     *
     * @param optionalOffset given offset value.
     * @return validated limit and offset value.
     * @throws RemoteFetchCoreException Error while set offset
     */
    private int validateOffset(OptionalInt optionalOffset) throws RemoteFetchCoreException {

        int offset = optionalOffset.orElse(0);

        if (offset < 0) {
            String message = "Invalid offset applied. Offset should not negative. offSet: " + offset;
            throw new RemoteFetchCoreException("Unable to retrieve remote fetch configuration list " + message);
        }
        return offset;
    }
}
