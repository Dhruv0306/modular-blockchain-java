@echo off
setlocal enabledelayedexpansion

:: Help message
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help

:: Default values
set ENV=default
set LOG_LEVEL=

:: Parse command line arguments
:parse_args
if "%1"=="" goto :end_parse_args
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
echo Unknown option: %1
goto :show_help

:end_parse_args

:: Set config file based on environment
set CONFIG_FILE=config/blockchain.properties
if not "%ENV%"=="default" (
  set CONFIG_FILE=config/blockchain-%ENV%.properties
)

:: Build the command
set CMD=mvn exec:java -Dexec.mainClass=com.example.blockchain.Main -Dexec.args="%CONFIG_FILE%"

:: Add log level override if provided
if not "%LOG_LEVEL%"=="" (
  set CMD=%CMD% -Dorg.slf4j.simpleLogger.defaultLogLevel=%LOG_LEVEL%
)

echo Running blockchain with configuration: %CONFIG_FILE%
if not "%LOG_LEVEL%"=="" (
  echo Overriding log level: %LOG_LEVEL%
)

:: Execute the command
%CMD%
goto :eof

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
