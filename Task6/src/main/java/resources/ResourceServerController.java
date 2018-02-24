package resources;

public class ResourceServerController implements ResourceServerControllerMBean {
	private ResourceServer resourceServer;
	
	public ResourceServerController(ResourceServer resourceServer) {
		this.resourceServer = resourceServer;
	}
	
	@Override
	public int getAge() {
		return resourceServer.getAge();
	}
	
	@Override
	public String getName() {
		return resourceServer.getName();
	}
}