package com.bq.oss.lib.ws.filter;

import javax.ws.rs.container.ContainerRequestContext;

import com.bq.oss.lib.ws.model.CustomHeaders;

/**
 * @author Francisco Sanchez
 */
public class FilterUtil {
    public static boolean hasNoRedirectHeader(ContainerRequestContext request) {
        return Boolean.parseBoolean(request.getHeaderString(CustomHeaders.NO_REDIRECT_HEADER.toString()));
    }

    public static boolean redirect(int status) {
        return status >= 300 && status < 400;
    }
}
