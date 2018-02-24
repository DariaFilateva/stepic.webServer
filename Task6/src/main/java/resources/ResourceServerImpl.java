package resources;

import sax.ReadXMLFileSAX;

public class ResourceServerImpl implements ResourceServer {

	private TestResource resource;
	
	@Override
	public void readResource(String path){
		resource = (TestResource) ReadXMLFileSAX.readXML(path);
	}
	
	@Override
	public int getAge() {
		return resource.getAge();
	}
	
	@Override
	public String getName() {
		return resource.getName();
	}
}
