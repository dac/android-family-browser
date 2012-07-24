#!/bin/bash
#
# @(#) share/tools/install_android_sdk_on_debian.bash
#
# USAGE
#
#   bash share/tools/install_android_sdk_on_debian.bash
#
# DESCRIPTION
#
#   Install Android SDK/NDK on Debin / Ubuntu / LinuxMint
#

_ARCH=` /bin/uname -m `

mkdir -p "${HOME}/run" && cd "${HOME}/run"

#--------------------------------------- install debian packages

echo ". [sudo] installing extra packages: ${_ARCH}"

sudo apt-get install --yes bison build-essential curl flex gcc-multilib git-core g++-multilib gnupg
sudo apt-get install --yes gperf libx11-dev pngcrush schedtool x11proto-core-dev zip zlib1g-dev

# next are needed only on amd64 - ignore errors on 32-bit platforms
if [ "${_ARCH}" = "x86_64" ]
then
    # if using 64-bit Fedora install
    #sudo yum install glibc.i686 ncurses-libs.i686 libstdc++.i686
    sudo apt-get install --yes libc6-dev-i386 lib32ncurses5-dev ia32-libs lib32readline5-dev lib32z-dev
    sudo apt-get install --yes libncurses5:i386
fi

# next are needed by us
sudo apt-get install --yes advancecomp doxygen optipng pngcrush python python-pysqlite2 sox

# install java6
echo "sun-java6-jdk shared/accepted-sun-dlj-v1-1 boolean true" | debconf-set-selections
#sudo apt-get install --yes sun-java6-jdk
#sudo update-java-alternatives --set java-6-sun
sudo apt-get install --yes openjdk-6-jdk
sudo update-java-alternatives --set openjdk-6

#--------------------------------------- install non-debian packages

_ANT_TARBALL="apache-ant-1.8.4-bin.tar.gz"
_SDK_TARBALL="android-sdk_r20.0.1-linux.tgz"
_NDK_TARBALL="android-ndk-r8b-linux-x86.tar.bz2"

_ECLIPSE_PREFIX="http://ftp.osuosl.org/pub/eclipse//technology/epp/downloads/release/juno/R/"
_ECLIPSE_TARBALL="eclipse-jee-juno-linux-gtk.tar.gz"
[ "${_ARCH}" = "x86_64" ] && _ECLIPSE_TARBALL="eclipse-jee-juno-linux-gtk-x86_64.tar.gz"

echo ". installing ${_ANT_TARBALL}, ${_SDK_TARBALL}, ${_NDK_TARBALL} ..."

# cleanup old releases
rm -fr apache-ant-*     2>/dev/null
rm -fr android-sdk-*    2>/dev/null
rm -fr android-ndk-*    2>/dev/null
rm -fr eclipse*         2>/dev/null

# download required tarballs
wget "http://www.apache.org/dist/ant/binaries/${_ANT_TARBALL}"
wget "http://dl.google.com/android/${_SDK_TARBALL}"
wget "http://dl.google.com/android/ndk/${_NDK_TARBALL}"

# download optional tarballs
wget "${_ECLIPSE_PREFIX}${_ECLIPSE_TARBALL}"

# install tarballs
tar xzf "${_ANT_TARBALL}"
tar xzf "${_SDK_TARBALL}"
tar xjf "${_NDK_TARBALL}"
tar xzf "${_ECLIPSE_TARBALL}"

# cleanup tarballs
rm "${_ANT_TARBALL}"
rm "${_SDK_TARBALL}"
rm "${_NDK_TARBALL}"
rm "${_ECLIPSE_TARBALL}"

#--------------------------------------- install setenv-android.sh

echo ". updating Android SDK"

# install setenv-android.sh

for _DIR in ${PWD}/apache-ant-*
do
    [ -d "${_DIR}" ] && _ANT_HOME="${_DIR}"
done

for _DIR in ${PWD}/android-sdk-*
do
    [ -d "${_DIR}" ] && _DROID_SDK_HOME="${_DIR}"
done

for _DIR in ${PWD}/android-ndk-*
do
    [ -d "${_DIR}" ] && _DROID_NDK_HOME="${_DIR}"
done

for _DIR in /usr/lib/jvm/java-6-sun-*
do
    [ -d "${_DIR}" ] && _JAVA_HOME="${_DIR}"
done

_PATH="${_ANT_HOME}/bin:${_JAVA_HOME}/bin"
_PATH="${_PATH}:${_DROID_NDK_HOME}"
_PATH="${_PATH}:${_DROID_SDK_HOME}/tools:${_DROID_SDK_HOME}/platform-tools"

_LD_LIBRARY_PATH="${_ANT_HOME}/lib:${_JAVA_HOME}/lib"

cat > setenv-android.sh <<END_OF_FILE
ANT_HOME="${_ANT_HOME}"
JAVA_HOME="${_JAVA_HOME}"
LD_LIBRARY_PATH="${_LD_LIBRARY_PATH}:\$LD_LIBRARY_PATH"
PATH="${_PATH}:\$PATH"
export ANT_HOME JAVA_HOME LD_LIBRARY_PATH PATH
END_OF_FILE

# install android sdk targets
. setenv-android.sh

# update - this takes long time
android update sdk --all --no-ui < /dev/null

#-------------------------------------- The End
