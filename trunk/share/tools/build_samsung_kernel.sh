#
# Build a SAMSUNG NEXUS Android Kernel
#

# you NEED the Android NDK (not the SDK) here:
_NDK_DIRECTORY="${HOME}/run/android-ndk-r5b/"
_KERNEL_DIRECTORY="${HOME}/go/android.googlesource.com/kernel/samsung"

#_KERNEL_REMOTE="android-samsung-2.6.35-gingerbread"
#_KERNEL_LOCAL="android-samsung-2.6.35"

_KERNEL_REMOTE="android-samsung-3.0-ics-mr1"
_KERNEL_LOCAL="android-samsung-3.0"

export ARCH="arm"
# TODO - this $CROSS_COMPILE is specific to android-ndk-r5b/...
# it might need to be smarter once we use newer NDK distributions!
export CROSS_COMPILE="${_NDK_DIRECTORY}/toolchains/arm-eabi-4.4.0/prebuilt/linux-x86/bin/arm-eabi-"

mkdir -p "${_KERNEL_DIRECTORY}"
cd "${_KERNEL_DIRECTORY}"

rm -fr samsung/                         2>/dev/null
git clone https://android.googlesource.com/kernel/samsung.git
cd samsung/

# need to checkout a detached branch before destroying ${_KERNEL_REMOTE}
# since it is not possible to delete the "current" working branch
git checkout origin/HEAD

# delete any old tracking branch, if it exists
git branch -d "${_KERNEL_LOCAL}"        2>/dev/null

# checkout remote branch
git checkout --track -b "${_KERNEL_LOCAL}" "remotes/origin/${_KERNEL_REMOTE}"
git clean -fd

echo "--- Configuring ${_KERNEL_REMOTE}"

make clean

# create initial .config file - it will be based on "versatile_defconfig"
#make defconfig ARCH="${ARCH}"
make herring_defconfig ARCH="${ARCH}"   # Samsung Nexus S
#make capela_defconfig ARCH="${ARCH}"   # Samsung Galaxy

# must change a few options to the original .config file
# NOTE: CONFIG_IKCONFIG is just to enable /proc/config.gz
# NOTE: CONFIG_BCM4329 is for building bcm4329.ko
# must change a few options to the original .config file
mv -f .config .config.samsung
(   cat .config.samsung | \
        sed -e "s/=m/=y/" | \
        grep -v -e CONFIG_IKCONFIG | \
        grep -v -e CONFIG_TUN | \
        grep -v -e CONFIG_NET_KEY | \
        grep -v -e CONFIG_PPP | \
        grep -v -e CONFIG_PPP_ASYNC | \
        grep -v -e CONFIG_PPP_SYNC_TTY | \
        grep -v -e CONFIG_BCM4329
    echo "CONFIG_IKCONFIG=y"
    echo "CONFIG_TUN=y" 
    echo "CONFIG_NET_KEY=y"
    echo "CONFIG_PPP=y" 
    echo "CONFIG_PPP_ASYNC=y" 
    echo "CONFIG_PPP_SYNC_TTY=y"
    echo "CONFIG_BCM4329=m"
) > .config
rm .config.samsung

echo "--- Building kernel ${_KERNEL_REMOTE} image"

rm -f "${_KERNEL_DIRECTORY}/arch/arm/boot/zImage" \
      "${_KERNEL_DIRECTORY}/drivers/net/wireless/bcm4329/bcm4329.ko" \
      2>/dev/null

# NOTE: CFLAGS_MODULE is for building bcm4329.ko
make -k zImage modules ARCH="${ARCH}" CROSS_COMPILE="${CROSS_COMPILE}" CFLAGS_MODULE="-fno-pic"

if [ ! -s "${_KERNEL_DIRECTORY}/arch/arm/boot/zImage" ]
then
    echo "*** Failed!"
    exit 1
fi

echo "Kernel ${_KERNEL_REMOTE} created as: "
ls -l "${_KERNEL_DIRECTORY}/arch/arm/boot/zImage" \
      "${_KERNEL_DIRECTORY}/drivers/net/wireless/bcm4329/bcm4329.ko"

#--------------------------------------- The End
