package com.softwaretunnel.javaapp.service;

import java.util.List;
import java.util.ResourceBundle;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softwaretunnel.javaapp.ExceptionMessage;
import com.softwaretunnel.javaapp.persistance.entity.Employee;

public class EmployeeService {

	private String restUrl;
	CloseableHttpClient client = HttpClients.createDefault();

	public EmployeeService() {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("system");
		restUrl = resourceBundle.getString("REST_SERVICE_URL");
	}

	public Employee insertEmployeeRecord(Employee employee) throws Exception {

		try {
			// Convert object to JSON
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonBody = objectMapper.writeValueAsString(employee);

			// create request
			HttpPost request = new HttpPost(restUrl + "/create-employee");
			request.setHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(jsonBody)); // Set JSON entity

			// get response
			try (CloseableHttpResponse response = client.execute(request)) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					throw new Exception(ExceptionMessage.REST_CALL_FAILED.toString());
				}

				String responseBody = EntityUtils.toString(response.getEntity());
				ObjectMapper responseObjectMapper = new ObjectMapper();
				Employee employeeInResponse = responseObjectMapper.readValue(responseBody, Employee.class);

				return employeeInResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public Employee updateEmployeeRecord(Employee employeeToUpdate) throws Exception {

		try {
			// Convert object to JSON
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonBody = objectMapper.writeValueAsString(employeeToUpdate);

			// create request
			HttpPut request = new HttpPut(restUrl + "/update-employee");
			request.setHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(jsonBody)); // Set JSON entity

			// get response
			try (CloseableHttpResponse response = client.execute(request)) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					throw new Exception(ExceptionMessage.REST_CALL_FAILED.toString());
				}
				String responseBody = EntityUtils.toString(response.getEntity());
				ObjectMapper responseObjectMapper = new ObjectMapper();
				Employee employeeInResponse = responseObjectMapper.readValue(responseBody, Employee.class);

				return employeeInResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public void deleteEmployeeRecord(Employee employee) throws Exception {

		try {
			// create request
			HttpDelete request = new HttpDelete(restUrl + "/delete-employee/" + employee.getId());

			// get response
			try (CloseableHttpResponse response = client.execute(request)) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					throw new Exception(ExceptionMessage.REST_CALL_FAILED.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	public List<Employee> getEmployeeRecords() throws Exception {

		try {
			// create request
			HttpGet request = new HttpGet(restUrl + "/get-employees");

			// get response
			try (CloseableHttpResponse response = client.execute(request)) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					throw new Exception(ExceptionMessage.REST_CALL_FAILED.toString());
				}

				String responseBody = EntityUtils.toString(response.getEntity());
				ObjectMapper responseObjectMapper = new ObjectMapper();
				List<Employee> employees = responseObjectMapper.readValue(responseBody,
						new TypeReference<List<Employee>>() {
						});

				return employees;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
