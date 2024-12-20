/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AleLisette
 */

/**
 * Implementación del jugador Hexmastery usando el algoritmo MIN-MAX con poda alfa-beta.
 * @author Alelisette
 */
public class Hexmastery implements IPlayer , IAuto {

    private static final int INFINIT = Integer.MAX_VALUE;
    private static final int MENYS_INFINIT = Integer.MIN_VALUE;

    private String _name; // Nombre del jugador
    private int _profMax; // Profundidad máxima del árbol de búsqueda
    long _nodesExplorats;
    private PlayerType _myplayer;
    private boolean _istimeout;
    
    //constructor minimax 
    public Hexmastery(String name, int profunditatMaxima) {
        this._name = name;
        this._profMax = profunditatMaxima;
    }

    //constructor minimax with IDS
    public Hexmastery(String name) {
        this._name = name;
        this._profMax = 1;
        this._istimeout = false;
    }
    
    @Override
    public PlayerMove move(HexGameStatus s) {
        _myplayer = s.getCurrentPlayer();
        Point millormov = null; //Inicialitzem amb el millormoviment a null
        _nodesExplorats = 0;
        int h_actual = MENYS_INFINIT;
        int h_alpha = MENYS_INFINIT;
        int h_beta = INFINIT;        
        List<Point> possiblesMovs = obtePossiblesMoviments(s);
        _istimeout = false;

        while (!_istimeout) {
            for (Point mov : possiblesMovs) {
                HexGameStatus AuxEstat = new HexGameStatus(s);
                AuxEstat.placeStone(mov);
                
                int h_minima = MIN(AuxEstat, _profMax-1, s.getCurrentPlayerColor(), h_alpha, h_beta);
                if (h_actual < h_minima) {
                    h_actual = h_minima;
                    millormov = mov;
                }
            }
              
            ++_profMax;
        }
        
        if (_istimeout) {
        // Devuelve el último nodo completamente evaluado si se detectó timeout
            return new PlayerMove(millormov, _nodesExplorats, _profMax, SearchType.MINIMAX);
        }
        
        return new PlayerMove(millormov, _nodesExplorats, _profMax, SearchType.MINIMAX);
    }

    @Override
    public void timeout() {
        //System.out.println("Temps esgontant..");
        _istimeout = true;
    }

    @Override
    public String getName() {
        return "HexMastery(" + _name + ")";
    }

    /**
     * Función MIN del algoritmo Minimax con poda alfa-beta.
     * @param s Estado actual del juego
     * @param profunditat Profundidad actual
     * @param colorAct Color del jugador actual
     * @param _alpha Valor alfa para poda
     * @param _beta Valor beta para poda
     * @return El valor mínimo posible del estado
     */
    private int MIN(HexGameStatus s, int profunditat, int colorAct, int _alpha, int _beta) {
        if (profunditat == 0 || _istimeout ) {
            ++_nodesExplorats;
            return 0;
        }
        
        if(s.isGameOver()) {
            if (s.GetWinner()== _myplayer) {
                return INFINIT;
            } else {
                return MENYS_INFINIT;
            } 
        }
        
        int millorvalor = INFINIT;
        List<Point> possiblesMoviments = obtePossiblesMoviments(s);
        
        for (Point mov : possiblesMoviments) {
            HexGameStatus nouEstat = new HexGameStatus(s);
            nouEstat.placeStone(mov);
            
            int ha = MAX(nouEstat, profunditat-1, colorAct, _alpha, _beta); 
            millorvalor = Math.min(millorvalor, ha);
            _beta = Math.min(millorvalor, _beta);
            
            if (_beta <= _alpha) { // PODA
                break;
            }
        }
        
        return millorvalor;
    }

    /**
     * Función MAX del algoritmo Minimax con poda alfa-beta.
     * @param s Estado actual del juego
     * @param profunditat Profundidad actual
     * @param colorAct Color del jugador actual
     * @param _alpha Valor alfa para poda
     * @param _beta Valor beta para poda
     * @return El valor máximo posible del estado
     */
    private int MAX(HexGameStatus s, int profunditat, int colorAct, int _alpha, int _beta) {
        if (profunditat == 0 || _istimeout) {
            ++_nodesExplorats;
            return 0;
        }    //return heuristica;
        
        if (s.isGameOver()) {
            if (s.GetWinner() == _myplayer) {
                return INFINIT;
            } else {
                return MENYS_INFINIT;
            }   
        }
        
        int millorvalor = MENYS_INFINIT;
        List<Point> possiblesMoviments = obtePossiblesMoviments(s);
        
        for (Point mov : possiblesMoviments) {
            HexGameStatus nouEstat = new HexGameStatus(s);
            nouEstat.placeStone(mov);
            
            int ha = MIN(nouEstat, profunditat-1, colorAct, _alpha, _beta); 
            millorvalor = Math.max(millorvalor, ha);
            _alpha = Math.max(millorvalor, _alpha);
            
            if (_beta <= _alpha) { // PODA
                break;
            }
        }
        
        return millorvalor;
    }
    
    private List<Point> obtePossiblesMoviments(HexGameStatus s) {
        List<Point> moviments = new ArrayList<>();
        for (int i = 0; i < s.getSize(); ++i) {
            for (int j = 0; j < s.getSize(); ++j) {
                if (s.getPos(i, j) == 0) moviments.add(new Point(i,j));
            }
        }
        return moviments;
    }
}