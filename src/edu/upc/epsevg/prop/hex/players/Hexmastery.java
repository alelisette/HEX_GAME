/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Dijkstra;
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

    private static final double INFINIT = Integer.MAX_VALUE;
    private static final double MENYS_INFINIT = Integer.MIN_VALUE;

    private String _name; // Nombre del jugador
    private int _profMax; // Profundidad máxima del árbol de búsqueda
    long _nodesExplorats;
    private PlayerType _myplayer;
    private boolean _istimeout;
    private final boolean  _ids;
    
    
    //constructor minimax 
    public Hexmastery(String name, int profunditatMaxima) {
        this._name = name;
        this._profMax = profunditatMaxima;
        this._ids = false;
    }

    //constructor minimax with IDS
    public Hexmastery(String name) {
        this._name = name;
        this._profMax = 1;
        this._istimeout = false;
        this._ids = true;
    }
    
    @Override
    public PlayerMove move(HexGameStatus s) {
        _nodesExplorats = 0;
        _myplayer = s.getCurrentPlayer();
        Point millormovTotal = null; //Inicialitzem amb el millormoviment a null
        double h_total = MENYS_INFINIT;
        //double h_alpha = MENYS_INFINIT;
        //double h_beta = INFINIT;        
        List<Point> possiblesMovs = obtePossiblesMoviments(s);
        
 
        
        if (_ids) {
            _istimeout = false;
            _profMax = 1;
            
            while (!_istimeout) {
                Point millormov = null;
                double h_actual = MENYS_INFINIT;
                // int profMax_actual = 1;

                for (Point mov : possiblesMovs) {
                    HexGameStatus AuxEstat = new HexGameStatus(s);
                    AuxEstat.placeStone(mov);

                    double h_minima = MIN(AuxEstat, _profMax-1, s.getCurrentPlayerColor(), MENYS_INFINIT, INFINIT);
                    if (h_actual < h_minima) {
                        h_actual = h_minima;
                        millormov = mov;
                    }
                }
                
                if (h_actual == INFINIT) return new PlayerMove(millormov, _nodesExplorats, _profMax, SearchType.MINIMAX_IDS);
                    
                if (!_istimeout && millormov != null && h_total < h_actual) {
                    millormovTotal = millormov;
                    ++_profMax;  
                }
                
            }
            
            return new PlayerMove(millormovTotal, _nodesExplorats, _profMax, SearchType.MINIMAX_IDS);
        } else {
            for (Point mov : possiblesMovs) {
                HexGameStatus AuxEstat = new HexGameStatus(s);
                AuxEstat.placeStone(mov);

                double h_minima = MIN(AuxEstat, _profMax-1, s.getCurrentPlayerColor(), MENYS_INFINIT, INFINIT);
                if (h_total < h_minima) {
                    h_total = h_minima;
                    millormovTotal = mov;
                }
            }
            
            return new PlayerMove(millormovTotal, _nodesExplorats, _profMax, SearchType.MINIMAX);
        }
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
    private double MIN(HexGameStatus s, int profunditat, int colorAct, double _alpha, double _beta) {
        if(s.isGameOver()) {
            if (s.GetWinner()== _myplayer) {
                return INFINIT;
            } else {
                return MENYS_INFINIT;
            } 
        }
        
        if (profunditat == 0 || _istimeout ) {
            ++_nodesExplorats;
            //return 0;
            return Heuristica.getHeuristica(s, _myplayer);
        }
        
        /*if(s.isGameOver()) {
            if (s.GetWinner()== _myplayer) {
                return INFINIT;
            } else {
                return MENYS_INFINIT;
            } 
        }*/
        
        double millorvalor = INFINIT;
        List<Point> possiblesMoviments = obtePossiblesMoviments(s);
        
        for (Point mov : possiblesMoviments) {
            HexGameStatus nouEstat = new HexGameStatus(s);
            nouEstat.placeStone(mov);
            
            double ha = MAX(nouEstat, profunditat-1, colorAct, _alpha, _beta); 
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
    private double MAX(HexGameStatus s, int profunditat, int colorAct, double _alpha, double _beta) {
        if (s.isGameOver()) {
            if (s.GetWinner() == _myplayer) {
                return INFINIT;
            } else {
                return MENYS_INFINIT;
            }   
        }
        
        if (profunditat == 0 || _istimeout) {
            ++_nodesExplorats;
            // return 0;
            /*if (s.isGameOver()) {
                if (s.GetWinner() == _myplayer) {
                    return INFINIT;
                } else {
                    return MENYS_INFINIT;
                }   
            }
            else*/ return Heuristica.getHeuristica(s, _myplayer);
            // return getHeuristica(s, _myplayer);
            // return _heuristica;
        }   
        
        /*if (s.isGameOver()) {
            if (s.GetWinner() == _myplayer) {
                return INFINIT;
            } else {
                return MENYS_INFINIT;
            }   
        }*/
        
        double millorvalor = MENYS_INFINIT;
        List<Point> possiblesMoviments = obtePossiblesMoviments(s);
        
        for (Point mov : possiblesMoviments) {
            HexGameStatus nouEstat = new HexGameStatus(s);
            nouEstat.placeStone(mov);
            
            double ha = MIN(nouEstat, profunditat-1, colorAct, _alpha, _beta); 
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