/*
 * Copyright 2004-2005 The Apache Software Foundation or its licensors,
 *                     as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.rmi.remote;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jcr.RepositoryException;

/**
 * Remote version of the JCR {@link javax.jcr.Workspace Workspace} interface.
 * Used by the
 * {@link org.apache.jackrabbit.rmi.server.ServerWorkspace ServerWorkspace}
 * and
 * {@link org.apache.jackrabbit.rmi.client.ClientWorkspace ClientWorkspace}
 * adapters to provide transparent RMI access to remote workspaces.
 * <p>
 * Most of the methods in this interface are documented only with a reference
 * to a corresponding Workspace method. In these cases the remote object
 * will simply forward the method call to the underlying Workspace instance.
 * Complex return values like namespace registries and other objects are
 * returned as remote references to the corresponding remote interface. Simple
 * return values and possible exceptions are copied over the network
 * to the client. RMI errors are signalled with RemoteExceptions.
 *
 * @author Jukka Zitting
 * @see javax.jcr.Workspace
 * @see org.apache.jackrabbit.rmi.client.ClientWorkspace
 * @see org.apache.jackrabbit.rmi.server.ServerWorkspace
 */
public interface RemoteWorkspace extends Remote {

    /**
     * @see javax.jcr.Workspace#getName()
     * @throws RemoteException on RMI errors
     */
    String getName() throws RemoteException;

    /**
     * @see javax.jcr.Workspace#copy(java.lang.String,java.lang.String)
     * @throws RemoteException on RMI errors
     */
    void copy(String from, String to)
            throws RepositoryException, RemoteException;

    /**
     * @see javax.jcr.Workspace#copy(java.lang.String,java.lang.String,java.lang.String)
     * @throws RemoteException on RMI errors
     */
    void copy(String from, String to, String workspace)
            throws RepositoryException, RemoteException;

    /**
     * @see javax.jcr.Workspace#clone(java.lang.String, java.lang.String, java.lang.String, boolean)
     * @throws RemoteException on RMI errors
     */
    void clone(String from, String to, String workspace, boolean removeExisting)
            throws RepositoryException, RemoteException;

    /**
     * @see javax.jcr.Workspace#move(java.lang.String, java.lang.String)
     * @throws RemoteException on RMI errors
     */
    void move(String from, String to)
            throws RepositoryException, RemoteException;
    
    /**
     * @see javax.jcr.Workspace#getNodeTypeManager()
     * @throws RemoteException on RMI errors
     */
    RemoteNodeTypeManager getNodeTypeManager()
            throws RepositoryException, RemoteException;

    /**
     * @see javax.jcr.Workspace#getNamespaceRegistry()
     * @throws RemoteException on RMI errors
     */
    RemoteNamespaceRegistry getNamespaceRegistry()
            throws RepositoryException, RemoteException;

    /**
     * @seejavax.jcr.Workspace#getQueryManager()
     * @throws RemoteException on RMI errors
     */
    RemoteQueryManager getQueryManager()
            throws RepositoryException, RemoteException;

    /**
     * @see javax.jcr.Workspace#getAccessibleWorkspaceNames()
     * @throws RemoteException on RMI errors
     */
    String[] getAccessibleWorkspaceNames()
            throws RepositoryException, RemoteException;

    /**
     * @see javax.jcr.Workspace#importXML(java.lang.String, byte[], int)
     * @throws RemoteException on RMI errors
     */
    void importXML(String path, byte[] xml, int uuidBehaviour)
            throws IOException, RepositoryException, RemoteException;

}
