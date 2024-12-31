/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Heuristica;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.MyStatus;
import edu.upc.epsevg.prop.hex.MyStatus.nType;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.SearchType;
import edu.upc.epsevg.prop.hex.zobrist;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació del jugador Hexmastery utilitzant l'algoritme Minimax amb poda alfa-beta.
 * Inclou suport per a IDS (Iterative Deepening Search) i una taula de transposicions.
 * @author AleLisette
 */
public class Hexmastery implements IPlayer , IAuto {
    /**
     * Valor infinit positiu per a l'algoritme Minimax.
     */
    private static final double INFINIT = Integer.MAX_VALUE;
    
    /**
     * Valor infinit negatiu per a l'algoritme Minimax.
     */
    private static final double MENYS_INFINIT = Integer.MIN_VALUE;
    
    /**
     * Nom del jugador.
     */
    private String _name; 
    
    /**
     * Profunditat màxima de l'arbre de cerca.
     */
    private int _profMax; 
    
    /**
     * Nombre de nodes explorats durant l'execució de Minimax.
     */
    long _nodesExplorats;
    
    /**
     * Tipus del jugador actual: PLAYER1 o PLAYER2
     */
    private PlayerType _myplayer;
    
     /**
     * Indicador de si s'ha esgotat el temps.
     */
    private boolean _istimeout;
    
    /**
     * Indicador per a la cerca amb IDS (Iterative Deepening Search).
     */
    private final boolean  _ids;
    
    /**
     * Objecte Zobrist per al càlcul de hashes únics per a estats de joc.
     */
    private final zobrist _z;
    
    /**
     * Taula de transposició per a guardar estats de joc ja avaluats.
     */
    private final Map<Long, MyStatus> _tt;
    
    
    /**
     * Constructor de la classe Hexmastery per a l'algoritme Minimax.
     *
     * @param name Nom del jugador.
     * @param profunditatMaxima Profunditat màxima per a la cerca.
     * @param mida Mida del tauler. Necessari per inicialitzar zobrist.
     */
    public Hexmastery(String name, int profunditatMaxima, int mida) {
        this._name = name;
        this._profMax = profunditatMaxima;
        this._ids = false;
        this._z = new zobrist(mida);
        this._tt = new HashMap<>();
    }

    /**
     * Constructor de la classe Hexmastery per a l'algoritme Minimax amb IDS.
     *
     * @param name Nom del jugador.
     * @param mida Mida del tauler. Necessari per inicialitzar zobrist. 
     */
    public Hexmastery(String name, int mida) {
        this._name = name;
        this._profMax = 1;
        this._istimeout = false;
        this._ids = true;
        this._z = new zobrist(mida);
        this._tt = new HashMap<>();
        
    }
    
    /**
     * Genera el següent moviment en funció de l'estat actual del joc.
     *
     * @param s Estat actual del joc: HexGameStatus.
     * @return El moviment calculat pel jugador.
     */
    @Override
    public PlayerMove move(HexGameStatus s) {
        _myplayer = s.getCurrentPlayer();

        _nodesExplorats = 0;
        
        double _alpha = MENYS_INFINIT;
        double _beta = INFINIT;   
        Point millormovTotal = null;
        
        
        long hash = _z.calculateFullHash(s);
        List<Point> possiblesMovs = obtePossiblesMoviments(s);
        
        double h_total = MENYS_INFINIT;
        Point millormov = null;
        
        if (_ids) {
            _istimeout = false;
            _profMax = 1;
            
            while (!_istimeout) {
                double h_actual = MENYS_INFINIT;
                
                MyStatus entry = _tt.get(hash);
                if (entry != null) {
                    millormov = entry._bestMove; 
                    
                    if (possiblesMovs.contains(millormov)) { // Si hi és a la llista, posem el moviemnt al principi.
                        possiblesMovs.remove(millormov);
                        possiblesMovs.add(0, millormov); 
                    }
                }
                
                ordenaMovimentsSegunTT(s, possiblesMovs); // ordenació dels nodes fills

                for (Point mov : possiblesMovs) {
                    HexGameStatus AuxEstat = new HexGameStatus(s);
                    AuxEstat.placeStone(mov);
                    
                    long updatedHash = _z.updateHash(hash, mov.x, mov.y, 0, PlayerType.getColor(_myplayer));
                    double h_minima = MIN(AuxEstat, _profMax-1, s.getCurrentPlayerColor(), _alpha, _beta, updatedHash);
                    if (h_actual < h_minima) {
                        h_actual = h_minima;
                        millormov = mov;
                    }
                }
                
                if (h_actual == INFINIT) return new PlayerMove(millormov, _nodesExplorats, _profMax, SearchType.MINIMAX_IDS);
                
                nType tipo = nType.EXACT;
                if (h_actual <= _alpha) {
                    tipo = nType.ALFA;
                } else if (h_actual >= _beta) {
                    tipo = nType.BETA;
                }
            
                if (!_istimeout && millormov != null) {
                    millormovTotal = millormov;
                    ++_profMax;  
                    
                    if (entry == null) { // Guardem a la taula de transposició aquest estat del tauler nou.
                        MyStatus newStatus = new MyStatus(s, _z, h_actual, millormovTotal, _profMax, tipo);
                        _tt.put(hash, newStatus);
                    }
                }                
            }
            
            return new PlayerMove(millormovTotal, _nodesExplorats, _profMax, SearchType.MINIMAX_IDS);
            
        } else {
            double h_minima;
            ordenaMovimentsSegunTT(s, possiblesMovs);
            
            if (!s.isGameOver()) {
                MyStatus n = _tt.get(hash);
                if (n != null) {
                    millormov = n._bestMove; 

                    if (possiblesMovs.contains(millormov)) {
                        possiblesMovs.remove(millormov);
                        possiblesMovs.add(0, millormov); // Posem el millor moviment al principi de la llista.
                    }
                }
                
                for (int i = 0; i < possiblesMovs.size(); i++) {
                    Point mov = possiblesMovs.get(i);
                    HexGameStatus AuxEstat = new HexGameStatus(s);
                    
                    AuxEstat.placeStone(mov);
                    
                    long updatedHash = _z.updateHash(hash, mov.x, mov.y, 0, PlayerType.getColor(_myplayer));
                    h_minima = MIN(AuxEstat, _profMax-1, s.getCurrentPlayerColor(), MENYS_INFINIT, INFINIT, updatedHash);
                    if (h_total < h_minima) {
                        h_total = h_minima;
                        millormovTotal = mov;
                    }
                    
                    nType tipo = nType.EXACT;
                    if (h_minima <= _alpha) {
                        tipo = nType.ALFA;
                    } else if (h_minima >= _beta) {
                        tipo = nType.BETA;
                    }
                    
                    if (n == null) {
                        MyStatus newStatus = new MyStatus(s, _z, h_total, millormovTotal, _profMax, tipo);
                        _tt.put(hash, newStatus);
                    }
                }
                if (millormovTotal == null) millormovTotal = possiblesMovs.get(0);
            }    
            
            return new PlayerMove(millormovTotal, _nodesExplorats, _profMax, SearchType.MINIMAX);
        }
    }
    
    /**
     * Indica que s'ha esgotat el temps disponible per a calcular el moviment.
     */
    @Override
    public void timeout() {
        _istimeout = true;
    }
    
    /**
     * Obté el nom del jugador.
     *
     * @return Nom del jugador.
     */
    @Override
    public String getName() {
        return "HexMastery(" + _name + ")";
    }

   /**
     * Implementació de la funció MIN per a l'algoritme Minimax amb poda alfa-beta.
     *
     * @param s Estat actual del joc.
     * @param profunditat Profunditat actual de la cerca.
     * @param colorAct Color del jugador actual.
     * @param _alpha Valor alfa per a la poda.
     * @param _beta Valor beta per a la poda.
     * @param hash Hash de l'estat actual.
     * @return Valor mínim calculat.
     */
    private double MIN(HexGameStatus s, int profunditat, int colorAct, double _alpha, double _beta, long hash) {
        if(s.isGameOver()) {
            if (s.GetWinner()== _myplayer) {
                return INFINIT;
            } else {
                return MENYS_INFINIT;
            } 
        }
        
        if (profunditat == 0 || _istimeout) {
            ++_nodesExplorats;
            return Heuristica.getHeuristica(s, _myplayer);
        }
       
        MyStatus entry = _tt.get(hash);
        if (entry != null && entry._depth > profunditat) {
            // Si la profunditat de la cerca a aquest MyStatus és prou bona.
            switch (entry._type) {
                case EXACT:
                    return entry._eval;
                case ALFA:
                    _alpha = Math.max(_alpha, entry._eval);
                    if (_beta <= _alpha) {
                        // Poda
                        return _alpha;
                    }
                    break;
                case BETA:
                    _beta = Math.min(_beta, entry._eval);
                    if (_beta <= _alpha) {
                        // Poda
                        return _beta;
                    }
                    break;
            }
        }
        
        double millorvalor = INFINIT;
        Point millorMov = null;
        List<Point> possiblesMoviments = obtePossiblesMoviments(s);

        if (entry != null && entry._bestMove != null) {
            reordenaSegunBestMove(possiblesMoviments, entry._bestMove);
        }
        
        for (int i = 0; i < possiblesMoviments.size(); ++i) {
            Point mov = possiblesMoviments.get(i);
            HexGameStatus nouEstat = new HexGameStatus(s);
            
            nouEstat.placeStone(mov);
            
            long updatedHash = _z.updateHash(hash, mov.x, mov.y, 0, s.getCurrentPlayerColor());
            double ha = MAX(nouEstat, profunditat-1, colorAct, _alpha, _beta, updatedHash); 
            
            millorvalor = Math.min(millorvalor, ha);
            _beta = Math.min(millorvalor, _beta);
            
            if (_beta <= _alpha) { // PODA
                millorMov = mov;
                break;
            }
        }
        
        // Si no ha hagut poda, ho guardem com a ALPHA o EXACT
        //   - EXACT si alpha < millorValor < beta 
        //   - ALFA/BETA coincideix amb els límits
        nType tipo = nType.EXACT;
        if (millorvalor <= _alpha) {
            tipo = nType.ALFA;
        } else if (millorvalor >= _beta) {
            tipo = nType.BETA;
        }
        guardarEnTransTable(hash, millorvalor, millorMov, profunditat, tipo, s);
        
        
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
    private double MAX(HexGameStatus s, int profunditat, int colorAct, double _alpha, double _beta, long hash) {
        if (s.isGameOver()) {
            if (s.GetWinner() == _myplayer) {
                return INFINIT;
            } else {
                return MENYS_INFINIT;
            }   
        }
        
        if (profunditat == 0 || _istimeout) {
            ++_nodesExplorats;
             return Heuristica.getHeuristica(s, _myplayer);

        }   
        
        MyStatus entry = _tt.get(hash);
        if (entry != null && entry._depth > profunditat) {
            switch (entry._type) {
                case EXACT:
                    return entry._eval;
                case ALFA:
                    _alpha = Math.max(_alpha, entry._eval);
                    if (_beta <= _alpha) {
                        return _alpha;
                    }
                    break;
                case BETA:
                    _beta = Math.min(_beta, entry._eval);
                    if (_beta <= _alpha) {
                        return _beta;
                    }
                    break;
            }
        }
        
        double millorvalor = MENYS_INFINIT;
        Point millorMov = null;
        List<Point> possiblesMoviments = obtePossiblesMoviments(s);
        

        if (entry != null && entry._bestMove != null) {
            reordenaSegunBestMove(possiblesMoviments, entry._bestMove);
        }
        
        for (int i = 0; i < possiblesMoviments.size(); i++) {
            Point mov = possiblesMoviments.get(i);
            HexGameStatus nouEstat = new HexGameStatus(s);
            
            nouEstat.placeStone(mov);
            
            long updatedHash = _z.updateHash(hash, mov.x, mov.y, 0, s.getCurrentPlayerColor());
            double ha = MIN(nouEstat, profunditat-1, colorAct, _alpha, _beta, updatedHash); 
            millorvalor = Math.max(millorvalor, ha);
            _alpha = Math.max(millorvalor, _alpha);
            
            if (_beta <= _alpha) { // PODA
                millorMov = mov;
                break;
            }
        }
        
        nType tipo = nType.EXACT;
        if (millorvalor >= _beta) {
            tipo = nType.BETA;
        }
        guardarEnTransTable(hash, millorvalor, millorMov, profunditat, tipo, s);
        
        return millorvalor;
    }
    
     /**
     * Obté tots els moviments possibles en l'estat actual del joc. És a dir,
     * les caselles lliures del tauler.
     * 
     * @param s Estat actual del joc.
     * @return Llista de moviments possibles.
     */
    private List<Point> obtePossiblesMoviments(HexGameStatus s) {
        List<Point> moviments = new ArrayList<>();
        for (int i = 0; i < s.getSize(); ++i) {
            for (int j = 0; j < s.getSize(); ++j) {
                if (s.getPos(i, j) == 0) moviments.add(new Point(i,j));
            }
        }
 
        return moviments;
    }
    
    
    /**
     * Ordena els moviments segons la informació de la transposition table.
     *
     * @param s Estat actual del joc.
     * @param moviments Llista de moviments a ordenar.
     */
    private void ordenaMovimentsSegunTT(HexGameStatus s, List<Point> moviments) {
        long hash = _z.calculateFullHash(s);
        MyStatus entry = _tt.get(hash);

        if (entry != null && entry._bestMove != null) {
            reordenaSegunBestMove(moviments, entry._bestMove);
        }
    }
    
    /**
     * Reordena la llista de moviments col·locant el millor moviment primer.
     *
     * @param possiblesMoviments Llista de moviments possibles.
     * @param bestMove El millor moviment identificat.
     */
    private void reordenaSegunBestMove(List<Point> possiblesMoviments, Point bestMove) {
        if (bestMove != null && possiblesMoviments.contains(bestMove)) {

            int index = possiblesMoviments.indexOf(bestMove);
            possiblesMoviments.set(index, possiblesMoviments.get(0));
            possiblesMoviments.set(0, bestMove);
        }
    }

    
    /**
     * Guarda o actualitza la informació en la transposition table.
     *
     * @param hash Hash de l'estat.
     * @param eval Valor avaluat.
     * @param bestMove El millor moviment identificat.
     * @param depth Profunditat de l'estat.
     * @param type Tipus de node (ALFA, BETA, EXACT).
     * @param original Estat original per copiar.
     */
    private void guardarEnTransTable(long hash, double eval, Point bestMove, int depth, nType type, HexGameStatus original) {
        MyStatus nouStatus = new MyStatus(original, _z, eval, bestMove, depth, type);
        _tt.put(hash, nouStatus);
    }   
}