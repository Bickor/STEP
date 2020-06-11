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
 * Servlet that holds login information.
 */
@WebServlet("/nickname")
public class NicknameServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("application/json;");

      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
          String nickname = getNickname(userService.getCurrentUser().getUserId());
          String json = convertToJsonUsingGson(nickname);
          response.getWriter().println(json);
          //response.sendRedirect("/index.html");
      } else {
          response.sendError(401);
      }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

      UserService userService = UserServiceFactory.getUserService();
      if (!userService.isUserLoggedIn()) {
          response.sendRedirect("/nickname");
          return;
      }

      String nickname = request.getParameter("nickname");
      String id = userService.getCurrentUser().getUserId();

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity entity = new Entity("UserInfo", id);
      entity.setProperty("id", id);
      entity.setProperty("nickname", nickname);

      datastore.put(entity);

      response.sendRedirect("/index.html");

  }

  private String getNickname(String id) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("UserInfo").setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
      PreparedQuery results = datastore.prepare(query);
      Entity entity = results.asSingleEntity();
      if (entity == null) {
          return "";
      }
      String nickname = (String) entity.getProperty("nickname");
      return nickname;
  }

  /**
  * Converts a List instance into a JSON string using the Gson library.
  */
  private static String convertToJsonUsingGson(String messages) {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    return json;
  }

}