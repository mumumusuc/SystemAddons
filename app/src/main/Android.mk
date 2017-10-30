LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_PACKAGE_NAME := SystemAddons
LOCAL_CERTIFICATE := platform
LOCAL_MODULE_TAGS := optional
LOCAL_DEX_PREOPT:=false

LOCAL_STATIC_ANDROID_LIBRARIES := \
    android-support-v4 \
    android-support-v7-appcompat \
    android-support-design \
    android-support-v7-recyclerview
    #android-support-transition \

LOCAL_PROGUARD_FLAG_FILES := \
    ../../../../../../../frameworks/support/design/proguard-rules.pro \
    ../../../../../../../frameworks/support/v7/recyclerview/proguard-rules.pro


LOCAL_SRC_FILES := \
    $(call all-java-files-under, java)

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res \
    frameworks/support/v7/appcompat/res \
    frameworks/support/design/res

#LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.appcompat \
    --extra-packages android.support.design

LOCAL_USE_AAPT2 := true
include $(BUILD_PACKAGE)
