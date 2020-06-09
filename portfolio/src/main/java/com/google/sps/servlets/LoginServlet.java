package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Dictionary;
import java.util.Hashtable;
import com.google.gson.Gson;

/**
 * Servlet that holds login information.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private Dictionary<String, String> login;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    login = new Hashtable<>();

    UserService userService = UserServiceFactory.getUserService();

    // Check if user logged in
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      // Text displayed
      login.put("Loggedin", "True");
      login.put("User", userEmail);
      login.put("URL", logoutUrl);
      //response.getWriter().println("User is Logged in with: " + userEmail);
      //response.getWriter().println("To log out go to: <a href=\"" + logoutUrl + "\">here</a>");
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      // Text displayed
      login.put("Loggedin", "False");
      login.put("URL", loginUrl);
      //response.getWriter().println("User is not logged in.");
      //response.getWriter().println("Login <a href=\"" + loginUrl + "\">here</a>");
    }
    String json = convertToJsonUsingGson(login);
    response.getWriter().println(json);
  }

  /**
  * Converts a List instance into a JSON string using the Gson library.
  */
  private String convertToJsonUsingGson(Dictionary messages) {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    return json;
  }

}