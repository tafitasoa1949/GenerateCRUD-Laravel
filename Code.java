package shakai;

import java.sql.*;

import java.io.*;

public class Code {
     public Colonne[] getAllColonne(String table, Connection con) throws Exception {
          Colonne[] list_colonne = null;
          try {
               DatabaseMetaData metaData = con.getMetaData();
               ResultSet rs = metaData.getColumns(null, null, table, null);
               int counteur = 0;
               while (rs.next()) {
                    counteur++;
               }
               list_colonne = new Colonne[counteur];
               rs = metaData.getColumns(null, null, table, null);
               int index = 0;
               while (rs.next()) {
                    Colonne colonne = new Colonne();
                    colonne.setNom(rs.getString("COLUMN_NAME"));
                    colonne.setType(rs.getString("TYPE_NAME"));
                    list_colonne[index] = colonne;
                    index++;
               }
          } catch (Exception e) {
               e.printStackTrace();
          }
          return list_colonne;
     }

     public static String changeMaj1erLettre(String lettre) {
          return lettre.substring(0, 1).toUpperCase() + lettre.substring(1);
     }

     public static String readBodyFile(String nomFichier) throws Exception {
          StringBuilder contenu = new StringBuilder();
          try (BufferedReader reader = new BufferedReader(new FileReader(nomFichier))) {
               String ligne;
               while ((ligne = reader.readLine()) != null) {
                    contenu.append(ligne).append("\n");
               }
          } catch (Exception e) {
               e.printStackTrace();
          }
          return contenu.toString();
     }

     public String replaceVariableModel(String table, Colonne[] list_Colonnes, String function_sequence, String contenu)
               throws Exception {
          contenu = contenu.replace("#model#", changeMaj1erLettre(table));
          contenu = contenu.replace("#table#", table);
          contenu = contenu.replace("#sequence_name#", function_sequence);
          StringBuilder attributs = new StringBuilder();
          StringBuilder var_atttributs = new StringBuilder();
          for (int i = 0; i < list_Colonnes.length; i++) {
               attributs.append("'").append(list_Colonnes[i].getNom()).append("'");
               if (i < list_Colonnes.length - 1) {
                    attributs.append(", "); // Ajouter une virgule après chaque attribut sauf le dernier
               }
               var_atttributs.append("\t\t").append("$").append(table).append("->").append(list_Colonnes[i].getNom())
                         .append(" = $data['").append(list_Colonnes[i].getNom()).append("'];\n");
          }
          contenu = contenu.replace("#attributs#", attributs.toString());
          contenu = contenu.replace("#var_atttributs#", var_atttributs.toString());
          return contenu;
     }

     public void generateModel(String url_projet, String table, String prefix, Connection con) throws Exception {
          try {
               Colonne[] list_Colonnes = this.getAllColonne(table, con);
               String nomFichierTemplate = "template/model.tftsoa";
               String contenuTemplate = readBodyFile(nomFichierTemplate);
               String contenuFinal = this.replaceVariableModel(table, list_Colonnes, prefix, contenuTemplate);
               // System.out.println(contenuFinal);
               String nomFichierFinal = changeMaj1erLettre(table) + ".php";
               String cheminDossierModels = url_projet + "/App/Models/";
               File dossierModels = new File(cheminDossierModels);
               dossierModels.mkdirs();
               String cheminFichierFinal = cheminDossierModels + nomFichierFinal;
               FileWriter writer = new FileWriter(cheminFichierFinal);
               writer.write(contenuFinal);
               writer.close();
          } catch (Exception e) {
               throw e;
          }
     }

     public String replaceVariableRequest(String table, Colonne[] list_Colonnes, String contenu)
               throws Exception {
          try {
               contenu = contenu.replace("#tableRequest#", changeMaj1erLettre(table) + "Request");
               StringBuilder attributs = new StringBuilder();
               StringBuilder colonneMessage = new StringBuilder();
               for (int i = 1; i < list_Colonnes.length; i++) {
                    attributs.append("\t\t").append("'").append(list_Colonnes[i].getNom()).append("' => 'required'");
                    colonneMessage.append("\t\t'").append(list_Colonnes[i].getNom()).append(".required' => 'Le champ ")
                              .append(list_Colonnes[i].getNom()).append(" est obligatoire.'");
                    if (i < list_Colonnes.length - 1) {
                         attributs.append(",\n");
                         colonneMessage.append(",\n");
                    } else {
                         attributs.append("\n");
                    }
               }
               contenu = contenu.replace("#attributs#", attributs.toString());
               contenu = contenu.replace("#colonne_message#", colonneMessage.toString());
          } catch (Exception e) {
               throw e;
          }
          return contenu.toString();
     }

     public String generateRequest(String url_projet, String table, Connection con) throws Exception {
          try {
               Colonne[] list_Colonnes = this.getAllColonne(table, con);
               String nomFichierTemplate = "template/request.tftsoa";
               String contenuTemplate = readBodyFile(nomFichierTemplate);
               String contenuFinal = this.replaceVariableRequest(table, list_Colonnes, contenuTemplate);
               // System.out.println(contenuFinal);
               String nomFichierFinal = changeMaj1erLettre(table) + "Request.php";
               String cheminDossierModels = url_projet + "App/Http/Requests/";
               File dossierModels = new File(cheminDossierModels);
               dossierModels.mkdirs();
               String cheminFichierFinal = cheminDossierModels + nomFichierFinal;
               FileWriter writer = new FileWriter(cheminFichierFinal);
               writer.write(contenuFinal);
               writer.close();
          } catch (Exception e) {
               throw e;
          }
          return changeMaj1erLettre(table) + "Request";
     }

     public String replaceVariableController(String table, Colonne[] list_Colonnes, String nom_request, String contenu)
               throws Exception {
          try {
               contenu = contenu.replace("#tablecontroller#", changeMaj1erLettre(table) + "Controller");
               contenu = contenu.replace("#table#", changeMaj1erLettre(table));
               contenu = contenu.replace("#classe#", table);
               contenu = contenu.replace("#request_name#", nom_request);
               StringBuilder attributs = new StringBuilder();
               StringBuilder atttributs_update = new StringBuilder();
               for (int i = 1; i < list_Colonnes.length; i++) {
                    attributs.append("\t\t").append("'").append(list_Colonnes[i].getNom()).append("' => $request->")
                              .append(list_Colonnes[i].getNom());
                    atttributs_update.append("").append("$").append(table).append("->")
                              .append(list_Colonnes[i].getNom()).append(" = ").append("$request->")
                              .append(list_Colonnes[i].getNom()).append(";");
                    if (i < list_Colonnes.length - 1) {
                         attributs.append(",\n");
                         atttributs_update.append("\n");
                    } else {
                         attributs.append("\n");
                    }
               }
               contenu = contenu.replace("#attributs#", attributs.toString());
               contenu = contenu.replace("#attributs_update#", atttributs_update.toString());
          } catch (Exception e) {
               throw e;
          }
          return contenu;
     }

     public String generateController(String url_projet, String table, String nom_request, Connection con)
               throws Exception {
          try {
               Colonne[] list_Colonnes = this.getAllColonne(table, con);
               String nomFichierTemplate = "template/controller.tftsoa";
               String contenuTemplate = readBodyFile(nomFichierTemplate);
               String contenuFinal = this.replaceVariableController(table, list_Colonnes, nom_request, contenuTemplate);
               // System.out.println(contenuFinal);
               String nomFichierFinal = changeMaj1erLettre(table) + "Controller.php";
               String cheminDossierModels = url_projet + "App/Http/Controllers/";
               File dossierModels = new File(cheminDossierModels);
               dossierModels.mkdirs();
               String cheminFichierFinal = cheminDossierModels + nomFichierFinal;
               FileWriter writer = new FileWriter(cheminFichierFinal);
               writer.write(contenuFinal);
               writer.close();
               return changeMaj1erLettre(table) + "Controller";
          } catch (Exception e) {
               throw e;
          }
     }

     public void generateRoute(String url_projet, String classe, String nomController) throws Exception {
          try {
               String nomFichierTemplate = "template/route.tftsoa";
               String contenu = readBodyFile(nomFichierTemplate);
               contenu = contenu.replace("#classe#", classe);
               contenu = contenu.replace("#nomController#", nomController);

               // Chemin du fichier de route
               String cheminRoute = url_projet + "routes/web.php";

               // Lecture du contenu existant du fichier de route
               String contenuExistant = readBodyFile(cheminRoute);

               // Ecriture du contenu existant suivi du nouveau contenu
               String nouveauContenu = contenuExistant + "//\n" + contenu;
               writeToFile(cheminRoute, nouveauContenu);

               // Lecture du contenu du fichier de route pour vérification
               String route = readBodyFile(cheminRoute);
          } catch (Exception e) {
               throw e;
          }
     }

     // Méthode pour écrire dans un fichier
     public void writeToFile(String chemin, String contenu) throws IOException {
          try (FileWriter writer = new FileWriter(chemin)) {
               writer.write(contenu);
          }
     }

     public Code(String url_projet, String table, String function_sequence, Connection con) throws Exception {
          try {
               this.generateModel(url_projet, table, function_sequence, con);
               String nomRequest = this.generateRequest(url_projet, table, con);
               String nomController = this.generateController(url_projet, table, nomRequest, con);
               // this.generateRoute(url_projet, table, nomController);
          } catch (Exception e) {
               e.printStackTrace();
          }
     }
}
