package esquemadejogos;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class JanelaPrincipal {

    JFrame j;
    JPanel pPainelDeCima;
    JPanel pPainelDeBaixo;
    JComboBox jc;
    JTextArea jtAreaDeStatus;
    JTabbedPane tabbedPane;
    JScrollPane pPainelDeExibicaoDeDados = null;
    JTable jt;
    JTable selectTable;
    JPanel pPainelDeInsecaoDeDados;
    DBFuncionalidades bd;
    

    private void createSelectTable(String table)
    {
        if(selectTable != null)
            pPainelDeExibicaoDeDados.remove(selectTable);
        selectTable = bd.preencherTableSelect(table);
        pPainelDeExibicaoDeDados.setViewportView(selectTable);
        //Metodo anterior, nao funciona com ScrollPaneLayout por algum motivo
        //pPainelDeExibicaoDeDados.add(selectTable);
    }
    
    public void ExibeJanelaPrincipal() {
        /*Janela*/
        j = new JFrame("ICMC-USP - SCC0541 - Pratica 5");
        j.setSize(700, 500);
        j.setLayout(new BorderLayout());
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*Painel da parte superior (north) - com combobox e outras informações*/
        pPainelDeCima = new JPanel();
        j.add(pPainelDeCima, BorderLayout.NORTH);
        jc = new JComboBox();
        pPainelDeCima.add(jc);

        /*Painel da parte inferior (south) - com área de status*/
        pPainelDeBaixo = new JPanel();
        j.add(pPainelDeBaixo, BorderLayout.SOUTH);
        jtAreaDeStatus = new JTextArea();
        jtAreaDeStatus.setText("Aqui é sua área de status");
        pPainelDeBaixo.add(jtAreaDeStatus);

        /*Painel tabulado na parte central (CENTER)*/
        tabbedPane = new JTabbedPane();
        j.add(tabbedPane, BorderLayout.CENTER);

        /*Tab de exibicao*/
        /*Mudada para JScrollPane por JPanel nao exibir o titulo das JTables por algum motivo*/
        pPainelDeExibicaoDeDados = new JScrollPane();
        pPainelDeExibicaoDeDados.setLayout(new ScrollPaneLayout());
        tabbedPane.add(pPainelDeExibicaoDeDados, "Exibição");
        

        /*Tab de inserção*/
        pPainelDeInsecaoDeDados = new JPanel();
        int nColunas = 3;
        pPainelDeInsecaoDeDados.setLayout(new GridLayout(nColunas, 2));
        /*
         * Encontrar tuplas da tabela
         * 
         * for(int i = 0; i < nColunas; i++)
         * {
         *   pPainelDeInsecaoDeDados.add(new JLabel(nomeColuna[i]));
         *   pPainelDeInsecaoDeDados.add(new JTextField("Digite aqui"));
         * }
         * 
         * Fazer comando de insert para o oracle
         * Fazer combobox para todos os Checks "IN" algum dominio, com todas as opcoes presentes
         * Fazer combobox para chaves estrangeiras com todas as possíveis chaves (buscar dado das tabelas com a FK)
         */
        pPainelDeInsecaoDeDados.add(new JLabel("Coluna1"));
        pPainelDeInsecaoDeDados.add(new JTextField("Digite aqui"));
        pPainelDeInsecaoDeDados.add(new JLabel("Coluna2"));
        pPainelDeInsecaoDeDados.add(new JTextField("Digite aqui"));
        pPainelDeInsecaoDeDados.add(new JLabel("Coluna3"));
        pPainelDeInsecaoDeDados.add(new JTextField("Digite aqui"));
        tabbedPane.add(pPainelDeInsecaoDeDados, "Inserção");

        this.DefineEventos();
        j.setVisible(true);

        bd = new DBFuncionalidades(jtAreaDeStatus);
        if (bd.conectar()) {
            bd.pegarNomesDeTabelas(jc);
        }
        /*Cria a JTable com os dados resultantes do select na tabela escolhida*/
        createSelectTable((String) jc.getItemAt(0));
    }

    private void DefineEventos() {
        jc.addActionListener(
                new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox jcTemp = (JComboBox) e.getSource();
                String metadata;
                /*Cria string com metadados sobre a tabela*/
                metadata = (String) jcTemp.getSelectedItem() + "\n";
                metadata += bd.getMetaData((String) jcTemp.getSelectedItem());
                jtAreaDeStatus.setText(metadata);
                
                /*Cria a JTable com os dados resultantes do select na tabela escolhida*/
                createSelectTable((String) jcTemp.getSelectedItem());
                bd.getDDL();
            }
        });
    }
}
