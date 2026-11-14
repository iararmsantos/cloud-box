package com.dgb.cloudbox.services;

import com.dgb.cloudbox.dto.VmRequest;
import com.dgb.cloudbox.dto.VmResponse;
import com.dgb.cloudbox.entity.VirtualMachine;
import com.dgb.cloudbox.entity.VmStatus;
import com.dgb.cloudbox.exception.InvalidStatusTransitionException;
import com.dgb.cloudbox.exception.ResourceNotFoundException;
import com.dgb.cloudbox.objectmother.VirtualMachineObjectMother;
import com.dgb.cloudbox.objectmother.VmRequestObjectMother;
import com.dgb.cloudbox.repository.VmRepository;
import com.dgb.cloudbox.service.VmService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VmServiceTest {
    @Mock
    VmRepository vmRepository;

    @InjectMocks
    private VmService vmService;

    @Test
    void createVm_shouldSaveVmWithProvisioningStatusAndMappedFields() {
        // Arrange
        VmRequest request = VmRequestObjectMother.createDefault();
        when(vmRepository.save(any(VirtualMachine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        vmService.createVm(request);

        // Assert
        ArgumentCaptor<VirtualMachine> captor = ArgumentCaptor.forClass(VirtualMachine.class);
        verify(vmRepository).save(captor.capture());
        VirtualMachine saved = captor.getValue();

        assertAll(
                () -> assertEquals(VmStatus.PROVISIONING, saved.getStatus()),
                () -> assertEquals(request.getName(), saved.getName()),
                () -> assertEquals(request.getCpuCores(), saved.getCpuCores()),
                () -> assertEquals(request.getMemoryGb(), saved.getMemoryGb())
                // ...remaining request fields
        );
        verifyNoMoreInteractions(vmRepository);
    }

    @Test
    void createVm_shouldReturnResponseBuiltFromSavedEntity() {
        // Arrange
        VmRequest request = VmRequestObjectMother.createDefault();

        // The repository returns an entity with DB-generated values (e.g. the id),
        // which proves the response comes from the saved entity, not the request.
        VirtualMachine persisted = VirtualMachineObjectMother.createWithStatus(VmStatus.PROVISIONING);

        when(vmRepository.save(any(VirtualMachine.class))).thenReturn(persisted);

        // Act
        VmResponse result = vmService.createVm(request);

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(persisted.getId(), result.getId()),
                () -> assertEquals(persisted.getStatus(), result.getStatus()),
                () -> assertEquals(persisted.getName(), result.getName())
        );
    }

    @Test
    void getAllVms_shouldReturnListOfVmResponse() {
        // Arrange
        VirtualMachine persisted = VirtualMachineObjectMother.createDefault();

        when(vmRepository.findAll())
                .thenReturn(List.of(persisted));

        // Act
        List<VmResponse> result = vmService.getAllVms();

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(persisted.getId(), result.getFirst().getId()),
                () -> assertEquals(persisted.getStatus(), result.getFirst().getStatus()),
                () -> assertEquals(persisted.getName(), result.getFirst().getName())
        );
    }

    @Test
    void getVm_shouldReturnResponse() throws BadRequestException {
        // Arrange
        VirtualMachine persisted = VirtualMachineObjectMother.createDefault();

        when(vmRepository.findById(persisted.getId()))
                .thenReturn(Optional.of(persisted));

        // Act
        VmResponse result = vmService.getVm(persisted.getId());

        // Assert
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(persisted.getId(), result.getId()),
                () -> assertEquals(persisted.getStatus(), result.getStatus()),
                () -> assertEquals(persisted.getName(), result.getName())
        );
    }

    @Test
    void getVm_shouldThrowBadRequest_whenIdIsNull() {
        // Act + Assert
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> vmService.getVm(null));

        assertEquals("Virtual Machine id cannot be null.", ex.getMessage());
        verifyNoInteractions(vmRepository);
    }

    @Test
    void getVm_shouldThrowResourceNotFound_whenVmDoesNotExist() {
        // Arrange
        Long id = 99L;
        when(vmRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> vmService.getVm(id));

        assertEquals("Virtual Machine with id 99 not found", ex.getMessage());
        verify(vmRepository).findById(id);
        verify(vmRepository, never()).save(any(VirtualMachine.class));
    }

    @Test
    void updateVm_shouldThrowBadRequest_whenRequestIsNull() {
        // Act + Assert
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> vmService.updateVm(1L, null));

        assertEquals("Request cannot be null.", ex.getMessage());
        verifyNoInteractions(vmRepository);
    }

    @Test
    void updateVm_shouldThrowBadRequest_whenIdIsNull() {
        // Arrange
        VmRequest request = VmRequestObjectMother.createDefault();

        // Act + Assert
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> vmService.updateVm(null, request));

        assertEquals("Virtual Machine id cannot be null.", ex.getMessage());
        verifyNoInteractions(vmRepository);
    }

    @Test
    void updateVm_shouldCheckRequestBeforeId_whenBothAreNull() {
        // Documents the validation order
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> vmService.updateVm(null, null));

        assertEquals("Request cannot be null.", ex.getMessage());
        verifyNoInteractions(vmRepository);
    }

    @Test
    void updateVm_shouldThrowResourceNotFound_whenVmDoesNotExist() {
        // Arrange
        Long id = 99L;
        VmRequest request = VmRequestObjectMother.createDefault();
        when(vmRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> vmService.updateVm(id, request));

        assertEquals("Virtual Machine with id 99 not found", ex.getMessage());
        verify(vmRepository).findById(id);
        verify(vmRepository, never()).save(any(VirtualMachine.class));
    }

    @Test
    void updateVm_shouldUpdateExistingEntityAndSaveIt() throws BadRequestException {
        // Arrange
        Long id = 1L;
        VirtualMachine existing = VirtualMachineObjectMother.createDefault();
        existing.setId(id);

        // A request whose values differ from the existing entity
        VmRequest request = VmRequestObjectMother.createDefault();

        when(vmRepository.findById(id)).thenReturn(Optional.of(existing));
        when(vmRepository.save(any(VirtualMachine.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        VmResponse result = vmService.updateVm(id, request);

        // Assert - saved entity
        ArgumentCaptor<VirtualMachine> captor = ArgumentCaptor.forClass(VirtualMachine.class);
        verify(vmRepository).save(captor.capture());
        VirtualMachine saved = captor.getValue();

        assertAll(
                () -> assertSame(existing, saved),
                () -> assertEquals(id, saved.getId()),
                () -> assertEquals(request.getName(), saved.getName()),
                () -> assertEquals(request.getCpuCores(), saved.getCpuCores()),
                () -> assertEquals(request.getMemoryGb(), saved.getMemoryGb())
        );

        // Assert - response
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(id, result.getId()),
                () -> assertEquals(request.getName(), result.getName())
        );

        verify(vmRepository).findById(id);
        verifyNoMoreInteractions(vmRepository);
    }

    @Test
    void updateVm_shouldReturnResponseBuiltFromSavedEntity() throws BadRequestException {
        // Arrange
        Long id = 1L;
        VirtualMachine existing = VirtualMachineObjectMother.createDefault();
        VmRequest request = VmRequestObjectMother.createDefault();

        // Repository returns a different instance, proving the response
        // comes from save()'s return value and not from the entity passed in
        VirtualMachine persisted = VirtualMachineObjectMother.createDefault();

        when(vmRepository.findById(id)).thenReturn(Optional.of(existing));
        when(vmRepository.save(any(VirtualMachine.class))).thenReturn(persisted);

        // Act
        VmResponse result = vmService.updateVm(id, request);

        // Assert
        assertEquals(persisted.getName(), result.getName());
    }

    @Test
    void deleteVm_shouldDeleteVm_WhenVmExists() throws BadRequestException {
        // Arrange
        Long vmId = 999L;
        VirtualMachine virtualMachine = VirtualMachineObjectMother.createDefault();

        when(vmRepository.findById(vmId))
                .thenReturn(Optional.of(virtualMachine));

        // Act
        vmService.deleteVm(vmId);

        // Assert
        verify(vmRepository).findById(virtualMachine.getId());
        verify(vmRepository).delete(virtualMachine);
    }

    @Test
    void deleteVm_shouldThrowBadRequest_whenIdIsNull() {
        // Act + Assert
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> vmService.deleteVm(null));

        assertEquals("Virtual Machine id cannot be null.", ex.getMessage());
        verifyNoInteractions(vmRepository);
    }

    @Test
    void deleteVm_shouldThrowResourceNotFound_whenVmDoesNotExist() {
        // Arrange
        Long id = 99L;
        when(vmRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> vmService.deleteVm(id));

        assertEquals("Virtual Machine with id 99 not found", ex.getMessage());
        verify(vmRepository).findById(id);
        verify(vmRepository, never()).save(any(VirtualMachine.class));
    }

    @Test
    void start_shouldUpdateStatusToRunning_WhenStatusStopped() {
        // Arrange
        Long id = 1L;
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.STOPPED);
        vm.setId(id);
        when(vmRepository.findById(id)).thenReturn(Optional.of(vm));

        // Act
        VmResponse result = vmService.start(id);

        // Assert
        assertEquals(VmStatus.RUNNING, vm.getStatus());
        assertEquals(VmStatus.RUNNING, result.getStatus());
        assertEquals(id, result.getId());
    }

    @Test
    void start_shouldThrowResourceNotFound_whenVmDoesNotExist() {
        // Arrange
        Long id = 99L;
        when(vmRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> vmService.start(id));

        assertEquals("Virtual Machine with id 99 not found", ex.getMessage());
        verify(vmRepository).findById(id);
        verify(vmRepository, never()).save(any(VirtualMachine.class));
    }

    @Test
    void start_shouldReturnRunningResponseWithoutChanges_whenAlreadyRunning() {
        // Arrange
        Long id = 1L;
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.RUNNING);
        vm.setId(id);
        LocalDateTime timestampBefore = vm.getLastStatusChangedAt();
        when(vmRepository.findById(id)).thenReturn(Optional.of(vm));

        // Act
        VmResponse result = vmService.start(id);

        // Assert
        assertEquals(VmStatus.RUNNING, result.getStatus());
        assertEquals(timestampBefore, vm.getLastStatusChangedAt());
    }

    @Test
    void start_shouldPropagateInvalidTransition_whenProvisioning() {
        // Arrange
        Long id = 1L;
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.PROVISIONING);
        vm.setId(id);
        when(vmRepository.findById(id)).thenReturn(Optional.of(vm));

        // Act + Assert
        assertThrows(InvalidStatusTransitionException.class, () -> vmService.start(id));
        assertEquals(VmStatus.PROVISIONING, vm.getStatus());
    }

    @Test
    void stop_shouldUpdateStatusToStopped_WhenStatusRunning() {
        // Arrange
        Long id = 1L;
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.RUNNING);
        vm.setId(id);
        when(vmRepository.findById(id)).thenReturn(Optional.of(vm));

        // Act
        VmResponse result = vmService.stop(id);

        // Assert
        assertEquals(VmStatus.STOPPED, vm.getStatus());
        assertEquals(VmStatus.STOPPED, result.getStatus());
        assertEquals(id, result.getId());
    }

    @Test
    void stop_shouldThrowResourceNotFound_whenVmDoesNotExist() {
        // Arrange
        Long id = 99L;
        when(vmRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> vmService.stop(id));

        assertEquals("Virtual Machine with id 99 not found", ex.getMessage());
        verify(vmRepository).findById(id);
        verify(vmRepository, never()).save(any(VirtualMachine.class));
    }

    @Test
    void stop_shouldReturnStoppedResponseWithoutChanges_whenAlreadyStopped() {
        // Arrange
        Long id = 1L;
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.STOPPED);
        vm.setId(id);
        LocalDateTime timestampBefore = vm.getLastStatusChangedAt();
        when(vmRepository.findById(id)).thenReturn(Optional.of(vm));

        // Act
        VmResponse result = vmService.stop(id);

        // Assert
        assertEquals(VmStatus.STOPPED, result.getStatus());
        assertEquals(timestampBefore, vm.getLastStatusChangedAt());
    }

    @Test
    void provisioned_shouldUpdateStatusToStopped() {
        // Arrange
        Long id = 1L;
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.PROVISIONING);
        vm.setId(id);
        when(vmRepository.findById(id)).thenReturn(Optional.of(vm));

        // Act
        VmResponse result = vmService.markProvisioned(id);

        // Assert
        assertEquals(VmStatus.STOPPED, vm.getStatus());
        assertEquals(VmStatus.STOPPED, result.getStatus());
        assertEquals(id, result.getId());
    }

    @Test
    void markProvisioned_shouldThrowResourceNotFound_whenVmDoesNotExist() {
        // Arrange
        Long id = 99L;
        when(vmRepository.findById(id)).thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> vmService.markProvisioned(id));

        assertEquals("Virtual Machine with id 99 not found", ex.getMessage());
        verify(vmRepository).findById(id);
        verify(vmRepository, never()).save(any(VirtualMachine.class));
    }

    @Test
    void markProvisioned_shouldPropagateInvalidTransition_whenRunning() {
        // Arrange
        Long id = 1L;
        VirtualMachine vm = VirtualMachineObjectMother.createWithStatus(VmStatus.RUNNING);
        vm.setId(id);
        when(vmRepository.findById(id)).thenReturn(Optional.of(vm));

        // Act + Assert
        assertThrows(InvalidStatusTransitionException.class, () -> vmService.markProvisioned(id));
        assertEquals(VmStatus.RUNNING, vm.getStatus());
    }
}
