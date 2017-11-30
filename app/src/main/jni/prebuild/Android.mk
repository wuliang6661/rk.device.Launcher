include $(CLEAR_VARS)
LOCAL_MODULE := opencv_java
LOCAL_SRC_FILES := prebuild/libopencv_java.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := stereovision
LOCAL_SRC_FILES := prebuild/libstereovision.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := face
LOCAL_SRC_FILES := prebuild/libface.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := comm
LOCAL_SRC_FILES := prebuild/libcomm.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := cvc
LOCAL_SRC_FILES := prebuild/libcvc.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := dsvc
LOCAL_SRC_FILES := prebuild/libdscv.so
include $(PREBUILT_SHARED_LIBRARY)



