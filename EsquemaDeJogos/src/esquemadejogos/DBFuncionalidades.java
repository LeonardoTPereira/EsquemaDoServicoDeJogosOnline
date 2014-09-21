/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package esquemadejogos;

import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author junio
 */
public class DBFuncionalidades {
    Connection connection;
    Statement stmt;
    Statement stmt2;
    Statement stmt3;
    ResultSet rs;
    ResultSet rsColunms;
    ResultSet rsContent;
    ResultSet rsPK;
    JTextArea jtAreaDeStatus;
    Vector<String> columnNames;
    Vector<String> pkColumns;
    Vector<Vector<String>> tableData;
    
    public DBFuncionalidades(JTextArea jtaTextArea){
        jtAreaDeStatus = jtaTextArea;
    }

    public DBFuncionalidades() {
        
    }
    
    public void getDDL()
    {
        Statement s;
        ResultSet rsddl;
        try
        {
            System.out.println("ddl:");
            s = connection.createStatement();
            /*
            s.executeQuery("EXECUTE dbms_metadata.set_transform_param(dbms_metadata.session_transform,'STORAGE',false");
            s.executeQuery("EXECUTE dbms_metadata.set_transform_param(dbms_metadata.session_transform,'SEGMENT_ATTRIBUTES',false");
            s.executeQuery("EXECUTE dbms_metadata.set_transform_param(dbms_metadata.session_transform,'SQLTERMINATOR',true)");*/
            rsddl = s.executeQuery("select DBMS_METADATA.GET_DDL(object_type,object_name) from user_objects where object_type = 'TABLE'");
            while(rsddl.next()){
                System.out.println(rsddl.getString(1));
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    
    public String getMetaData(String tableName)
    {
        String metaData = new String();
        Statement st;
        ResultSet rsmd;
        try
        {
            st = connection.createStatement();
            /*Search for the metadata and adds it to the string to be shown in the lower part of the window*/
            rsmd = st.executeQuery("SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH, NUM_NULLS, NUM_DISTINCT, "
                    + "DATA_DEFAULT, COLUMN_ID, NULLABLE from USER_TAB_COLUMNS where table_name = '" + tableName + "'");
                while (rsmd.next()) {
                    metaData += "ID ="+rsmd.getString("COLUMN_ID")+" |NAME ="+rsmd.getString("COLUMN_NAME") 
                        +" |TYPE ="+rsmd.getString("DATA_TYPE")+" |LENGTH ="+rsmd.getString("DATA_LENGTH")+" |NULL? ="+rsmd.getString("NULLABLE")
                            + " |DEFAULT =" + rsmd.getString("DATA_DEFAULT")+" |#NULLS = " + rsmd.getString("NUM_NULLS")+" |#DISTINCT = "+rsmd.getString("NUM_DISTINCT")+"\n";
                }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return metaData;
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
        columnNames = new Vector();
        tableData = new Vector<>();
        pkColumns = new Vector();
        System.out.println(tableName);
        try{
            /*SELEÇÃO*/
            stmt = connection.createStatement();
            stmt2 = connection.createStatement();
            stmt3 = connection.createStatement();
            rsContent = stmt.executeQuery("SELECT * FROM "+tableName);
            
            /*Return the name of the columns that are primary keys*/
            rsPK = stmt3.executeQuery("SELECT cols.table_name, cols.column_name, cols.position, cons.status, cons.owner "
                    + "FROM all_constraints cons, all_cons_columns cols "
                    + "WHERE cols.table_name = '"+tableName+"' "
                    + "AND cons.constraint_type = 'P' "
                    + "AND cons.constraint_name = cols.constraint_name "
                    + "AND cons.owner = cols.owner "
                    + "ORDER BY cols.table_name, cols.position");
            /*Return the column names from the table*/
            rsColunms = stmt2.executeQuery("SELECT COLUMN_NAME from USER_TAB_COLUMNS where table_name = '" + tableName + "'");
            /*Save the results in vectors*/
            while (rsPK.next()) {
                pkColumns.add(rsPK.getString("COLUMN_NAME"));
            }
            while (rsColunms.next()) {
                columnNames.add(rsColunms.getString("COLUMN_NAME"));
            }
            int j = 0;
            while (rsContent.next()) {
                tableData.add(new Vector());
                for(int i = 0; i < columnNames.size(); i++)
                {
                    tableData.get(j).add(rsContent.getString(columnNames.get(i)));
                }
                j++;
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        /*Creates a new JTable with the information from the select*/
        jtSelect = new JTable(tableData, columnNames){
            /*This method changes the background color from the columns that are primary keys*/
            public Component prepareRenderer( TableCellRenderer r, int rw, int col)
            {
            Component c = super.prepareRenderer(r, rw, col);
            c.setBackground(Color.WHITE);
            if(pkColumns.contains(columnNames.get(col)))
            {
                c.setBackground(Color.GREEN);
            }  
            return c;
            }
        };
        return jtSelect;
    }
    
    public void exibeDados(JTable tATable, String sTableName){
        
    }
    //public void preencheComboBoxComRestricoesDeCheck
    //public void preencheComboBoxComValoresReferenciados
    //
}
