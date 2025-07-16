@echo off
setlocal enabledelayedexpansion

:: =============================================================================
:: Blockchain Runner Script
:: This script runs a modular blockchain application with configurable settings
:: for environment, logging, and data persistence.
:: =============================================================================

:: Check for help flags
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help

:: Initialize default configuration values
set ENV=default
set LOG_LEVEL=
set PERSIST_ENABLED=TRUE
set PERSIST_DIR=data
set PERSIST_FILE=chain-data.json

:: Parse command line arguments
:: Supports both long (--arg) and short (-a) argument formats
:parse_args
if "%1"=="" goto :end_parse_args

:: Environment setting
if "%1"=="--env" (
  set ENV=%2
  shift
  shift
  goto :parse_args
)
if "%1"=="-e" (
  set ENV=%2
  shift
  shift
  goto :parse_args
)

:: Log level setting
if "%1"=="--log" (
  set LOG_LEVEL=%2
  shift
  shift
  goto :parse_args
)
if "%1"=="-l" (
  set LOG_LEVEL=%2
  shift
  shift
  goto :parse_args
)

:: Persistence enabled flag
if "%1"=="--persistEnabled" (
  set PERSIST_ENABLED=%2
  shift
  shift
  goto :parse_args
)
if "%1"=="-pE" (
  set PERSIST_ENABLED=%2
  shift
  shift
  goto :parse_args
)

:: Persistence directory setting
if "%1"=="--persistDir" (
  set PERSIST_DIR=%2
  shift
  shift
  goto :parse_args
)
if "%1"=="-pD" (
  set PERSIST_DIR=%2
  shift
  shift
  goto :parse_args
)

:: Persistence file setting
if "%1"=="--persistFile" (
  set PERSIST_FILE=%2
  shift
  shift
  goto :parse_args
)
if "%1"=="-pF" (
  set PERSIST_FILE=%2
  shift
  shift
  goto :parse_args
)
echo Unknown option: %1
goto :show_help

:end_parse_args

:: Configure environment-specific settings
:: Uses default config file if no environment specified
set CONFIG_FILE=config/blockchain.properties
if not "%ENV%"=="default" (
  set CONFIG_FILE=config/blockchain-%ENV%.properties
)

:: Build the base command with main class and config file
set CMD=mvn exec:java -Dexec.mainClass=com.example.blockchain.Main -Dexec.args="%CONFIG_FILE%"

:: Add optional log level configuration
if not "%LOG_LEVEL%"=="" (
  set CMD=%CMD% -Dorg.slf4j.simpleLogger.defaultLogLevel=%LOG_LEVEL%
)

:: Display current configuration
echo Running blockchain with configuration: %CONFIG_FILE%
if not "%LOG_LEVEL%"=="" (
  echo Overriding log level: %LOG_LEVEL%
)

:: Configure persistence settings
:: If enabled, set directory and file
:: If disabled, explicitly set enabled=false
if "%PERSIST_ENABLED%"=="TRUE" (
  if not "%PERSIST_DIR%"=="" (
    set CMD=%CMD% -Dpersistence.dir=%PERSIST_DIR%
  ) else (
    set CMD=%CMD% -Dpersistence.dir=./persistence
  )
  if not "%PERSIST_FILE%"=="" (
    set CMD=%CMD% -Dpersistence.file=%PERSIST_FILE%
  ) else (
    set CMD=%CMD% -Dpersistence.file=blockchain-data.json
  )
) else (
  set CMD=%CMD% -Dpersistence.enabled=false
)

:: Display persistence configuration
echo Persistence enabled: %PERSIST_ENABLED%
if "%PERSIST_ENABLED%"=="TRUE" (
  echo Persistence directory: %PERSIST_DIR%
  echo Persistence file: %PERSIST_FILE%
) else (
  echo Persistence is disabled.
)

:: Execute the fully configured command
%CMD%
goto :eof

:: Help section
:: Displays usage information and examples
:show_help
echo Usage: run-blockchain.bat [OPTIONS]
echo Run the modular blockchain with different configurations.
echo.
echo Options:
echo  -e, --env ENV  Set the environment (dev, prod, default)
echo  -l, --log LEVEL  Override the log level (TRACE, DEBUG, INFO, WARN, ERROR)
echo  -h, --help  Show this help message
echo.
echo Examples:
echo  run-blockchain.bat --env dev
echo  run-blockchain.bat --env prod --log DEBUG
exit /b 0
