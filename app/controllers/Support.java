package controllers;

import models.Admin;
import play.*;
import play.libs.Crypto;
import play.mvc.*;
import views.html.*;
import play.data.*;
import java.io.*;
import java.util.*;
import play.db.ebean.*;


public class Support extends Controller {
  
  public static Result addAdmin() {

	  Admin newadmin = new Admin();
	  newadmin.setUsername("root2");
	  newadmin.setUserpass(Crypto.encryptAES("root2"));
	  newadmin.save();

	  return ok(signIn.render());
  }

}