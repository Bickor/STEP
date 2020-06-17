package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.data.Marker;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Servlet for markers in the map.
 */ 
@WebServlet("/markers")
public class MarkerServlet extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        
        Collection<Marker> markers = new ArrayList<>();

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query query = new Query("Marker");
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity: results.asIterable()) {
            double lat = (double) entity.getProperty("lat");
            double lng = (double) entity.getProperty("lng");
            String content = (String) entity.getProperty("content");

            markers.add(new Marker(lat, lng, content));
        }

        Gson gson = new Gson();
        String json = gson.toJson(markers);

        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lng = Double.parseDouble(request.getParameter("lng"));
        String content = Jsoup.clean(request.getParameter("content"), Whitelist.none());

        Entity markerEntity = new Entity("Marker");
        markerEntity.setProperty("lat", lat);
        markerEntity.setProperty("lng", lng);
        markerEntity.setProperty("content", content);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(markerEntity);
    }


}