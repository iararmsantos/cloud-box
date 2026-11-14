package com.dgb.cloudbox.dto;

import com.dgb.cloudbox.entity.VirtualMachine;
import com.dgb.cloudbox.entity.VmStatus;
import com.dgb.cloudbox.objectmother.VirtualMachineObjectMother;
import com.dgb.cloudbox.objectmother.VmRequestObjectMother;
import com.dgb.cloudbox.objectmother.VmResponseObjectMother;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VirtualMachineMapperTest {
    @Test
    void mapTo_whenRequestIsNull_returnsNull() {
        // Act / Assert
        assertThat(VirtualMachineMapper.mapTo((VmRequest) null)).isNull();
    }

    @Test
    void mapTo_whenRequestDtoIsValid_mapsAllFields() {
        // Arrange
        VmRequest request = VmRequestObjectMother.createDefault();

        // Act
        VirtualMachine result = VirtualMachineMapper.mapTo(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getCustomerId()).isEqualTo(request.getCustomerId());
        assertThat(result.getCpuCores()).isEqualTo(request.getCpuCores());
        assertThat(result.getMemoryGb()).isEqualTo(request.getMemoryGb());
        assertThat(result.getStatus()).isEqualTo(request.getStatus());
    }

    @Test
    void mapTo_whenResponseIsNull_returnsNull() {
        // Act / Assert
        assertThat(VirtualMachineMapper.mapTo((VmResponse) null)).isNull();
    }

    @Test
    void mapTo_whenResponseDtoIsValid_mapsAllFields() {
        // Arrange
        VmResponse response = VmResponseObjectMother.createDefault();

        // Act
        VirtualMachine result = VirtualMachineMapper.mapTo(response);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(response.getName());
        assertThat(result.getCustomerId()).isEqualTo(response.getCustomerId());
        assertThat(result.getCpuCores()).isEqualTo(response.getCpuCores());
        assertThat(result.getMemoryGb()).isEqualTo(response.getMemoryGb());
        assertThat(result.getStatus()).isEqualTo(response.getStatus());
    }

    @Test
    void updateEntity_whenResponseDtoIsValid_mapsAllFields() {
        // Arrange
        VmRequest request = VmRequestObjectMother.createDefault();
        VirtualMachine target = VirtualMachineObjectMother.createWithStatus(VmStatus.PROVISIONING);

        // Act
        VirtualMachineMapper.updateEntity(request, target);

        // Assert
        assertThat(target.getId()).isNotNull();
        assertThat(target.getName()).isEqualTo(request.getName());
        assertThat(target.getCustomerId()).isEqualTo(request.getCustomerId());
        assertThat(target.getCpuCores()).isEqualTo(request.getCpuCores());
        assertThat(target.getMemoryGb()).isEqualTo(request.getMemoryGb());
        assertThat(target.getStatus()).isEqualTo(request.getStatus());
    }
}