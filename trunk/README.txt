#!/usr/bin/env less
# coding: utf-8
#
# @(#) README.txt
# By David Arturo Chaves <david@chaves.ca>

USING THE COMMAND-LINE ANDROID SDK TOOLS
========================================

We use the 'Makefile' file to build the package which is published in
the Android Market.  This 'Makefile' can also be used to run automated
unit-tests.  Please read the 'Makefile' file for more details

UPDATING / REPLACING THE FAMILY-TREE DATABASE
=============================================

The file 'familyTree.yaml' contains the Family Tree Database in YAML
format.  The script share/tools/familyTree_yaml.py processes this file and
produces many other artifacts which are needed to produce the final
.apk package

Please use the command-line 'make' to process the 'familyTree.yaml' file
once you modify or replace its content

If you replace this file completely, or if you want to have a different
starting point (which is currently the node named
'(Sofía Cristina Chaves Chen,1995)'), then you will need to update the
value DEFAULT_NODE_KEY in share/tools/familyTree_yaml.py

If you replace this file completely, then you will need to update the
unit-tests as well, in source file test/src/.../sample/SampleData.java

If you update this file, then consider increasing the value
DATABASE_VERSION in the source file DatabaseSession.java

You might want to read
    http://code.google.com/p/android-family-browser/wiki/Database

WORKING WITH ECLIPSE
====================

See share/wiki/Eclipse.wiki for more information about

#--------------------------------------- The End
