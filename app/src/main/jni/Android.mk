LOCAL_PATH := $(call my-dir)

# enable local log system
LOCAL_LDLIBS += -llog
# root
LOCAL_CERTIFICATE := platform

include $(LOCAL_PATH)/prebuild/Android.mk