package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.players.HumanPlayer;
import edu.upc.epsevg.prop.hex.players.RandomPlayer;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.players.H_E_X_Player;

import edu.upc.epsevg.prop.hex.players.Hexmastery;

import javax.swing.SwingUtilities;

/**
 * Checkers: el joc de taula.
 * @author bernat
 */

//CLASSE PRINCIPAL 

public class Game {
        /**
     * @param args
     */
    public static void main(String[] args) { 
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                IPlayer player1 = new H_E_X_Player(2/*GB*/); //limit de RAM
                //IPlayer player1 = new RandomPlayer("Pep" /*nom*/); 
                //IPlayer player1 = new HumanPlayer("Human");

                //IPlayer player1 = new HumanPlayer("Human");
                //IPlayer player2 = new H_E_X_Player(2/*GB*/); //limit de RAM
                
                //IPlayer player2 = new Hexmastery("HexMastery", 8);              
                IPlayer player2 = new Hexmastery("HexMastery"); // usa IDS
                
                new Board(player1 , player2, 7 /*mida*/,  10 /*s*/, false); //player, mida de tauler i al principi fer.ho amb 9 i despres els tests fer-ho amb 11
                //el time runnow es de 10s , el que hem de fer es un MINIMAX on li hem de dir el numero denivells i quan fem aixo hem de fer un MINIMAX +ID  (Iterative detecting)  començo
                //baixant per un nivell despres amb 2 nivells i si tot OK torno a baixar un nivell més a 3 i em dona un timeout i es queda amb el minimax en mitat, quan passa
                //aaixo he de descartar aquesta iteracio no acabada pel timeout i agafar la anterior que va ser la que acabat abans i agafar la millor jugadaa de aquell nivell.
                
             }
        });
    }
}
