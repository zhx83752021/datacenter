import java.sql.*;
import java.nio.file.*;
import java.util.stream.Collectors;
import java.util.List;

public class SqlRunner {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java SqlRunner <sql_file_path>");
            return;
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/smart_manager_db?useSSL=false&serverTimezone=Asia/Shanghai&allowMultiQueries=true&characterEncoding=utf-8";
        String user = "root";
        String password = "password";

        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {

            Path path = Paths.get(args[0]);
            if (!Files.exists(path)) {
                System.out.println("File not found: " + args[0]);
                return;
            }

            String sql = Files.readAllLines(path)
                    .stream()
                    .filter(line -> !line.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));

            String[] queries = sql.split(";");
            int count = 0;
            for (String q : queries) {
                if (!q.trim().isEmpty()) {
                    try {
                        if (q.trim().toUpperCase().startsWith("SELECT")) {
                            ResultSet rs = stmt.executeQuery(q);
                            ResultSetMetaData meta = rs.getMetaData();
                            int colCount = meta.getColumnCount();
                            while (rs.next()) {
                                for (int i = 1; i <= colCount; i++) {
                                    System.out.print(rs.getString(i) + " | ");
                                }
                                System.out.println();
                            }
                        } else {
                            stmt.execute(q);
                            count++;
                        }
                    } catch (Exception e) {
                        System.out.println("Error in query: " + q);
                        System.out.println(e.getMessage());
                    }
                }
            }
            System.out.println("Finished executing SQL commands.");
        } catch (Exception e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
