/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esquemadejogos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;

/**
 *
 * @author junio
 */
public class DBFuncionalidades {
    Connection connection;
    Statement stmt;
    Statement stmt2;
    ResultSet rs;
    ResultSet rsColunms;
    ResultSet rsContent;
    JTextArea jtAreaDeStatus;
    ArrayList<String> columnNames;
    
    public DBFuncionalidades(JTextArea jtaTextArea){
        jtAreaDeStatus = jtaTextArea;
    }
    
    public boolean conectar(){       
        /*
         * Fazer dinamicamente isto para qualquer usuario
         */
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        
        System.out.println("Enter your password: ");
        String password = scanner.nextLine();
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@grad.icmc.usp.br:15212:orcl",
                    username,
                    password);
            return true;
        } catch (ClassNotFoundException ex) {
            jtAreaDeStatus.setText("Problema: verifique o driver do banco de dados");
        } catch(SQLException ex){
            jtAreaDeStatus.setText("Problema: verifique seu usuário e senha");
        }
        return false;
    }
    
    public void pegarNomesDeTabelas(JComboBox jc){
        String s = "";
        try {
            s = "SELECT table_name FROM user_tables";
            stmt = connection.createStatement();
            rs = stmt.executeQuery(s);
            while (rs.next()) {
                jc.addItem(rs.getString("table_name"));
            }
            stmt.close();
        } catch (SQLException ex) {
            jtAreaDeStatus.setText("Erro na consulta: \"" + s + "\"");
        }        
    }
    
    /*Cria a JTable com os dados resultantes do select na tabela escolhida*/
    /*Por enquanto, soh imprime os dados na tela*/
    public JTable preencherTableSelect( String tableName )
    {
        
        JTable jtSelect = null;
        columnNames = new ArrayList();
        System.out.println(tableName);
        try{
            /*SELEÇÃO*/
            stmt = connection.createStatement();
            stmt2 = connection.createStatement();
            rsContent = stmt.executeQuery("SELECT * FROM "+tableName);
            rsColunms = stmt2.executeQuery("SELECT COLUMN_NAME from USER_TAB_COLUMNS where table_name = '" + tableName + "'");
            while (rsColunms.next()) {
                System.out.print(rsColunms.getString("COLUMN_NAME")+ " - ");
                columnNames.add(rsColunms.getString("COLUMN_NAME"));
            }
            System.out.println("");
            while (rsContent.next()) {
                for(int i = 0; i < columnNames.size(); i++)
                {
                    System.out.print(rsContent.getString(columnNames.get(i))+" - ");
                }
                System.out.println("");
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return jtSelect;
    }
    
    public void exibeDados(JTable tATable, String sTableName){
        
    }
    //public void preencheComboBoxComRestricoesDeCheck
    //public void preencheComboBoxComValoresReferenciados
    //
}
