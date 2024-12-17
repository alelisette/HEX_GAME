/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

/**
 *
 * @author Aleli
 */
public class MyStatus extends HexGameStatus{
    
    public MyStatus(int i) {
        super(i);
    }
    /* OPTIMITZACIONS AMB EL IDS + FALTA IMPLEMENTAR EL METODE HASHCODE QUE FACI EL METODE DE FUNCIO DE HASH
    //TAULER
    HexGameStatus status; //--->hash
    
        long[][] zobrist = new long[11][11][3];
        for (int i=0; i<11; ++i) {
            for(int j=0; j<11; j++) {
                for (int k=0; k < 3; k++) {
                    zobrist[i][j][k] = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                }
            }
        }
        long hash=0;
        
        int valor = status.getPos(x, y);
        hash ^= zobrist[x][y][valor+1];
    */
}
