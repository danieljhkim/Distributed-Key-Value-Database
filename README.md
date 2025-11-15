# KvDB - Distributed Key-Value Database

[![Build and Test](https://github.com/danieljhkim/Distributed-Key-Value-Database/actions/workflows/build.yml/badge.svg)](https://github.com/danieljhkim/Distributed-Key-Value-Database/actions/workflows/build.yml)

A Redis-like distributed key-value store implemented in Java with clustering capabilities.

## Features

This project provides a lightweight distributed in-memory database, interfaced with a CLI.
Supports a distributed architecture with a coordinator node and multiple storage nodes using modulo-based sharding strategy, enabling horizontal scalability.
The system ensures data durability through periodic disc persistence and offers basic fault tolerance with node failure recovery via WAL.

### Failure recovery

Failure of any node is managed by the coordinator node, by delegating failed node's work to another healthy node and, once the failed node is back online, it syncs the state via 2 WAL's:
- **Primary WAL**: Logs from the failed storage node
- **Secondary WAL**: Logs from the coordinator node that were kept while the node was down

### Client-Server Communication

- The server uses a combination of TCP sockets and gRPC for server-server communication
- Individual nodes can be accessed directly or through the coordinator


--- 

## Architecture Overview

KvDB follows a distributed architecture with the following components:

- **Coordinator Node**: Manages the cluster topology, routes client requests to appropriate nodes. Performs health checks and delegates tasks in case of node failures.
- **Storage Nodes**: Store the actual key-value data and handle read/write operations
- **Client Interface**: Connects to the coordinator for executing commands

```
         +-----------------------------+
         |        Client / CLI         |
         +-------------+---------------+
                       |
                       v
         +-------------+---------------+
         |       Coordinator Node      |
         |  - Knows cluster topology   |
         |  - Handles client requests  |
         |  - Routes to correct node   |
         +-------------+---------------+
                       |
       ----------------+------------------
      |                |                  |
+-----+-----+    +-----+-----+     +------+------+
|   Node A  |    |   Node B  |     |   Node C     |
| - KV store|    | - KV store|     | - KV store   |
+-----------+    +-----------+     +-------------+
```

--- 

## CLI Preview

![kvclient](assets/kvclient.png)

---

## Usage

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- Go 1.21+ (for CLI client)

### Starting the Cluster

1. Package the project using Maven:

```bash
make java
````

2. Start the Cluster: Coordinator Server and Node Servers

```bash
make run_cluster 
```

3. Start the Client CLI

**Option 1** (recommended): [Use kvcli CLI Go application](./golang/kvcli/README.md)
```bash
make build_cli

# connect to the cluster
kv connect --host localhost --port 7000
```

### Basic CLI Commands

#### In-Memory Store Operations

- `SET key value` - Set key to hold string value
- `GET key` - Get the value of key (returns `(nil)` if key doesn't exist)
- `DEL key` - Delete one or more keys
- `EXISTS key` - Check if a key exists (returns 1 if exists, 0 if not)

#### SQL-like Operations

- `SQL INIT [table_name]` - Initialize a new table
- `SQL USE [table_name]` - Switch to an existing table
- `SQL GET [key]` - Retrieve value (returns `(nil)` if key doesn't exist)
- `SQL SET [key] [value]` - Store a key-value pair
- `SQL DEL [key]` - Remove a key-value pair
- `SQL CLEAR` - Remove all entries from the current table
- `SQL PING` - Check connection to database

#### Other Commands

- `PING` - Test connection
- `HELP` - Show help message
- `EXIT` - Exit the client

#### Response Conventions

- Missing keys return `(nil)` for GET operations
- Successful operations return `OK` or `true`
- Errors are prefixed with `ERR:`

---

## Testing

The project includes comprehensive unit tests for the command parsing and execution layer.

### Running Tests

```bash
# Run all tests
mvn test

# Run tests for a specific module
mvn test -pl kv.server
mvn test -pl kv.coordinator
```

### Test Coverage

- **KVCommandParser**: 21 tests covering command parsing, validation, and response formatting
- **SQLCommandParser**: 25 tests covering SQL-like commands, initialization requirements, and error handling
- **Coordinator KVCommandParser**: 15 tests for distributed command routing

All tests use lightweight fake executors to avoid external dependencies and ensure fast, reliable test execution.

--- 

## Configuration

Storage node configuration is done via `application.properties` file located in the `kv.common/src/main/resources/<node_id>` directory for locally running.
If running the storage nodes remotely, the configuration files should be placed in `kv.server/src/main/resources/<node_id>` directory.

### File-based Persistence

The system supports file-based persistence with options for auto-flushing and custom file types.

```properties
kvdb.persistence.filepath=data/kvstore.dat
kvdb.persistence.filetype=dat
kvdb.persistence.enableAutoFlush=true
kvdb.persistence.autoFlushInterval=2
```

### Cluster Configuration

The coordinator uses a YAML configuration file to define the cluster topology, located in the `kv.coordinator/src/main/resources/cluster-config.yaml` file:

```yaml
nodes:
  - id: node1
    host: 127.0.0.1
    port: 8081
    useGrpc: true

  - id: node2
    host: 127.0.0.1
    port: 8082
    useGrpc: true

```

--- 

# License

This project is licensed under the MIT License.
