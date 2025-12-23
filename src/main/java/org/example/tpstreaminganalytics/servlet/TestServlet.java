package org.example.tpstreaminganalytics.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        out.println("<h1>🧪 Test Servlet - WORKING!</h1>");
        out.println("<p>Servlet is working correctly on Tomcat 9</p>");
        out.println("<p>Context Path: " + req.getContextPath() + "</p>");
        out.println("<p>Request URI: " + req.getRequestURI() + "</p>");
        out.println("<p><a href='" + req.getContextPath() + "/'>Back to Home</a></p>");

        out.close();
    }
}