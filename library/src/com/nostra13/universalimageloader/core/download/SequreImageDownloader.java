package com.nostra13.universalimageloader.core.download;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class SequreImageDownloader extends BaseImageDownloader {
	
	public static String TAG = SequreImageDownloader.class.getSimpleName();
	
	private Map<String, String> mRequestProperties;

	public SequreImageDownloader(Context context, Map<String, String> requestProperties) {
		super(context);
		mRequestProperties = requestProperties;
	}

	@Override
	protected HttpURLConnection createConnection(String url) throws IOException {
		String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
		HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
		conn.setConnectTimeout(connectTimeout);
		conn.setReadTimeout(readTimeout);
		
		Set<String> keySet = mRequestProperties.keySet();
		for(String key: keySet) {
			conn.setRequestProperty(key, mRequestProperties.get(key));
		}
		
		usingSSL(conn);
		return conn;
	}
	


	private void usingSSL(HttpURLConnection connection) {
		if ("https".equals(connection.getURL().getProtocol())) {
			try {
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() {
						return new java.security.cert.X509Certificate[] {};
					}

					public void checkClientTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
					}

					public void checkServerTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
					}
				} };
				try {
					SSLContext sc = SSLContext.getInstance("TLS");
					sc.init(null, trustAllCerts,
							new java.security.SecureRandom());
					((HttpsURLConnection) connection).setSSLSocketFactory(sc
							.getSocketFactory());
					((HttpsURLConnection) connection)
							.setHostnameVerifier(new HostnameVerifier() {
								@Override
								public boolean verify(String hostname,
										SSLSession session) {
									return true;
								}
							});
				} catch (KeyManagementException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				Log.e(TAG, "usingSSL", e);
			}

		}
	}

}
