package com.dgb.cloudbox.entity;

public enum VmStatus {
    RUNNING, STOPPED, PROVISIONING;

    public boolean canTransitionTo(VmStatus target) {
        return switch (this) {
            case PROVISIONING, RUNNING -> target == STOPPED;
            case STOPPED      -> target == RUNNING || target == PROVISIONING;
        };
    }
}
