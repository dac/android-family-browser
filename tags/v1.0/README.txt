#!/usr/bin/env less
# coding: utf-8
#
# @(#) README.txt
# By David Arturo Chaves <david@chaves.ca>

USING THE COMMAND-LINE ANDROID SDK TOOLS
========================================

We use the 'Makefile' file to build the package which is published in the Android Market.
This 'Makefile' can also be used to run automated unit-tests.
Please read the 'Makefile' file for more details

This makefile was originally written for 'android-sdk-linux_x86-1.6_r1' on Ubuntu 9.10

USING ECLIPSE FOR EDITING AND DEBUGGING
=======================================

You need to install the Android Development Tools (ADT) plugin for Eclipse,
see http://developer.android.com/sdk/eclipse-adt.html

Then, you need to create two Android projects, "FamilyBrowser" and "FamilyBrowserTest",
using File > New > Android Project:

  * Enter "Project name": FamilyBrowser
  * Turn on check-box "Create project on existing source"
  * In "Location", browse to the current directory (".")
  * Be sure that the Build Target "Android 1.6" becomes selected
  * Push the [Finish] button

  * Enter "Project name": FamilyBrowserTest
  * Turn on check-box "Create project on existing source"
  * In "Location", browse to the test sub-directory ("./test")
  * Be sure that the Build Target "Android 1.6" becomes selected
  * Push the [Finish] button

You still need to use the command-line 'Makefile' to process the 'familyTree.yaml' file

We originally used Eclipse 3.1 on Microsoft Windows 7

UPDATING / REPLACING THE FAMILY-TREE DATABASE
=============================================

The file 'familyTree.yaml' contains the Family Tree Database in YAML format

The script tools/create_familyTree_db.py is used to process this file and to
create a sqlite3 database file 'familyTree.db', which is later split in many
pieces like 'res/raw/family_tree_db_[0-7]' (by script tools/split.py), which
are used to build the final .apk file

Please use the command-line 'Makefile' to process the 'familyTree.yaml' file
once you modify or replace its content

If you replace this file completely, or if you want to have a different starting
point (which is currently the node named '(Sofía Cristina Chaves Chen,1995)'), then
you will need to update the value DEFAULT_NODE_KEY in tools/create_familyTree_db.py

If you replace this file completely, then you will need to update the unit-tests as
well, in source file test/src/.../database/SampleData.java

If you update this file, then consider increasing the value DATABASE_VERSION in
the source file database/NodeModel.java

You might want to read http://code.google.com/p/android-family-browser/wiki/Database

#--------------------------------------- The End
