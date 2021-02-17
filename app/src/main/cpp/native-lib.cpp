#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_eit_brnnda_Utils_Constent_apiURL(JNIEnv* env,jobject /* this */) {
    std::string url = "aHR0cHM6Ly9icm5uZGEuZXNzZW50aWFsLWluZm90ZWNoLmRldg==";
    return env->NewStringUTF(url.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_eit_brnnda_Utils_Constent_apiKEY(JNIEnv* env,jobject /* this */) {
    std::string apiKey = "YWE=";
    return env->NewStringUTF(apiKey.c_str());
}
extern "C" JNIEXPORT jstring JNICALL
Java_com_eit_brnnda_Utils_Constent_amarPayStoreId(JNIEnv* env,jobject /* this */) {
    std::string secret = "YWFtYXJwYXl0ZXN0";
    return env->NewStringUTF(secret.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_eit_brnnda_Utils_Constent_amarPaySignature(JNIEnv* env,jobject /* this */) {
    std::string secret = "ZGJiNzQ4OTRlODI0MTVhMmY3ZmYwZWMzYTk3ZTQxODM=";
    return env->NewStringUTF(secret.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_eit_brnnda_Utils_Constent_apStore(JNIEnv* env,jobject /* this */) {
    std::string secret = "aamarpaytest";
    return env->NewStringUTF(secret.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_eit_brnnda_Utils_Constent_apSignature(JNIEnv* env,jobject /* this */) {
    std::string secret = "dbb74894e82415a2f7ff0ec3a97e4183";
    return env->NewStringUTF(secret.c_str());
}

