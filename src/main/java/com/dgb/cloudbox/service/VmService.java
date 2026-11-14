package com.dgb.cloudbox.service;

import com.dgb.cloudbox.dto.VirtualMachineMapper;
import com.dgb.cloudbox.dto.VmRequest;
import com.dgb.cloudbox.dto.VmResponse;
import com.dgb.cloudbox.entity.VirtualMachine;
import com.dgb.cloudbox.exception.ResourceNotFoundException;
import com.dgb.cloudbox.repository.VmRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VmService {
    private final VmRepository vmRepository;

    public VmService(VmRepository vmRepository) {
        this.vmRepository = vmRepository;
    }

    public VmResponse createVm(VmRequest request) {
        VirtualMachine entity = VirtualMachineMapper.mapTo(request);

        return VmResponse.mapTo(vmRepository.save(entity));
    }

    public VmResponse getVm(Long id) throws BadRequestException {
        if (id == null) {
            throw new BadRequestException("Virtual Machine id cannot be null.");
        }

        VirtualMachine entity = findVmOrThrow(id);

        return VmResponse.mapTo(entity);
    }

    public List<VmResponse> getAllVms() {
        return VmResponse.mapToList(vmRepository.findAll());
    }

    public VmResponse updateVm(Long id, VmRequest request) throws BadRequestException {
        if (request == null) {
            throw new BadRequestException("Request cannot be null.");
        }

        if (id == null) {
            throw new BadRequestException("Virtual Machine id cannot be null.");
        }

        VirtualMachine entity = findVmOrThrow(id);

        VirtualMachineMapper.updateEntity(request, entity);

        return VmResponse.mapTo(vmRepository.save(entity));
    }

    public void deleteVm(Long id) throws BadRequestException {
        if (id == null) {
            throw new BadRequestException("Virtual Machine id cannot be null.");
        }

        VirtualMachine entity = findVmOrThrow(id);

        vmRepository.delete(entity);
    }

    @Transactional
    public VmResponse start(Long id) {
        VirtualMachine entity = findVmOrThrow(id);

        entity.start();

        return VmResponse.mapTo(entity);
    }

    @Transactional
    public VmResponse stop(Long id) {
        VirtualMachine entity = findVmOrThrow(id);

        entity.stop();

        return VmResponse.mapTo(entity);
    }

    @Transactional
    public VmResponse markProvisioned(Long id) {
        VirtualMachine entity = findVmOrThrow(id);

        entity.completeProvisioning();


        return VmResponse.mapTo(entity);
    }

    private VirtualMachine findVmOrThrow(Long id) {
        return vmRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Virtual Machine with id " + id + " not found"
                        )
                );
    }
}
