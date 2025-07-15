#!/bin/bash

# Help message
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

# Default values
ENV="default"
LOG_LEVEL=""

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
  -e | --env)
    ENV="$2"
    shift 2
    ;;
  -l | --log)
    LOG_LEVEL="$2"
    shift 2
    ;;
  -h | --help)
    show_help
    ;;
  *)
    echo "Unknown option: $1"
    show_help
    ;;
  esac
done

# Set config file based on environment
CONFIG_FILE="blockchain.properties"
if [ "$ENV" != "default" ]; then
  CONFIG_FILE="blockchain-${ENV}.properties"
fi

# Build the command
CMD="mvn exec:java -Dexec.mainClass=com.example.blockchain.Main -Dexec.args=\"${CONFIG_FILE}\""

# Add log level override if provided
if [ ! -z "$LOG_LEVEL" ]; then
  CMD="$CMD -Dorg.slf4j.simpleLogger.defaultLogLevel=$LOG_LEVEL"
fi

echo "Running blockchain with configuration: $CONFIG_FILE"
if [ ! -z "$LOG_LEVEL" ]; then
  echo "Overriding log level: $LOG_LEVEL"
fi

# Execute the command
eval $CMD
