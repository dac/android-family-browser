package ca.chaves.familyBrowser.test.database;

import ca.chaves.familyBrowser.database.NodeModel;
import ca.chaves.familyBrowser.test.helpers.NodeSample;

/**
 * This class returns data for use inside unit tests.
 *
 * @author "David Chaves <david@chaves.ca>"
 */
public class SampleData {

    /** i acute in lower case */
    private static final String i = "\u00ED";
    /** e acute in lower case */
    private static final String e = "\u00D9";
    /** u acute in lower case */
    private static final String u = "\u00FA";

    /**
     * Return the default node data.
     *
     * @return NodeSample object
     */
    public static NodeSample getDefaultNode() {
        /**
         * DEFAULT_NODE_NAME must be keep up to date with the DEFAULT_NODE_KEY
         * value in tools/create_familyTree_db.py, as it corresponds to the
         * "name" attribute for the node_id == NodeModel.DEFAULT_NODE_ID.
         */
        final String DEFAULT_NODE_NAME = "Sof" + i + "a Cristina Chaves Chen";
        final NodeSample Sofia_Chaves = new NodeSample(DEFAULT_NODE_NAME);
        Sofia_Chaves.setNodeId(NodeModel.DEFAULT_NODE_ID);
        return Sofia_Chaves;
    }

    /**
     * Return the root node of a sample tree.
     *
     * @return NodeSample object
     */
    public static NodeSample getSampleTree() {
        final NodeSample Alberto_Rojas = new NodeSample("Bernardo Alberto de Las Piedades Rojas Soto");
        final NodeSample Berta_Trejos = new NodeSample("Yn" + e + "s Berta de Las Piedades Trejos Calvo");
        final NodeSample Lucia_Trejos = new NodeSample("Luc" + i + "a del Socorro Trejos Calvo");
        Lucia_Trejos.setParents(Alberto_Rojas, Berta_Trejos);
        final NodeSample Modesto_Chaves = new NodeSample("Modesto Rosario de Jes" + u + "s Chaves Rodr" + i + "guez");
        final NodeSample David_Chaves = new NodeSample("David Arturo Chaves Trejos");
        David_Chaves.setParents(Modesto_Chaves, Lucia_Trejos);
        final NodeSample Fabiola_Chaves = new NodeSample("Ana Fabiola Chaves Trejos");
        Fabiola_Chaves.setParents(Modesto_Chaves, Lucia_Trejos);
        Fabiola_Chaves.setSiblings(David_Chaves);
        David_Chaves.setSiblings(Fabiola_Chaves);
        final NodeSample Sofia_Chaves = new NodeSample("Sof" + i + "a Cristina Chaves Chen");
        Sofia_Chaves.setParents(David_Chaves);
        Sofia_Chaves.setNodeId(NodeModel.DEFAULT_NODE_ID);
        return Sofia_Chaves;
    }
}
