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

import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;

/** Servlet that returns some example content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> messages;

  /**
    * Initialize the list with messages.
    */
  @Override
  public void init() {
      messages = new ArrayList<String>();
  }

  /**
    * Receive an input from frontend.
    */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    

      //Get the input from the form.
      String comment = request.getParameter("textInput");

      //Create entity
      Entity taskEntity = new Entity("Task");

      taskEntity.setProperty("comment", comment);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

      //Add entity to datastore
      datastore.put(taskEntity);

      messages.add(comment);

      //Give new information to frontend.
      doGet(request, response);

      //Redirect to index.html.
      response.sendRedirect("/index.html");
  }
  
  /**
   * Converts a List instance into a JSON string using the Gson library.
   */
  private String convertToJsonUsingGson(List messages) {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    return json;
  }

  /**
    * Function to give content to the frontend.
    */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String json = convertToJsonUsingGson(messages);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
