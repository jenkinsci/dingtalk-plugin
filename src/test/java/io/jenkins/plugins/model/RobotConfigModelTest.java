package io.jenkins.plugins.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RobotConfigModelTest {
    @Test
    void testCreateSign() {

        long timestamp = 1735693200000L;
        String secret = "SEC045ae0433d866d0019a47f441d3da2c3c5862b8a8c46f8ab1de83a6fb0b9a54c";

        String expectedSign = "BSe0QzwtBdzTlEnmaVptmDApFF%2FEyDdmvpE%2FDajHtoE%3D";

        String actualSign = RobotConfigModel.createSign(timestamp, secret);

        System.out.println("Actual: " + actualSign);

        assertEquals(expectedSign, actualSign);
    }
}
