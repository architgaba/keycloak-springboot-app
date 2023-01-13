package com.dotcms.master_control.model.access;

import lombok.Data;
import javax.persistence.*;

@Data
public class UserAccess {

    private String firstName;
    private String lastName;
    private String email;


}
