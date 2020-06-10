package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.HashMap;
import com.google.gson.Gson;

/**
 * Servlet that holds login information.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    Map<String, Object> login = new HashMap<>();

    UserService userService = UserServiceFactory.getUserService();

    // Check if user logged in
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      // Text displayed
      login.put("Loggedin", true);
      login.put("User", userEmail);
      login.put("URL", logoutUrl);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      // Text displayed
      login.put("Loggedin", false);
      login.put("URL", loginUrl);
    }
    String json = convertToJsonUsingGson(login);
    response.getWriter().println(json);
  }

  /**
  * Converts a List instance into a JSON string using the Gson library.
  */
  private static String convertToJsonUsingGson(Map messages) {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    return json;
  }

}