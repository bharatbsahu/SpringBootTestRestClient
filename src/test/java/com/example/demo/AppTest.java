package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@Import({WebConfiguration.class, HttpClientConfig.class})
@Disabled
public class AppTest {

  @Autowired
  RestClient restClient;

  /*@Autowired
  RestTemplate restTemplate;*/

  @Test
  public void contextLoads() {
    Assertions.assertNotNull(restClient);
    //Assertions.assertNotNull(restTemplate);
  }

  @Test
  public void testGetAll() {

    List<com.example.demo.Employee> employeeList = restClient.get()
        .uri("/employees")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(List.class);

    Assertions.assertNotNull(employeeList);
    Assertions.assertEquals(18, employeeList.size());
  }

  @Test
  public void testGetAll_WithResponseEntity() {

    ResponseEntity<List> responseEntity = restClient.get()
        .uri("/employees")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .toEntity(List.class);

    Assertions.assertNotNull(responseEntity.getBody());
    Assertions.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    Assertions.assertNotEquals(null, responseEntity.getHeaders());
  }

  @Test
  public void testGetById() {

	  com.example.demo.Employee employee = restClient.get()
        .uri("/employees/1")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(com.example.demo.Employee.class);

	  System.out.println("*************************** employee:"+employee);
    Assertions.assertNotNull(employee);
    Assertions.assertEquals(1, employee.getId());
    Assertions.assertEquals("Bilbo Baggins", employee.getName());
    Assertions.assertEquals("burglar", employee.getRole());
  }

  @Test
  public void testPostAndDelete() {

	  com.example.demo.Employee newEmployee = new com.example.demo.Employee("Amit", "active");

    ResponseEntity<Void> responseEntity = restClient.post()
        .uri("/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .body(newEmployee)
        .retrieve()
        .toBodilessEntity();

    Assertions.assertEquals(HttpStatus.CREATED.value(), responseEntity.getStatusCode().value());
    Assertions.assertEquals("http://localhost:8081/employees/5",
        responseEntity.getHeaders().get("Location").get(0));

    responseEntity = restClient.delete()
        .uri("/employees/19")
        .retrieve()
        .toBodilessEntity();

    Assertions.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
  }

  @Test
  public void testPut() {

	  com.example.demo.Employee employee = restClient.get()
        .uri("/employees/1")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(com.example.demo.Employee.class);

    String originalName = employee.getName();

    employee.setName("Updated_Name-" + originalName);

    ResponseEntity<com.example.demo.Employee> responseEntity = restClient.put()
        .uri("/employees/1")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(employee)
        .retrieve()
        .toEntity(com.example.demo.Employee.class);

    Assertions.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
    Assertions.assertEquals(employee.getName(), responseEntity.getBody().getName());

    employee.setName(originalName);

    restClient.put()
        .uri("/employees/1")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(employee)
        .retrieve()
        .toEntity(com.example.demo.Employee.class);
  }

  @Test
  public void testException() {

    HttpClientErrorException thrown = Assertions.assertThrows(HttpClientErrorException.class,
        () -> {

        	com.example.demo.Employee employee = restClient.get()
              .uri("/employees/5")
              .accept(MediaType.APPLICATION_JSON)
              .retrieve()
              .body(com.example.demo.Employee.class);
        });

    Assertions.assertEquals(404, thrown.getStatusCode().value());
  }

  @Test
  public void testExchangeMethod() {

    List<com.example.demo.Employee> list = restClient.get()
        .uri("/employees")
        .accept(MediaType.APPLICATION_JSON)
        .exchange((request, response) -> {
          List apiResponse = null;
          if (response.getStatusCode().is4xxClientError()
              || response.getStatusCode().is5xxServerError()) {
            Assertions.fail("Error occurred in test execution. Check test data and api url.");
          } else {
            ObjectMapper mapper = new ObjectMapper();
            apiResponse = mapper.readValue(response.getBody(), List.class);
          }
          return apiResponse;
        });

    Assertions.assertEquals(4, list.size());
  }

}

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

/*class Employee {

  private long id;
  private String name;
  private String status;

  public Employee() {
  }

  public Employee(long id, String name, String status) {
    this.id = id;
    this.name = name;
    this.status = status;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
*/