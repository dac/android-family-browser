#!/bin/sh
#
# USAGE
#
#   . setenv.sh         # the "dot" is part of the command line
#
# ASSUMPTIONS
#
#   * Android SDK/NDK requires some Debian/Ubuntu packages pre-installed:
#
#     $  sudo apt-get install --yes bison build-essential curl flex gcc-multilib git-core g++-multilib gnupg
#     $  sudo apt-get install --yes gperf libx11-dev pngcrush schedtool x11proto-core-dev zip zlib1g-dev
#     ## next are needed only on amd64 - ignore errors on 32-bit intel
#     $  sudo apt-get install --yes libc6-dev-i386 lib32ncurses5-dev ia32-libs lib32readline5-dev lib32z-dev

#   * Java 1.6 is installed:
#
#     $  sudo apt-get install --yes sun-java6-jdk
#     $  sudo update-java-alternatives --set java-6-sun
#
#   * Ant 1.8 is installed:
#
#     $  cd "${HOME}/run/"
#     $  wget http://www.apache.org/dist/ant/binaries/apache-ant-1.8.2-bin.tar.g
#     $  tar xzf apache-ant-1.8.2-bin.tar.gz
#     $  export ANT_HOME="${HOME}/run/apache-ant-1.8.2"
#     $  export PATH="${ANT_HOME}/bin:${PATH}"
#     $  export LD_LIBRARY_PATH="${ANT_HOME}/lib:${LD_LIBRARY_PATH}"
#

#--------------------------------------- android

_DROID_APP_NAME="FamilyBrowser"
_DROID_APP_PACKAGE="ca.chaves.familyBrowser"
_DROID_APP_VERSION="2"          # bump right after new release
_DROID_APP_VERSNAME="2.0"       # bump right after new release

_DROID_APP_KEYSTORE="share/certs/${_DROID_APP_NAME}.keystore"
_DROID_APP_KEYALIAS="${_DROID_APP_NAME}_key"

# android-4 = Android 1.6
_DROID_SDK_VERSION="4"

# where the Android SDK/NDK live - there must be
# just ONE sdk/ndk installations under $HOME/run/ :
#     $  cd "${HOME}/run/"
#     $  wget http://dl.google.com/android/android-sdk_r12-linux_x86.tgz
#     $  wget http://dl.google.com/android/ndk/android-ndk-r6-linux-x86.tar.bz2
#     $  tar xzf android-sdk_r12-linux_x86.tgz
#     $  tar xjf android-ndk-r6-linux-x86.tar.bz2
_DROID_SDK_HOME="${HOME}"/run/android-sdk-*
_DROID_NDK_HOME="${HOME}"/run/android-ndk-*

# where the Android kernel will come from :
_DROID_KERNEL_DOMAIN="android.googlesource.com"
_DROID_KERNEL_VENDOR="common"   # hardware vendor
_DROID_KERNEL_VENDOR="goldfish" # hardware: Emulator (in the Android SDK)
# for Arm vendors, look at "arch/arm/configs/" inside the Linux Kernel sources

# $_DROID_KERNEL_HOME is where Linux Kernel sources live - the git repo would be
# $_DROID_KERNEL_HOME/goldfish/.git with the working copy in $_DROID_KERNEL_HOME/goldfish/ :
#     $  cd "${HOME}/go/${_DROID_KERNEL_DOMAIN}/kernel"    
#     $  git clone git://${_DROID_KERNEL_DOMAIN}/kernel/goldfish.git
[ -d "${HOME}/go/${_DROID_KERNEL_DOMAIN}/kernel" ] && _DROID_KERNEL_HOME="${HOME}/go/${_DROID_KERNEL_DOMAIN}/kernel"

# $_DROID_PLATFORM_HOME is where Android Platform sources live - the git repo would be
# $_DROID_PLATFORM_HOME/.git with the working copy in $_DROID_PLATFORM_HOME/ :
[ -d "${HOME}/go/${_DROID_KERNEL_DOMAIN}/android-2.2.1_r1" ] && _DROID_PLATFORM_HOME="${HOME}/go/${_DROID_KERNEL_DOMAIN}/android-2.2.1_r1"
[ -d "${HOME}/go/${_DROID_KERNEL_DOMAIN}/android-2.3.7_r1" ] && _DROID_PLATFORM_HOME="${HOME}/go/${_DROID_KERNEL_DOMAIN}/android-2.3.7_r1"
[ -d "${HOME}/go/${_DROID_KERNEL_DOMAIN}/android-4.0.1_r1" ] && _DROID_PLATFORM_HOME="${HOME}/go/${_DROID_KERNEL_DOMAIN}/android-4.0.1_r1"

#--------------------------------------- generals

# ensure that all alphabetic sorts produce same results elsewhere
LANG="C"
LC_ALL="C"
export LANG LC_ALL

PYTHONDONTWRITEBYTECODE="1"             # python: do not write .pyc files
PYTHONPATH="${PYTHONPATH}:${PWD}/share/python"
export PYTHONDONTWRITEBYTECODE PYTHONPATH

#--------------------------------------- The End
