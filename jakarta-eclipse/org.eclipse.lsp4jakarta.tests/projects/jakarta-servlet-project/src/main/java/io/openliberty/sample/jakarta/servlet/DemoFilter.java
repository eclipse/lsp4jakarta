package io.openliberty.sample.jakarta.servlet;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

@WebFilter(filterName = "filter", urlPatterns = { "/filter" })
public class DemoFilter implements Filter {
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		res.setContentType("text/html;charset=UTF-8");
		res.getWriter().println("This request visited the filter!");
		chain.doFilter(req, res);
	}
}