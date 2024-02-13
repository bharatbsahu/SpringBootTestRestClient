package com.example.demo;

import java.util.List;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class SpringBootTestRestClientApplicationTests {

    private static RestTemplate restTemplate;
    
    @Autowired
    RestClient restClient;
    
    //private static final String EmployeeUrl = "http://localhost:" + "8081" + "/employees";
    private static final String EmployeeUrl = "http://localhost:" + "8081";
    

	@Test
	void contextLoads() {
	   // Assertions.assertNotNull(restClient);
		restTemplate = new RestTemplate();
	    Assertions.assertNotNull(restTemplate);
	}
	

	
	@Test
	void GetByID() {
		
		List<Employee> employeeList = restClient.get()
			    .uri( "/employees")
			    .accept(MediaType.APPLICATION_JSON)
			    .retrieve()
			    .body(List.class);

			Assertions.assertNotNull(employeeList);
			
			
	}
	

}
/*
@TestConfiguration
class WebConfiguration {

  @Autowired
  CloseableHttpClient httpClient;

  @Value("${REMOTE_BASE_URI:http://localhost:8081}")
  String baseURI;

  @Bean
  RestClient restClient() {
    return RestClient.builder()
        .baseUrl(baseURI)
        //.requestInterceptor(...)
        //.defaultHeader("AUTHORIZATION", fetchToken())
        //.messageConverters(...)
        .requestFactory(clientHttpRequestFactory())
        .build();
    //return RestClient.create(restTemplate());
  }

  @Bean
  public RestTemplate restTemplate() {
    RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
    return restTemplate;
  }

  @Bean
  public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
    clientHttpRequestFactory.setHttpClient(httpClient);
    return clientHttpRequestFactory;
  }
}
*/