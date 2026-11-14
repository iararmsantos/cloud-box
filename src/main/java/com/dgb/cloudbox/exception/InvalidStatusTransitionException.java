package com.dgb.cloudbox.exception;

import com.dgb.cloudbox.entity.VmStatus;
import lombok.Getter;

@Getter
public class InvalidStatusTransitionException extends RuntimeException{

    private final VmStatus from;
    private final VmStatus to;
    private final Long vmId;

    public InvalidStatusTransitionException(VmStatus from, VmStatus to, Long vmId) {
        super("VM " + vmId + ": cannot change status from " + from + " to " + to);
        this.from = from;
        this.to = to;
        this.vmId = vmId;
    }

    public InvalidStatusTransitionException(String message, VmStatus from, VmStatus to, Long vmId) {
        super(message);
        this.from = from;
        this.to = to;
        this.vmId = vmId;
    }
}
