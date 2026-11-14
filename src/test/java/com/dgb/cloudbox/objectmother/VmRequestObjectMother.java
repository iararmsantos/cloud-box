package com.dgb.cloudbox.objectmother;

import com.dgb.cloudbox.dto.VmRequest;
import com.dgb.cloudbox.entity.VmStatus;

public class VmRequestObjectMother {
    public static VmRequest createDefault() {
        return VmRequest.builder()
                .customerId(999L)
                .name("vm-999")
                .cpuCores(8)
                .memoryGb(1)
                .status(VmStatus.PROVISIONING)
                .build();
    }

    public static VmRequest createWithStatus(VmStatus vmStatus) {
        return VmRequest.builder()
                .customerId(999L)
                .name("vm-999")
                .cpuCores(8)
                .memoryGb(1)
                .status(vmStatus)
                .build();
    }
}
