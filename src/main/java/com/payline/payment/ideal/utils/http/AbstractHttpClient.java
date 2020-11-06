package com.payline.payment.ideal.utils.http;


import com.payline.payment.ideal.exception.HttpCallException;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * This utility class provides a basic HTTP client to send requests, using OkHttp library.
 * It must be extended to match each payment method needs.
 */
public abstract class AbstractHttpClient {

    private CloseableHttpClient client;
    private static final Logger LOGGER = LogManager.getLogger(AbstractHttpClient.class);


    /**
     * Instantiate a HTTP client.
     */

    protected AbstractHttpClient() {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2 * 1000)
                .setConnectionRequestTimeout(3 * 1000)
                .setSocketTimeout(4 * 1000)
                .build();

        this.client = HttpClientBuilder.create()
                .useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier())).build();

    }


    /**
     * Send a GET request
     *
     * @param url  URL RL scheme + host
     * @param path URL path
     * @return The response returned from the HTTP call
     */

    StringResponse doGet(String url, String path, Header[] headers){
        try {
            URI uri = new URI(url + path);

            final HttpGet httpGetRequest = new HttpGet(uri);
            httpGetRequest.setHeaders(headers);

            return getStringResponse(url, httpGetRequest);
        } catch (URISyntaxException e) {
            throw new HttpCallException("AbstractHttpClient.doGet.URISyntaxException", e);
        }
    }

    /**
     * Send a POST request.
     *
     * @param url  URL scheme + host
     * @param path URL path
     * @param body Request body
     * @return The response returned from the HTTP call
     */
    StringResponse doPost(String url, String path, Header[] headers, HttpEntity body) {

        try {
            URI uri = new URI(url + path);

            final HttpPost httpPostRequest = new HttpPost(uri);
            httpPostRequest.setHeaders(headers);
            httpPostRequest.setEntity(body);

            return getStringResponse(url, httpPostRequest);

        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HttpCallException("AbstractHttpClient.doPost.URISyntaxException", e);
        }
    }

    StringResponse getStringResponse(String url, HttpRequestBase httpPostRequest) {
        final long start = System.currentTimeMillis();
        int count = 0;
        StringResponse strResponse = null;
        String errMsg = null;
        while (count < 3 && strResponse == null) {
            try (CloseableHttpResponse httpResponse = this.client.execute(httpPostRequest)) {

                LOGGER.info("Start partner call... [URL: {}]", url);
                strResponse = StringResponse.fromHttpResponse( httpResponse );

                final long end = System.currentTimeMillis();

                LOGGER.info("End partner call [T: {}ms] [CODE: {}]", end - start, strResponse.getStatusCode());

            } catch (final IOException e) {
                LOGGER.error("Error while partner call [T: {}ms]", System.currentTimeMillis() - start, e);
                errMsg = e.getMessage();
            } finally {
                count++;
            }
        }

        if (strResponse == null) {
            if (errMsg == null) {
                throw new HttpCallException("Http response is empty");
            }
            throw new HttpCallException(errMsg);
        }
        LOGGER.info("Response obtained from partner API [{} {}]", strResponse.getStatusCode(), strResponse.getStatusMessage() );
        return strResponse;
    }


}
