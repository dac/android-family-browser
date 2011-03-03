#! /usr/bin/make -f
#
# @(#) Makefile
#
# USAGE
#
#   shell>  export PATH="$PATH:/path-to-android-sdk/tools"
#   shell>  make release
#
# DESCRIPTION
#
# We use this file in order to:
#
#   1. generate the sqlite database from yaml source
#   2. build Android binary packages
#
# You can use Eclipse to do (2.), but
# you still need to use this Makefile to do (1.) in Linux
#
# RUNNING THE COMMAND-LINE ANDROID EMULATOR
#
# Create an Android device with name "AndroidApp_avd":
#   shell>  android
#   # goto Virtual Devices > New
#   # create new device AndroidApp_avd for Platform 1.6
# Start the Android emulator:
#   shell>  emulator -avd AndroidApp_avd &
#   # creates a window with title "5554:AndroidApp_avd"
# Install AndroidApp-debug.apk into the running emulator:
#   shell>  make adb-debug
#

ANDROID_APPLICATION_NAME = .activities.FamilyBrowser

# id 4 = Android 1.6 (API level 4)
# use `android list targets` to see which APIs are available
ANDROID_TARGET_ID ?= 4

#--------------------------------------- phony targets

# build a debug package
debug : bin/$(ANDROID_APPLICATION_NAME)-debug.apk

# build a release package
release : bin/$(ANDROID_APPLICATION_NAME)-unsigned.apk

# remove all intermediate files
clean :
	rm -fr bin/* gen/*
	rm -fr res/raw/family_tree_db_* tools/familyTree.db

# clobber Ant files
ant-clobber : clean
	rm -fr build.xml local.properties

# clobber Eclipse files
eclipse-clobber : clean
	rm -fr .settings .project .classpath

clobber: ant-clobber eclipse-clobber

.PHONY : debug release clean clobber
.PHONY : ant-clobber eclipse-clobber

# spawn a debug package
adb-debug : bin/$(ANDROID_APPLICATION_NAME)-debug.apk
	adb install bin/$(ANDROID_APPLICATION_NAME)-debug.apk

# spawn a release package
adb-release : install bin/$(ANDROID_APPLICATION_NAME)-unsigned.apk
	adb install bin/$(ANDROID_APPLICATION_NAME)-unsigned.apk

.PHONY : adb-debug adb-release

#--------------------------------------- file targets

# process familyTree.yaml
tools/familyTree.db : tools/familyTree.yaml tools/create_familyTree_db.py
	python tools/create_familyTree_db.py
	@ls -l $@

# split database file
res/raw/family_tree_db_0 \
res/raw/family_tree_db_1 \
res/raw/family_tree_db_2 \
res/raw/family_tree_db_3 \
res/raw/family_tree_db_4 \
res/raw/family_tree_db_5 \
res/raw/family_tree_db_6 \
res/raw/family_tree_db_7 \
: tools/familyTree.db
	python tools/split.py -i tools/familyTree.db -o res/raw/family_tree_db_ -n 8
	@ls -l res/raw/family_tree_db_*

# generate Ant files
build.xml local.properties : res/raw/family_tree_db_0
	android update project -t $(ANDROID_TARGET_ID) -p .

# run Ant to build a debug package
bin/$(ANDROID_APPLICATION_NAME)-debug.apk : build.xml
	ant debug
	@ls -l $@

# run Ant to build a release unsigned package
bin/$(ANDROID_APPLICATION_NAME)-unsigned.apk : build.xml
	ant release
	@ls -l $@

#--------------------------------------- The End
