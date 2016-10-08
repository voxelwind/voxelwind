#include <openssl/evp.h>
#include "net_md_5_bungee_jni_cipher_NativeCipherImpl.h"

typedef unsigned char byte;

jlong JNICALL Java_net_md_15_bungee_jni_cipher_NativeCipherImpl_init(JNIEnv* env, jobject obj, jboolean forEncryption, jbyteArray key, jbyteArray iv) {
    jbyte *keyBytes = env->GetByteArrayElements(key, NULL);
    jbyte *ivBytes = env->GetByteArrayElements(iv, NULL);

    // TODO: Perhaps we need to throw some exceptions in the unlikely event this fails?
    EVP_CIPHER_CTX *cipherCtx = EVP_CIPHER_CTX_new();
    EVP_CipherInit(cipherCtx, EVP_aes_256_cfb8(), (byte*) keyBytes, (byte*) ivBytes, forEncryption);

    env->ReleaseByteArrayElements(key, keyBytes, JNI_ABORT);
    env->ReleaseByteArrayElements(iv, ivBytes, JNI_ABORT);
    return (jlong) cipherCtx;
}

void Java_net_md_15_bungee_jni_cipher_NativeCipherImpl_free(JNIEnv* env, jobject obj, jlong ctx) {
    // TODO: Perhaps we need to throw some exceptions in the unlikely event this fails?
    EVP_CIPHER_CTX_free((EVP_CIPHER_CTX*) ctx);
}

void Java_net_md_15_bungee_jni_cipher_NativeCipherImpl_cipher(JNIEnv* env, jobject obj, jlong ctx, jlong in, jlong out, jint length) {
    // TODO: Perhaps we need to throw some exceptions in the unlikely event this fails?
    EVP_CipherUpdate((EVP_CIPHER_CTX*) ctx, (byte*) out, &length, (byte*) in, length);
}
