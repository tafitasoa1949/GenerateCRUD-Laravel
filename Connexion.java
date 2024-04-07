package shakai;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connexion {
     public static Connection getconnection(String database) throws Exception {
          try {
               Class.forName("org.postgresql.Driver");
               Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database, "postgres",
                         "admin");
               return con;
          } catch (SQLException e) {
               e.getMessage();
          }
          return null;
     }
}
