/** package org.example.tpstreaminganalytics.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/rest/*")
public class SimpleApiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        if (path == null || path.equals("/") || path.equals("/health")) {
            out.println("{");
            out.println("  \"status\": \"OK\",");
            out.println("  \"service\": \"streaming-analytics\",");
            out.println("  \"version\": \"1.0\",");
            out.println("  \"timestamp\": \"" + new java.util.Date() + "\"");
            out.println("}");
        }
        else if (path.equals("/test")) {
            out.println("{");
            out.println("  \"message\": \"API is working!\",");
            out.println("  \"server\": \"Tomcat 9.0.109\",");
            out.println("  \"endpoints\": [");
            out.println("    \"/api/health\",");
            out.println("    \"/api/test\",");
            out.println("    \"/api/videos/top\"");
            out.println("  ]");
            out.println("}");
        }
        else if (path.equals("/videos/top")) {
            out.println("[");
            out.println("  {");
            out.println("    \"videoId\": \"video_001\",");
            out.println("    \"title\": \"Introduction to Java\",");
            out.println("    \"category\": \"education\",");
            out.println("    \"views\": 1500");
            out.println("  },");
            out.println("  {");
            out.println("    \"videoId\": \"video_002\",");
            out.println("    \"title\": \"JavaScript Tips 2024\",");
            out.println("    \"category\": \"education\",");
            out.println("    \"views\": 2800");
            out.println("  },");
            out.println("  {");
            out.println("    \"videoId\": \"video_003\",");
            out.println("    \"title\": \"Workout Music Mix\",");
            out.println("    \"category\": \"music\",");
            out.println("    \"views\": 4500");
            out.println("  }");
            out.println("]");
        }
        else {
            resp.setStatus(404);
            out.println("{");
            out.println("  \"error\": \"Endpoint not found\",");
            out.println("  \"path\": \"" + path + "\"");
            out.println("}");
        }

        out.close();
    }
}**/