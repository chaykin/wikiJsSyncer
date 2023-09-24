package ru.chaykin.wjss.graphql.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.net.URIBuilder;

public class ClientApi {
    private final ObjectMapper mapper = new ObjectMapper();

    public <T> T query(Class<T> type, String query) {
	try {
	    Response response = Request.get(createLocation(query))
			    //TODO
			    .addHeader("Authorization",
					    "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6NywiZW1haWwiOiJrLmNoYXlraW5AbWV0YW1vZGVsLnJ1IiwibmFtZSI6IktpcmlsbCBDaGF5a2luIiwiYXYiOiIiLCJ0eiI6IkFzaWEvQWxtYXR5IiwibGMiOiJlbiIsImRmIjoiIiwiYXAiOiIiLCJwZXJtaXNzaW9ucyI6WyJyZWFkOnBhZ2VzIiwicmVhZDphc3NldHMiLCJyZWFkOmNvbW1lbnRzIiwid3JpdGU6Y29tbWVudHMiLCJtYW5hZ2U6cGFnZXMiLCJkZWxldGU6cGFnZXMiLCJ3cml0ZTpzdHlsZXMiLCJ3cml0ZTpzY3JpcHRzIiwicmVhZDpoaXN0b3J5IiwicmVhZDpzb3VyY2UiLCJtYW5hZ2U6YXNzZXRzIiwid3JpdGU6YXNzZXRzIiwid3JpdGU6cGFnZXMiXSwiZ3JvdXBzIjpbM10sImlhdCI6MTY5NDg0NjIzMiwiZXhwIjoxNjk0ODQ4MDMyLCJhdWQiOiJ1cm46d2lraS5qcyIsImlzcyI6InVybjp3aWtpLmpzIn0.X7mLwP9FPmGCT5iNMCirCwfRK8oK-Zd-_RFY7dGvdUQ7ShamYLn_81nJHWmmtngM8H5uAhBiqSJzERZQdtJczDjwmR1EN7sGKl9XC3onwfN-Oryu1ut87qBQHW_evPUGHsXL0Lxx94OabrAITyf3xGvmh-9KJkVdvsVmwrJLT8rG--WCVZH0OKCpV4v_xwxq5SzY6b0HgxLyrl60Q536YsB6Z4utqncgaJVUr3WY93usITJ6iwVwW0TihGwsMWSWBrip9uU5aIrL1iVeD7NRUj7dWHRwTthXt2y5HKbDb4W3IyLz90FWQSGDP00kWRSF_Bm-JaQciXSBerFbtbYgvg")
			    .execute();
	    byte[] content = response.returnContent().asBytes();
	    return mapper.readValue(content, type);
	} catch (URISyntaxException e) {
	    throw new RuntimeException("Invalid request url", e);
	} catch (IOException e) {
	    throw new RuntimeException("Failed to execute query", e);
	}
    }

    private URI createLocation(String query) throws URISyntaxException {
	String baseUrl = "https://wiki.digitalepoch.ru/graphql"; //TODO
	return new URIBuilder(baseUrl).addParameter("query", query).build();
    }
}
