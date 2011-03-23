package ca.chaves.familyBrowser.database;

import ca.chaves.familyBrowser.R;

/**
 * This class encapsulates the database model used in here. The model consists
 * of two tables, one to store the NodeAttributes instances, and the other to
 * store the NodeObject instances. This class defines and uses many
 * "magic values", which were defined in the create_familyTree_db.py script.
 *
 * @warning please keep this file up-to-date with tools/create_familyTree_db.py
 * @author "David Chaves <david@chaves.ca>"
 */
public class NodeModel {

    /** This is the current database version */
    public static final int DATABASE_VERSION = 1;
    /** This is the database name - notice it includes the database version */
    public static final String DATABASE_NAME = "family_tree_v" + DATABASE_VERSION + ".db";

    /** @see "http://www.mail-archive.com/android-developers@googlegroups.com/msg28194.html" */
    public static final Integer[] DATABASE_RAW_FILES = new Integer[] {
                    new Integer(R.raw.family_tree_db_0), //
                    new Integer(R.raw.family_tree_db_1), //
                    new Integer(R.raw.family_tree_db_2), //
                    new Integer(R.raw.family_tree_db_3), //
                    new Integer(R.raw.family_tree_db_4), //
                    new Integer(R.raw.family_tree_db_5), //
                    new Integer(R.raw.family_tree_db_6), //
                    new Integer(R.raw.family_tree_db_7), //
    };

    // Attributes - table, index and column names

    private static final String ATTRIBUTES_TABLE = "attrs_tab";
    private static final String ATTRIBUTES_COLUMN_ATTRIBUTE_ID = "attr_id";
    private static final String ATTRIBUTES_COLUMN_HIDDEN_FLAG = "attr_hidden";
    private static final String ATTRIBUTES_COLUMN_TEXT_VALUE = "attr_value";

    // Nodes - table, index and column names

    private static final String NODES_TABLE = "nodes_tab";
    private static final String NODES_INDEX = "nodes_idx";
    private static final String NODES_COLUMN_NODE_ID = "node_id";
    private static final String NODES_COLUMN_ATTRIBUTE_ID = "attr_id";
    private static final String NODES_COLUMN_LINK_ID = "link_id";
    private static final String NODES_COLUMN_TEXT_VALUE = "text_value";

    /**
     * This is the magic NODES_COLUMN_ATTRIBUTE_ID for field "name" - the source
     * file tools/create_familyTree_db.py defines this value.
     */
    public static final int NAME_ATTRIBUTE_ID = 1;
    /**
     * This is the default node_id - we start browsing at this location - the
     * source file tools/create_familyTree_db.py defines this value.
     */
    public static final int DEFAULT_NODE_ID = 1000000;

    // Nodes - DDL statements

    /**
     * SQL statement to create the NodeObject table indexes.
     */
    public static final String CREATE_NODES_INDEX_SQL = //
    "create index if not exists " + NODES_INDEX + //
                    " on " + NODES_TABLE + " (" + //
                    " " + NODES_COLUMN_NODE_ID + //
                    "," + NODES_COLUMN_ATTRIBUTE_ID + //
                    "," + NODES_COLUMN_LINK_ID + //
                    ")";

    // Nodes - query statements

    /**
     * SQL statement to retrieve the node attributes of a given node_id.
     */
    public static final String SELECT_NODE_ATTRIBUTE_SQL = //
    "select " + NODES_COLUMN_ATTRIBUTE_ID + "," + NODES_COLUMN_TEXT_VALUE + //
                    " from " + NODES_TABLE + //
                    " where " + NODES_COLUMN_NODE_ID + " = ?" + //
                    " order by " + NODES_COLUMN_ATTRIBUTE_ID;

    // Nodes and Links - query statements

    /**
     * SQL statement to retrieve the parent node_id(s) of a given node_id. These
     * "50,51,...,54" are the NODES_COLUMN_ATTRIBUTE_ID(s) for fields "parent",
     * "father", "mother", and alike - the source file
     * tools/create_familyTree_db.py defines these values.
     */
    public static final String SELECT_NODE_PARENTS_SQL = createSelectLinkIdsStmt("50,51,52,53,54");
    /**
     * SQL statement to retrieve the partner node_id(s) of a given node_id.
     * These "60,65" are the NODES_COLUMN_ATTRIBUTE_ID(s) for fields "partner",
     * "spouse", and alike - the source file tools/create_familyTree_db.py
     * defines these values.
     */
    public static final String SELECT_NODE_PARTNERS_SQL = createSelectLinkIdsStmt("60,65");
    /**
     * SQL statement to retrieve the child node_id(s) of a given node_id. This
     * "90" is the NODES_COLUMN_ATTRIBUTE_ID for field "child" - the source file
     * tools/create_familyTree_db.py defines this value.
     */
    public static final String SELECT_NODE_CHILDREN_SQL = createSelectLinkIdsStmt("90");
    /**
     * SQL statement to retrieve the sibling node_id(s) of a given node_id. This
     * "91" is the NODES_COLUMN_ATTRIBUTE_ID for field "sibling" - the source
     * file tools/create_familyTree_db.py defines this value.
     */
    public static final String SELECT_NODE_SIBLINGS_SQL = createSelectLinkIdsStmt("91");

    /** This is an internal function, to help creating SQL SELECT statements */
    private static String createSelectLinkIdsStmt(final String attr_ids) {
        return "select " + NODES_COLUMN_LINK_ID + "," + NODES_COLUMN_TEXT_VALUE + //
                        " from " + NODES_TABLE + //
                        " where " + NODES_COLUMN_NODE_ID + " = ?" + //
                        " and " + NODES_COLUMN_ATTRIBUTE_ID + " in (" + attr_ids + ")" + //
                        " order by " + NODES_COLUMN_LINK_ID;
    }

    // Attributes - query statements

    /**
     * SQL statement to retrieve the global node attribute's associated values.
     */
    public static final String SELECT_ATTRIBUTES_SQL = //
    "select " + ATTRIBUTES_COLUMN_ATTRIBUTE_ID + "," + //
                    ATTRIBUTES_COLUMN_HIDDEN_FLAG + "," + //
                    ATTRIBUTES_COLUMN_TEXT_VALUE + //
                    " from " + ATTRIBUTES_TABLE;
}
