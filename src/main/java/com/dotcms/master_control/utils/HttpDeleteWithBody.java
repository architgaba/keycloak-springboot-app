package com.dotcms.master_control.utils;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
    public static final String METHOD_NAME = "DELETE";

    public HttpDeleteWithBody(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    public String getMethod() {
        return METHOD_NAME;
    }
}
