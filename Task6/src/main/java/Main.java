import resources.ResourceServer;
import resources.ResourceServerController;
import resources.ResourceServerControllerMBean;
import resources.ResourceServerImpl;

import servlets.ResourcesServlet;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import java.lang.management.ManagementFactory;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

class Main {
	public static void main(String[] args) throws Exception {
		
		ResourceServer resourceServer = new ResourceServerImpl();
		
		ResourceServerControllerMBean resourceServerController = new ResourceServerController(resourceServer);
		MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName resourceServerControllerName = new ObjectName("Admin:type=ResourceServerController");
		mbeanServer.registerMBean(resourceServerController, resourceServerControllerName);
		
		ServletContextHandler servletContextHandler = new ServletContextHandler();
		servletContextHandler.addServlet(new ServletHolder(new ResourcesServlet(resourceServer)), ResourcesServlet.PAGE_URL);
		
		Server server = new Server(8080);
		server.setHandler(servletContextHandler);
		
		server.start();
		System.out.println("Server started");
		server.join();
	}
}