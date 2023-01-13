package com.dotcms.master_control.service.access;


import com.dotcms.master_control.vo.*;
import com.dotcms.master_control.model.access.UserAccess;
import com.dotcms.master_control.utils.KeycloakUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;


@Service
public class UserAccessServiceImpl implements UserAccessService {
    private static final Logger log = LogManager.getLogger(UserAccessServiceImpl.class);


    @Autowired
    private KeycloakSecurityContext keycloakSecurityContext;


    @Override
    public ResponseEntity<?> createUser(UserAccess userAccess) {
        try {
                String issuer = keycloakSecurityContext.getToken().getIssuer();
                String keycloakAuthUrl = issuer.substring(0, issuer.indexOf("/realms"));
                Boolean userCreated = KeycloakUtils.createUser(userAccess.getEmail(), userAccess.getFirstName(), userAccess.getLastName(), userAccess.getEmail(),
                        keycloakSecurityContext.getTokenString(), keycloakAuthUrl, keycloakSecurityContext.getRealm());
                if (userCreated) {
                    return ResponseDomain.postResponse("User created successfully.");
                } else return ResponseDomain.badRequest("Something went wrong.");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseDomain.badRequest("Something went wrong.");
        }
    }

    @Override
    public ResponseEntity<?> getAllUsers() {
        List<UserRepresentation> userAccessList = new ArrayList<>();
        try {
            String issuer = keycloakSecurityContext.getToken().getIssuer();
            String keycloakAuthUrl = issuer.substring(0, issuer.indexOf("/realms"));
            try {
                userAccessList = KeycloakUtils.fetchKeycloakUsers(keycloakSecurityContext.getTokenString(), keycloakAuthUrl, keycloakSecurityContext.getRealm());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return new ResponseEntity<>(userAccessList, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseDomain.badRequest("Something went wrong.");
        }
    }
    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        try(InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    @Override
    public ResponseEntity<?> uploadUsers(MultipartFile file) {
        try {
                    try {
                        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
                        XSSFSheet worksheet = workbook.getSheetAt(0);
                        List<JSONObject> objList = new ArrayList<>();
                        for (int index = 0; index < worksheet.getPhysicalNumberOfRows( ); index++) {
                            if ( index > 0 ) {
                                String name = "";
                                JSONObject obj = new JSONObject();
                                XSSFRow row = worksheet.getRow( index );
                                try {
                                    UserAccess userAccess = new UserAccess();
                                    userAccess.setFirstName(row.getCell(0) != null ? row.getCell(0).toString() : "");
                                    userAccess.setLastName(row.getCell(1) != null ? row.getCell(1).toString() : "");
                                    name = userAccess.getFirstName() + " " + userAccess.getLastName();
                                    userAccess.setEmail(row.getCell(2) != null ? row.getCell(2).toString() : "");
                                        ResponseEntity<?> response = createUser(userAccess);
                                        obj.put("name", userAccess.getFirstName() + " " + userAccess.getLastName());
                                        obj.put("status", "Success");
                                        objList.add(obj);
                                } catch (Exception e) {
                                    log.error(e.getMessage());
                                    obj.put("name", name);
                                    obj.put("status", "Failed");
                                    objList.add(obj);
                                }
                            }
                        }
                        workbook.close();
                        return new ResponseEntity<>(objList, HttpStatus.OK);
                    } catch (IOException ex) {
                        log.error(ex.getMessage(), ex);
                        return ResponseDomain.badRequest("Please check the data formats. Something wrong in there.");
                    }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return ResponseDomain.badRequest("Something went wrong.");
        }
    }

}
