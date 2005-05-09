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
package org.apache.jackrabbit.session.nodetype;

import javax.jcr.nodetype.ItemDefinition;
import javax.jcr.nodetype.NodeType;

import org.apache.jackrabbit.session.SessionHelper;
import org.apache.jackrabbit.state.nodetype.ItemDefinitionState;

/**
 * Immutable and session-bound item definition frontend. An instance
 * of this class presents the underlying item definition state using
 * the JCR ItemDef interface.
 * <p>
 * By not exposing the setter methods of the underlying state instance,
 * this class intentionally makes it impossible for a JCR client to modify
 * item definition information.
 */
public class SessionItemDefinition implements ItemDefinition {

    /** Helper for accessing the current session. */
    private final SessionHelper helper;

    /** The node type that contains this item definition. */
    private final NodeType type;

    /** The underlying item definitions state. */
    private final ItemDefinitionState state;

    /**
     * Creates an item definition frontend that is bound to the
     * given node type, session, and underlying item definition state.
     *
     * @param helper helper for accessing the current session
     * @param type declaring node type
     * @param state underlying item definition state
     */
    protected SessionItemDefinition(
            SessionHelper helper, NodeType type, ItemDefinitionState state) {
        this.helper = helper;
        this.type = type;
        this.state = state;
    }

    /**
     * Returns the node type that contains this item definition.
     *
     * @return declaring node type
     * @see ItemDefinition#getDeclaringNodeType()
     */
    public NodeType getDeclaringNodeType() {
        return type;
    }

    /**
     * Returns the prefixed JCR name of this item definition. The underlying
     * qualified name is mapped to a prefixed JCR name using the namespace
     * mappings of the current session.
     *
     * @return prefixed JCR name
     * @see ItemDefinition#getName()
     */
    public String getName() {
        return helper.getName(state.getName());
    }

    /**
     * Returns the value of the AutoCreated item definition property.
     * The returned value is retrieved from the underlying item
     * definition state.
     *
     * @return AutoCreated property value
     * @see ItemDefinition#isAutoCreated()
     */
    public boolean isAutoCreated() {
        return state.isAutoCreated();
    }

    /**
     * Returns the value of the Mandatory item definition property.
     * The returned value is retrieved from the underlying item
     * definition state.
     *
     * @return Mandatory property value
     * @see ItemDefinition#isMandatory()
     */
    public boolean isMandatory() {
        return state.isMandatory();
    }

    /**
     * Returns the value of the OnParentVersion item definition property.
     * The returned value is retrieved from the underlying item
     * definition state.
     *
     * @return OnParentVersion property value
     * @see ItemDefinition#getOnParentVersion()
     */
    public int getOnParentVersion() {
        return state.getOnParentVersion();
    }

    /**
     * Returns the value of the Protected item definition property.
     * The returned value is retrieved from the underlying item
     * definition state.
     *
     * @return Protected property value
     * @see ItemDefinition#isProtected()
     */
    public boolean isProtected() {
        return state.isProtected();
    }

}
