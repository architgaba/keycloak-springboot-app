package com.dotcms.master_control.service.access;


import com.dotcms.master_control.model.access.UserAccess;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserAccessService {

    ResponseEntity<?> createUser(UserAccess userAccess);


    ResponseEntity<?> getAllUsers();


    ResponseEntity<?> uploadUsers(MultipartFile file);

}
