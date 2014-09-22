package esquemadejogos;

import esquemadejogos.JanelaPrincipal;

public class Main {

    public static void main(String[] args) {
        /*DBFuncionalidades db = new DBFuncionalidades();
        if (db.conectar()) {
            db.getDDL();
        }*/
        JanelaPrincipal j = new JanelaPrincipal();
        j.ExibeJanelaPrincipal();
        
    }
    
}
