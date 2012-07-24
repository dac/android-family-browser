#!/bin/bash
#
# @(#) share/tools/build-sys.bash
#
# USAGE
#
#   bash share/tools/build-sys.bash start-emulator
#   bash share/tools/build-sys.bash build-kernel
#
# DESCRIPTION
#
#   Build the complate platform or kernel for the Android Emulator.
#

. setenv.sh

#--------------------------------------- setup

setup_custom_emulator() {
    [ ! -x "${_DROID_PLATFORM_HOME}/out/host/linux-x86/bin/emulator" ] && return
    # To start a custom-built emulator, where
    # the Android platform was built at ${_DROID_PLATFORM_HOME}:
    #
    # the platform's output would be the "${_DROID_PLATFORM_HOME}/out" directory;
    # that includes, besides the ROM images, a bunch of tools too, including the emulator;
    # for example, the emulator is at "${_DROID_PLATFORM_HOME}/out/host/linux-x86/bin/emulator"
    #
    #   export ANDROID_PRODUCT_OUT="${_DROID_PLATFORM_HOME}/out/target/product/generic",
    #   ${_DROID_PLATFORM_HOME}/out/host/linux-x86/bin/emulator -kernel ....
    #
  # ANDROID_PRODUCT_OUT="${_DROID_PLATFORM_HOME}/out/target/product/generic"
  # export ANDROID_PRODUCT_OUT
  # _EMULATOR_EXE="${_DROID_PLATFORM_HOME}/out/host/linux-x86/bin/emulator"
  # echo "Using custom emulator ${_EMULATOR_EXE} ..."
}

setup_custom_kernel() {
    [ ! -s "${_DROID_KERNEL_HOME}/${_DROID_KERNEL_VENDOR}/arch/arm/boot/zImage" ] && return
    _EMULATOR_KERNEL="${_DROID_KERNEL_HOME}/${_DROID_KERNEL_VENDOR}/arch/arm/boot/zImage"
    echo "Using custom kernel ${_EMULATOR_KERNEL} ..."
}

#--------------------------------------- emulator

start_emulator() {
    _EMULATOR_EXE="emulator"
    _EMULATOR_AVD="Android${_DROID_SDK_VERSION}_avd"
    _EMULATOR_KERNEL="arch/arm/boot/zImage"
    # create the android image, if it does not exist yet
    if [ ! -s "${HOME}/.android/avd/${_EMULATOR_AVD}.ini" ]
    then
        echo "--- Creating ${_EMULATOR_AVD} for Android${_DROID_SDK_VERSION}"
        android create avd -t "android-${_DROID_SDK_VERSION}" -n "${_EMULATOR_AVD}" -f
        android list avd
    fi
    # create the sdcard image, if it does not exist yet
    if [ ! -s sdcard.img ]
    then
        mksdcard -l SDCARD 128M sdcard.img
    fi
    echo "--- Starting Android Emulator ${ANDROID_SERIAL}"
    [ -n "${_DROID_KERNEL_HOME}"   ] && [ -d "${_DROID_KERNEL_HOME}"   ] && setup_custom_kernel
    [ -n "${_DROID_PLATFORM_HOME}" ] && [ -d "${_DROID_PLATFORM_HOME}" ] && setup_custom_emulator
    # start a new emulator process
    # see http://stackoverflow.com/questions/1923526/expanding-the-size-of-an-android-virtual-device-emulator-instance
    if [ -s "${_EMULATOR_KERNEL}" ]
    then
        "${_EMULATOR_EXE}" \
            -avd "${_EMULATOR_AVD}" \
            -kernel "${_EMULATOR_KERNEL}" \
            -partition-size 128 -wipe-data \
            -show-kernel -verbose \
            -sdcard sdcard.img \
            >emulator.log 2>&1 &
        #   -scale 0.5
        #   -trace emulator.trace.log
    else
        "${_EMULATOR_EXE}" \
            -avd "${_EMULATOR_AVD}" \
            -partition-size 128 -wipe-data \
            -show-kernel -verbose \
            -sdcard sdcard.img \
            >emulator.log 2>&1 &
        #   -scale 0.5
        #   -trace emulator.trace.log
    fi
    sleep 30
    adb wait-for-device
    echo "Please run \` adb logcat \` in another terminal,"
    echo "       in order to see the logcat output"
}

#--------------------------------------- kernel

build_kernel() {
    if [ -z "${_DROID_KERNEL_HOME}" ]
    then
        echo "error: environment _DROID_KERNEL_HOME is not set"
        echo "Please update your setenv.sh file"
        exit 1
    fi
    if [ ! -d "${_DROID_KERNEL_HOME}" ]
    then
        echo "error: _DROID_KERNEL_HOME [${_DROID_KERNEL_HOME}] is not a directory"
        echo "Please update your setenv.sh file"
        exit 1
    fi
    # next needs a running devices
    #_ANDROID_KERNEL_VERSION="` adb shell 'cat /proc/version' | sed -e 's/Linux version //' -e 's/-.*$//' `"
    _ANDROID_KERNEL_VERSION="2.6.29" # the current version running in the current emulator
    if [ -z "${_ANDROID_KERNEL_VERSION}" ]
    then
        echo "error: could not figure out which kernel version we need"
        echo "Please start the emulator before building a custom kernel"
        exit 1
    fi
    echo "--- Fetching Linux ${_ANDROID_KERNEL_VERSION} in ${_DROID_KERNEL_HOME} ...  PLEASE WAIT"
    mkdir -p "${_DROID_KERNEL_HOME}"
    if [ -d "${_DROID_KERNEL_HOME}/${_DROID_KERNEL_VENDOR}/.git" ]
    then
        cd "${_DROID_KERNEL_HOME}/${_DROID_KERNEL_VENDOR}"
        git fetch origin
    else
        cd "${_DROID_KERNEL_HOME}"
        git clone "git://${_DROID_KERNEL_DOMAIN}/kernel/${_DROID_KERNEL_VENDOR}.git"
    fi
    cd "${_DROID_KERNEL_HOME}/${_DROID_KERNEL_VENDOR}" # the kernel source is now in here
    # need to checkout a detached branch before destroying "${_DROID_KERNEL_VENDOR}-${_ANDROID_KERNEL_VERSION}"
    # since it is not possible to delete the "current" working branch
    git checkout origin/HEAD
    # delete any old tracking branch, if it exists
    git branch -d "${_DROID_KERNEL_VENDOR}-${_ANDROID_KERNEL_VERSION}" 2>/dev/null
    # there might be some typos in the remote branch name, like "gldfish" vs "${_DROID_KERNEL_VENDOR}"
    git checkout --track -b "${_DROID_KERNEL_VENDOR}-${_ANDROID_KERNEL_VERSION}" ` git branch -a | grep remotes | grep "fish-${_ANDROID_KERNEL_VERSION}" `
    echo "--- Configuring ${_DROID_KERNEL_VENDOR}-${_ANDROID_KERNEL_VERSION}"
    export ARCH="arm"
    # TODO - this $CROSS_COMPILE is specific to android-ndk-r5b/...
    # it might need to be smarter once we use newer NDK distributions!
    export CROSS_COMPILE="${_DROID_NDK_HOME}/toolchains/arm-eabi-4.4.0/prebuilt/linux-x86/bin/arm-eabi-"
    # create initial .config file
  # make defconfig              ARCH="${ARCH}"
    make goldfish_defconfig     ARCH="${ARCH}"  # goldfish = Emulator's Hardware
  # make herring_defconfig      ARCH="${ARCH}"  # herring = Samsung's Nexus S
  # make capela_defconfig       ARCH="${ARCH}"  # capela = Samsung's Galaxy
    # must change a few options to the ${_DROID_KERNEL_VENDOR}'s original .config file
    mv -f ".config" ".config.${_DROID_KERNEL_VENDOR}"
    (   cat ".config.${_DROID_KERNEL_VENDOR}"   | \
            sed -e "s/=m/=y/"                   | \
            grep -v -e CONFIG_IKCONFIG
        # TODO - add more customizations here
        echo "CONFIG_IKCONFIG=y"                # for /proc/config.gz
        echo "CONFIG_IKCONFIG_PROC=y"
    ) > .config
    rm .config.${_DROID_KERNEL_VENDOR}
    echo "--- Building kernel ${_ANDROID_KERNEL_VERSION} image"
    make -j8 all ARCH="${ARCH}" CROSS_COMPILE="${CROSS_COMPILE}"
    if [ ! -s "${_DROID_KERNEL_HOME}/${_DROID_KERNEL_VENDOR}/arch/arm/boot/zImage" ]
    then
        echo "*** Failed!"
        exit 1
    fi
    echo "Kernel ${_ANDROID_KERNEL_VERSION} created as ${_DROID_KERNEL_HOME}/${_DROID_KERNEL_VENDOR}/arch/arm/boot/zImage"
    echo "Please restart the emulator"
    echo "       in order to run with the new kernel"
}

#--------------------------------------- main

build_sys() {
    (
        case "$1" in
        run-emulator | start-emulator )
            start_emulator
            ;;
        build-kernel )
            build_kernel
            ;;
        * )
            echo "Usage: $0 { run-emulator }"
            echo "Once the device ${ANDROID_SERIAL} is running:"
            echo "       $0 { build-kernel } "
            ;;
        esac
    )
}

( build_sys $* )

#--------------------------------------- The End
