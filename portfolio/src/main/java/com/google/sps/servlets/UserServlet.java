package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
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
 * Servlet that holds user information.
 */
@WebServlet("/user")
public class UserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("application/json;");

      Map<String, Object> user = new HashMap<>();

      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
          String nickname = getNickname(userService.getCurrentUser().getEmail());
          user.put("nickname", nickname);
          String json = convertToJsonUsingGson(user);
          response.getWriter().println(json);
      } else {
          response.sendError(401);
      }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      UserService userService = UserServiceFactory.getUserService();
      if (!userService.isUserLoggedIn()) {
          response.sendError(401);
          return;
      }

      String nickname = request.getParameter("nickname");
      String id = userService.getCurrentUser().getUserId();
      String email = userService.getCurrentUser().getEmail();

      // Updates UserInfo database.
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity userEntity = new Entity("UserInfo", id);
      userEntity.setProperty("id", id);
      userEntity.setProperty("email", email);
      userEntity.setProperty("nickname", nickname);

      datastore.put(userEntity);

      // Updates comments database with new nickname,
      Query query = new Query("Comment").setFilter(new Query.FilterPredicate("userId", Query.FilterOperator.EQUAL, id));
      PreparedQuery results = datastore.prepare(query);

      for (Entity commentEntity : results.asIterable()) {
          commentEntity.setProperty("comment", commentEntity.getProperty("comment").toString());
          commentEntity.setProperty("userEmail", commentEntity.getProperty("userEmail").toString());
          commentEntity.setProperty("nickname", nickname);
          commentEntity.setProperty("timestamp", commentEntity.getProperty("timestamp").toString());
          datastore.put(commentEntity);
      }
      
      response.sendRedirect("/index.html");
  }

  /**
   * Get the nickname of a user by using the email.
   */ 
  public static String getNickname(String email) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("UserInfo").setFilter(new Query.FilterPredicate("email", Query.FilterOperator.EQUAL, email));
      PreparedQuery results = datastore.prepare(query);
      Entity entity = results.asSingleEntity();
      if (entity == null) {
          return "";
      }
      String nickname = (String) entity.getProperty("nickname");
      return nickname;
  }

  /**
  * Converts a Map instance into a JSON string using the Gson library.
  */
  private static String convertToJsonUsingGson(Map messages) {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    return json;
  }

}