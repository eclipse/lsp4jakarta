package io.openliberty.sample.jakarta.servlet;

import jakarta.fake.annotation.WebServlet;

@WebServlet(name = "demoServlet", urlPatterns = { "/demo" })
public class DontExtendHttpServlet {

}