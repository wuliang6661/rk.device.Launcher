include $(CLEAR_VARS)
LOCAL_MODULE := cvc
LOCAL_SRC_FILES := prebuild/libcvc.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := peripherals
LOCAL_SRC_FILES := prebuild/libperipherals.so
include $(PREBUILT_SHARED_LIBRARY)