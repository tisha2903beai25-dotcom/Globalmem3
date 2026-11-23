package safety;

import java.util.*;
import java.io.*;
import java.sql.*;

public class SafetyAuditLogger implements Runnable {

    private Queue<String> eventQueue = new ArrayDeque<>();

    private String filePath;
    private Connection connection;

    private boolean running = true;

    // Constructor
    public SafetyAuditLogger(String filePath, String dbUrl)
            throws Exception {

        this.filePath = filePath;

        File file = new File(filePath);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        file.createNewFile();

        // JDBC connection
        connection = DriverManager.getConnection(dbUrl);

        createTable();
    }

    // Create database table
    private void createTable() throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS safety_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    event TEXT NOT NULL
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    // Producer: Submit event
    public synchronized void submitEvent(String event) {

        eventQueue.add(event);

        System.out.println("Event added to queue: " + event);

        notifyAll();
    }

    // Consumer: Process events
    @Override
    public void run() {

        while (true) {

            String event;

            synchronized (this) {

                while (eventQueue.isEmpty() && running) {

                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                if (eventQueue.isEmpty() && !running) {
                    break;
                }

                event = eventQueue.poll();
            }

            try {
                writeToFile(event);
                insertIntoDatabase(event);

            } catch (Exception e) {
                System.out.println("Logging error: " + e.getMessage());
            }
        }
    }

    // Character stream: Write file
    public void writeToFile(String event) throws IOException {

        try (FileWriter writer = new FileWriter(filePath, true)) {

            writer.write(event + "\n");
        }
    }

    // JDBC: Insert event
    public void insertIntoDatabase(String event)
            throws SQLException {

        String sql = "INSERT INTO safety_events(event) VALUES(?)";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, event);

            ps.executeUpdate();
        }
    }

    // JDBC: Read records
    public synchronized List<String> readAllEvents()
            throws SQLException {

        List<String> records = new ArrayList<>();

        String sql = "SELECT id, event FROM safety_events";

        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String event = rs.getString("event");

                records.add(id + ": " + event);
            }
        }

        return records;
    }

    // JDBC: Update record
    public synchronized void updateEvent(int id, String newEvent)
            throws SQLException {

        String sql = "UPDATE safety_events SET event = ? WHERE id = ?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setString(1, newEvent);
            ps.setInt(2, id);

            ps.executeUpdate();
        }
    }

    // JDBC: Delete record
    public synchronized void deleteEvent(int id)
            throws SQLException {

        String sql = "DELETE FROM safety_events WHERE id = ?";

        try (PreparedStatement ps =
                     connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();
        }
    }

    // Character stream: Read file
    public void displayFileLogs() throws IOException {

        System.out.println("\n--- FILE LOGS ---");

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    // Byte stream: Backup file
    public void createBackup(String backupPath)
            throws IOException {

        try (FileInputStream input =
                     new FileInputStream(filePath);

             FileOutputStream output =
                     new FileOutputStream(backupPath)) {

            byte[] buffer = new byte[1024];

            int bytesRead;

            while ((bytesRead = input.read(buffer)) != -1) {

                output.write(buffer, 0, bytesRead);
            }
        }
    }

    // StreamTokenizer: Count words
    public int countWords() throws IOException {

        int count = 0;

        try (FileReader reader = new FileReader(filePath)) {

            StreamTokenizer tokenizer =
                    new StreamTokenizer(reader);

            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {

                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    count++;
                }
            }
        }

        return count;
    }

    // Stop producer-consumer thread
    public synchronized void shutdown() {

        running = false;

        notifyAll();
    }

    // Close database
    public void close() throws SQLException {

        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
