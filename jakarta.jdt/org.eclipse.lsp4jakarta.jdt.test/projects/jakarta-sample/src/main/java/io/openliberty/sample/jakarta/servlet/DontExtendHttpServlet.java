package io.openliberty.sample.jakarta.servlet;

import jakarta.servlet.annotation.WebServlet;

@WebServlet(name = "demoServlet", urlPatterns = { "/demo" })
public class DontExtendHttpServlet {

}