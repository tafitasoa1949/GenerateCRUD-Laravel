package shakai;

import java.sql.Connection;

import shakai.Code;
import shakai.Connexion;

public class Main {
     public static void main(String[] args) throws Exception {
          String database = "eval";
          String classe = "test";
          String prefix = "TEST";
          Connection con = Connexion.getconnection(database);
          String url_projet = "D:/ITU/L3/Semestre 6/Preparation Evaluation/eval/";
          new Code(url_projet, classe, prefix, con);
     }
}