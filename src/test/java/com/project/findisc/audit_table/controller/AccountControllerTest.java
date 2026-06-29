package com.project.findisc.audit_table.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.findisc.audit_table.entity.AccountEntity;
import com.project.findisc.audit_table.service.AccountService;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAccount_success() throws Exception {

        AccountEntity account = new AccountEntity();
        account.setAccountId(1L);
        account.setAccountNumber("ACC1001");
        account.setAccountHolderName("Goutham");
        account.setBranch("Mysore");

        when(accountService.create(account)).thenReturn(account);
        
        mockMvc.perform(post("/customer/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated());
    }
}