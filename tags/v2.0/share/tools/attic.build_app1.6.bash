#!/bin/bash
#
# @(#) share/tools/attic.build_app1.6.bash
#
# @see http://blog.javia.org/android-and-proguard-the-perfect-pair/
#

JAVA_SOURCES=` find src -name \*.java `
JAVA_LIBS=` find libs -name \*.jar `

PLATFORM=${_DROID_SDK_HOME}/platforms/android-1.6/
ANDROID_JAR=${PLATFORM}/android.jar
PROGUARD_JAR=/path/to/proguard/lib/proguard.jar

PKRES=bin/resource.ap_

SIGNED_APK=${_DROID_APP_NAME}-unalign.apk
ALIGNED_APK=${_DROID_APP_NAME}.apk

set -e	# exit on error
mkdir -p bin/classes gen

aapt package -f -m -J gen -M AndroidManifest.xml -S res -I ${ANDROID_JAR} -F ${PKRES}
javac -d bin/classes -classpath bin/classes:$JAVA_LIBS -sourcepath src:gen -target 1.5 -bootclasspath ${ANDROID_JAR} -g ${JAVA_SOURCES}
java -jar ${PROGUARD_JAR} -injars ${JAVA_LIBS}:bin/classes -outjar bin/obfuscated.jar -libraryjars ${ANDROID_JAR} @proguard.cfg
dx --dex --output=bin/classes.dex bin/obfuscated.jar
apkbuilder bin/${SIGNED_APK} -u -z ${PKRES} -f bin/classes.dex
jarsigner -keystore ${_DROID_APP_KEYSTORE} bin/${SIGNED_APK} ${_DROID_APP_KEYALIAS}
zipalign -f 4 bin/${SIGNED_APK} bin/${ALIGNED_APK}

#--------------------------------------- The End
