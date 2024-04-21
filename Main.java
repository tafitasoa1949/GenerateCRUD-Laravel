package shakai;

import java.sql.Connection;

public class Main {
     public static void main(String[] args) throws Exception {
          String database = "tsakitsaky";
          String classe = "paiement";
          String function_sequence = "gen_payement_seq_id";
          Connection con = Connexion.getconnection(database);
          // String url_projet = "D:/ITU/L3/Semestre 6/Preparation Evaluation/eval/";
          String url_projet = "D:/Personnel/Projet/Tsakitsaky/";
          new Code(url_projet, classe, function_sequence, con);
     }
}