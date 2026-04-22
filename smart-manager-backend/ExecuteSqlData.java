import java.sql.*;
import java.nio.file.*;
import java.util.stream.Collectors;

public class ExecuteSqlData {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/smart_manager_db?useSSL=false&serverTimezone=UTC&allowMultiQueries=true";
        String user = "root";
        String password = ""; // 根据之前的 context，密码可能为空或者是 123456

        try (Connection conn = DriverManager.getConnection(url, user, password);
                Statement stmt = conn.createStatement()) {

            String sqlPath = "e:/datacenter2/smart-manager-backend/sql/test_data.sql";
            String sql = Files.readAllLines(Paths.get(sqlPath))
                    .stream()
                    .filter(line -> !line.trim().startsWith("--"))
                    .collect(Collectors.joining("\n"));

            // 拆分 SQL，因为有些驱动不支持 allowMultiQueries 同时执行很多语句
            String[] queries = sql.split(";");
            int count = 0;
            for (String q : queries) {
                if (!q.trim().isEmpty()) {
                    stmt.execute(q);
                    count++;
                }
            }
            System.out.println("Success: Executed " + count + " queries from test_data.sql");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
