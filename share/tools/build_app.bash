#!/bin/bash
#
# @(#) share/tools/build_app.bash
#
# USAGE
#
#   bash share/tools/build_app.sh build  [ debug | release ]
#   bash share/tools/build_app.sh deploy [ debug | release ]
#   bash share/tools/build_app.sh clean
#
# DESCRIPTION
#
#   Builds .apk files from Java sources.
#

. setenv.sh

# for debugging - echo commands to execute
#set -x

#--------------------------------------- ant

# usage: run_ant DIRECTORY [ debug | release ]
run_ant() {
    _SUBDIR="$1"
    _TARGET="$2"
    _ANT_CMD="ant"
    [ -x "${ANT_HOME}/bin/ant" ] && _ANT_CMD="${ANT_HOME}/bin/ant"
    # get colorful ant output
    _ANT_LOGGER="-logger org.apache.tools.ant.NoBannerLogger"
    _ANT_LOGGER="-logger org.apache.tools.ant.listener.AnsiColorLogger"
    # input handlers for passwords
    _ANT_INPUT="-inputhandler org.apache.tools.ant.input.SecureInputHandler"
    # fix targets
    [ "${_TARGET}" = "debug" ] && _TARGET="emma debug"
    # run `ant`
    ( cd "${_SUBDIR}" && "${_ANT_CMD}" -keep-going ${_ANT_LOGGER} ${_ANT_INPUT} ${_TARGET} )
    if [ $? != 0 ]
    then
        echo "*** ${_ANT_CMD} failed!"
        exit 1
    fi
}

# usage: build_ant [ debug | release ]
build_ant() {
    update_ant $1
    run_ant android $1
    run_ant app     $1
  # run_ant lite    $1
    run_ant main    $1
    run_ant test    $1
}

# clobber ant files
# usage: clean_ant
clean_ant() {
    if [ -s main/build.xml ]
    then
        build_ant clean
        # remove Ant files
        # WARNING: keep */local.properties   : they might have good key.store.password/key.alias.password
        # WARNING: keep */project.properties : they are used by Eclipse
        rm -f */build.xml
    fi
}

# usage: update_ant [ debug | release ]
# @warning requires SDK r14 or higher
# @see http://developer.android.com/guide/developing/projects/projects-cmdline.html
update_ant() {
    [ -s main/AndroidManifest.xml ] || build_templates $1

    # make all project.properties empty
    > android/project.properties
    >     app/project.properties
  # >    lite/project.properties
    >    main/project.properties
    >    test/project.properties

    # update ${sdk.dir} to the right value in "local.properties"

    android --verbose update lib-project \
        --path android \
        --target "android-${_DROID_SDK_VERSION}"
  # echo "android.library=true" >> android/project.properties

    android --verbose update lib-project \
        --path app \
        --target "android-${_DROID_SDK_VERSION}"
    android --verbose update project \
        --library ../android \
        --path app \
        --target "android-${_DROID_SDK_VERSION}"
  # echo "android.library=true" >> app/project.properties

  # android --verbose update project \
  #     --library ../app
  #     --name "${_DROID_APP_NAME}Lite" \
  #     --path lite
  #     --target "android-${_DROID_SDK_VERSION}" \

    android --verbose update project \
        --library ../android \
        --name "${_DROID_APP_NAME}" \
        --path main \
        --target "android-${_DROID_SDK_VERSION}"
    android --verbose update project \
        --library ../app \
        --name "${_DROID_APP_NAME}" \
        --path main \
        --target "android-${_DROID_SDK_VERSION}"

    android --verbose update test-project \
        --main ../main \
        --path test
    android --verbose update project \
        --library ../android \
        --path test \
        --target "android-${_DROID_SDK_VERSION}"
    android --verbose update project \
        --library ../app \
        --path test \
        --target "android-${_DROID_SDK_VERSION}"

    # next files were created by ` android update project `
    # but they must not exist because we use proguard.txt
  # rm -f */proguard.cfg */proguard-project.txt
}

#--------------------------------------- jni

# usage: run_ndk-build DIRECTORY [ debug | release ]
run_ndk-build() {
    # ndk-build produce binaries with symbols
    # under $1/obj/local/.../ to be used
    # for debugging with gdb
    ndk-build -C $1 --keep-going
    if [ $? != 0 ]
    then
        echo "*** ndk-build failed!"
        exit 1
    fi
}

# usage: build_jni [ debug | release ]
build_jni() {
    for _DIRNAME in android
    do
        run_ndk-build "${_DIRNAME}" $1
    done
}

# usage: clean_jni
clean_jni() {
    for _DIRNAME in android
    do
        if [ -s "${_DIRNAME}"/jni/Android.mk ]
        then
            ndk-build -C "${_DIRNAME}" --keep-going clean
            ndk-build -C "${_DIRNAME}" --keep-going clean # needed twice
        fi
        rm -fr "${_DIRNAME}"/obj/ "${_DIRNAME}"/libs/
        # clean .d files created with gcc -M:
        find "${_DIRNAME}"/jni -name \*.d -type f | xargs -r rm -f
    done
}

#--------------------------------------- media

# inkscape is a vector-based drawing program
# which uses .svg files, which really are xml files
# @see http://soledadpenades.com/2010/11/26/programmatically-building-drawables/
# @see http://code.google.com/p/svg-android/
# TODO: use share/tools/build-drawables.py instead
export_svg() {
    _DIRNAME="$1"
    mkdir -p "${_DIRNAME}"/res/drawable
    mkdir -p "${_DIRNAME}"/res/drawable-ldpi  # ~120dpi, 36x36 icons
    mkdir -p "${_DIRNAME}"/res/drawable-mdpi  # ~160dpi, 48x48 icons
    mkdir -p "${_DIRNAME}"/res/drawable-hdpi  # ~240dpi, 72x72 icons
    mkdir -p "${_DIRNAME}"/res/drawable-xhdpi # ~320dpi, 96x96 icons
    find "${_DIRNAME}"/graphics -name \*.svg -type f 2>/dev/null | \
        while read _FILE
        do
            _BASENAME="` basename \"${_FILE}\" .svg `"
            inkscape "${_FILE}" \
                --export-dpi="120" \
                --export-png="${_DIRNAME}"/res/drawable-ldpi/"${_BASENAME}".png \
                --export-area-drawing
            inkscape "${_FILE}" \
                --export-dpi="160" \
                --export-png="${_DIRNAME}"/res/drawable-mdpi/"${_BASENAME}".png \
                --export-area-drawing
            inkscape "${_FILE}" \
                --export-dpi="240" \
                --export-png="${_DIRNAME}"/res/drawable-hdpi/"${_BASENAME}".png \
                --export-area-drawing
            inkscape "${_FILE}" \
                --export-dpi="320" \
                --export-png="${_DIRNAME}"/res/drawable-xhdpi/"${_BASENAME}".png \
                --export-area-drawing
        done
}

optimize_png() {
    _CRUSHED="crushed-$$.png"
    find */graphics */res */tarball -name \*.png ! -name \*.9.png -type f | \
        while read _FILE
        do
            pngcrush \
                -brute -l 9 -reduce \
                -rem gAMA -rem alla -rem text \
                -q "${_FILE}" "${_CRUSHED}"
            mv -f "${_CRUSHED}" "${_FILE}"
            optipng -o7 -q "${_FILE}"
            advpng -z -4 "${_FILE}"
        done >/dev/null
}

optimize_ogg() {
    find */graphics */res */tarball -name \*.ogg -type f | \
        while read _FILE
        do
            sox "${_FILE}" -C 0 ogg.tmp
            mv -f ogg.tmp "${_FILE}"
        done >/dev/null
}

optimize_images() {
    export_svg android
    export_svg app
  # export_svg lite
    export_svg main
    optimize_png
    optimize_ogg
}

#--------------------------------------- apk

# usage: sign_apk TARGET.apk SOURCE.apk
# sign SOURCE.apk jarfile into TARGET.apk
sign_apk() {
    echo "--- Signing $2 into $1 ..."
    _INPUT="$2"
    _OUTPUT="$1"
    _UNSIGNED="`  basename $2 .apk `-$$-unsigned.apk"
    _UNALIGNED="` basename $1 .apk `-$$-unaligned.apk"
    _UNZIP_DIR="` basename $1 .apk `-$$-unzip.tmp"
    rm -fr "${_OUTPUT}" "${_UNALIGNED}" "${_UNSIGNED}" "${_UNZIP_DIR}" 2>/dev/null
#   # sign the unsigned package
#   #     Ok so this is about the dreaded jarsigner error on -DEBUG.apk files :
#   #         unable to sign jar: java.util.zip.ZipException: invalid entry compressed size
#   #     99% of the time what that means is your jar is already signed
#   #     all you have to do is extract it, delete the META-INF directory and repack it:
#   #         deleting: META-INF/MANIFEST.MF
#   #         deleting: META-INF/CERT.SF
#   #         deleting: META-INF/CERT.RSA
#   jarsigner -keystore "${_DROID_APP_KEYSTORE}" -signedjar "${_UNALIGNED}" "${_INPUT}" "${_DROID_APP_KEYALIAS}" >/dev/null
#   if [ $? != 0 ]
#   then
        mkdir -p "${_UNZIP_DIR}"
        unzip "${_INPUT}" -d "${_UNZIP_DIR}" >/dev/null
        rm -fr "${_UNZIP_DIR}"/META-INF/        # remove all previous signatures
        jar cf "${_UNSIGNED}" -C "${_UNZIP_DIR}" .
        # try to sign it again
        jarsigner -keystore "${_DROID_APP_KEYSTORE}" -signedjar "${_UNALIGNED}" "${_UNSIGNED}" "${_DROID_APP_KEYALIAS}"
#   fi
    # postprocess the signed package
    zipalign -f 4 "${_UNALIGNED}" "${_OUTPUT}"
    rm -fr "${_UNALIGNED}" "${_UNSIGNED}" "${_UNZIP_DIR}" 2>/dev/null
    # verify signature in the final target
    #jarsigner -verify -certs "${_OUTPUT}"
}

# usage: deploy_apk INSTALLABLE.apk JAVA_PACKAGE_NAME
deploy_apk() {
    adb wait-for-device
    if [ -n "$2" ]
    then
        echo "--- Uninstalling old ${ANDROID_SERIAL} $2"
        adb uninstall "$2"
    fi
    # remove old files from the Android's HOME directory
    adb shell "rm /data/data/${_DROID_APP_PACKAGE}/databases/*.db" >/dev/null 2>&1
    # install new package
    if [ -n "$1" ]
    then
        echo "--- Installing new ${ANDROID_SERIAL} $1"
        adb install -r "$1"
        # set libc.so in debugging mode
        #adb shell stop
        adb shell setprop libc.debug.malloc 10
        adb shell setprop log.redirect-stdio true
        # see http://android-developers.blogspot.com/2011/07/debugging-android-jni-with-checkjni.html
        adb shell setprop debug.checkjni 1
        # see http://www.netmite.com/android/mydroid/dalvik/docs/embedded-vm-control.html
        adb shell setprop dalvik.vm.checkjni true
        adb shell setprop dalvik.vm.enableassertions all
        #adb shell start
        # check the device has the required kernel modules
        adb pull /proc/config.gz # this needs "CONFIG_IKCONFIG=y" and "CONFIG_IKCONFIG_PROC=y"
        gunzip < config.gz | grep -e CONFIG_IKCONFIG
        rm config.gz
      # echo "--- Spawning ${ANDROID_SERIAL} $2"
      # adb shell "am start -n $2/.splash.SplashActivity"
    fi
}

#--------------------------------------- doxygen

# usage: run_doxygen DOXYGEN.CONFIG
run_doxygen() {
    doxygen "$@" >doxygen.log 2>&1
}

#--------------------------------------- tarball

# usage: build_database
build_database() {
    (
        export DROID_APP_VERSION="${_DROID_APP_VERSION}"
        python share/tools/familyTree_yaml.py
    )
    optimize_images
}

# create keystore for signing release packages
# usage: build_keystore
build_keystore() {
    _DIRNAME="` dirname \"${_DROID_APP_KEYSTORE}\" `"
    mkdir -p "${_DIRNAME}"
    # generate signing keys
    keytool -genkey -v \
        -keystore "${_DROID_APP_KEYSTORE}" -alias "${_DROID_APP_KEYALIAS}" \
        -keyalg RSA -keysize 2048 -validity 20000 \
        -dname "CN=David Chaves, OU=Chaves-Trejos Family, O=Costa Rica, L=Vancouver, ST=BC, C=CA"
}

# create $/res/raw/tarball_* - assumes databases/jni already built
# usage: create_tarball DIRECTORY
create_tarball() {
    (
        cd $1/build/tarball/
        find * -type d | sed 's,^\./,,' | sort > ../tarball.list
        find * -type f | sed 's,^\./,,' | sort >> ../tarball.list
        # ... February 3rd, 2011 7:30am is the date when
        # ... my father, Modesto Chaves-Rodríguez, died in Costa Rica
        tar --format=ustar --no-recursion \
            --group=0 --owner=0 --mtime="2011-02-03 07:30 CST" --numeric-owner \
            -cf ../tarball.tar ` cat ../tarball.list `
    )
    # NOTE: Apple does not have `sha1sum`
    sha1sum --binary $1/build/tarball.tar | cut -f1 "-d " > $1/build/tarball.sha1
    gzip -9 < $1/build/tarball.tar > $1/build/tarball.tar.gz
    #tar tvzf $1/build/tarball.tar.gz
    #ls -ld $1/build/tarball*
    # split tarball in pieces like "res/raw/tarball_%d" -
    # prior to Android 2.3, we could not open compressed files that were larger than 1MB
    python share/tools/split.py -i $1/build/tarball.tar.gz -o $1/res/raw/tarball_ -n 8
    #ls -l $1/res/raw/tarball*
}

# build {lite,main,test}/res/raw/tarball - assumes databases/jni already built
# usage: build_tarballs
build_tarballs() {
  # mkdir -p lite/build/ lite/res/raw/
    mkdir -p main/build/ main/res/raw/
    # clear old tarballs, if any
    rm -fr */build/tarball*             2>/dev/null
  # rm -fr lite/res/raw/tarball*        2>/dev/null
    rm -fr main/res/raw/tarball*        2>/dev/null
    rm -fr test/res/raw/tarball*        2>/dev/null
    find */tarball -name \*~ -type f | xargs -r rm
    # init the "main" tarball
  # cp -fa lite/tarball/ lite/build/
    cp -fa main/tarball/ main/build/
    # reset all permissions to something well-known
    find */build/tarball -type d | xargs -r chmod 0711
    find */build/tarball -type f | xargs -r chmod 0600
    find */build/tarball -type f -name LICENSE\* | xargs -r chmod 0644
    find */build/tarball -type f -name README\*  | xargs -r chmod 0644
    # copy JNI executables into {lite,main,test}/build/tarball
  # mkdir -p lite/build/tarball/bin/ lite/build/tarball/lib/
  # mkdir -p main/build/tarball/bin/ main/build/tarball/lib/
  # for _FILE in \
  # ; do
  #     cp -fa "native/libs/armeabi/${_FILE}" main/build/tarball/bin/
  #     chmod 0711 "main/build/tarball/bin/${_FILE}"
  # done
    # create final tarballs, one per project
  # create_tarball lite
    create_tarball main
}

# usage: build_templates [ debug | release ]
build_templates() {
    # update build settings
    _DROID_GIT_BRANCH=""
    _DROID_BUILD_UUID=""
    _DROID_TARBALL_HASH=""
    [ "$1" != clean ] && _DROID_GIT_BRANCH="` git rev-parse HEAD `"
    [ "$1" != clean ] && _DROID_BUILD_UUID="` run_uuid `"
    [ -s main/build/tarball.sha1 ] && _DROID_TARBALL_HASH="` cat main/build/tarball.sha1 `"
    # remove all backup files from src - they will end up inside the .apk file
    find */src */res templates -name \*~ -type f | xargs -r rm
    # process templates/* files
    (
        export DROID_APP_KEYALIAS="${_DROID_APP_KEYALIAS}"
        export DROID_APP_KEYSTORE="${_DROID_APP_KEYSTORE}"
        export DROID_APP_NAME="${_DROID_APP_NAME}"
        export DROID_APP_PACKAGE="${_DROID_APP_PACKAGE}"
        export DROID_APP_VERSION="${_DROID_APP_VERSION}"
        export DROID_APP_VERSNAME="${_DROID_APP_VERSNAME}"
        export DROID_BUILD_UUID="${_DROID_BUILD_UUID}"
        export DROID_DEBUG_ENABLED="yes"
        export DROID_GIT_BRANCH="${_DROID_GIT_BRANCH}"
        export DROID_SDK_VERSION="${_DROID_SDK_VERSION}"
        export DROID_TARBALL_HASH="${_DROID_TARBALL_HASH}"

        # in Python, an empty string means False
        [ "$1" = release ] && DROID_DEBUG_ENABLED=""

        # plain files
        find templates -type f ! -path ".svn" | sed -e "s,^templates/,," | sort | \
            while read _FILE
            do
                _DIRNAME="` dirname \"${_FILE}\" `"
                echo ". ${_FILE} ${_DIRNAME}"

                mkdir -p "${_DIRNAME}"
                run_tempita --env -o "${_FILE}" "templates/${_FILE}"
            done
    )
}

#--------------------------------------- tempita

run_tempita() {
    # WARNING: must use "-m tempita.__init_" for python 2.6, but not for 2.5 nor 2.7
    python -W ignore -m tempita.__init__ "$@"
}

#--------------------------------------- uuid

run_uuid() {
    if [ -x /usr/bin/uuidgen ]
    then
        /usr/bin/uuidgen
    else
        python share/tools/uuidgen.py
    fi
}

#--------------------------------------- actions

# usage: do_build [ debug | release ]
do_build() {
    [ -s "${_DROID_APP_KEYSTORE}" ] || build_keystore
    [ -s "main/tarball/databases/v${_DROID_APP_VERSION}.db" ] || build_database
    clean_ant           # Ant sometimes get confused with BuildManifest.java
    build_templates $1
    build_jni $1
    build_tarballs
    build_ant $1
    run_doxygen share/doxygen/Doxyfile.txt
}

# usage: do_clean
do_clean() {
    clean_ant
    clean_jni
    # remove doxygen files
    rm -fr share/doxygen/html
    # remove all intermediate files
    rm -fr   android/bin android/build android/gen
    rm -fr   app/bin     app/build     app/gen
  # rm -fr   lite/bin    lite/build    lite/gen    lite/res/raw/tarball*
    rm -fr   main/bin    main/build    main/gen    main/res/raw/tarball*
    rm -fr   test/bin    test/build    test/gen    test/res/raw/tarball*
    # but keep Eclipse happy - it needs the "gen/" directory
    mkdir -p android/bin android/build android/gen
    mkdir -p app/bin     app/build     app/gen
  # mkdir -p lite/bin    lite/build    lite/gen    lite/res/raw
    mkdir -p main/bin    main/build    main/gen    main/res/raw
    mkdir -p test/bin    test/build    test/gen    test/res/raw
    # remove $EDITOR backup files
    find . -name \*~ -type f | xargs -r rm -f
    # remove other temporary files
    find . -name \*~ -o -name \*.pyc -o -name [1xyz] | xargs -r rm
}

# usage: do_clobber
do_clobber() {
    # clobber database
  # rm -f "lite/tarball/databases/v${_DROID_APP_VERSION}.db"
    rm -f "main/tarball/databases/v${_DROID_APP_VERSION}.db"
    # clobber signing file
    #rm -f "${_DROID_APP_KEYSTORE}"
}

# usage: do_database
do_database() {
    build_database
}

# usage: do_deploy PATH/TO/INSTALLABLE.APK JAVA_PACKAGE_NAME
do_deploy() {
    # bin/*.apk are the .apk files produced by Eclipse
    # build/*.apk are the .apk files produced by Ant
    if [ "$1" = release ]
    then
      # deploy_apk lite/bin/${_DROID_APP_NAME}Lite-release.apk ${_DROID_APP_PACKAGE}.lite
        deploy_apk main/bin/${_DROID_APP_NAME}-release.apk ${_DROID_APP_PACKAGE}
        deploy_apk test/bin/${_DROID_APP_NAME}Test-release.apk ${_DROID_APP_PACKAGE}.test
    else
      # deploy_apk lite/bin/${_DROID_APP_NAME}Lite-debug.apk ${_DROID_APP_PACKAGE}.lite
        deploy_apk main/bin/${_DROID_APP_NAME}-debug.apk ${_DROID_APP_PACKAGE}
        deploy_apk test/bin/${_DROID_APP_NAME}Test-debug.apk ${_DROID_APP_PACKAGE}.test
    fi
}

# usage: do_init [ debug | release ]
do_init() {
    [ -s "${_DROID_APP_KEYSTORE}" ] || build_keystore
    build_templates $1
    update_ant $1
    optimize_images
}

# usage: do_keystore
do_keystore() {
    build_keystore
}

#--------------------------------------- main

build_app() {
    (
        case "$1-$2" in
        build- | debug- )
            echo "--- Build ${_DROID_APP_NAME} debug"
            do_build debug
            ;;
        release- )
            echo "--- Build ${_DROID_APP_NAME} release"
            do_build release
            ;;
        build-debug | build-release )
            echo "--- Build ${_DROID_APP_NAME} $2"
            do_build $2
            ;;
        database- | build-database )
            echo "--- Make database"
            do_database
            ;;
        init- | build-init )
            echo "--- Init debug"
            do_init debug
            ;;
        init-debug | init-release )
            echo "--- Init $2"
            do_init $2
            ;;
        keystore- | build-keystore )
            echo "--- Make ${_DROID_APP_KEYSTORE}"
            do_keystore
            ;;
        deploy- )
            echo "--- Deploy ${_DROID_APP_NAME} debug"
            do_deploy debug
            ;;
        deploy-debug | deploy-release )
            echo "--- Deploy ${_DROID_APP_NAME} $2"
            do_deploy $2
            ;;
        clean- | clean-debug | clean-release )
            echo "--- Clean build"
            do_clean
            ;;
        clobber- )
            echo "--- Clobber make"
            do_clean
            do_clobber
            ;;
        debug-deploy | release-deploy )
            echo "--- Build, deploy ${_DROID_APP_NAME} $1"
            #do_clean
            do_build $1
            do_deploy $1
            ;;
        *help* | * )
            echo "Usage: $0 [ init | clean | clobber ] "
            echo "Usage: $0 [ debug | release ] "
            echo "       $0 build [ debug | release ] "
            echo "       $0 build [ init | database | keystore ] "
            echo "Once the device ${ANDROID_SERIAL} is running: "
            echo "       $0 deploy [ debug | release ] "
            echo "       $0 [ debug | release ] deploy "
            ;;
        esac
    )
}

( build_app $* )

#--------------------------------------- The End
