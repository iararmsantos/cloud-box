package com.dgb.cloudbox.entity;

import com.dgb.cloudbox.exception.InvalidStatusTransitionException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VirtualMachine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Long customerId;

    @NotNull
    private Integer cpuCores;

    @NotNull
    private Integer memoryGb;

    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VmStatus status = VmStatus.PROVISIONING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime lastStatusChangedAt;

    // in VirtualMachine
    public void start() {
        if (status == VmStatus.RUNNING) return;
        transitionTo(VmStatus.RUNNING);
    }

    public void stop() {
        if (status == VmStatus.STOPPED) return;
        transitionTo(VmStatus.STOPPED);
    }

    public void completeProvisioning() {
        switch (status) {
            case PROVISIONING -> transitionTo(VmStatus.STOPPED);
            case STOPPED -> { /* already provisioned: no-op, 200 */ }
            case RUNNING -> throw new InvalidStatusTransitionException(
                    "VM " + id + ": cannot mark as provisioned while RUNNING",
                    status, VmStatus.STOPPED, id);
        }
    }

    private void transitionTo(VmStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(status, target, id);
        }
        this.status = target;
        this.lastStatusChangedAt = LocalDateTime.now();
    }
}
