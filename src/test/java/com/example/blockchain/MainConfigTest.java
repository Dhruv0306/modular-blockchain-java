package com.example.blockchain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.example.blockchain.core.config.ChainConfig;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * Tests for the configuration loading logic in the Main class.
 */
public class MainConfigTest {
    
    private MockedStatic<ChainConfig> mockedChainConfig;
    private MockedStatic<Main> mockedMain;
    private ChainConfig mockConfig;
    
    @BeforeEach
    public void setUp() {
        mockConfig = mock(ChainConfig.class);
        mockedChainConfig = Mockito.mockStatic(ChainConfig.class);
        mockedChainConfig.when(() -> ChainConfig.getInstance(anyString())).thenReturn(mockConfig);
        
        mockedMain = Mockito.mockStatic(Main.class, Mockito.CALLS_REAL_METHODS);
    }
    
    @AfterEach
    public void tearDown() {
        mockedChainConfig.close();
        mockedMain.close();
    }
    
    @Test
    public void testDefaultConfigurationPath() {
        // Test default configuration (no args, no env)
        mockedMain.when(() -> Main.getEnvVariable("BLOCKCHAIN_ENV")).thenReturn(null);
        
        // Call main with no arguments
        Main.main(new String[]{});
        
        // Verify the default config path was used
        mockedChainConfig.verify(() -> 
            ChainConfig.getInstance("config/blockchain.properties"), times(1));
    }
    
    @Test
    public void testCommandLineArgumentConfigPath() {
        // Test command line argument configuration
        mockedMain.when(() -> Main.getEnvVariable("BLOCKCHAIN_ENV")).thenReturn(null);
        
        String[] args = {"custom-config.properties"};
        Main.main(args);
        
        // Verify the command line argument config path was used
        mockedChainConfig.verify(() -> 
            ChainConfig.getInstance("custom-config.properties"), times(1));
    }
    
    @Test
    public void testEnvironmentVariableConfigPath() {
        // Test environment variable configuration
        mockedMain.when(() -> Main.getEnvVariable("BLOCKCHAIN_ENV")).thenReturn("test");
        
        // Call main with no arguments
        Main.main(new String[]{});
        
        // Verify the environment-specific config path was used
        mockedChainConfig.verify(() -> 
            ChainConfig.getInstance("config/blockchain-test.properties"), times(1));
    }
    
    @Test
    public void testEmptyEnvironmentVariableConfigPath() {
        // Test empty environment variable configuration
        mockedMain.when(() -> Main.getEnvVariable("BLOCKCHAIN_ENV")).thenReturn("");
        
        // Call main with no arguments
        Main.main(new String[]{});
        
        // Verify the default config path was used (empty env should not trigger env-specific config)
        mockedChainConfig.verify(() -> 
            ChainConfig.getInstance("config/blockchain.properties"), times(1));
    }
}