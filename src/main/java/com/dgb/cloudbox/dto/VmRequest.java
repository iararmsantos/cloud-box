package com.dgb.cloudbox.dto;

import com.dgb.cloudbox.entity.VmStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VmRequest {
    @NotBlank
    private String name;

    @NotNull
    private Long customerId;

    @Min(value = 0)
    private Integer cpuCores;

    @Min(value = 0)
    private Integer memoryGb;

    private VmStatus status = VmStatus.PROVISIONING;
}
