#
# @(#) templates/share/jni/Android.mk
# THIS FILE MIGHT HAVE BEEN GENERATED FROM A TEMPLATE.
# PLEASE EDIT ONLY THE ORIGINAL TEMPLATE FILE.
#

#--------------------------------------- CFLAGS / CPPFLAGS

_DROID_CFLAGS   := -DANDROID_CHANGES -DANDROID_PATCHED -DANDROID_SMP
_DROID_CFLAGS   += -D_REENTRANT
#_DROID_CFLAGS  += -std=gnu99

# add full warnings on gcc...
_DROID_CFLAGS   += -Wall -Wextra

# next is not really needed, but it is useful to find out
# which .c and .h files are really for the final binaries
_DROID_CFLAGS   += -MD

_DROID_CPPFLAGS +=

#--------------------------------------- LDFLAGS

_DROID_LDFLAGS  := -Wl,-no-undefined

#_DROID_LDFLAGS += -Wl,-rpath=/data/data/ca.chaves.familyBrowser/lib/$(TARGET_ARCH)
# add the default Android settings
#_DROID_LDFLAGS += -Wl,-rpath=/system/lib -Wl,-rpath=/lib -Wl,-dynamic-linker,/system/bin/linker

#--------------------------------------- DEBUG / NDEBUG

# remove unused code - see http://gcc.gnu.org/ml/gcc-help/2003-08/msg00128.html
_DROID_CFLAGS   += -fdata-sections -ffunction-sections
_DROID_LDFLAGS  += -Wl,--gc-sections
_DROID_CFLAGS   += -O3 -DNDEBUG
_DROID_LDFLAGS  += -Wl,--strip-all -Wl,--discard-all

#--------------------------------------- The End
