/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Dijkstra;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
/**
 *
 * @author Alelisette
 */

public class Heuristica {
    //heuristica = h1 + h2 + h3 + h4
    private static int _heuristica;
    
    static public int getHeuristica(HexGameStatus hgs, PlayerType jugador) {
        _heuristica = calculateHeuristica(hgs, jugador);
        return _heuristica;
    }
    
    static private int calculateHeuristica(HexGameStatus hgs, PlayerType jugador) {
        int h = calculaDiferencia(hgs, jugador);
        return h;
    }
    
    static private int calculaDiferencia(HexGameStatus hgs, PlayerType jugador) {
        Dijkstra d = new Dijkstra();
        
        int d_jugador = Dijkstra.calculaDistanciaMinima(hgs, jugador);
        int d_rival = Dijkstra.calculaDistanciaMinima(hgs, PlayerType.opposite(jugador));
        
        int diferencia = d_rival - d_jugador;
        return diferencia;
    }
     
} 