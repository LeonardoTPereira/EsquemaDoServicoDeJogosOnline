package oracleconnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.smartcardio.CommandAPDU;

public class MainAlternativo {

    public static void main(String args[]) {
        Statement stmt;
        ResultSet rs;
        Connection connection;
        PreparedStatement pstmt;
        
        try{
            //Conexão
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection =  DriverManager.getConnection("jdbc:oracle:thin:@grad.icmc.usp.br:15212:orcl", "7573621", "19sword99");
            
            System.out.println("Conexão estabelecida, GGWP");
            //Consulta
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT * FROM USUARIO");
            while (rs.next()) {
                System.out.println(rs.getString("CPF") + "-"
                        + rs.getString("NOME") + "-"
                        + rs.getString("EMAIL") + "-"
                        + rs.getString("NASCIMENTO") + "-"
                        + rs.getString("IDADE") + "-"
                        + rs.getString("PAIS") + "-"
                        );
            }
            
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    String s, insert, resp;
            
            while(true)
            {
                System.out.println("Digite SAIR para sair (duh!)");
                System.out.println("Digite nome da tabela:");
                s = bufferRead.readLine();
                //Condicao de saida
                if(s.compareTo("SAIR")==0)
                    break;
                //Validar tabela
                rs = stmt.executeQuery("SELECT * FROM USER_TABLES WHERE TABLE_NAME = '"+s+"'");
                if(!rs.next())
                {
                    System.out.println("Nome de tabela inválido");
                }
                else
                {
                    System.out.println("Nome de tabela válido");
                    insert = "INSERT INTO " + s + " VALUES(";
                    
                    rs = stmt.executeQuery("SELECT COLUMN_NAME, DATA_TYPE, DATA_LENGTH FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '"+s+"'");
                    while(rs.next())
                    {
                        System.out.println("Digite um valor pro atributo "+ rs.getString("COLUMN_NAME") + " de tipo " + rs.getString("DATA_TYPE") + " e de tamanho "+ rs.getString("DATA_LENGTH") + ":");
                        resp = bufferRead.readLine();
                        
                        if( resp.startsWith("empty_"))
                        {
                            insert += resp + ",";
                        }
                        else
                        {
                            insert += " '" + resp + "',";
                        }
                    }
                    insert = insert.substring(0, insert.length()-1) + ")";
                    
                    try
                    {
                        pstmt = connection.prepareStatement(insert);
                        pstmt.executeUpdate();
                        pstmt.close();
                        System.out.println("Sucesso");
                    }
                    catch( SQLException sqlerror)
                    {
                        System.out.println("Dados não inseridos");
                        System.out.println("Erro: "+sqlerror.toString());
                    }
                }
            }
            
            stmt.close();
            connection.close();
            
        } catch(Exception e) {
            System.out.println(e.toString());
        }
        
    }
}
