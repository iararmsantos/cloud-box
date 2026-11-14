package com.dgb.cloudbox.entity;

import com.dgb.cloudbox.exception.InvalidStatusTransitionException;
import com.dgb.cloudbox.objectmother.VirtualMachineObjectMother;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class VirtualMachineTest {
    @Test
    void start_shouldTransitionToRunningAndUpdateTimestamp_whenStopped() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.STOPPED);
        LocalDateTime before = LocalDateTime.now();

        // Act
        vm.start();

        // Assert
        LocalDateTime after = LocalDateTime.now();
        assertEquals(VmStatus.RUNNING, vm.getStatus());
        assertNotNull(vm.getLastStatusChangedAt());
        assertFalse(vm.getLastStatusChangedAt().isBefore(before));
        assertFalse(vm.getLastStatusChangedAt().isAfter(after));
    }

    @Test
    void start_shouldBeIdempotentAndNotTouchTimestamp_whenAlreadyRunning() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.RUNNING);
        LocalDateTime timestampBefore = vm.getLastStatusChangedAt();

        // Act
        vm.start();

        // Assert
        assertEquals(VmStatus.RUNNING, vm.getStatus());
        assertEquals(timestampBefore, vm.getLastStatusChangedAt());
    }

    @Test
    void start_shouldThrowAndKeepState_whenProvisioning() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.PROVISIONING);
        LocalDateTime timestampBefore = vm.getLastStatusChangedAt();

        // Act + Assert
        assertThrows(InvalidStatusTransitionException.class, vm::start);

        assertEquals(VmStatus.PROVISIONING, vm.getStatus());
        assertEquals(timestampBefore, vm.getLastStatusChangedAt());
    }

    @Test
    void stop_shouldTransitionToStoppedAndUpdateTimestamp_whenRunning() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.RUNNING);
        LocalDateTime before = LocalDateTime.now();

        // Act
        vm.stop();

        // Assert
        LocalDateTime after = LocalDateTime.now();
        assertEquals(VmStatus.STOPPED, vm.getStatus());
        assertNotNull(vm.getLastStatusChangedAt());
        assertFalse(vm.getLastStatusChangedAt().isBefore(before));
        assertFalse(vm.getLastStatusChangedAt().isAfter(after));
    }

    @Test
    void stop_shouldBeIdempotentAndNotTouchTimestamp_whenAlreadyRunning() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.STOPPED);
        LocalDateTime timestampBefore = vm.getLastStatusChangedAt();

        // Act
        vm.stop();

        // Assert
        assertEquals(VmStatus.STOPPED, vm.getStatus());
        assertEquals(timestampBefore, vm.getLastStatusChangedAt());
    }

    @Test
    void stop_shouldTransitionToStoppedAndUpdateTimestamp_whenProvisioning() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.PROVISIONING);
        LocalDateTime before = LocalDateTime.now();

        // Act
        vm.stop();

        // Assert
        LocalDateTime after = LocalDateTime.now();
        assertEquals(VmStatus.STOPPED, vm.getStatus());
        assertFalse(vm.getLastStatusChangedAt().isBefore(before));
        assertFalse(vm.getLastStatusChangedAt().isAfter(after));
    }

    @Test
    void markProvisioned_shouldTransitionToProvisioningAndUpdateTimestamp_whenStopped() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.PROVISIONING);
        LocalDateTime before = LocalDateTime.now();

        // Act
        vm.completeProvisioning();

        // Assert
        LocalDateTime after = LocalDateTime.now();
        assertEquals(VmStatus.STOPPED, vm.getStatus());
        assertNotNull(vm.getLastStatusChangedAt());
        assertFalse(vm.getLastStatusChangedAt().isBefore(before));
        assertFalse(vm.getLastStatusChangedAt().isAfter(after));
    }

    @Test
    void markProvisioned_shouldBeIdempotentAndNotTouchTimestamp_whenAlreadyRunning() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.STOPPED);
        LocalDateTime timestampBefore = vm.getLastStatusChangedAt();

        // Act
        vm.completeProvisioning();

        // Assert
        assertEquals(VmStatus.STOPPED, vm.getStatus());
        assertEquals(timestampBefore, vm.getLastStatusChangedAt());
    }

    @Test
    void markProvisioned_shouldThrowAndKeepState_whenProvisioning() {
        // Arrange
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.RUNNING);
        LocalDateTime timestampBefore = vm.getLastStatusChangedAt();

        // Act + Assert
        assertThrows(InvalidStatusTransitionException.class, vm::completeProvisioning);

        assertEquals(VmStatus.RUNNING, vm.getStatus());
        assertEquals(timestampBefore, vm.getLastStatusChangedAt());
    }
}
