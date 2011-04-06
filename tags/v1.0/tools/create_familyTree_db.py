#!/usr/bin/env python
# coding: utf-8
#
# @(#) tools/create_familyTree_db.py
#
# WARNING: if you change any of the "magic values" in this file, then do not
# forget to update the source file familyBrowser/database/NodeModel.java too
#

"""
   Create sqlite3 database "familyTree.db" from source "familyTree.yaml"
"""

import codecs
import os
import sqlite3
import yaml

#--------------------------------------- constants

# input filename
FAMILY_TREE_YAML = 'familyTree.yaml'
# output filename
FAMILY_TREE_SQLITE = 'familyTree.db'

# categorize some fields used inside FAMILY_TREE_YAML
SURE_PARENTS = 'parent', 'parent2', 'father', 'mother',
PROBABLE_PARENTS = 'parent?', 'father?',
PARENT_FIELDS = SURE_PARENTS + PROBABLE_PARENTS
PARTNER_FIELDS = 'partner', 'partner2', 'parter3', 'partner4',
LINKING_FIELDS = PARENT_FIELDS + PARTNER_FIELDS

# the DEFAULT_NODE_ID defines the node where the FamilyBrowser application
# starts browsing at - if you change this value, then you need to update it
# in the Java source code too, familyBrowser/database/NodeModel.java
DEFAULT_NODE_ID = 1000000
# the yaml key that will be made to have id = DEFAULT_NODE_ID
# by the way, "Sofía Chaves" is David Chaves' daugther
DEFAULT_NODE_KEY = u'(Sofía Cristina Chaves Chen,1995)'

# define all the "node attributes" metadata; remember that
# fields will be displayed in numeric order by attribute_id
ATTRIBUTES = (
    # columns: attribute_id, field_name_in_yaml_file, unicode_label, hidden_flag
    (  1, 'name', u'Name', True ),
    (  2, 'aka', u'Also known as', False ),
    (  3, 'aka2', u'Also known as', False ),
    (  4, 'aka3', u'Also known as', False ),
    (  5, 'gender', u'Gender', False ),
    (  6, 'profession', u'Profession', False ),
    (  7, 'country', u'Country', False ),
    ( 10, 'birthdate', u'Birth', False ),
    ( 11, 'birthplace', u'At', False ),
    ( 12, 'birthplace?', u'Probably at', False ),
    ( 20, 'deathdate', u'Death', False ),
    ( 21, 'deathplace', u'At', False ),
    ( 50, 'father', u'Father', True ),
    ( 51, 'mother', u'Mother', True ),
    ( 52, 'parent', u'Parent', True ),
    ( 53, 'parent2', u'Parent', True ),
    ( 54, 'parent?', u'Probable parent', True ),
    ( 60, 'partner', u'Partner', False ),
    ( 61, 'weddingdate', u'Wedding', False ),
    ( 62, 'weddingplace', u'At', False ),
    ( 65, 'partner2', u'Second partner', False ),
    ( 66, 'weddingdate2', u'Second wedding', False ),
    ( 67, 'weddingplace2', u'At', False ),
    ( 68, 'partner3', u'Third partner', False ),
    ( 69, 'weddingdate3', u'Third wedding', False ),
    ( 70, 'weddingplace3', u'At', False ),
    ( 90, 'child', u'Child', True ),
    ( 91, 'sibling', u'Sibling', True ),
    ( 95, 'comments', u'More details', False ),
)

# next attribute_id(s) are shared with the Java sources, and
# they are defined inside the ATTRIBUTES table [see above]
PARENT_ATTRIBUTES = 50, 51, 52, 53, 54,
NAME_ATTRIBUTE = 1
CHILD_ATTRIBUTE = 90
SIBLING_ATTRIBUTE = 91

#--------------------------------------- general utilities

def utf8(value):
    """
        convert utf8 strings into unicode
    """
    if isinstance(value, unicode):
        return value.encode('utf8')
    return str(value)

def commize(sequence):
    """
        convert sequence into string, adding commas
    """
    return ', '.join((str(elem) for elem in sequence))

def log_print(*values):
    """
        generate logging
    """
    for value in values:
        print utf8(value),
    print

def log_info(*values):
    """
        generate logging, similar to Android's Log.i()
    """
    log_print(*values)
    return

def log_verbose(*values):
    """
        generate logging, similar to Android's Log.v()
    """
    #log_print(' ', *values)
    return

def get_project_directory():
    """
        return the project directory
    """
    # this function assumes that this script lives in <ProjectDirectory>/tools/
    return os.path.normpath(os.path.join(os.path.dirname(__file__), '..'))

def load_familyTree_dictionary(project_dir):
    """
        load the yaml file into a Python dictionary
    """
    file = os.path.join(project_dir, FAMILY_TREE_YAML)
    text = codecs.open(file, 'r', encoding = 'utf8').read()
    return yaml.safe_load(text)

#--------------------------------------- family tree dictionary

def extract_name(key):
    """
        extract the person's name from the yaml key
    """
    # we assume that the "key" is like "(Full Name, blah...)"
    fields = key.lstrip('(').rstrip(')').split(',')
    return fields[0]

def update_node_name(family_tree, node, key):
    """
        update the 'name' attribute, if it does not exist
    """
    name = node.get('name', '')
    if not name:
        name = extract_name(key)
    node['name'] = name
    return name

def update_node_gender(family_tree, node, key):
    """
        update the 'gender' attribute

        this function is needed because the yaml file uses
        'M' and 'F' for gender, but we want to display it
        as 'Male' and 'Female' instead
    """
    gender = node.get('gender', '').upper()
    if gender.startswith('M'):
        gender = 'Male'
    if gender.startswith('F'):
        gender = 'Female'
    node['gender'] = gender
    return gender

def update_node_internal_weight(family_tree, node, key):
    """
        compute the 'internal weight' attribute

        the 'internal weight' allows us to sort the nodes
        in a collating sequence where the first nodes have
        more ancestors that the last nodes

        we use the 'internal weight' to sort node_id(s)
        in node lists (parents/partners/children/etc...),
        since we want to display in the top the nodes with
        more parents
    """
    # the "internal weight" of one node is 1 plus
    # the internal weight of all the parent nodes
    weight = node.get('.weight', 0)
    if not weight:
        weight = 1
        for parent_field in PARENT_FIELDS:
            parent_key = node.get(parent_field, None)
            if parent_key is not None:
                parent_node = family_tree.get(parent_key, None)
                if parent_node is not None:
                    weight += update_node_internal_weight(family_tree, parent_node, parent_key)
    node['.weight'] = weight
    return weight

def for_all_nodes(family_tree, functor):
    """
        apply functor to all nodes in the family_tree
    """
    for key in family_tree:
        node = family_tree[key]
        functor(family_tree, node, key)

def keys_sorted_by_weight(family_tree):
    """
        return the family_tree keys,
        sorted by the node's internal weight
    """
    def compare(a, b):
        node_a = family_tree[a]
        node_b = family_tree[b]
        # reverse order by weight
        value_a = node_a['.weight']
        value_b = node_b['.weight']
        if value_a < value_b:
            return +1 # descending order
        elif value_b < value_a:
            return -1 # descending order
        # normal order by birthdate, if available
        if node_a.get('birthdate', None) and node_b.get('birthdate', None):
            value_a = node_a['birthdate']
            value_b = node_b['birthdate']
            if value_a < value_b:
                return -1 # ascending order
            elif value_b < value_a:
                return +1 # ascending order
        if node_a.get('birthdate', None):
            return -1 # ascending order
        if node_b.get('birthdate', None):
            return +1 # ascending order
        # normal order by name
        value_a = node_a['name']
        value_b = node_b['name']
        if value_a < value_b:
            return -1 # ascending order
        elif value_b < value_a:
            return +1 # ascending order
        return 0
    return sorted(family_tree.keys(), cmp = compare)

def keys_sorted_by_IDs(family_tree):
    """
        return the family_tree keys,
        sorted by the node's internal id (node_id)
    """
    def compare(a, b):
        node_a = family_tree[a]
        node_b = family_tree[b]
        return node_a['.id'] - node_b['.id']
    return sorted(family_tree.keys(), cmp = compare)

def get_family_tree_fields(family_tree):
    """
        return a sequence of all fields used on all nodes
    """
    fields = set()
    for node in family_tree.itervalues():
        fields.update(set(node.keys()))
    return sorted(fields)

def assign_family_tree_internal_IDs(family_tree):
    """
       assign node_id(s) to all nodes in the family_tree,
       using the 'internal weight' to sort them, and
       ensuring that the right node has node_id = DEFAULT_NODE_ID
    """
    internal_id = 1
    for key in keys_sorted_by_weight(family_tree):
        node = family_tree[key]
        node['.id'] = internal_id
        internal_id += 1
    # be sure that the DEFAULT_NODE_KEY has the id DEFAULT_NODE_ID
    node = family_tree[DEFAULT_NODE_KEY]
    offset = node['.id']
    for node in family_tree.itervalues():
        node['.id'] += (DEFAULT_NODE_ID - offset)

def print_family_tree_summary(family_tree):
    """
        print family_tree summary
    """
    log_info('familyTree - node count:', len(family_tree))
    for field in get_family_tree_fields(family_tree):
        log_verbose('Field:', field)
    for key in keys_sorted_by_IDs(family_tree):
        node = family_tree[key]
        name = node['name']
        log_verbose('Name:', name, ':', node['.id'], ':', node['.weight'])

def load_family_tree():
    """
        load the family_tree from the yaml file, and
        fix/update some of the node fields
    """
    log_info('. loading familyTree data')
    project_dir = get_project_directory()
    family_tree = load_familyTree_dictionary(project_dir)
    for_all_nodes(family_tree, update_node_name)
    for_all_nodes(family_tree, update_node_gender)
    for_all_nodes(family_tree, update_node_internal_weight)
    assign_family_tree_internal_IDs(family_tree)
    print_family_tree_summary(family_tree)
    return family_tree

#--------------------------------------- sqlite3

def open_family_tree_db(project_dir):
    """
       open/create an empty sqlite3 database
    """
    file = os.path.join(project_dir, FAMILY_TREE_SQLITE)
    # always create a new database - remove old one if it exists
    if os.path.exists(file):
        os.unlink(file)
    # create an empty database in AutoCommit mode
    conn = sqlite3.connect(file, isolation_level = None)
    # Note: the SQLite file format is cross-platform.
    # A database file written on one machine can be copied to and
    # used on a different machine with a different architecture.
    # Big-endian or little-endian, 32-bit or 64-bit does not matter.
    # All machines use the same file format
    return conn

def close_family_tree_db(conn):
    """
       close the sqlite3 database
    """
    conn.close()

def create_android_tables(conn):
    """
       create all sqlite3 tables that Android uses internally
    """
    log_info('. creating system tables')
    # create android_metadata - this table is needed by Android
    # @see http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/
    conn.execute("""
        CREATE TABLE IF NOT EXISTS android_metadata (locale TEXT);
    """)
    conn.commit()
    conn.execute("""
        INSERT INTO android_metadata VALUES ('en_US');
    """)
    conn.commit()

def create_family_tree_tables(conn):
    """
       create all sqlite3 tables that we use

       remember to update the Java source database/NodeModel.java
       if you change any of the table names or columns
    """
    log_info('. creating application tables')
    # create tables - these tables have no "_id" column,
    # because we do not use cursor adaptors in Android
    conn.execute("""
        create table attrs_tab (
              attr_id     integer not null
            , attr_hidden integer not null default 0
            , attr_value  text not null
        );
    """)
    conn.execute("""
        create table nodes_tab (
              node_id     integer not null
            , attr_id     integer not null
            , link_id     integer null default null
            , text_value  text not null
        );
    """)
    conn.commit()

def create_family_tree_indexes(conn):
    """
        create all sqlite3 indexes we use in here
    """
    log_info('. creating temporary indexes')
    conn.execute("""
        create unique index nodes_idx on
            nodes_tab(node_id, attr_id, link_id);
    """)
    conn.execute("""
        create index nodes_idx2 on
            nodes_tab(link_id, attr_id);
    """)
    conn.commit()

def vacuum_family_tree_db(conn):
    """
        vacuum the sqlite3 database in order to
        optimize its size

        we also remove all the indexes that we created
        and used in here, since the Android application
        really needs a different set of indexes
    """
    log_info('. cleaning up database')
    conn.commit()
    conn.execute("drop index nodes_idx;")
    conn.execute("drop index nodes_idx2;")
    conn.commit()
    conn.execute("vacuum;")
    conn.commit()

def populate_attributes_table(conn):
    """
        populate the sqlite3 table containing
        the ATTRIBUTES metadata
    """
    log_info('. populating "attributes" table')
    curs = conn.cursor()
    attributes_map = {}
    for attribute in ATTRIBUTES:
        internal_id, internal_name, label, hidden = attribute
        attributes_map[internal_name] = internal_id
        log_verbose('Adding attribute', internal_name, ':', internal_id)
        curs.execute("""
            insert into attrs_tab(attr_id, attr_hidden, attr_value)
            values (?, ?, ?);
        """, (internal_id, 1 if hidden else 0, label,))
    curs.close()
    conn.commit()
    return attributes_map

def populate_nodes_table(conn, family_tree, attributes_map):
    """
        populate the sqlite3 table containing
        the node data from the family_tree dictionary
    """
    log_info('. populating "nodes" table')
    curs = conn.cursor()

    def attr_id_map(a):
        try:
            return int(attributes_map[a])
        except:
            return None

    def compare(a, b):
        value_a = attr_id_map(a)
        value_b = attr_id_map(b)
        if value_a is not None and value_b is not None:
            return value_a - value_b
        if value_a is not None:
            return -1
        if value_b is not None:
            return +1
        return 0

    for key in keys_sorted_by_IDs(family_tree):
        node = family_tree[key]
        node_id = node['.id']
        for attrib_key in sorted(node.keys(), cmp = compare):
            try:
                text_value = unicode(node[attrib_key])
                attr_id = attributes_map[attrib_key]
            except:
                log_verbose('ignore', key, ':', attrib_key)
                continue
            if attrib_key in LINKING_FIELDS:
                link_node = family_tree[text_value]
                log_verbose('Adding', key, ':', attrib_key, ':', link_node['name'], '=', link_node['.id'], ':', attr_id)
                curs.execute("""
                    insert into nodes_tab(node_id, attr_id, link_id, text_value)
                    values (?, ?, ?, ?);
                """, (node_id, attr_id, link_node['.id'], link_node['name'],))
            else:
                log_verbose('Adding', key, ':', attrib_key, ':', text_value, ':', attr_id)
                curs.execute("""
                    insert into nodes_tab(node_id, attr_id, text_value)
                    values (?, ?, ?);
                """, (node_id, attr_id, text_value,))

    curs.close()
    conn.commit()

def populate_materialized_view(conn, family_tree, sql_stmt, attr_id):
    """
       populate some useful materialized views - these views allow
       us to write simpler sql statements inside the Android application,
       since we pre-compute all joins and other complex SELECT statements
       that return children and siblings for any node_id

       notice we use the same node table to store the materialized views

       @see http://en.wikipedia.org/wiki/Materialized_view
    """

    def fetch_links(node_id):
        curs = conn.cursor()
        curs.execute(sql_stmt, (node_id,))
        link_map = {}
        for row in curs:
            link_id, text_value = int(row[0]), unicode(row[1])
            link_map[link_id] = text_value
        curs.close()
        return link_map

    def save_links(key, node_id, link_map):
        curs = conn.cursor()
        for link_id in sorted(link_map.keys()):
            text_value = link_map[link_id]
            log_verbose('Adding', key, ':', text_value, '=', link_id, ':', attr_id)
            curs.execute("""
                insert into nodes_tab(node_id, attr_id, link_id, text_value)
                values (?, ?, ?, ?);
            """, (node_id, attr_id, link_id, text_value,))
        curs.close()
        conn.commit()

    for key in keys_sorted_by_IDs(family_tree):
        node = family_tree[key]
        node_id = node['.id']
        save_links(key, node_id, fetch_links(node_id))

def populate_children_table(conn, family_tree):
    """
        populate the materialized view containing the children links
    """
    log_info('. adding "children" links')
    parent_attributes = commize(PARENT_ATTRIBUTES)
    sql_stmt = """
        select A.node_id, A.text_value
            from nodes_tab P
                join nodes_tab A
                  on A.node_id = P.node_id
                 and A.attr_id = %d
            where P.link_id = ?
              and P.attr_id in ( %s );
    """ % (NAME_ATTRIBUTE, parent_attributes)
    populate_materialized_view(conn, family_tree, sql_stmt, CHILD_ATTRIBUTE)

def populate_siblings_table(conn, family_tree):
    """
        populate the materialized view containing the siblings links
    """
    log_info('. adding "siblings" links')
    parent_attributes = commize(PARENT_ATTRIBUTES)
    sql_stmt = """
        select A.node_id, A.text_value
            from nodes_tab P
                join nodes_tab O
                  on O.link_id = P.link_id
                 and O.node_id <> P.node_id
                 and O.attr_id in ( %s )
                join nodes_tab A
                  on A.node_id = O.node_id
                 and A.attr_id = %d
            where P.node_id = ?
              and P.attr_id in ( %s );
    """ % (parent_attributes, NAME_ATTRIBUTE, parent_attributes)
    populate_materialized_view(conn, family_tree, sql_stmt, SIBLING_ATTRIBUTE)

def create_family_tree_db(family_tree):
    """
       create and populate the sqlite3 database from family_tree
    """
    project_dir = get_project_directory()
    conn = open_family_tree_db(project_dir)
    create_android_tables(conn)
    create_family_tree_tables(conn)
    attributes_map = populate_attributes_table(conn)
    populate_nodes_table(conn, family_tree, attributes_map)
    create_family_tree_indexes(conn)
    populate_children_table(conn, family_tree)
    populate_siblings_table(conn, family_tree)
    vacuum_family_tree_db(conn)
    close_family_tree_db(conn)

#--------------------------------------- main

# main code: no command-line parameters needed
family_tree = load_family_tree()
create_family_tree_db(family_tree)

#--------------------------------------- The End
