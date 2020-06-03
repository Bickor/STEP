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

import java.util.ArrayList;
import com.google.gson.Gson;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> messages;

  /**
    * Initialize the list with messages.
    */
  @Override
  public void init() {
      messages = new ArrayList<>();
      messages.add("Hello, this is Martin!");
      messages.add("How is your day?");
      messages.add("Did you learn something new today?");
  }
  
  /**
   * Converts a ServerStats instance into a JSON string using the Gson library. Note: We first added
   * the Gson library dependency to pom.xml.
   */
  private String convertToJsonUsingGson(ArrayList messages) {
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
