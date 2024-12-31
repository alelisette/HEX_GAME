/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

/**
 * Classe que implementa la heurística utilitzada per avaluar l'estat d'un joc Hex.
 * Proporciona funcions per calcular i obtenir el valor heurístic basat en diversos factors.
 * 
 * @author AleLisette
 */
public class Heuristica {
    /**
     * Valor heurístic calculat per l'últim estat del joc.
     */
    private static double _heuristica;
    
     /**
     * Obté el valor heurístic de l'estat actual del joc per al jugador indicat.
     * 
     * @param hgs Estat actual del joc Hex.
     * @param jugador Tipus de jugador pel qual es calcula la heurística.
     * @return El valor heurístic calculat.
     */
    static public double getHeuristica(HexGameStatus hgs, PlayerType jugador) {
        _heuristica = calculaHeuristica(hgs, jugador);
        return _heuristica;
    }
    
    /**
     * Calcula el valor heurístic de l'estat del joc Hex per al jugador especificat.
     * Considera distàncies mínimes i mitjanes en funció de Dijkstra per al jugador
     * i el seu rival.
     * 
     * @param hgs Estat actual del joc Hex.
     * @param jugador Tipus de jugador pel qual es calcula la heurística.
     * @return El valor heurístic calculat.
     */
    static private double calculaHeuristica(HexGameStatus hgs, PlayerType jugador) {
        PlayerType rival = PlayerType.opposite(jugador);

        int d_jugador = Dijkstra.calculaDistanciaMinima(hgs, jugador, jugador);
        double m_jugador = Dijkstra.calculaMitjanaDistancies(hgs, jugador);
        
        int d_rival = Dijkstra.calculaDistanciaMinima(hgs, rival, jugador);
        double m_rival = Dijkstra.calculaMitjanaDistancies(hgs, rival);

        double d = d_rival - d_jugador;
        double m = m_rival - m_jugador;
        
        double h = d;
        if (d == 0) h = m;

        return h;
    }
} 