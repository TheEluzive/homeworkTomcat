package org.example.app.util;

import jakarta.servlet.http.HttpServletRequest;
import org.example.app.domain.User;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.Authentication;

import java.util.ArrayList;

public class UserHelper {
    private UserHelper() {
    }

    // TODO: beautify
    public static User getUser(HttpServletRequest req) {
        return ((User) ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR)).getPrincipal());
    }



    public static boolean isRoles(HttpServletRequest req, String stringRole) {
        ArrayList<String> listRoles = (ArrayList<String>)
                ((Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR)).getAuthorities();
        for (String roles : listRoles
        ) {
            if (roles.equals(stringRole))
                return true;
        }
        return false;
    }
}
