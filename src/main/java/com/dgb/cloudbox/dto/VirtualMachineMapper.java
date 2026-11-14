package com.dgb.cloudbox.dto;

import com.dgb.cloudbox.entity.VirtualMachine;
import com.dgb.cloudbox.entity.VmStatus;

import java.time.LocalDateTime;

public class VirtualMachineMapper {
    public static VirtualMachine mapTo(VmRequest request) {
        if (request == null) {
            return null;
        }

        return VirtualMachine.builder()
                .name(request.getName())
                .customerId(request.getCustomerId())
                .cpuCores(request.getCpuCores())
                .memoryGb(request.getMemoryGb())
                .build();
    }

    public static VirtualMachine mapTo(VmResponse response) {
        if (response == null) {
            return null;
        }
        return VirtualMachine.builder()
                .id(response.getId())
                .name(response.getName())
                .customerId(response.getCustomerId())
                .cpuCores(response.getCpuCores())
                .memoryGb(response.getMemoryGb())
                .createdAt(response.getCreatedAt())
                .status(response.getStatus())
                .build();
    }

    public static void updateEntity(VmRequest request, VirtualMachine target) {
        target.setCpuCores(request.getCpuCores());
        target.setCustomerId(request.getCustomerId());
        target.setMemoryGb(request.getMemoryGb());
        target.setName(request.getName());
        target.setLastStatusChangedAt(LocalDateTime.now());
    }
}
