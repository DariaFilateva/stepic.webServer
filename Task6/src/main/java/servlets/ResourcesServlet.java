package servlets;

import resources.ResourceServer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourcesServlet extends HttpServlet {
	public static final String PAGE_URL = "/resources";
	private final ResourceServer resourceServer;
	
	public ResourcesServlet(ResourceServer resourceServer) {
		this.resourceServer = resourceServer;
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String path = request.getParameter("path");
		resourceServer.readResource(path);
	}
}
