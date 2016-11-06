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

	  Admin.clearBase();
	  Admin newadmin = new Admin();
	  newadmin.setUsername("admin");
	  newadmin.setUserpass(Crypto.encryptAES("admin"));
	  newadmin.save();

	  return ok(signInPage.render());
  }

}