package com.dgb.cloudbox.dto;

import com.dgb.cloudbox.entity.VirtualMachine;
import com.dgb.cloudbox.entity.VmStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VmResponse {
    private Long id;

    private String name;

    private Long customerId;

    private Integer cpuCores;

    private Integer memoryGb;

    private VmStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime lastStatusChangedAt;

    public static VmResponse mapTo(VirtualMachine entity) {
        if (entity == null) {
            return null;
        }

        return VmResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .customerId(entity.getCustomerId())
                .cpuCores(entity.getCpuCores())
                .memoryGb(entity.getMemoryGb())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .lastStatusChangedAt(entity.getLastStatusChangedAt())
                .build();
    }

    public static List<VmResponse> mapToList(List<VirtualMachine> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(VmResponse::mapTo)
                .collect(Collectors.toList());
    }
}
