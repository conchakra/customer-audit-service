package com.project.findisc.audit_table;

import com.project.findisc.audit_table.entity.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.project.findisc.audit_table.storage.FileStorageProvider;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationtest") // Use a separate profile for integration tests
//skip this test
public class CustomerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
private FileStorageProvider storageProvider;


    private String getBaseUrl() {
        return "http://localhost:" + port + "/customer/api/v1/customers";
    }

    // ✅ CREATE CUSTOMER (MULTIPART)
    @Test
void testCreateCustomer() throws Exception {

        when(storageProvider.saveFile(any()))
        .thenReturn("test-photo.jpg");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
        body.add("name", "Integration User");
        body.add("phone", "8888888888");
        body.add("status","Active");
        body.add("kyc", "Pending");

        // fake file
        ByteArrayResource file = new ByteArrayResource("test image".getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return "test.jpg";
            }
        };

        body.add("photo", file);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
        getBaseUrl(),
        request,
        String.class
);

System.out.println("Response from API: " + response.getBody());

System.out.println("STATUS = " + response.getStatusCode());
System.out.println("BODY = " + response.getBody());

assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ✅ GET ALL CUSTOMERS
    @Test
    void testGetAllCustomers() {

        ResponseEntity<CustomerEntity[]> response = restTemplate.getForEntity(getBaseUrl(), CustomerEntity[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}