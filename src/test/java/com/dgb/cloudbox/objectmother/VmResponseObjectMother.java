package com.dgb.cloudbox.objectmother;

import com.dgb.cloudbox.dto.VmResponse;
import com.dgb.cloudbox.entity.VmStatus;

public class VmResponseObjectMother {
    public static VmResponse createDefault() {
        return VmResponse.builder()
                .id(999L)
                .customerId(999L)
                .name("vm-999")
                .cpuCores(8)
                .memoryGb(1)
                .status(VmStatus.RUNNING)
                .build();
    }

    public static VmResponse createWithStatus(VmStatus vmStatus) {
        return VmResponse.builder()
                .id(999L)
                .customerId(999L)
                .name("vm-999")
                .cpuCores(8)
                .memoryGb(1)
                .status(vmStatus)
                .build();
    }
}
