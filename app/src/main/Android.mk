LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_DEX_PREOPT:=false

LOCAL_STATIC_ANDROID_LIBRARIES := \
	android-support-design \
	android-support-v4 \
	android-support-v7-appcompat

ifeq ($(TARGET_BUILD_APPS),)
support_library_root_dir := frameworks/support
else
support_library_root_dir := prebuilts/sdk/current/support
endif    

LOCAL_SRC_FILES := \
    $(call all-java-files-under, java)

LOCAL_RESOURCE_DIR := \
    $(LOCAL_PATH)/res \
    $(support_library_root_dir)/v7/appcompat/res \
    $(support_library_root_dir)/design/res 

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay

LOCAL_PACKAGE_NAME := SystemAddons
LOCAL_CERTIFICATE := platform

include $(BUILD_PACKAGE)
