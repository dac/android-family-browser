#! /usr/bin/make -f
#
# @(#) Makefile for GNU Make
#
# USAGE
#
#   shell>  export PATH="$PATH:/path-to-android-sdk/tools"
#   shell>  export PATH="$PATH:$JAVA_HOME/bin"
#   shell>  make release
#
#   shell>  make start-emulator
#   shell>  make install-release
#   shell>  make run-tests
#   shell>  make run-monkey
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
# Create an Android device with name "FamilyBrowser_avd":
#   shell>  android
#   # goto Virtual Devices > New
#   # create new device FamilyBrowser_avd for Platform 1.6
# Start the Android emulator:
#   shell>  emulator -avd FamilyBrowser_avd &
#   # creates a window with title "5554:FamilyBrowser_avd"
# Install bin/FamilyBrowser-debug.apk into the running emulator:
#   shell>  adb install bin/FamilyBrowser-debug.apk
#

MAIN_NAME := FamilyBrowser
MAIN_PACKAGE := ca.chaves.familyBrowser
MAIN_SOURCES := $(wildcard src/*/*/*/*/*.java) $(wildcard res/*/*.xml)

TEST_NAME := $(MAIN_NAME)Test
TEST_PACKAGE := $(MAIN_PACKAGE).test
TEST_SOURCES := $(wildcard test/src/*/*/*/*/*/*.java) $(wildcard test/res/*/*.xml)

# id 4 = Android 1.6 (API level 4)
# use `android list targets` to see which APIs are available
ANDROID_TARGET_ID := 4

LANG := C
LC_ALL := C
export LANG LC_ALL

#--------------------------------------- phony targets

all : debug release
	@ls -l bin/$(MAIN_NAME).apk test/bin/$(TEST_NAME).apk
	@ls -l bin/$(MAIN_NAME)-debug.apk test/bin/$(TEST_NAME)-debug.apk

# build a debug package
debug : \
    bin/$(MAIN_NAME)-debug.apk \
    test/bin/$(TEST_NAME)-debug.apk

# build a release signed package
release : \
    bin/$(MAIN_NAME).apk \
    test/bin/$(TEST_NAME).apk

# remove all intermediate files
clean :
	-rm -fr bin/* gen/*
	-rm -fr test/bin/* test/gen/*
	-find . -name \*~ -o -name \*.pyc -o -name [1xyz] | xargs -r rm

# clobber Ant files
clobber-ant : clean
	-rm -f  build.xml local.properties
	-rm -f  test/build.xml test/local.properties

# clobber Eclipse files
clobber-eclipse : clean
	-rm -fr .settings test/.settings

# clobber signing files
clobber-keystore : clean
	-rm -f  $(MAIN_NAME).keystore

# clobber files
clobber : clobber-ant clobber-eclipse
	-rm -f  res/raw/family_tree_db_*
	-rm -f  familyTree.db

# remove trailing blanks - I do not like them
remove-trailing-blanks :
	sed -i -e "s/\s*$$//" Makefile $(MAIN_SOURCES) $(TEST_SOURCES) *.txt *.xml */*.py

.PHONY : all debug release clean clobber
.PHONY : clobber-ant clobber-eclipse clobber-keystore
.PHONY : remove-trailing-blanks

#--------------------------------------- android targets

# create android device
$(HOME)/.android/avd/Android$(ANDROID_TARGET_ID)_avd.ini :
	android create avd -t $(ANDROID_TARGET_ID) -n "Android$(ANDROID_TARGET_ID)_avd" -f
	android list avd

# start android emulator
start-emulator : \
    $(HOME)/.android/avd/Android$(ANDROID_TARGET_ID)_avd.ini
	emulator -avd "Android$(ANDROID_TARGET_ID)_avd" &
	sleep 3 && adb wait-for-device

# install debug package - it requires an emulator running
install-debug : \
    bin/$(MAIN_NAME)-debug.apk \
    test/bin/$(TEST_NAME)-debug.apk \
    $(HOME)/.android/avd/Android$(ANDROID_TARGET_ID)_avd.ini
	-adb uninstall $(TEST_PACKAGE)
	-adb uninstall $(MAIN_PACKAGE)
	adb install bin/$(MAIN_NAME)-debug.apk
	adb install test/bin/$(TEST_NAME)-debug.apk

# install release package - it requires an emulator running
install-release : \
    bin/$(MAIN_NAME).apk \
    test/bin/$(TEST_NAME).apk \
    $(HOME)/.android/avd/Android$(ANDROID_TARGET_ID)_avd.ini
	-adb uninstall $(TEST_PACKAGE)
	-adb uninstall $(MAIN_PACKAGE)
	adb install bin/$(MAIN_NAME).apk
	adb install test/bin/$(TEST_NAME).apk

# run test package - it requires an emulator running
run-tests :
	adb shell am instrument -w $(TEST_PACKAGE)/android.test.InstrumentationTestRunner

# run android logcat - it requires an emulator running
run-logcat :
	adb logcat 2>&1 | tee logcat.txt

# run http://developer.android.com/guide/developing/tools/monkey.html
run-monkey :
	adb shell monkey -p $(MAIN_PACKAGE) -v 1000

.NOTPARALLEL : install-debug install-release
.PHONY : start-emulator install-debug install-release run-tests run-logcat run-monkey

#--------------------------------------- file targets

# sign .apk target
# NOTE: this macro assumes that $@ is a .apk filename, like $@ = FILENAME.apk, and
#       that this macro is used right after executing Ant, which builds a temporary
#       file FILENAME-unsigned.apk
define SIGN_APK_TARGET
	if [ -s $@ ] ; then rm -f $@ ; fi
	# sign the unsigned package
	jarsigner -keystore $(MAIN_NAME).keystore \
		-signedjar $(subst .apk,-signed.apk,$@) $(subst .apk,-unsigned.apk,$@) \
		$(MAIN_NAME)_key
	# postprocess the signed package
	zipalign 4 $(subst .apk,-signed.apk,$@) $@
	# verify signature in the final target
	jarsigner -verify $@
endef # SIGN_APK_TARGET

# process familyTree.yaml
familyTree.db : familyTree.yaml tools/create_familyTree_db.py
	python tools/create_familyTree_db.py

# split the database file into pieces smaller than 1Mb
res/raw/family_tree_db_0 \
res/raw/family_tree_db_1 \
res/raw/family_tree_db_2 \
res/raw/family_tree_db_3 \
res/raw/family_tree_db_4 \
res/raw/family_tree_db_5 \
res/raw/family_tree_db_6 \
res/raw/family_tree_db_7 \
: familyTree.db
	python tools/split.py -i familyTree.db -o res/raw/family_tree_db_ -n 8
	@ls -l res/raw/family_tree_db_*

# generate Ant files - main project
build.xml local.properties : res/raw/family_tree_db_0
	android update project -t $(ANDROID_TARGET_ID) -n "$(MAIN_NAME)" -p .

# generate Ant files - test project
test/build.xml test/local.properties : build.xml local.properties
	cd test && android update project -t $(ANDROID_TARGET_ID) -n "$(TEST_NAME)" -p .

# build the main debug package
bin/$(MAIN_NAME)-debug.apk : build.xml $(MAIN_SOURCES)
	# activate calls to Log methods in the source code
	sed --in-place -e "s/= false;/= true;/" src/ca/chaves/familyBrowser/helpers/Log.java
	-find src res -name \*~ | xargs -r rm
	# run Ant to build bin/$(MAIN_NAME)-debug.apk
	ant debug

# build the test debug package
test/bin/$(TEST_NAME)-debug.apk : bin/$(MAIN_NAME)-debug.apk test/build.xml $(TEST_SOURCES)
	# run Ant to build test/bin/$(TEST_NAME)-debug.apk
	cd test && ant debug

# build the main release signed package
bin/$(MAIN_NAME).apk : build.xml $(MAIN_NAME).keystore $(MAIN_SOURCES)
	# deactivate any calls to Log methods in the source code
	sed --in-place -e "s/= true;/= false;/" src/ca/chaves/familyBrowser/helpers/Log.java
	-find src res -name \*~ | xargs -r rm
	# build the release package
	ant release
	$(SIGN_APK_TARGET)

# build the test release signed package
test/bin/$(TEST_NAME).apk : bin/$(MAIN_NAME).apk test/build.xml $(MAIN_NAME).keystore $(TEST_SOURCES)
	# build the release package
	cd test && ant release
	$(SIGN_APK_TARGET)

# create keystore for signing release packages
$(MAIN_NAME).keystore :
	# generate signing keys
	keytool -genkey -v \
		-keystore $(MAIN_NAME).keystore -alias $(MAIN_NAME)_key \
		-keyalg RSA -keysize 2048 -validity 20000 \
		-dname "CN=David Chaves, OU=Chaves-Trejos Family, O=Costa Rica, L=Vancouver, ST=BC, C=CA"

.NOTPARALLEL : \
    bin/$(MAIN_NAME)-debug.apk bin/$(MAIN_NAME).apk \
    test/bin/$(TEST_NAME)-debug.apk test/bin/$(TEST_NAME).apk

#--------------------------------------- The End
