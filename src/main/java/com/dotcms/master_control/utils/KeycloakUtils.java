package com.dotcms.master_control.utils;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.dotcms.master_control.vo.ResBody;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeycloakUtils {

    private static final Logger log = LogManager.getLogger(KeycloakUtils.class);

    public static Boolean createUser(String username, String firstName, String lastName, String email, String token,
                                     String keycloakUrl, String keycloakRealm) {
        log.info("Entering Service Class ::: KeycloakUtils ::: method ::: createUser");
        String arguments = String.format("%s ::: %s ::: %s ::: %s ::: %s ::: %s ::: %s", username, firstName, lastName, email, token,
                keycloakUrl, keycloakRealm);
        try {
            String keycloakCreateUserUrl = keycloakUrl + "/admin/realms/" + keycloakRealm + "/users";
            JsonObject userPayload = new JsonObject();
            userPayload.addProperty("username", username);
            userPayload.addProperty("enabled", Boolean.TRUE);
            userPayload.addProperty("firstName", firstName);
            userPayload.addProperty("lastName", lastName);
            userPayload.addProperty("email", email);
            userPayload.addProperty("emailVerified", Boolean.TRUE);

            StringEntity entity = new StringEntity(userPayload.toString(), ContentType.APPLICATION_JSON);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(keycloakCreateUserUrl);
            request.addHeader("Authorization", "Bearer " + token);
            request.setEntity(entity);

            HttpResponse response = httpClient.execute(request);
            log.info("Create User Response from Keycloak Rest API::" + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
                String keycloakUserId = fetchKeycloakUserID(token, keycloakUrl, keycloakRealm, email);
                if (!keycloakUserId.equalsIgnoreCase("")) {
                    Boolean passwordSet = resetPassword(token, keycloakUrl, keycloakRealm, keycloakUserId, "123");
                    if (passwordSet) {
                        log.info("Exit Service Class ::: KeycloakUtils ::: method ::: createUser");
                        return Boolean.TRUE;
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }
    public static String fetchKeycloakUserID(String token, String keycloakUrl, String keycloakRealm, String
            emailId) {
        log.info("Entering Service Class ::: KeycloakUtils ::: method ::: fetchKeycloakUserID");
        String arguments = String.format("%s ::: %s ::: %s ::: %s", token, keycloakUrl, keycloakRealm, emailId);
        log.info("Method Arguments :::" + arguments);
        String userId = "";
        String getUserDetailsUrl = keycloakUrl + "/admin/realms/" + keycloakRealm + "/users?email=" + emailId;
        log.info("getUserDetailsUrl :: " + getUserDetailsUrl);
        HttpGet getRequest = new HttpGet(getUserDetailsUrl);
        getRequest.addHeader("accept", "application/json");
        getRequest.addHeader("Authorization", "Bearer " + token);
        try {
            UserRepresentation[] userRepresentations = HTTPUtils.callRS(getRequest, UserRepresentation[].class);
            if (userRepresentations.length > 0) {
                log.info("Keycloak User Details" + userRepresentations[0].toString());
                log.info("Keycloak User ID :::" + userRepresentations[0].getId());
                log.info("Exit Service Class ::: KeycloakUtils ::: method ::: fetchKeycloakUserID");
                return userRepresentations[0].getId();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (NullPointerException npe) {
            log.error(npe.getMessage(), npe);
        }
        return userId;
    }

    public static Boolean resetPassword(String token, String keycloakUrl, String keycloakRealm, String kcUserId, String password) {
        log.info("Entering Service Class ::: KeycloakUtils ::: method ::: resetPassword");
        String resetPasswordUrl = keycloakUrl + "/admin/realms/" + keycloakRealm + "/users/" + kcUserId + "/reset-password";
        log.info("Reset password Url :: " + resetPasswordUrl);
        HttpPut putRequest = new HttpPut(resetPasswordUrl);
        putRequest.addHeader("Accept", "application/json");
        putRequest.addHeader("Authorization", "Bearer " + token);
        JsonObject userPayload = new JsonObject();
        userPayload.addProperty("type", "password");
        userPayload.addProperty("value", password);
        userPayload.addProperty("temporary", "false");

        StringEntity entity = new StringEntity(userPayload.toString(), ContentType.APPLICATION_JSON);
        putRequest.setEntity(entity);

        try {
            CloseableHttpResponse response = HTTPUtils.callHTTPPutRS(putRequest);
            if (response.getStatusLine().getStatusCode() == 204 || response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201) {
                log.info("Exit Service Class ::: KeycloakUtils ::: method ::: resetPassword");
                return Boolean.TRUE;
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("Exit Service Class ::: KeycloakUtils ::: method ::: resetPassword");
        return false;
    }

    public static List<UserRepresentation> fetchKeycloakUsers(String token, String keycloakUrl, String keycloakRealm) {
        log.info("Entering Service Class ::: KeycloakUtils ::: method ::: fetchKeycloakUserID");
        List<ResBody> userMailStatusList = new ArrayList<>();
        int max = 1000;
        String userId = "";
        List<UserRepresentation> userRepresentationsList= new ArrayList<>();
        String getUserDetailsUrl = keycloakUrl + "/admin/realms/" + keycloakRealm + "/users?max=" + max;
        log.info("getUserDetailsUrl :: " + getUserDetailsUrl);
        HttpGet getRequest = new HttpGet(getUserDetailsUrl);
        getRequest.addHeader("accept", "application/json");
        getRequest.addHeader("Authorization", "Bearer " + token);
        try {
            userRepresentationsList = Arrays.asList(HTTPUtils.callRS(getRequest, UserRepresentation[].class));
            return userRepresentationsList;
        } catch (IOException e) {
            log.error("Exception while fetching user details from Keycloak :::" + e.getMessage());
            log.error(e.getMessage(), e);
        } catch (NullPointerException npe) {
            log.error("NullPointerException occured while fetching user details from Keycloak :::" + npe.getMessage());
            log.error(npe.getMessage(), npe);
        }
        log.info("Exit Service Class ::: KeycloakUtils ::: method ::: fetchKeycloakUserID");
        return userRepresentationsList;
    }






}
