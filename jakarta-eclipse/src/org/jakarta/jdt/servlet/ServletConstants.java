package org.jakarta.jdt.servlet;

import org.eclipse.lsp4j.DiagnosticSeverity;

public class ServletConstants {

    /* @WEBListener */
    public static final String WEB_LISTENER = "WebListener";
    public static final String SERVLET_CONTEXT_LISTENER = "ServletContextListener";
    public static final String SERVLET_CONTEXT_ATTRIBUTE_LISTENER = "ServletContextAttributeListener";
    public static final String SERVLET_REQUEST_LISTENER = "ServletRequestListener";
    public static final String SERVLET_REQUEST_ATTRIBUTE_LISTENER = "ServletRequestAttributeListener";
    public static final String HTTP_SESSION_LISTENER = "HttpSessionListener";
    public static final String HTTP_SESSION_ATTRIBUTE_LISTENER = "HttpSessionAttributeListener";
    public static final String HTTP_SESSION_ID_LISTENER = "HttpSessionIdListener";

    /* @WEBServlet */
    public static final String WEB_SERVLET = "WebServlet";
    public static final String HTTP_SERVLET = "HttpServlet";

    /* @WEBFilter */
    public static final String WEBFILTER = "WebFilter";
    public static final String FILTER = "Filter";

    /* Annotation Member Value names */
    public static final String URL_PATTERNS = "urlPatterns";
    public static final String VALUE = "value";
    public static final String SERVLET_NAMES = "servletNames";

    /* Diagnostics fields constants */
    public static final String DIAGNOSTIC_SOURCE = "jakarta-servlet";
    public static final String DIAGNOSTIC_CODE = "ExtendHttpServlet";
    public static final String DIAGNOSTIC_CODE_MISSING_ATTRIBUTE = "CompleteHttpServletAttributes";
    public static final String DIAGNOSTIC_CODE_DUPLICATE_ATTRIBUTES = "InvalidHttpServletAttribute";
    public static final String DIAGNOSTIC_CODE_FILTER = "ImplementFilter";
    public static final String DIAGNOSTIC_CODE_FILTER_MISSING_ATTRIBUTE = "CompleteWebFilterAttributes";
    public static final String DIAGNOSTIC_CODE_FILTER_DUPLICATE_ATTRIBUTES = "InvalidWebFilterAttribute";
    public static final String DIAGNOSTIC_CODE_LISTENER = "ImplementListener";
    public static final DiagnosticSeverity SEVERITY = DiagnosticSeverity.Error;
}
