package connector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;


public class HttpConnector {

    private static int BUFFER_SIZE = 2048;

    public static final String DEFAULT_SSL_PROTOCOL = "TLSv1.1";
    public static final String DEFAULT_KEY_ALGORITHM = KeyManagerFactory.getDefaultAlgorithm();
    public static final String DEFAULT_KEY_STORE_TYPE = KeyStore.getDefaultType();
    public static final String DEFAULT_TRUST_ALGORITHM = TrustManagerFactory.getDefaultAlgorithm();
    public static final String DEFAULT_TRUST_STORE_TYPE = KeyStore.getDefaultType();

    private String url;
    private int connectTimeout;
    private int readTimeout;

    private boolean isProxy;
    private String proxyType;
    private String proxyIp;
    private int proxyPort;

    private SSLConnectionSocketFactory sslConnectionSocketFactory;

    public HttpConnector(String url, int connectTimeout, int readTimeout) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        this.url = url;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    private static HostnameVerifier uncheckHostname = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    public void initSSL(String keyStorePath, char[] keyStorePassword, String trustStorePath, char[] trustStorePassword) throws GeneralSecurityException, IOException {

        FileInputStream keyStoreStream = null;
        FileInputStream trustStoreStream = null;
        try {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(DEFAULT_KEY_ALGORITHM);
            KeyStore keyStore = KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE);

            keyStoreStream = new FileInputStream(keyStorePath);
            keyStore.load(keyStoreStream, keyStorePassword);
            keyManagerFactory.init(keyStore, keyStorePassword);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(DEFAULT_TRUST_ALGORITHM);
            KeyStore trustStore = KeyStore.getInstance(DEFAULT_TRUST_STORE_TYPE);

            trustStoreStream = new FileInputStream(trustStorePath);
            trustStore.load(trustStoreStream, trustStorePassword);
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = null;
            sslContext = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL);
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, uncheckHostname);
        } finally {
            if (keyStoreStream != null) {
                try {
                    keyStoreStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (trustStoreStream != null) {
                try {
                    trustStoreStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initProxy(boolean isProxy, String proxyType, String proxyIp, int proxyPort) {
        this.isProxy = isProxy;
        this.proxyType = proxyType;
        this.proxyIp = proxyIp;
        this.proxyPort = proxyPort;
    }

    public String getAccessToken(String request) {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            // httpPost.releaseConnection();
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
            httpPost.setConfig(requestConfig);

            // 设置请求参数
            StringEntity stringEntity = new StringEntity(request, "UTF-8");
            httpPost.setEntity(stringEntity);

            String result = null;
            httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                // byte[] fileEntity = IOUtil.readFile(resEntity.getContent());
                // IOUtil.writeFile("E:/", "E:/1.txt", fileEntity);
                result = EntityUtils.toString(resEntity, "UTF-8");
            } else {
                StringBuilder builder = new StringBuilder();
                // 获取响应消息实体
                HttpEntity entity = httpResponse.getEntity();
                // 响应状态
                builder.append("status:" + httpResponse.getStatusLine());
                builder.append("headers:");
                HeaderIterator iterator = httpResponse.headerIterator();
                while (iterator.hasNext()) {
                    builder.append("\t" + iterator.next());
                }
                // 判断响应实体是否为空
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    builder.append("response content:" + responseString);
                }
                result = builder.toString();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getEvidenceSource(String accessToken) {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            // httpPost.releaseConnection();
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
            httpGet.setConfig(requestConfig);

            httpGet.setHeader("ApiAccessToken", accessToken);

            String result = null;
            httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                byte[] fileEntity = IOUtil.readFile(resEntity.getContent());
                IOUtil.writeFile("E:/", "E:/1.txt", fileEntity);
            } else {
                StringBuilder builder = new StringBuilder();
                // 获取响应消息实体
                HttpEntity entity = httpResponse.getEntity();
                // 响应状态
                builder.append("status:" + httpResponse.getStatusLine());
                builder.append("headers:");
                HeaderIterator iterator = httpResponse.headerIterator();
                while (iterator.hasNext()) {
                    builder.append("\t" + iterator.next());
                }
                // 判断响应实体是否为空
                if (entity != null) {
                    String responseString = EntityUtils.toString(entity);
                    builder.append("response content:" + responseString);
                }
                result = builder.toString();
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String deal(String source, String signature, String queryCode, List<File> fileList) throws Exception {
        HttpPost httpPost = new HttpPost(url);

        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            if (sslConnectionSocketFactory != null) {
                httpClient = HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory).build();
            } else {
                httpClient = HttpClientBuilder.create().build();
            }
            RequestConfig requestConfig = null;
            if (isProxy) {
                // 设置代理、请求和传输超时时间
                HttpHost proxy = new HttpHost(proxyIp, proxyPort, proxyType);
                requestConfig = RequestConfig.custom().setProxy(proxy).setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
            } else {
                // 设置请求和传输超时时间
                requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
            }
            httpPost.setConfig(requestConfig);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.setCharset(Charset.forName("UTF-8"));
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            if (fileList != null && fileList.size() > 0) {
                for (File file : fileList) {
                    multipartEntityBuilder.addBinaryBody("evidenceFile", file);
                }
            }
            // 给参数赋值
            if (source != null) {
                multipartEntityBuilder.addBinaryBody("source", source.getBytes("UTF-8"));
            }
            if (signature != null) {
                multipartEntityBuilder.addBinaryBody("signature", signature.getBytes("UTF-8"));
            }
            if (queryCode != null) {
                multipartEntityBuilder.addBinaryBody("queryCode", queryCode.getBytes("UTF-8"));
            }
            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);

            httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            String response = null;
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                response = EntityUtils.toString(resEntity, "UTF-8");
            } else {
                // 获取响应消息实体
                HttpEntity entity = httpResponse.getEntity();
                // 判断响应实体是否为空
                if (entity != null) {
                    response = EntityUtils.toString(entity, "UTF-8");
                }
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public byte[] download(String source, String signature, String queryCode, List<File> fileList) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            if (sslConnectionSocketFactory != null) {
                httpClient = HttpClientBuilder.create().setSSLSocketFactory(sslConnectionSocketFactory).build();
            } else {
                httpClient = HttpClientBuilder.create().build();
            }
            RequestConfig requestConfig = null;
            if (isProxy) {
                // 设置代理、请求和传输超时时间
                HttpHost proxy = new HttpHost(proxyIp, proxyPort, proxyType);
                requestConfig = RequestConfig.custom().setProxy(proxy).setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
            } else {
                // 设置请求和传输超时时间
                requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(readTimeout).build();
            }
            httpPost.setConfig(requestConfig);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            if (fileList != null && fileList.size() > 0) {
                for (File file : fileList) {
                    multipartEntityBuilder.addBinaryBody("evidenceFile", file);
                }
            }
            // 给参数赋值
            if (source != null) {
                multipartEntityBuilder.addBinaryBody("source", source.getBytes("UTF-8"));
            }
            if (signature != null) {
                multipartEntityBuilder.addBinaryBody("signature", signature.getBytes("UTF-8"));
            }
            if (queryCode != null) {
                multipartEntityBuilder.addBinaryBody("queryCode", queryCode.getBytes("UTF-8"));
            }
            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);

            httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            byte[] response = null;
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                response = read(resEntity.getContent());
            } else {
                // 获取响应消息实体
                HttpEntity entity = httpResponse.getEntity();
                // 判断响应实体是否为空
                if (entity != null) {
                    System.out.println(EntityUtils.toString(entity, "UTF-8"));
                }
            }
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] read(InputStream inputStream) {
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            byte[] buff = new byte[BUFFER_SIZE];
            int r = 0;
            while ((r = inputStream.read(buff)) != -1) {
                baos.write(buff, 0, r);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
