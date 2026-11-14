package com.dgb.cloudbox.dto;

import com.dgb.cloudbox.entity.VmStatus;

public record ErrorResponse(
        int status,
        String message,
        Long vmId,
        VmStatus from,
        VmStatus to
) {}
