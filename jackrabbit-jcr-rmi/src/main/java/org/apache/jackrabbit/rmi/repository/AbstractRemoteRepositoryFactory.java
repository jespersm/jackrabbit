/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.rmi.repository;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.rmi.client.LocalAdapterFactory;
import org.apache.jackrabbit.rmi.remote.RemoteRepository;

/**
 * @deprecated RMI support is deprecated and will be removed in a future version of Jackrabbit; see <a href=https://issues.apache.org/jira/browse/JCR-4972 target=_blank>Jira ticket JCR-4972</a> for more information.
 * <p>
 * Abstract base class for repository factories that make a remote repository
 * available locally. Subclasses need to implement the
 * {@link #getRemoteRepository()} method to actually retrieve the remote
 * repository reference.
 *
 * @since 1.4
 */
@Deprecated public abstract class AbstractRemoteRepositoryFactory
        implements RepositoryFactory {

    /**
     * Local adapter factory.
     */
    private final LocalAdapterFactory factory;

    /**
     * Creates a factory for looking up a repository from the given RMI URL.
     *
     * @param factory local adapter factory
     */
    protected AbstractRemoteRepositoryFactory(LocalAdapterFactory factory) {
        this.factory = factory;
    }

    /**
     * Returns a local adapter for the remote repository.
     *
     * @return local adapter for the remote repository
     * @throws RepositoryException if the remote repository is not available
     */
    public Repository getRepository() throws RepositoryException {
        return factory.getRepository(getRemoteRepository());
    }

    /**
     * Returns the remote repository reference.
     *
     * @return remote repository reference
     * @throws RepositoryException if the remote repository is not available
     */
    protected abstract RemoteRepository getRemoteRepository()
            throws RepositoryException;

}
