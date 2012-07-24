#!/bin/sh
#
# USAGE
#
#     build.sh COMMAND
#

. setenv.sh

# for debugging - echo commands to execute
#set -x

#--------------------------------------- main

case "$1" in
clean )
    exec bash share/tools/build_app.bash clean
    ;;
debug | release )
    exec bash share/tools/build_app.bash build $1
    ;;
debug-deploy )
    exec bash share/tools/build_app.bash debug deploy
    ;;
deploy-debug )
    exec bash share/tools/build_app.bash deploy debug
    ;;
deploy-release )
    exec bash share/tools/build_app.bash deploy release
    ;;
emulator )
    exec bash share/tools/build_sys.bash start-emulator
    ;;
kernel )
    exec bash share/tools/build_sys.bash build-kernel
    ;;
hierarchyviewer )
    # see http://www.android10.org/index.php/articlesother/267-android-tools-hierarchy-viewer
    # see http://developer.android.com/guide/developing/debugging/debugging-ui.html#hierarchyViewer
    shift
    exec hierarchyviewer $*
    ;;
init )
    exec bash share/tools/build_app.bash build init
    ;;
layoutopt )
    # nalize layout resources
    shift
    exec layoutopt $* */res
    ;;
logcat )
    shift
    exec python share/tools/logcat.py $*
    ;;
monkey )
    # see http://developer.android.com/guide/developing/tools/monkey.html
    shift
    adb shell monkey -p "${_DROID_APP_PACKAGE}" -v 1000
    ;;
release-deploy )
    exec bash share/tools/build_app.bash release deploy
    ;;
sqlite )
    # see /system/xbin/sqlite3
    shift
    adb shell "sqlite3 /data/data/${_DROID_APP_PACKAGE}/databases/v${_DROID_APP_VERSION}.db"
    ;;
tests )
    shift
    # setup the application assets
    adb shell am start \
        -a "${_DROID_APP_PACKAGE}.intent.action.SETUP" \
        -n "${_DROID_APP_PACKAGE}/.main.Setup"
    # run the unit-tests
    adb shell am instrument -w \
        "${_DROID_APP_PACKAGE}.test/android.test.InstrumentationTestRunner"
  # adb shell am instrument -w \
  #     -e coverage true \
  #     "${_DROID_APP_PACKAGE}.test/android.test.InstrumentationTestRunner"
    ;;
* )
    echo "Usage: $0 ( init | debug | release | clean )"
    echo "       $0 ( emulator | logcat | monkey | tests )"
    echo "       $0 ( hierarchyviewer | layoutopt | sqlite )"
    echo "Once the device ${ANDROID_SERIAL} is running:"
    echo "       $0 ( deploy-debug | deploy-release )"
    echo "       $0 ( debug-deploy | release-deploy )"
    echo "       $0 ( kernel )"
    ;;
esac

#--------------------------------------- The End
