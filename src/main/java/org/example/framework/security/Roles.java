package org.example.framework.security;

public class Roles {
  private Roles() {}

  public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_MODERATOR = "ROLE_MODERATOR";
  public static final String ROLE_USER = "ROLE_USER";

  public static String getById(long i){
    switch ((int)i){
      case -1 : return ROLE_ANONYMOUS; // okay
      case 1 : return ROLE_ADMIN;
      //case 1 : return  ROLE_MODERATOR;
      //case 2: return ROLE_USER;
      default: return ROLE_ANONYMOUS;

    }

  }

}
