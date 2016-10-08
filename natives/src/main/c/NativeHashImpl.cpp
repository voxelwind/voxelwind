#include <openssl/evp.h>
#include <stdlib.h>
#include "com_voxelwind_server_jni_hash_NativeHashImpl.h"

typedef unsigned char byte;

jlong JNICALL Java_com_voxelwind_server_jni_hash_NativeHashImpl_init(JNIEnv *env, jobject obj) {
    EVP_MD_CTX *mdCtx = EVP_MD_CTX_create();
    EVP_DigestInit(mdCtx, EVP_sha256());

    return (jlong) mdCtx;
}

void JNICALL Java_com_voxelwind_server_jni_hash_NativeHashImpl_update(JNIEnv *env, jobject obj, jlong ctx, jlong in, jint bytes) {
    EVP_DigestUpdate((EVP_MD_CTX*) ctx, (byte*) in, bytes);
}

// This is from the existing native functions and may not even be needed. TODO
jint throwException(JNIEnv *env, const char* message, int err) {
    // These can't be static for some unknown reason
    jclass exceptionClass = env->FindClass("net/md_5/bungee/jni/NativeCodeException");
    jmethodID exceptionInitID = env->GetMethodID(exceptionClass, "<init>", "(Ljava/lang/String;I)V");

    jstring jMessage = env->NewStringUTF(message);

    jthrowable throwable = (jthrowable) env->NewObject(exceptionClass, exceptionInitID, jMessage, err);
    return env->Throw(throwable);
}

jbyteArray JNICALL Java_com_voxelwind_server_jni_hash_NativeHashImpl_digest(JNIEnv *env, jobject obj, jlong ctx) {
    // SHA-256 produces 32 bytes (256 bits)
    void *output = malloc(32);
    if (output == NULL) {
        throwException(env, "Ran out of memory while allocating memory for SHA256 output: ", 0);
    }
    EVP_DigestFinal((EVP_MD_CTX*) ctx, (unsigned char*) output, NULL);
    jbyteArray array = env->NewByteArray(32);
    env->SetByteArrayRegion(array, 0, 32, (jbyte*) output);

    // Output is no longer required.
    free(output);
    return array;
}
