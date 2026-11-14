package com.dgb.cloudbox.objectmother;

import com.dgb.cloudbox.entity.VirtualMachine;
import com.dgb.cloudbox.entity.VmStatus;

public class VirtualMachineObjectMother {
    public static VirtualMachine createDefault() {
        return VirtualMachine.builder()
                .id(999L)
                .customerId(999L)
                .name("vm-999")
                .cpuCores(8)
                .memoryGb(1)
                .status(VmStatus.RUNNING)
                .build();
    }

    public static VirtualMachine createWithStatus(VmStatus vmStatus) {
        return VirtualMachine.builder()
                .id(999L)
                .customerId(999L)
                .name("vm-999")
                .cpuCores(8)
                .memoryGb(1)
                .status(vmStatus)
                .build();
    }
}
