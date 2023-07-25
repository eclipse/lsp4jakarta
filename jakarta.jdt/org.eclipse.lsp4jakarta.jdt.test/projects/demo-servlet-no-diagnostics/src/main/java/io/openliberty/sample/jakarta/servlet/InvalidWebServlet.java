package io.openliberty.sample.jakarta.servlet;

import jakarta.fake.ServletException;
import jakarta.fake.annotation.WebServlet;
import jakarta.fake.http.HttpServlet;
import jakarta.fake.http.HttpServletRequest;
import jakarta.fake.http.HttpServletResponse;
import java.io.IOException;

@WebServlet()
public class InvalidWebServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html;charset=UTF-8");
		res.getWriter().println("Hello Jakarta EE 9 + Open Liberty!");
	}
}