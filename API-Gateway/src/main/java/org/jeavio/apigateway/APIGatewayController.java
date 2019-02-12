package org.jeavio.apigateway;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
	public String UrlMapper() {
		
		response.setContentType("application/json");
		
//		StringBuilder str=new StringBuilder();
//		Enumeration<String> headers=request.getHeaderNames();
//		while(headers.hasMoreElements()) {
//			String name=headers.nextElement();
//			str.append(name+" : "+request.getHeader(name)+" \n");
//		}
//		str.append(request.getParameter("hey"));
		
//		// Getting servlet request URL
//        String url = request.getRequestURL().toString();
//        System.out.print(url);
//        // Getting servlet request query string.
//        String queryString = request.getQueryString();
//        System.out.print(queryString);
//        // Getting request information without the hostname.
		
        String uri = request.getRequestURI();
        System.out.print(uri);
        //System.out.println(swaggerObject.getPaths().get("/api/comments/{commentId}/delete").get().toString());
	return new String("hi");
	}
}
