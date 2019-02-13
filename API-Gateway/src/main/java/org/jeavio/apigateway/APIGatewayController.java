package org.jeavio.apigateway;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class APIGatewayController {
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	@Autowired
	private Swagger swaggerObject;
	
	@RequestMapping
	public HttpMethodService UrlMapper() {
		
		response.setContentType("application/json");
		
//		StringBuilder str=new StringBuilder();
//		Enumeration<String> headers=request.getHeaderNames();
//		while(headers.hasMoreElements()) {
//			String name=headers.nextElement();
//			str.append(name+" : "+request.getHeader(name)+" \n");
//		}
//		str.append(request.getParameter("hey"));
		
        String uri = request.getRequestURI();
        String method = request.getMethod().toLowerCase();
        HttpMethodService serviceBody=parseRequest(uri, method);
        return serviceBody;
        
	}
	
	private HttpMethodService parseRequest(String uri,String method) {
		Set<String> urlSet=swaggerObject.getPaths().keySet();
        if(urlSet.contains(uri) && swaggerObject.getPaths().get(uri).get().keySet().contains(method)) {
        	return swaggerObject.getPaths().get(uri).get(method);
        }
        else {

        	HttpMethodService service= new HttpMethodService();
        	service.set("402", "Forbidden error");
        	return service;
        }
	}
}
