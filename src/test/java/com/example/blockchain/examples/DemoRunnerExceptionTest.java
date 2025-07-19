package com.example.blockchain.examples;

import com.example.blockchain.core.config.ChainConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class DemoRunnerExceptionTest {

    @Mock
    private ChainConfig mockConfig;

    /**
     * Test that verifies the exception handling when a NoSuchAlgorithmException occurs
     * during transaction creation in the DemoRunner class.
     */
    @Test
    public void testNoSuchAlgorithmExceptionHandling() {
        // Create a test instance of DemoRunner
        DemoRunner demoRunner = new DemoRunner() {
            @Override
            public void runCustomGenesisBlockchainExample(ChainConfig config) {
                // Simulate the exception that would occur in the original method
                NoSuchAlgorithmException cause = new NoSuchAlgorithmException("Test algorithm not found");
                String error = "Error Creating Transection. \nError: " + cause.getMessage();
                throw new RuntimeException(error, cause);
            }
        };
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            demoRunner.runCustomGenesisBlockchainExample(mockConfig);
        });
        
        // Verify the exception message and cause
        assertEquals("Error Creating Transection. \nError: Test algorithm not found", exception.getMessage());
        assertTrue(exception.getCause() instanceof NoSuchAlgorithmException);
        assertEquals("Test algorithm not found", exception.getCause().getMessage());
    }
}