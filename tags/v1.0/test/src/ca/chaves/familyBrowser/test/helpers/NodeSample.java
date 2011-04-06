package ca.chaves.familyBrowser.test.helpers;

import java.util.List;

import junit.framework.Assert;

import ca.chaves.familyBrowser.helpers.NodeObject;
import ca.chaves.familyBrowser.helpers.NodeValueList;

/**
 * This class represents a NodeObject data to be used inside unit tests.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeSample {

    /** This is the node id */
    private Integer m_id;
    /** This is the node name */
    private final String m_name;
    /** This is the parents list */
    private NodeSample[] m_parents;
    /** This is the siblings list */
    private NodeSample[] m_siblings;
    /**
     * This flag is true if this node was already visited in
     * assertEqualsAndAdd()
     */
    private boolean m_wasAlreadyVisited;

    /**
     * Constructor.
     *
     * @param name
     */
    public NodeSample(final String name) {
        this.m_name = name;
        Assert.assertNotNull(this.m_name);
    }

    /**
     * @return the nodeId
     */
    public Integer getNodeId() {
        return this.m_id;
    }

    /**
     * Sets the nodeId.
     *
     * @param nodeId
     */
    public void setNodeId(final int nodeId) {
        Assert.assertTrue(nodeId > 0);
        this.m_id = new Integer(nodeId);
        Assert.assertNotNull(this.m_id);
    }

    /**
     * @return the node name
     */
    public String getNodeName() {
        return this.m_name;
    }

    /**
     * Set the parents for this node.
     *
     * @param parents
     */
    public void setParents(final NodeSample... parents) {
        this.m_parents = parents;
    }

    /**
     * Set the siblings for this node.
     *
     * @param siblings
     */
    public void setSiblings(final NodeSample... siblings) {
        this.m_siblings = siblings;
    }

    /**
     * This is an internal function, to process a particular NodeValueList from
     * assertAndAdd().
     *
     * @param sampleList
     * @param listIndex
     * @param nodeObject
     * @param visitingQueue
     */
    private static void addListToVisitingQueue(final NodeSample[] sampleList, final int listIndex, final NodeObject nodeObject,
                    final List<NodeSample> visitingQueue) {
        Assert.assertTrue(0 <= listIndex);
        Assert.assertTrue(listIndex < NodeObject.LIST_MAX);
        Assert.assertNotNull(nodeObject);
        Assert.assertNotNull(visitingQueue);
        if (sampleList == null) {
            return;
        }
        final NodeValueList valueList = nodeObject.getNodeListValues(listIndex);
        Assert.assertNotNull(valueList);
        for (NodeSample sampleNode : sampleList) {
            sampleNode.m_id = valueList.findId(sampleNode.m_name);
            Assert.assertNotNull(sampleNode.m_id);
            visitingQueue.add(sampleNode);
        }
    }

    /**
     * Update the visiting queue with links from this node.
     *
     * @param nodeObject
     * @param visitingQueue
     */
    public void addToVisitingQueue(final NodeObject nodeObject, final List<NodeSample> visitingQueue) {
        if (!this.m_wasAlreadyVisited) {
            return;
        }
        this.m_wasAlreadyVisited = true;
        addListToVisitingQueue(this.m_parents, NodeObject.LIST_PARENTS, nodeObject, visitingQueue);
        addListToVisitingQueue(this.m_siblings, NodeObject.LIST_SIBLINGS, nodeObject, visitingQueue);
    }
}
