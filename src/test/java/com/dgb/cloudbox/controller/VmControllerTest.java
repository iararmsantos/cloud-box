package com.dgb.cloudbox.controller;

import com.dgb.cloudbox.dto.VmRequest;
import com.dgb.cloudbox.dto.VmResponse;
import com.dgb.cloudbox.entity.VmStatus;
import com.dgb.cloudbox.objectmother.VmRequestObjectMother;
import com.dgb.cloudbox.objectmother.VmResponseObjectMother;
import com.dgb.cloudbox.service.VmService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VmController.class)
public class VmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private VmService vmService;

    @Test
    void createVirtualMachine_validRequest_returns201() throws Exception {
        VmRequest request = VmRequestObjectMother.createDefault();
        VmResponse response = VmResponseObjectMother.createDefault();

        when(vmService.createVm(any(VmRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/vms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(999))
                .andExpect(jsonPath("$.name").value(request.getName()));

        verify(vmService).createVm(any(VmRequest.class));
    }

    @Test
    void createVirtualMachine_invalidRequest_returns400() throws Exception {
        mockMvc.perform(post("/api/vms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(vmService);
    }

    @Test
    void createVirtualMachine_malformedJson_returns400() throws Exception {
        mockMvc.perform(post("/api/vms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not valid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createVirtualMachine_serviceThrowsBadRequest_returns400() throws Exception {
        when(vmService.createVm(any(VmRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad"));

        mockMvc.perform(post("/api/vms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRequest())))
                .andExpect(status().isBadRequest());
    }

    private VmRequest validRequest() {
        VmRequest r = new VmRequest();
        r.setName("my-vm");   // adapt to your real fields and setters
        r.setCpuCores(4);
        r.setMemoryGb(8192);
        return r;
    }

    /* GET ALL */
    @Test
    void getAllVirtualMachines_validRequest_returns200() throws Exception {
        // Arrange: prepare the data and tell the mock what to return
        VmResponse response = VmResponseObjectMother.createDefault();
        VmResponse response2 = VmResponseObjectMother.createWithStatus(VmStatus.STOPPED);

        when(vmService.getAllVms()).thenReturn(List.of(response, response2));

        // Act: send the HTTP request
        mockMvc.perform(get("/api/vms")
                        .accept(MediaType.APPLICATION_JSON))
                // Assert: check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(response.getId()))
                .andExpect(jsonPath("$.[0].name").value(response.getName()))
                .andExpect(jsonPath("$.[1].id").value(response2.getId()))
                .andExpect(jsonPath("$[1].name").value(response2.getName()));

        // Assert: check the controller really called the service
        verify(vmService).getAllVms();
    }

    @Test
    void getAllVirtualMachines_noVms_returnEmptyList() throws Exception {
        // Arrange
        when(vmService.getAllVms()).thenReturn(List.of());

        // Act
        mockMvc.perform(get("/api/vms")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllVirtualMachine_invalidRequest_returns404() throws Exception {
        when(vmService.getAllVms())
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "not"));
        mockMvc.perform(get("/api/vmss")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(null)))
                .andExpect(status().isNotFound());

        verifyNoInteractions(vmService);
    }

    @Test
    void getAllVirtualMachine_serviceThrowsBadRequest_returns400() throws Exception {
        when(vmService.getAllVms())
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad"));

        mockMvc.perform(get("/api/vms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRequest())))
                .andExpect(status().isBadRequest());
    }

    /* GET ID */
    @Test
    void getVirtualMachines_validRequest_returns200() throws Exception {
        // Arrange: prepare the data and tell the mock what to return
        VmResponse response = VmResponseObjectMother.createDefault();

        when(vmService.getVm(anyLong())).thenReturn(response);
        // Act: send the HTTP request
        mockMvc.perform(get("/api/vms/{id}", response.getId())
                        .accept(MediaType.APPLICATION_JSON))
                // Assert: check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.name").value(response.getName()));

        // Assert: check the controller really called the service
        verify(vmService).getVm(response.getId());
    }

    @Test
    void getVirtualMachines_noVms_returnEmptyObject() throws Exception {
        // Arrange
        when(vmService.getVm(1L)).thenReturn(new VmResponse());

        // Act
        mockMvc.perform(get("/api/vms/{id}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    void getVirtualMachine_invalidId_returns404() throws Exception {
        when(vmService.getVm(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/vms/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVirtualMachine_serviceThrowsBadRequest_returns400() throws Exception {
        mockMvc.perform(get("/api/vms/{id}", "abc"))   // not a number
                .andExpect(status().isBadRequest());

        verifyNoInteractions(vmService);
    }

    /* DELETE ID */
    @Test
    void deleteVirtualMachines_validRequest_returns201() throws Exception {
        doNothing().when(vmService).deleteVm(1L);

        // Act: send the HTTP request
        mockMvc.perform(delete("/api/vms/{id}", 1L))
                .andExpect(status().isNoContent());

        // Assert: check the controller really called the service
        verify(vmService).deleteVm(1L);
    }

    @Test
    void deleteVirtualMachine_invalidId_returns404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND))
                .when(vmService).deleteVm(99L);

        mockMvc.perform(delete("/api/vms/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVirtualMachine_serviceThrowsBadRequest_returns400() throws Exception {
        mockMvc.perform(delete("/api/vms/{id}", "abc"))   // not a number
                .andExpect(status().isBadRequest());

        verifyNoInteractions(vmService);
    }

    /* UPDATE ID*/

    @Test
    void updateVirtualMachine_validRequest_returns200() throws Exception {
        VmRequest request = VmRequestObjectMother.createDefault();
        VmResponse response = VmResponseObjectMother.createWithStatus(VmStatus.PROVISIONING);

        when(vmService.updateVm(eq(999L), any(VmRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/vms/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andDo(print()) // To debug and see what is being sent as body
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(request.getCustomerId()))
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.status").value(response.getStatus().name()));

        verify(vmService).updateVm(eq(999L), any(VmRequest.class));
    }

    @Test
    void updateVirtualMachine_invalidRequest_returns400() throws Exception {
        mockMvc.perform(put("/api/vms/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(vmService);
    }

    @Test
    void updateVirtualMachine_malformedJson_returns400() throws Exception {
        mockMvc.perform(put("/api/vms/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ not valid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateVirtualMachine_serviceThrowsBadRequest_returns400() throws Exception {
        when(vmService.updateVm(eq(999L), any(VmRequest.class)))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad"));

        mockMvc.perform(put("/api/vms/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(validRequest())))
                .andExpect(status().isBadRequest());
    }
}
