#include <openssl/evp.h>
#include <stdlib.h>
#include "com_voxelwind_server_jni_hash_NativeHashImpl.h"

typedef unsigned char byte;

void initializeDigest(EVP_MD_CTX *mdCtx) {
    EVP_DigestInit_ex(mdCtx, EVP_sha256(), NULL);
}

jlong JNICALL Java_com_voxelwind_server_jni_hash_NativeHashImpl_init(JNIEnv *env, jobject obj) {
    EVP_MD_CTX *mdCtx = EVP_MD_CTX_create();
    initializeDigest(mdCtx);
    return (jlong) mdCtx;
}

void JNICALL Java_com_voxelwind_server_jni_hash_NativeHashImpl_update(JNIEnv *env, jobject obj, jlong ctx, jlong in, jint bytes) {
    EVP_DigestUpdate((EVP_MD_CTX*) ctx, (byte*) in, bytes);
}

jbyteArray JNICALL Java_com_voxelwind_server_jni_hash_NativeHashImpl_digest(JNIEnv *env, jobject obj, jlong ctx) {
    // SHA-256 produces 32 bytes (256 bits)
    unsigned char output[32];

    // Hash the final output.
    EVP_MD_CTX *mdCtx = (EVP_MD_CTX*) ctx;
    EVP_DigestFinal_ex(mdCtx, (unsigned char*) output, NULL);

    // Reinitialize the context for further handling.
    initializeDigest(mdCtx);

    // Copy array into Java byte array.
    jbyteArray array = env->NewByteArray(32);
    env->SetByteArrayRegion(array, 0, 32, (jbyte*) output);
    return array;
}

JNIEXPORT void JNICALL Java_com_voxelwind_server_jni_hash_NativeHashImpl_free(JNIEnv *env, jobject obj, jlong ctx) {
    EVP_MD_CTX_destroy((EVP_MD_CTX*) ctx);
}