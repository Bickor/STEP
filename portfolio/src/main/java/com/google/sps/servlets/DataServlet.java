// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;

import com.google.sps.data.Comment;

/** Servlet that returns some content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
    * Receive an input from frontend.
    */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

      // Get the input from the form.
      String comment = request.getParameter("textInput");

      UserService userService = UserServiceFactory.getUserService();
      if (userService.isUserLoggedIn()) {
        // Create entity.
        Entity taskEntity = new Entity("Comment");

        long timestamp = System.currentTimeMillis();

        taskEntity.setProperty("comment", comment);
        taskEntity.setProperty("userEmail", userService.getCurrentUser().getEmail());
        taskEntity.setProperty("nickname", UserServlet.getNickname(userService.getCurrentUser().getEmail()));
        taskEntity.setProperty("userId", userService.getCurrentUser().getUserId());
        taskEntity.setProperty("timestamp", timestamp);

        // Add entity to datastore
        datastore.put(taskEntity);

        // Redirect to index.html.
        response.sendRedirect("/index.html");
      } else {
        response.sendError(401);
      }
  }
  
  /**
    * Function to give content to the frontend.
    */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Comment> messages = new ArrayList<>();

    // Populate array when initialized.
    Query query = new Query("Comment");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
        String comment = entity.getProperty("comment").toString();
        String useremail = entity.getProperty("userEmail").toString();
        long timestamp = Long.parseLong(entity.getProperty("timestamp").toString());
        System.out.println(entity.getProperty("nickname"));
        if (entity.getProperty("nickname") == null) {
            messages.add(new Comment(comment, useremail, "", timestamp));
        } else {
            String nickname = entity.getProperty("nickname").toString();
            messages.add(new Comment(comment, useremail, nickname, timestamp));
        }
    }
    
    // Turn array into json.
    String json = convertToJsonUsingGson(messages);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  /**
  * Converts a List instance into a JSON string using the Gson library.
  */
  private String convertToJsonUsingGson(List messages) {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    return json;
  }

}
