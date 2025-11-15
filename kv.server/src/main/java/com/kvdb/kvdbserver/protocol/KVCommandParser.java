package com.kvdb.kvdbserver.protocol;


import com.kvdb.kvcommon.protocol.CommandExecutor;
import com.kvdb.kvcommon.protocol.CommandParser;
import com.kvdb.kvdbserver.repository.BaseRepository;
import com.kvdb.kvdbserver.repository.KVStoreRepository;

public class KVCommandParser extends CommandParser {

    private static final String HELP_TEXT =
            """
        KV Command Parser Usage:
        SET [key] [value] - Store a key-value pair
        GET [key] - Retrieve value for a given key
        DEL [key] - Remove a key-value pair
        EXISTS [key] - Check if a key exists (returns 1 if exists, 0 if not)
        SIZE - Return the number of key-value pairs stored
        CLEAR - Remove all entries
        ALL - Return all key-value pairs
        PING - Check connection
        SHUTDOWN/QUIT/TERMINATE - Close the database connection
        HELP/INFO - Display this help message""";

    public KVCommandParser(CommandExecutor executor) {
        super(executor);
    }

    public KVCommandParser(BaseRepository store) {
        super(new BaseRepositoryAdapter(store));
    }

    public KVCommandParser() {
        super(new BaseRepositoryAdapter(new KVStoreRepository()));
    }

    @Override
    public String executeCommand(String[] parts, CommandExecutor executor) {
        if (executor == null) {
            executor = this.executor;
        }
        String cmd = parts[0].trim().toUpperCase();
        return switch (cmd) {
            case "HELP", "INFO" -> getHelpText();
            case "SET" -> handleSet(parts, executor);
            case "GET" -> handleGet(parts, executor);
            case "DEL" -> handleDelete(parts, executor);
            case "SHUTDOWN" -> handleShutdown(executor);
            case "PING" -> handlePing();
            default -> "ERR: Unknown command";
        };
    }

    private String handleSet(String[] parts, CommandExecutor executor) {
        if (parts.length != 3) return formatError("Usage: SET key value");
        return formatOk(String.valueOf(executor.put(parts[1], parts[2])));
    }

    private String handleGet(String[] parts, CommandExecutor executor) {
        if (parts.length != 2) return formatError("Usage: GET key");
        String value = executor.get(parts[1]);
        return value != null ? value : NIL_RESPONSE;
    }

    private String handleDelete(String[] parts, CommandExecutor executor) {
        if (parts.length != 2) return formatError("Usage: DEL key");
        return formatOk(String.valueOf(executor.delete(parts[1])));
    }

    private String handleExists(String[] parts, CommandExecutor executor) {
        if (parts.length != 2) return formatError("Usage: EXISTS key");
        return executor.exists(parts[1]) ? "1" : "0";
    }

    private String handleDrop(CommandExecutor executor) {
        try {
            int count = executor.truncate();
            return count > 0 ? OK_RESPONSE : formatError("No keys to delete");
        } catch (UnsupportedOperationException e) {
            return formatError("DROP operation not supported");
        }
    }

    private String handleShutdown(CommandExecutor executor) {
        try {
            return formatOk(executor.shutdown());
        } catch (UnsupportedOperationException e) {
            return formatError("SHUTDOWN operation not supported");
        }
    }

    private String handlePing() {
        return "PONG";
    }

    @Override
    public String getHelpText() {
        return HELP_TEXT;
    }
}
