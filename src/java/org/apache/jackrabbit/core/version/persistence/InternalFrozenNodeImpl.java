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
package org.apache.jackrabbit.core.version.persistence;

import org.apache.jackrabbit.core.Constants;
import org.apache.jackrabbit.core.value.InternalValue;
import org.apache.jackrabbit.core.NodeImpl;
import org.apache.jackrabbit.core.PropertyImpl;
import org.apache.jackrabbit.core.QName;
import org.apache.jackrabbit.core.value.InternalValue;
import org.apache.jackrabbit.core.nodetype.NodeTypeImpl;
import org.apache.jackrabbit.core.state.ItemStateException;
import org.apache.jackrabbit.core.state.NodeState;
import org.apache.jackrabbit.core.state.PropertyState;
import org.apache.jackrabbit.core.version.InternalFreeze;
import org.apache.jackrabbit.core.version.InternalFrozenNode;
import org.apache.jackrabbit.core.version.InternalVersionItem;
import org.apache.jackrabbit.core.version.PersistentVersionManager;
import org.apache.jackrabbit.core.version.InternalFrozenVersionHistory;

import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.OnParentVersionAction;
import javax.jcr.version.VersionException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
class InternalFrozenNodeImpl extends InternalFreezeImpl
        implements InternalFrozenNode, Constants {

    /**
     * checkin mode init. specifies, that node is only initialized
     */
    public static final int MODE_INIT = 0;

    /**
     * checkin mode version. specifies, that the OPV value should be used to
     * determine the checkin behaviour.
     */
    public static final int MODE_VERSION = 1;

    /**
     * checkin mode copy. specifies, that the items are always copied.
     */
    private static final int MODE_COPY = 2;

    /**
     * mode flag specifies, that the mode should be recursed. otherwise i
     * will be redetermined by the opv.
     */
    private static final int MODE_COPY_RECURSIVE = 6;

    /**
     * the underlying persistance node
     */
    private PersistentNode node;

    /**
     * the list of frozen properties
     */
    private PropertyState[] frozenProperties;

    /**
     * the frozen uuid of the original node
     */
    private String frozenUUID = null;

    /**
     * the frozen primary type of the orginal node
     */
    private QName frozenPrimaryType = null;

    /**
     * the frozen list of mixin types of the original node
     */
    private QName[] frozenMixinTypes = null;

    /**
     * Creates a new frozen node based on the given persistance node.
     *
     * @param node
     * @throws javax.jcr.RepositoryException
     */
    protected InternalFrozenNodeImpl(PersistentVersionManager vMgr,
                                     PersistentNode node,
                                     InternalVersionItem parent) throws RepositoryException {
        super(vMgr, parent);
        this.node = node;

        // init the frozen properties
        PropertyState[] props;
        try {
            props = node.getProperties();
        } catch (ItemStateException e) {
            throw new RepositoryException(e);
        }
        List propList = new ArrayList();

        for (int i = 0; i < props.length; i++) {
            PropertyState prop = props[i];
            if (prop.getName().equals(JCR_FROZENUUID)) {
                // special property
                frozenUUID = node.getPropertyValue(JCR_FROZENUUID).internalValue().toString();
            } else if (prop.getName().equals(JCR_FROZENPRIMARYTYPE)) {
                // special property
                frozenPrimaryType = (QName) node.getPropertyValue(JCR_FROZENPRIMARYTYPE).internalValue();
            } else if (prop.getName().equals(JCR_FROZENMIXINTYPES)) {
                // special property
                InternalValue[] values = node.getPropertyValues(JCR_FROZENMIXINTYPES);
                if (values == null) {
                    frozenMixinTypes = new QName[0];
                } else {
                    frozenMixinTypes = new QName[values.length];
                    for (int j = 0; j < values.length; j++) {
                        frozenMixinTypes[j] = (QName) values[j].internalValue();
                    }
                }
            } else if (prop.getName().equals(JCR_PRIMARYTYPE)) {
                // ignore
            } else if (prop.getName().equals(JCR_UUID)) {
                // ignore
            } else {
                propList.add(prop);
            }
        }
        frozenProperties = (PropertyState[]) propList.toArray(new PropertyState[propList.size()]);

        // do some checks
        if (frozenMixinTypes == null) {
            frozenMixinTypes = new QName[0];
        }
        if (frozenPrimaryType == null) {
            throw new RepositoryException("Illegal frozen node. Must have 'frozenPrimaryType'");
        }
        // init the frozen child nodes
        /*
        PersistentNode[] childNodes = node.getChildNodes();
        frozenChildNodes = new InternalFreeze[childNodes.length];
        for (int i = 0; i < childNodes.length; i++) {
        if (childNodes[i].hasProperty(JCR_FROZEN_PRIMARY_TYPE)) {
        frozenChildNodes[i] = new InternalFrozenNode(this, childNodes[i]);
        } else if (childNodes[i].hasProperty(JCR_VERSION_HISTORY)) {
        frozenChildNodes[i] = new InternalFrozenVersionHistory(this, childNodes[i]);
        } else {
        // unkown ?
        }
        }
        */

    }

    /**
     * Returns the name of this frozen node
     *
     * @return
     */
    public QName getName() {
        return node.getName();
    }

    public String getId() {
        return node.getUUID();
    }

    /**
     * {@inheritDoc}
     */
    public InternalFreeze[] getFrozenChildNodes() throws VersionException {
        try {
            // maybe add iterator?
            List entries = node.getState().getChildNodeEntries();
            InternalFreeze[] freezes = new InternalFreeze[entries.size()];
            Iterator iter = entries.iterator();
            int i = 0;
            while (iter.hasNext()) {
                NodeState.ChildNodeEntry entry = (NodeState.ChildNodeEntry) iter.next();
                freezes[i++] = (InternalFreeze) getVersionManager().getItem(entry.getUUID());
            }
            return freezes;
        } catch (RepositoryException e) {
            throw new VersionException("Unable to retrieve frozen child nodes", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasFrozenHistory(String uuid) {
        try {
            List entries = node.getState().getChildNodeEntries(uuid);
            if (entries.size() > 0) {
                return getVersionManager().getItem(uuid) instanceof InternalFrozenVersionHistory;
            }
        } catch (RepositoryException e) {
            // ignore
        }
        return false;
    }

    /**
     * Returns the list of frozen properties
     *
     * @return
     */
    public PropertyState[] getFrozenProperties() {
        return frozenProperties;
    }

    /**
     * Returns the frozen UUID
     *
     * @return
     */
    public String getFrozenUUID() {
        return frozenUUID;
    }

    /**
     * Returns the frozen primary type
     *
     * @return
     */
    public QName getFrozenPrimaryType() {
        return frozenPrimaryType;
    }

    /**
     * Returns the list of the frozen mixin types
     *
     * @return
     */
    public QName[] getFrozenMixinTypes() {
        return frozenMixinTypes;
    }

    /**
     * Checks-in a <code>src</code> node. It creates a new child node of
     * <code>parent</code> with the given <code>name</code> and adds the
     * source nodes properties according to their OPV value to the
     * list of frozen properties. It creates frozen child nodes for each child
     * node of <code>src</code> according to its OPV value.
     *
     * @param parent
     * @param name
     * @param src
     * @return
     * @throws RepositoryException
     */
    protected static PersistentNode checkin(PersistentNode parent, QName name,
                                            NodeImpl src, int mode)
            throws RepositoryException {

        PersistentNode node;

        // create new node
        node = parent.addNode(name, NativePVM.NT_REP_FROZEN, null);

        // initialize the internal properties
        if (src.isNodeType(MIX_REFERENCEABLE)) {
            node.setPropertyValue(JCR_FROZENUUID, InternalValue.create(src.getUUID()));
        }

        node.setPropertyValue(JCR_FROZENPRIMARYTYPE,
                InternalValue.create(((NodeTypeImpl) src.getPrimaryNodeType()).getQName()));

        if (src.hasProperty(NodeImpl.JCR_MIXINTYPES)) {
            NodeType[] mixins = src.getMixinNodeTypes();
            InternalValue[] ivalues = new InternalValue[mixins.length];
            for (int i = 0; i < mixins.length; i++) {
                ivalues[i] = InternalValue.create(((NodeTypeImpl) mixins[i]).getQName());
            }
            node.setPropertyValues(JCR_FROZENMIXINTYPES, PropertyType.NAME, ivalues);
        }
        if (mode != MODE_INIT) {
            // add the properties
            PropertyIterator piter = src.getProperties();
            while (piter.hasNext()) {
                PropertyImpl prop = (PropertyImpl) piter.nextProperty();
                int opv;
                if ((mode & MODE_COPY) > 0) {
                    opv = OnParentVersionAction.COPY;
                } else {
                    opv = prop.getDefinition().getOnParentVersion();
                }
                switch (opv) {
                    case OnParentVersionAction.ABORT:
                        parent.reload();
                        throw new VersionException("Checkin aborted due to OPV in " + prop.safeGetJCRPath());
                    case OnParentVersionAction.COMPUTE:
                    case OnParentVersionAction.IGNORE:
                    case OnParentVersionAction.INITIALIZE:
                        break;
                    case OnParentVersionAction.VERSION:
                    case OnParentVersionAction.COPY:
                        node.copyFrom(prop);
                        break;
                }
            }


            // add the frozen children and vistories
            NodeIterator niter = src.getNodes();
            while (niter.hasNext()) {
                NodeImpl child = (NodeImpl) niter.nextNode();
                int opv;
                if ((mode & MODE_COPY_RECURSIVE) > 0) {
                    opv = OnParentVersionAction.COPY;
                } else {
                    opv = child.getDefinition().getOnParentVersion();
                }
                switch (opv) {
                    case OnParentVersionAction.ABORT:
                        throw new VersionException("Checkin aborted due to OPV in " + child.safeGetJCRPath());
                    case OnParentVersionAction.COMPUTE:
                    case OnParentVersionAction.IGNORE:
                    case OnParentVersionAction.INITIALIZE:
                        break;
                    case OnParentVersionAction.VERSION:
                        if (child.isNodeType(MIX_VERSIONABLE)) {
                            // create frozen versionable child
                            PersistentNode newChild = node.addNode(child.getQName(), NativePVM.NT_REP_FROZEN_HISTORY, null);
                            newChild.setPropertyValue(JCR_VERSIONHISTORY,
                                    InternalValue.create(child.getVersionHistory().getUUID()));
                            newChild.setPropertyValue(JCR_BASEVERSION,
                                    InternalValue.create(child.getBaseVersion().getUUID()));
                            break;
                        }
                        // else copy but do not recurse
                        checkin(node, child.getQName(), child, MODE_COPY);
                        break;
                    case OnParentVersionAction.COPY:
                        checkin(node, child.getQName(), child, MODE_COPY_RECURSIVE);
                        break;
                }
            }
        }
        return node;
    }

}
