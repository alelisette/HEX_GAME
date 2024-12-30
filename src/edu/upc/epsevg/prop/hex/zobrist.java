/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Aleli
 */

/**
 * Clase para calcular el hash de un estado del tablero en el juego HEX usando Zobrist Hashing.
 */
public class zobrist {

    private final long[][][] zobrist; // Tabla de valores aleatorios
    private final SecureRandom random = new SecureRandom();

    /**
     * Constructor: inicializa la tabla de Zobrist con valores aleatorios.
     */
    public zobrist() {
        this.zobrist = new long[11][11][3]; // Tablero 11x11 con 3 estados posibles: vacío, jugador 1, jugador -1
        initTable();
    }

    /**
     * Inicializa la tabla Zobrist con valores aleatorios únicos para cada posición y estado.
     */
    private void initTable() {
        for (int i = 0; i < 11; ++i) {
            for (int j = 0; j < 11; ++j) {
                for (int k = 0; k < 3; ++k) { // 3 estados: vacío, jugador 1, jugador -1
                    zobrist[i][j][k] = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                }
            }
        }
    }

    /**
     * Calcula el hash completo de un estado del tablero.
     *
     * @param gs El estado del tablero actual (HexGameStatus).
     * @return Un valor hash único que representa el estado del tablero.
     */
    public long calculateFullHash(HexGameStatus gs) {
        long hash = 0;

        // Itera sobre todas las celdas del tablero
        for (int i = 0; i < gs.getSize(); i++) {
            for (int j = 0; j < gs.getSize(); j++) {
                int value = gs.getPos(i, j); // Obtiene el estado de la celda (0: vacío, 1: jugador blanco 1, -1: jugador negro -1)
                //if (value == 1 || value == -1 || value == 0) { // Solo considera celdas 
                    hash ^= zobrist[i][j][value+1]; // Aplica XOR con el valor correspondiente de la tabla Zobrist
                //}
            }
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

        return hash;
    }
}
