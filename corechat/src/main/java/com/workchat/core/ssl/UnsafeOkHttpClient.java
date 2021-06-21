package com.workchat.core.ssl;

import android.text.TextUtils;

import com.androidnetworking.interceptors.HttpLoggingInterceptor;

import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * https://futurestud.io/tutorials/retrofit-2-how-to-trust-unsafe-ssl-certificates-self-signed-expired
 */
public class UnsafeOkHttpClient {

    public static OkHttpClient getUnsafeOkHttpClient() {
        OkHttpClient.Builder builder = getBuilder("");
        return builder.build();
    }

    /*public static OkHttpClient getUnsafeOkHttpClient(CookieStore cookie) {
        OkHttpClient.Builder builder = getBuilder();
        builder.cookieJar(cookie);
        return builder.build();
    }*/


    public static OkHttpClient.Builder getBuilder(String token) {
        try {
            //#region PASS SERVER CERTIFICED///////////////////////////////////////////////////////////////
            //trust certificate
            X509TrustManager trustManager = null;
            SSLSocketFactory sslSocketFactory = null;
            try {
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                trustManager = (X509TrustManager) trustManagers[0];

                ////
                // Create a trust manager that does not validate certificate chains
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[]{};
                            }
                        }
                };

                // Install the all-trusting trust manager
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                // Create an ssl socket factory with our all-trusting manager
                sslSocketFactory = sslContext.getSocketFactory();

            } catch (Exception e) {
                e.printStackTrace();
            }
            //#endregion PASS SERVER CERTIFICED///////////////////////////////////////////////////////////////


            /////
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                    .cookieJar(cookie)
                    .connectTimeout(60 * 1000, TimeUnit.SECONDS);
//                    .addInterceptor(interceptor);

            if(!TextUtils.isEmpty(token)){
                builder.addNetworkInterceptor(new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Chain chain) throws IOException {
                            Request request = null;
                            Request original = chain.request();
                            // Request customization: add request headers
                            Request.Builder requestBuilder = original.newBuilder()
                                    .addHeader("Authorization", token);

                            request = requestBuilder.build();
                            return chain.proceed(request);
                        }
                    });
            }

            if (trustManager != null && sslSocketFactory != null) {
                builder.sslSocketFactory(sslSocketFactory, trustManager);
            }
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });


            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
