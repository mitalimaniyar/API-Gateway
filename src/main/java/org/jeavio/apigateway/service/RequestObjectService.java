package org.jeavio.apigateway.service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.jeavio.apigateway.model.GatewayIntegration;
import org.jeavio.apigateway.model.InputRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers.Default;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONValue;

@Service
public class RequestObjectService {

	@Autowired
	URLMethodService urlMethodService;

	@Autowired
	IntegrationService integrationService;

	@Autowired
	Map<String, String> cognitoIdMap;

	ObjectMapper objectMapper = new ObjectMapper();

	public InputRequest getInputObject(String uri, String method, Map<String, String> allParams, String requestBody) {
		InputRequest inputRequest = new InputRequest();
		if (requestBody != null && !requestBody.isEmpty()) {
			try {
				inputRequest = objectMapper.readValue(requestBody.getBytes(), InputRequest.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		UriTemplate temp = null;
		try {
			temp = urlMethodService.getUriTemp(uri, method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		inputRequest.putAll(temp.match(uri));
		inputRequest.putAll(allParams);

		return inputRequest;
	}

	public String getRequestBody(HttpServletRequest request, InputRequest inputRequest, String requestBody) {

		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		UriTemplate uriTemplate = new UriTemplate("/");
		VelocityEngine velocityEngine = new VelocityEngine();
		try {
			uriTemplate = urlMethodService.getUriTemp(uri, method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GatewayIntegration integrationObject = integrationService.getIntegrationObject(uriTemplate.toString(), method);

//		Template requestTemplate;
		if (integrationObject.getRequestTemplates() != null
				&& integrationObject.getRequestTemplates().get("application/json") != null) {
			if (integrationObject.getRequestTemplates().get("application/json").equals("__passthrough__")) {
				return requestBody;
			} else {

				VelocityContext context = new VelocityContext();
				if (request.getHeader("x-amz-security-token") != null) {
					context.put("context", getContextObject(request));
				}

				context.put("input", inputRequest);
				StringWriter writer = new StringWriter();
				String template = integrationObject.getRequestTemplates().get("application/json");
				boolean status = velocityEngine.evaluate(context, writer, "requestTemplate", template);
				if (status)
					return writer.toString();
				else
					return null;
			}

		} else
			return null;
	}

	public String getCognitoId(HttpServletRequest request) {
		String sessionToken = request.getHeader("x-amz-security-token");
		String cognitoId = null;
		if (sessionToken != null && cognitoIdMap.containsKey(sessionToken)) {
			cognitoId = cognitoIdMap.get(sessionToken);
		}
		return cognitoId;
	}

	private Map<String, Map<String, String>> getContextObject(HttpServletRequest request) {
		Map<String, Map<String, String>> context1 = new LinkedHashMap<String, Map<String, String>>();
		Map<String, String> identity = new LinkedHashMap<String, String>();
		identity.put("cognitoIdentityId", getCognitoId(request));
		context1.put("identity", identity);

		return context1;
	}

	public HttpUriRequest createRequest(HttpServletRequest request, InputRequest inputRequest, String requestBody) {
		String uri = request.getRequestURI();
		String method = request.getMethod().toLowerCase();

		UriTemplate uriTemplate = new UriTemplate("/");

		try {
			uriTemplate = urlMethodService.getUriTemp(uri, method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GatewayIntegration integrationObject = integrationService.getIntegrationObject(uriTemplate.toString(), method);

//		Setting Parameters
		Map<String, String> requestParameters = integrationObject.getRequestParameters();

		Map<String, String> headerParams = new LinkedHashMap<String, String>();
		Map<String, String> pathParams = new LinkedHashMap<String, String>();
		Map<String, String> querystringParams = new LinkedHashMap<String, String>();

		if (!requestParameters.isEmpty()) {
			for (String headerName : requestParameters.keySet()) {
				String[] recurseCall = headerName.split("\\.");
				String value = requestParameters.get(headerName);

				String paramValue = interpretParamValue(request, value, inputRequest);

				switch (recurseCall[2]) {
				case "header":
					headerParams.put(recurseCall[3], paramValue);
					break;
				case "path":
					pathParams.put(recurseCall[3], paramValue);
					break;
				case "querystring":
					querystringParams.put(recurseCall[3], paramValue);
					break;
				}
			}
		}

//		Creating Request Body
		String ParsedRequestBody = getRequestBody(request, inputRequest, requestBody);

//		Creating Url
		URI targetUri = getTargetUri(integrationObject, pathParams, querystringParams);

//		Creating Request
		StringEntity entity = null;
		switch (integrationObject.getHttpMethod()) {

		case "GET":

			HttpGet httpGet = new HttpGet("http://localhost:9090/home");

			if (!headerParams.isEmpty()) {
				for (String param : headerParams.keySet()) {
					httpGet.addHeader(param, headerParams.get(param));
				}
			}
			return httpGet;

		case "POST":
//		 		HttpPost httpPost=new HttpPost(targetUri);
			HttpPost httpPost = new HttpPost("http://localhost:9090/home");
			if (!headerParams.isEmpty()) {
				for (String param : headerParams.keySet()) {
					httpPost.addHeader(param, headerParams.get(param));
				}
			}
			try {
				entity = new StringEntity(ParsedRequestBody);
				entity.setContentType("application/json");

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpPost.setEntity(entity);
			return httpPost;

		case "PUT":
			HttpPut httpPut = new HttpPut("http://localhost:9090/home");

			try {
				entity = new StringEntity(ParsedRequestBody);
				entity.setContentType("application/json");

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			httpPut.setEntity(entity);
			return httpPut;

		case "PATCH":
			HttpPatch httpPatch = new HttpPatch("http://localhost:9090/home");

			try {
				entity = new StringEntity(ParsedRequestBody);
				entity.setContentType("application/json");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPatch.setEntity(entity);
			return httpPatch;

		case "DELETE":
			HttpDelete httpDelete = new HttpDelete("http://localhost:9090/home");

			if (!headerParams.isEmpty()) {
				for (String param : headerParams.keySet()) {
					httpDelete.addHeader(param, headerParams.get(param));
				}
			}
			return httpDelete;
		default:
			HttpGet httpGet2 = new HttpGet("http://localhost:9090/home");
			return httpGet2;
		}

	}

	private String interpretParamValue(HttpServletRequest request, String value, InputRequest inputRequest) {
		String paramValue = null;
		if (value.indexOf("'") != -1) {
			paramValue = value.replace("'", "");
		} else if (value.equals("context.identity.cognitoIdentityId")) {
			paramValue = getCognitoId(request);
		} else {
			String paramName = value.substring(value.lastIndexOf(".") + 1);
			paramValue = (String) inputRequest.params().get(paramName);
		}
		return paramValue;

	}

	private URI getTargetUri(GatewayIntegration integrationObject, Map<String, String> pathParams,
			Map<String, String> querystringParams) {

		URI targetUri = null;
		UriTemplate targetUrl = new UriTemplate(integrationObject.getUri());
		if (targetUrl.getVariableNames().isEmpty()) {
			try {
				targetUri = new URI(targetUrl.toString());
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			targetUri = targetUrl.expand(pathParams);
		}

//		Add querystrin if exist
		if (!querystringParams.isEmpty()) {

			List<NameValuePair> queryParams = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : querystringParams.entrySet()) {
				if (entry.getValue() != null)
					queryParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}

			URIBuilder builder = new URIBuilder().setScheme(targetUri.getScheme()).setHost(targetUri.getHost())
					.setPath(targetUri.getPath()).setParameters(queryParams);
			URI uri = null;
			try {
				uri = builder.build();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return uri;
		}

		return targetUri;
	}

}
