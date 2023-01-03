package restService;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Path("/server")

public class Server {
	
	@Context ServletContext servletContext;

	// Method called if TEXT_PLAIN is requested
	@Path("/getStatus")
	@GET
	@Produces (MediaType.TEXT_PLAIN)
	public String getTextStatus() {
		return "\"Server Running\"";
	}
	
	// Method called if XML is requested
	@Path("/getStatus")
	@GET
	@Produces (MediaType.TEXT_XML)
	public String getXMLStatus() {
		return "\"<?xml version=\"1.0\"?>" + "<hi>" + "Server Running" + "</hi>\"";
	}
	
	// Method called if HTML is requested
	@Path("/getStatus")
	@GET
	@Produces (MediaType.TEXT_HTML)
	public String getHTMLStatus() {
		return "\"<html>" + "<head>" + "<title>" + "Server Status" + "</title>" + "</head>" + "<body>" + "Server Running" + "</body>" + "</html>\"";
	}
	
	
	@Path("/getVersion")
	@GET
	@Produces (MediaType.TEXT_PLAIN)
	public String getVersion() {
		
		return "\"" + servletContext.getServerInfo() + "\"";
	}
}
