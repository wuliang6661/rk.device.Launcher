LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := Capture
LOCAL_SRC_FILES := capture_jni.c capture.c
# enable local log system
LOCAL_LDLIBS += -llog
# root
LOCAL_CERTIFICATE := platform

include $(BUILD_SHARED_LIBRARY)

include $(LOCAL_PATH)/prebuild/Android.mk