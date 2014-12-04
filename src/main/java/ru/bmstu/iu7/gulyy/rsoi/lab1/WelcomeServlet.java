package ru.bmstu.iu7.gulyy.rsoi.lab1;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by Константин on 02.12.2014.
 */
public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html; charset=utf-8");
        resp.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = resp.getWriter();

        String linkToLogin = "<h3><a href=\"RequestLoginPage\">Request private data</a></h3>";

        out.println("<html><body>");
        out.println(linkToLogin);
        out.println("</body></html>");
    }
}
