package com.dgb.cloudbox.controller;

import com.dgb.cloudbox.dto.VmRequest;
import com.dgb.cloudbox.dto.VmResponse;
import com.dgb.cloudbox.service.VmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/vms")
@Validated
public class VmController {
    private final VmService vmService;

    public VmController(VmService vmService) {
        this.vmService = vmService;
    }

    @PostMapping
    @Operation(responses = {
            @ApiResponse(description = "Success", responseCode = "201"),
            @ApiResponse(description = "Not found", responseCode = "404")
    })
    public ResponseEntity<VmResponse> createVirtualMachine(
            @Valid @RequestBody VmRequest request) throws BadRequestException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vmService.createVm(request));
    }

    @GetMapping
    @Operation(responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not found", responseCode = "404"),
            @ApiResponse(description = "Bad request", responseCode = "400")
    })
    public ResponseEntity<List<VmResponse>> getAllVirtualMachines() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(vmService.getAllVms());
    }

    @GetMapping("/{id}")
    @Operation(responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not found", responseCode = "404"),
            @ApiResponse(description = "Bad request", responseCode = "400")
    })
    public ResponseEntity<VmResponse> getVirtualMachine(@PathVariable("id") Long id) throws BadRequestException {

        return ResponseEntity.status(HttpStatus.OK)
                .body(vmService.getVm(id));
    }

    @PutMapping("/{id}")
    @Operation(responses = {
            @ApiResponse(description = "Success", responseCode = "200"),
            @ApiResponse(description = "Not found", responseCode = "404"),
            @ApiResponse(description = "Bad request", responseCode = "400")
    })
    public ResponseEntity<VmResponse> updateVirtualMachine(@PathVariable("id") Long id,
                                                           @Valid @RequestBody VmRequest request) throws BadRequestException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(vmService.updateVm(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(responses = {
            @ApiResponse(description = "Success", responseCode = "204"),
            @ApiResponse(description = "Not found", responseCode = "404"),
            @ApiResponse(description = "Bad request", responseCode = "400")
    })
    public ResponseEntity<Void> deleteVirtualMachine(
            @PathVariable Long id) throws BadRequestException {

        vmService.deleteVm(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    @Operation(responses = {
            @ApiResponse(responseCode = "200", description = "VM started"),
            @ApiResponse(responseCode = "404", description = "VM not found"),
            @ApiResponse(responseCode = "409", description = "VM cannot be started in its current state")
    })
    public ResponseEntity<VmResponse> start(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(vmService.start(id));
    }

    @PostMapping("/{id}/stop")
    @Operation(responses = {
            @ApiResponse(responseCode = "200", description = "VM stopped"),
            @ApiResponse(responseCode = "404", description = "VM not found"),
            @ApiResponse(responseCode = "409", description = "VM cannot be stopped in its current state")
    })
    public ResponseEntity<VmResponse> stop(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(vmService.stop(id));
    }

    @PostMapping("/{id}/provisioned")
    @Operation(responses = {
            @ApiResponse(responseCode = "200", description = "VM provisioned"),
            @ApiResponse(responseCode = "404", description = "VM not found"),
            @ApiResponse(responseCode = "409", description = "VM cannot be provisioned in its current state")
    })
    public ResponseEntity<VmResponse> provisioned(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(vmService.markProvisioned(id));
    }
}
