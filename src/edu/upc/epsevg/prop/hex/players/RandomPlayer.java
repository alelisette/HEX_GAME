package edu.upc.epsevg.prop.hex.players;


import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Jugador aleatori
 * @author bernat
 */
public class RandomPlayer implements IPlayer, IAuto { //RANDOM PLAYER HEM DE IMPLEMENTAR EL IPlayer per crear un nou jugador

    private String name;
    

    public RandomPlayer(String name) {
        this.name = name;
    }

    @Override
    public void timeout() { //nomes posar un bolea = true
        // Nothing to do! I'm so fast, I never timeout 8-)
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(HexGameStatus s) { //en aquest GameStatus es el status i hem de implementar

        int freeCells = 0;
        for(int i=0;i<s.getSize();i++){ //sempre fem un getsize
          for(int k=0;k<s.getSize();k++){
              if(s.getPos(i, k)==0) {
                  freeCells++; //incrementar el comptador de caselles buides
              }
            }  
        }

        if(freeCells==0) return null;        
        
        Random rand = new Random();
        int q = rand.nextInt(freeCells);
        freeCells = 0;
        for(  int i=0;i<s.getSize();i++){
          for(int k=0;k<s.getSize();k++){
              if(s.getPos(i, k)==0){
                  if(freeCells==q) return new PlayerMove( new Point(i,k), 0L, 0, SearchType.RANDOM); //fila i,k??
                  //Hem de implementar un player amb MINIMAX AMB PODA ALFA-BETA i un altre amb MINIMAX AMB IDS
                  //per optimitzar el IDS cada cop que recalculem una jugada millor hem de possar-la al inici com a primer fill de cada jugada
                  //es molt bo y la poda millora molt perque es optimitza i es facil i es cost constant fer ho a cada nivell
                  freeCells++;
              }
            }  
        }
        return null;        
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public String getName() {
        return "Random(" + name + ")";
    }

}
