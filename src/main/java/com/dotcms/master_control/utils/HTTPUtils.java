package com.dotcms.master_control.utils;

import com.google.common.primitives.Primitives;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HTTPUtils {

    public static CloseableHttpResponse  callHTTPPostRS(MultipartEntityBuilder builder,String uri) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPostObj = new HttpPost(uri);
        HttpEntity multipart = builder.build();
        httpPostObj.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(httpPostObj);
        return response;
    }
    public static HttpResponse callHTTPGetRS(String uri)throws IOException{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet =new HttpGet(uri);
            System.out.println("Executing request " + httpGet.getRequestLine());
            ResponseHandler<String> responseHandler = response -> {
                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            CloseableHttpResponse responseBody = httpclient.execute(httpGet);
            System.out.println("----------------------------------------");
            System.out.println(responseBody);
            return responseBody;
        }


    public static CloseableHttpResponse callHttpdeleteDoc(String url) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(url);
        System.out.println("Executing request " + httpDelete.getRequestLine());
        // Create a custom response handler
        ResponseHandler<String> responseHandler = response -> {
            int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
        CloseableHttpResponse responseBody = httpclient.execute(httpDelete);
        System.out.println("----------------------------------------");
        System.out.println(responseBody);

        return responseBody;
    }


    public static <T> T callRS(HttpGet getRequest, Class<T> classOfT) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet getRequest = new HttpGet(url);
//        getRequest.addHeader("accept", "application/json");
//        getRequest.addHeader("Authorization", "Bearer " + token);
        CloseableHttpResponse response = null;
        System.out.println("getRequest :: " + getRequest);
        response = httpClient.execute(getRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatusLine().getStatusCode());
        }
        HttpEntity entity = response.getEntity();
        BufferedReader br = new BufferedReader(new InputStreamReader((entity.getContent())));
        Gson gson = new Gson();
        Object responseJsonObject = gson.fromJson(br, classOfT);
        EntityUtils.consume(entity);
        response.close();
        return Primitives.wrap(classOfT).cast(responseJsonObject);
    }

    public static CloseableHttpResponse callHTTPPutRS(HttpPut putRequest) throws IOException {

        CloseableHttpResponse response = null;

        CloseableHttpClient httpClient = HttpClients.createDefault();

        response = httpClient.execute(putRequest);

        return response;
    }

    public static CloseableHttpResponse callHTTPPostRS(HttpPost postRequest) throws IOException {

        CloseableHttpResponse response = null;

        CloseableHttpClient httpClient = HttpClients.createDefault();

        response = httpClient.execute(postRequest);

        return response;
    }

    public static CloseableHttpResponse callHTTPDeleteRS(HttpDeleteWithBody deleteRequest) throws IOException {

        CloseableHttpResponse response = null;

        CloseableHttpClient httpClient = HttpClients.createDefault();

        response = httpClient.execute(deleteRequest);

        return response;
    }
}
