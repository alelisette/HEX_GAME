/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Classe per gestionar el sistema de Zobrist Hashing utilitzat per emmagatzemar i calcular
 * valors hash únics per a un estat del tauler en un joc de Hex.
 * 
 * @author AleLisette
 */
public class zobrist {
    /** Taula de Zobrist amb valors aleatoris per cada posició i estat possible. */
    private final long[][][] zobrist;
    
    /** La mida del tauler de joc. */
    int _mida;
    
    /**
     * Constructor de la classe zobrist.
     * Inicialitza la taula de Zobrist amb valors aleatoris.
     * 
     * @param mida La mida del tauler (número de files i columnes).
     */
    public zobrist(int mida) {
        zobrist = new long[mida][mida][3]; // Tauler amb 3 possibles estats: buit, jugador 1 (blanc), jugador -1 (negre)
        _mida = mida;
        initTable(); // Inicialitza la taula amb valors aleatoris
    }

    /**
     * Inicialitza la taula Zobrist amb valors aleatoris únics per a cada posició i estat.
     * El valor serà un nombre aleatori generat per a cada casella i cada jugador.
     */
    private void initTable() {
        for (int i = 0; i < _mida; ++i) {
            for (int j = 0; j < _mida; ++j) {
                for (int k = 0; k < 3; ++k) { 
                    zobrist[i][j][k] = ThreadLocalRandom.current().nextLong(Long.MAX_VALUE);
                }
            }
        }
    }

    /**
     * Calcula el hash complet d'un estat del tauler a partir de la taula de Zobrist.
     * 
     * @param gs L'estat actual del tauler (HexGameStatus).
     * @return Un valor hash únic que representa l'estat del tauler.
     */
    public long calculateFullHash(HexGameStatus gs) {
        long hash = 0;

        for (int i = 0; i < gs.getSize(); i++) {
            for (int j = 0; j < gs.getSize(); j++) {
                int value = gs.getPos(i, j); 
                hash ^= zobrist[i][j][value+1]; // Aplica l'operació XOR amb el valor de la taula Zobrist corresponent
            }
        }
        
        return hash;
    }
    
    /**
     * Actualitza el hash d'un estat del tauler després d'un moviment.
     * 
     * @param currentHash El hash actual de l'estat del tauler.
     * @param x La coordenada x de la cel·la on es realitza el moviment.
     * @param y La coordenada y de la cel·la on es realitza el moviment.
     * @param oldValue El valor antic de la cel·la abans del moviment.
     * @param newValue El nou valor de la cel·la després del moviment.
     * @return El nou hash després de l'actualització.
     */
    public long updateHash(long currentHash, int x, int y, int oldValue, int newValue) {
        if (oldValue != 0) {
            currentHash ^= zobrist[x][y][oldValue + 1];
        }
        if (newValue != 0) {
            currentHash ^= zobrist[x][y][newValue + 1];
        }
        return currentHash;
    }
}