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

jbyteArray JNICALL Java_com_voxelwind_server_jni_hash_NativeHashImpl_digest(JNIEnv *env, jobject obj, jlong ctx) {
    // SHA-256 produces 32 bytes (256 bits)
    unsigned char output[32];
    EVP_DigestFinal((EVP_MD_CTX*) ctx, (unsigned char*) output, NULL);
    jbyteArray array = env->NewByteArray(32);
    env->SetByteArrayRegion(array, 0, 32, (jbyte*) output);
    return array;
}
