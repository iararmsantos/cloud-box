package com.dgb.cloudbox.repository;

import com.dgb.cloudbox.entity.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VmRepository extends JpaRepository<VirtualMachine, Long> {
}
