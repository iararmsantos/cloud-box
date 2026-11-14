package com.dgb.cloudbox.entity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VmStatusTest {
    @ParameterizedTest(name = "{0} -> {1} allowed: {2}")
    @CsvSource({
            "PROVISIONING, PROVISIONING, false",
            "PROVISIONING, RUNNING,      false",
            "PROVISIONING, STOPPED,      true",

            "RUNNING,      PROVISIONING, false",
            "RUNNING,      RUNNING,      false",
            "RUNNING,      STOPPED,      true",

            "STOPPED,      PROVISIONING, true",
            "STOPPED,      RUNNING,      true",
            "STOPPED,      STOPPED,      false"
    })

    void canTransitionTo_shouldMatchTransitionTable(VmStatus from, VmStatus to, boolean expected) {
        assertEquals(expected, from.canTransitionTo(to));
    }
}
