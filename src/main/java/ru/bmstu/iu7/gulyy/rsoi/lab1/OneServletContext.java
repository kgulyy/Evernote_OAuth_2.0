package ru.bmstu.iu7.gulyy.rsoi.lab1;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by Константин on 01.12.2014.
 */
public class OneServletContext {
    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8071);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new WelcomeServlet()), "/*");
        context.addServlet(new ServletHolder(new RequestLoginPage()),"/RequestLoginPage/*");
        context.addServlet(new ServletHolder(new GetAccessToken()),"/GetAccessToken/*");

        server.start();
        server.join();
    }
}
