#!/usr/bin/make -f
# coding: utf-8
#
# @(#) Makefile for GNU Make
#
# USAGE
#
#   export PATH="$PATH:/path-to-android-sdk/tools"
#   export PATH="$PATH:$JAVA_HOME/bin"
#
#   make all
#
#   make release
#   make debug          # optional
#
# DESCRIPTION
#
#   We use this file in order to:
#
#     1. generate the sqlite database from yaml source
#     2. build Android binary packages
#
#   You can use Eclipse to do (2.), but
#   you still need to use this Makefile to do (1.) in Linux
#
# RUNNING THE COMMAND-LINE ANDROID EMULATOR
#
#   Create an Android device with name "FamilyBrowser_avd":
#     shell>  android
#     # goto Virtual Devices > New
#     # create new device FamilyBrowser_avd for Platform 1.6
#   Start the Android emulator:
#     shell>  emulator -avd FamilyBrowser_avd &
#     # creates a window with title "5554:FamilyBrowser_avd"
#   Install $(APK_DIRECTORY)/FamilyBrowser-debug.apk into the emulator:
#     shell>  adb install $(APK_DIRECTORY)/FamilyBrowser-debug.apk
#

_DROID_APP_NAME ?= FamilyBrowser
_DROID_APP_PACKAGE ?= ca.chaves.familyBrowser
_DROID_APP_VERSION ?= 2

_DROID_APP_KEYSTORE ?= share/certs/$(_DROID_APP_NAME).keystore

_DROID_TEST_NAME := $(_DROID_APP_NAME)Test
_DROID_TEST_PACKAGE := $(_DROID_APP_PACKAGE).test

# id 4 = Android 1.6 (API level 4)
_DROID_SDK_VERSION ?= 4

# where the final .apk files are created (inside test/, main/)
# Ant will use "build/", Eclipse will use "bin/"
#APK_DIRECTORY := build
APK_DIRECTORY := bin

#--------------------------------------- phony targets

all : release debug

clean :
	bash share/tools/build_app.bash clean

clobber :
	bash share/tools/build_app.bash clobber

# build a debug package
debug : \
    main/$(APK_DIRECTORY)/$(_DROID_APP_NAME)-debug.apk \
    test/$(APK_DIRECTORY)/$(_DROID_TEST_NAME)-debug.apk

# build a release package
release : \
    main/$(APK_DIRECTORY)/$(_DROID_APP_NAME)-release.apk \
    test/$(APK_DIRECTORY)/$(_DROID_TEST_NAME)-release.apk
	@cp -f main/$(APK_DIRECTORY)/$(_DROID_APP_NAME)-release.apk $(_DROID_APP_NAME).apk
	@echo "Upload $(_DROID_APP_NAME).apk to https://market.android.com/details?id=ca.chaves.familyBrowser"

.PHONY : all debug release clean clobber

#--------------------------------------- generated sources

sources : \
    android/jni/ca_chaves_android_util_POSIX.hpp

android/jni/ca_chaves_android_util_POSIX.hpp : debug
	javah -classpath android/bin/classes/ \
	    -o android/jni/ca_chaves_android_util_POSIX.hpp \
	    ca.chaves.android.util.POSIX

# remove trailing blanks - I do not like them
remove-trailing-blanks :
	sed -i -e "s/\s*$$//" Makefile
	find . \
	    -name \*.java	\
	    -o -name \*.xml	\
	    -o -name \*.yaml	\
	    -o -name \*.py	\
	    -o -name \*.txt	\
	    | xargs -r sed -i -e "s/\s*$$//"

.PHONY : sources remove-trailing-blanks

#--------------------------------------- file targets

main/build.xml main/project.properties main/local.properties \
test/build.xml test/project.properties test/local.properties :
	bash share/tools/build_app.bash init

main/$(APK_DIRECTORY)/$(_DROID_APP_NAME)-release.apk \
test/$(APK_DIRECTORY)/$(_DROID_TEST_NAME)-release.apk : \
    $(_DROID_APP_KEYSTORE) \
    main/tarball/databases/v$(_DROID_APP_VERSION).db
	bash share/tools/build_app.bash release

main/$(APK_DIRECTORY)/$(_DROID_APP_NAME)-debug.apk \
test/$(APK_DIRECTORY)/$(_DROID_TEST_NAME)-debug.apk : \
    $(_DROID_APP_KEYSTORE) \
    main/tarball/databases/v$(_DROID_APP_VERSION).db
	bash share/tools/build_app.bash debug

# process familyTree.yaml
main/tarball/databases/v$(_DROID_APP_VERSION).db : \
    familyTree.yaml \
    share/tools/familyTree_yaml.py
	bash share/tools/build_app.bash database

# create keystore for signing release packages
$(_DROID_APP_KEYSTORE) :
	bash share/tools/build_app.bash keystore

.NOTPARALLEL : \
    main/$(APK_DIRECTORY)/$(_DROID_APP_NAME)-debug.apk \
    main/$(APK_DIRECTORY)/$(_DROID_APP_NAME)-release.apk \
    test/$(APK_DIRECTORY)/$(_DROID_TEST_NAME)-debug.apk \
    test/$(APK_DIRECTORY)/$(_DROID_TEST_NAME)-release.apk

#--------------------------------------- The End
