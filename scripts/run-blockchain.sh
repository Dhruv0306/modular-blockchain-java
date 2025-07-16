#!/bin/bash

# This script runs a modular blockchain application with configurable settings
# It supports different environments, log levels, and persistence options
# Author: Unknown
# Last modified: Unknown

# Display help message with usage instructions and examples
show_help() {
  echo "Usage: run-blockchain.sh [OPTIONS]"
  echo "Run the modular blockchain with different configurations."
  echo ""
  echo "Options:"
  echo "  -e, --env ENV    Set the environment (dev, prod, default)" 
  echo "  -l, --log LEVEL  Override the log level (TRACE, DEBUG, INFO, WARN, ERROR)"
  echo "  -h, --help       Show this help message"
  echo ""
  echo "Examples:"
  echo "  ./run-blockchain.sh --env dev"
  echo "  ./run-blockchain.sh --env prod --log DEBUG"
  exit 0
}

# Initialize default configuration values
ENV="default"                    # Default environment
LOG_LEVEL=""                    # Default log level (empty = use config file setting)
PERSIST_ENABLED="TRUE"          # Enable data persistence by default
PERSIST_DIR="data"             # Default directory for persistence
PERSIST_FILE="chain-data.json" # Default persistence file name


# Parse command line arguments using a while loop and case statement
while [[ $# -gt 0 ]]; do
  case "$1" in
  # Environment setting
  -e | --env)
    ENV="$2"
    shift 2
    ;;
  # Log level override
  -l | --log)
    LOG_LEVEL="$2"
    shift 2
    ;;
  # Help message
  -h | --help)
    show_help
    ;;
  # Persistence enabled flag
  -pE | --persistEnabled)
    PERSIST_ENABLED="$2"
    shift 2
    ;;
  # Persistence directory path
  -pD | --persistDir)
    PERSIST_DIR="$2"
    shift 2
    ;;
  # Persistence file name
  -pF | --persistFile)
    PERSIST_FILE="$2"
    shift 2
    ;;
  # Handle unknown options
  *)
    echo "Unknown option: $1"
    show_help
    ;;
  esac
done

# Determine which config file to use based on environment
CONFIG_FILE="config/blockchain.properties"
if [ "$ENV" != "default" ]; then
  CONFIG_FILE="config/blockchain-${ENV}.properties"
fi

# Construct the base Maven command with main class and config file
CMD="mvn exec:java -Dexec.mainClass=com.example.blockchain.Main -Dexec.args=\"${CONFIG_FILE}\""

# Add log level system property if specified
if [ ! -z "$LOG_LEVEL" ]; then
  CMD="$CMD -Dorg.slf4j.simpleLogger.defaultLogLevel=$LOG_LEVEL"
fi

# Output the selected configuration file
echo "Running blockchain with configuration: $CONFIG_FILE"
if [ ! -z "$LOG_LEVEL" ]; then
  echo "Overriding log level: $LOG_LEVEL"
fi

# Add persistence-related system properties if enabled
if [ ! -z "$PERSIST_ENABLED" ]; then
  CMD="$CMD -Dpersistence.enabled=$PERSIST_ENABLED"
  if [ ! -z "$PERSIST_DIR" ]; then
    CMD="$CMD -Dpersistence.dir=$PERSIST_DIR"
  fi
  if [ ! -z "$PERSIST_FILE" ]; then
    CMD="$CMD -Dpersistence.file=$PERSIST_FILE"
  fi
fi

# Output persistence settings
echo "Persistence enabled: $PERSIST_ENABLED"
if [ ! -z "$persistEnabled" ]; then
  echo "Persistence directory: $PERSIST_DIR"
  echo "Persistence file: $PERSIST_FILE"
fi

# Display the final command for debugging
echo "Executing command: $CMD"

# Execute the constructed Maven command
eval $CMD
