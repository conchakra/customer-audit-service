package com.project.findisc.audit_table.controller;

import com.project.findisc.audit_table.entity.CustomerEntity;
import com.project.findisc.audit_table.enums.CustomerStatus;
import com.project.findisc.audit_table.enums.UserRole;
import com.project.findisc.audit_table.service.CustomerService;
import com.project.findisc.audit_table.storage.FileStorageProvider;
import com.project.findisc.audit_table.dto.StatusUpdateRequest;
import com.project.findisc.audit_table.dto.CustomerVerifyResponseDto;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/customer/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final FileStorageProvider storageProvider;

    public CustomerController(CustomerService customerService,
            FileStorageProvider storageProvider) {
        this.customerService = customerService;
        this.storageProvider = storageProvider;
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {

        CustomerStatus newStatus = CustomerStatus.valueOf(request.getStatus().toUpperCase());
        UserRole role = UserRole.valueOf(request.getRole().toUpperCase());

        customerService.updateCustomerStatus(
                id,
                newStatus,
                role,
                request.getUsername(),
                request.getRemarks());

        return ResponseEntity.ok("Status updated successfully");
    }

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<List<CustomerEntity>> getCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // ✅ GET BY ID
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerEntity> getCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                customerService.getCustomerById(customerId));
    }

    // ✅ VERIFY CUSTOMER
    @GetMapping("/verify/{customerId}")
    public ResponseEntity<CustomerVerifyResponseDto> verifyCustomer(@PathVariable Long customerId) {
        boolean exists = customerService.customerExists(customerId);

        if (exists) {
            return ResponseEntity.ok(
                    CustomerVerifyResponseDto.builder()
                            .success(true)
                            .customerId(String.valueOf(customerId))
                            .message("Customer exists")
                            .build());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomerVerifyResponseDto.builder()
                            .success(false)
                            .customerId(String.valueOf(customerId))
                            .message("Customer not found")
                            .build());
        }
    }

    // ✅ CREATE CUSTOMER
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerEntity> createCustomer(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String kyc,
            @RequestParam(required = false) String aadhaar,
            @RequestParam(required = false) String pan,
            @RequestParam(required = false) MultipartFile photo)
            throws IOException {

        CustomerEntity customer = new CustomerEntity();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setKyc(kyc);
        customer.setAadhaar(aadhaar);
        customer.setPan(pan);

        if (photo != null && !photo.isEmpty()) {
            String documentId = storageProvider.saveFile(photo);
            customer.setPhoto(documentId);
        }

        return ResponseEntity.ok(
                customerService.createCustomer(customer));
    }

    // ✅ UPDATE CUSTOMER
  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<CustomerEntity> updateCustomer(
        @PathVariable Long id,
        @RequestParam String name,
        @RequestParam String phone,
        @RequestParam String status,
        @RequestParam String kyc,
        @RequestParam(required = false) String aadhaar,
        @RequestParam(required = false) String pan,
        @RequestParam(required = false) MultipartFile photo)
        throws IOException {

    CustomerEntity existing = customerService.getCustomerById(id);

    existing.setName(name);
    existing.setPhone(phone);
    existing.setStatus(status);   // ✅ IMPORTANT FIX
    existing.setKyc(kyc);
    existing.setAadhaar(aadhaar);
    existing.setPan(pan);

    if (photo != null && !photo.isEmpty()) {
        String documentId = storageProvider.saveFile(photo);
        existing.setPhoto(documentId);
    }

    return ResponseEntity.ok(
            customerService.updateCustomer(id, existing));
}

    // ✅ DELETE CUSTOMER
    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable Long customerId) {

        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok("Customer deleted successfully");
    }
}