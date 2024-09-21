#include <jni.h>
#include <stdio.h>

extern "C" {
JNIEXPORT jobject JNICALL Java_com_github_shionic_hacklib_impl_HackLookupByNativeLibrary_makeLookup(JNIEnv *, jclass);
jint JNI_OnLoad(JavaVM *vm, void *reserved);
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    printf("Library loaded\n");
    return JNI_VERSION_1_8;
}

JNIEXPORT jobject JNICALL Java_com_github_shionic_hacklib_impl_HackLookupByNativeLibrary_makeLookup
  (JNIEnv * env, jclass cclazz) {
    jclass lookupClazz = env->FindClass("java/lang/invoke/MethodHandles$Lookup");
    if(lookupClazz == NULL) {
        return nullptr;
    }
    jfieldID lookupField = env->GetStaticFieldID(lookupClazz, "IMPL_LOOKUP", "Ljava/lang/invoke/MethodHandles$Lookup;");
    if(lookupField == NULL) {
            return nullptr;
    }
    jobject lookupObject = env->GetStaticObjectField(lookupClazz, lookupField);
    return lookupObject;
}