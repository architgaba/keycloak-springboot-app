package com.dotcms.master_control.controller;


import com.dotcms.master_control.model.access.UserAccess;
import com.dotcms.master_control.service.access.UserAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/access")
@CrossOrigin
public class AccessController {
    @Autowired
    private UserAccessService userAccessService;

    @PostMapping("/")
    private ResponseEntity<?> createUser(@RequestBody UserAccess userAccess) {
        return userAccessService.createUser(userAccess);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        return userAccessService.getAllUsers();
    }

    @PostMapping("/upload/bulk/users")
    public ResponseEntity<?> uploadUsers(@RequestParam MultipartFile file) {
        return userAccessService.uploadUsers(file);
    }





    }
