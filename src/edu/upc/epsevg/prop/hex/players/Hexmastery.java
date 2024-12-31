/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Dijkstra;
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
    private int _midaTauler;
    
    private zobrist z;
    private Map<Long, MyStatus> tt;
    
    
    //constructor minimax 
    public Hexmastery(String name, int profunditatMaxima, int mida) {
        this._name = name;
        this._profMax = profunditatMaxima;
        this._ids = false;
        this._midaTauler = mida;
        z = new zobrist(mida);
        tt = new HashMap<>();
    }

    //constructor minimax with IDS
    public Hexmastery(String name, int mida) {
        this._name = name;
        this._profMax = 1;
        this._istimeout = false;
        this._ids = true;
        this._midaTauler = mida;
        z = new zobrist(mida);
        tt = new HashMap<>();
        
    }
    
    @Override
    public PlayerMove move(HexGameStatus s) {
        long hash = z.calculateFullHash(s);
        
        _nodesExplorats = 0;
        _myplayer = s.getCurrentPlayer();
        Point millormovTotal = null; //Inicialitzem amb el millormoviment a null
        double h_total = MENYS_INFINIT;
        double _alpha = MENYS_INFINIT;
        double _beta = INFINIT;        
        List<Point> possiblesMovs = obtePossiblesMoviments(s);
        
        Point millormov = null;
        
        if (_ids) {
            _istimeout = false;
            _profMax = 1;
            
            while (!_istimeout) {
                //Point millormov = null;
                double h_actual = MENYS_INFINIT;
                // int profMax_actual = 1;
                MyStatus entry = tt.get(hash);
                if (entry != null) {
                    // Si existe, utilizamos el mejor movimiento almacenado.
                    millormov = entry.bestMove; 
                    // Si el mejor movimiento está en la lista de movimientos posibles, lo movemos al inicio.
                    if (possiblesMovs.contains(millormov)) {
                        possiblesMovs.remove(millormov);
                        possiblesMovs.add(0, millormov); // Ponemos el mejor movimiento al principio.
                    }
                }
                
                ordenaMovimentsSegunTT(s, possiblesMovs);

                for (Point mov : possiblesMovs) {
                    HexGameStatus AuxEstat = new HexGameStatus(s);
                    
                    if (s.getPos(mov) != 0) continue;
                    AuxEstat.placeStone(mov);
                    
                    long updatedHash = z.updateHash(hash, mov.x, mov.y, 0, PlayerType.getColor(_myplayer));
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
                    //h_total = h_actual;
                    millormovTotal = millormov;
                    ++_profMax;  
                    
                    if (entry == null) {
                        // Guardamos el estado en la tabla de transposición.
                        MyStatus newStatus = new MyStatus(s, z, h_actual, millormovTotal, _profMax, tipo);
                        tt.put(hash, newStatus);
                    }
                }
                
            }
            
            return new PlayerMove(millormovTotal, _nodesExplorats, _profMax, SearchType.MINIMAX_IDS);
        } else {
            double h_minima;
            ordenaMovimentsSegunTT(s, possiblesMovs);
            
            if (!s.isGameOver()) {
                MyStatus n = tt.get(hash);
                if (n != null) {
                    // Si existe, utilizamos el mejor movimiento almacenado.
                    millormov = n.bestMove; 
                    // Si el mejor movimiento está en la lista de movimientos posibles, lo movemos al inicio.
                    if (possiblesMovs.contains(millormov)) {
                        possiblesMovs.remove(millormov);
                        possiblesMovs.add(0, millormov); // Ponemos el mejor movimiento al principio.
                    }
                }
                
                for (int i = 0; i < possiblesMovs.size(); i++) {
                    Point mov = possiblesMovs.get(i);
                    HexGameStatus AuxEstat = new HexGameStatus(s);
                    
                    //if (s.getPos(mov) != 0) continue;
                    AuxEstat.placeStone(mov);
                    
                    long updatedHash = z.updateHash(hash, mov.x, mov.y, 0, PlayerType.getColor(_myplayer));
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
                        // Guardamos el estado en la tabla de transposición.
                        MyStatus newStatus = new MyStatus(s, z, h_total, millormovTotal, _profMax, tipo);
                        tt.put(hash, newStatus);
                    }
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
    private double MIN(HexGameStatus s, int profunditat, int colorAct, double _alpha, double _beta, long hash) {
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
        
        // 1. Calculamos el hash del estado
        //long hash = z.calculateFullHash(s);
        
        // 2. Revisamos si el estado está en la transposition table
        MyStatus entry = tt.get(hash);
        if (entry != null && entry.depth > profunditat) {
            // Si la profundidad de la TT es suficiente:
            switch (entry.type) {
                case EXACT:
                    // Devolvemos directamente su evaluación
                    return entry.eval;
                case ALFA:
                    // Ajustamos alpha
                    _alpha = Math.max(_alpha, entry.eval);
                    if (_beta <= _alpha) {
                        // Poda
                        return _alpha;
                    }
                    break;
                case BETA:
                    // Ajustamos beta
                    _beta = Math.min(_beta, entry.eval);
                    if (_beta <= _alpha) {
                        // Poda
                        return _beta;
                    }
                    break;
            }
        }
        
        // 3. Exploramos hijos
        double millorvalor = INFINIT;
        Point millorMov = null;
        List<Point> possiblesMoviments = obtePossiblesMoviments(s);
        // Ordenamos la lista de movimientos si en la TT hay info sobre el "bestMoveIndex"
        if (entry != null && entry.bestMove != null) {
        reordenaSegunBestMove(possiblesMoviments, entry.bestMove);
        }
        
        for (int i = 0; i < possiblesMoviments.size(); ++i) {
            Point mov = possiblesMoviments.get(i);
            HexGameStatus nouEstat = new HexGameStatus(s);
            
            //if (s.getPos(mov) != 0) continue;
            nouEstat.placeStone(mov);
            
            long updatedHash = z.updateHash(hash, mov.x, mov.y, 0, s.getCurrentPlayerColor());
            double ha = MAX(nouEstat, profunditat-1, colorAct, _alpha, _beta, updatedHash); 
            
            millorvalor = Math.min(millorvalor, ha);
            _beta = Math.min(millorvalor, _beta);
            
            if (_beta <= _alpha) { // PODA
                // Guardamos la entrada en la TT como BETA
                millorMov = mov;
                //guardarEnTransTable(hash, millorvalor, millorMov, profunditat, nType.BETA, s);
                break;
            }
        }
        
        // Si no ha habido poda, lo guardamos con ALFA o EXACT:
        //   - EXACT si alpha < millorValor < beta (para MIN es un valor exacto)
        //   - ALFA/BETA en caso de que coincidiese con los límites
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
        
        // 1. Calculamos el hash
        //long hash = z.calculateFullHash(s);
        
        // 2. Revisamos la transTable
        MyStatus entry = tt.get(hash);
        if (entry != null && entry.depth > profunditat) {
            switch (entry.type) {
                case EXACT:
                    return entry.eval;
                case ALFA:
                    _alpha = Math.max(_alpha, entry.eval);
                    if (_beta <= _alpha) {
                        return _alpha;
                    }
                    break;
                case BETA:
                    _beta = Math.min(_beta, entry.eval);
                    if (_beta <= _alpha) {
                        return _beta;
                    }
                    break;
            }
        }
        
        // 3. Exploramos hijos
        double millorvalor = MENYS_INFINIT;
        Point millorMov = null;
        List<Point> possiblesMoviments = obtePossiblesMoviments(s);
        
        // Ordenar la lista en función del bestMoveIndex si existe
        if (entry != null && entry.bestMove != null) {
            reordenaSegunBestMove(possiblesMoviments, entry.bestMove);
        }
        
        for (int i = 0; i < possiblesMoviments.size(); i++) {
            Point mov = possiblesMoviments.get(i);
            HexGameStatus nouEstat = new HexGameStatus(s);
            
            //if (s.getPos(mov) != 0) continue;
            nouEstat.placeStone(mov);
            
            long updatedHash = z.updateHash(hash, mov.x, mov.y, 0, s.getCurrentPlayerColor());
            double ha = MIN(nouEstat, profunditat-1, colorAct, _alpha, _beta, updatedHash); 
            millorvalor = Math.max(millorvalor, ha);
            _alpha = Math.max(millorvalor, _alpha);
            
            if (_beta <= _alpha) { // PODA
                // Guardamos la entrada en la TT como ALFA
                millorMov = mov;
                //guardarEnTransTable(hash, millorvalor, mov, profunditat, nType.ALFA, s);
                break;
            }
        }
        
        // Guardado en la TT
        nType tipo = nType.EXACT;
        if (millorvalor <= _alpha) {
            // Ojo: en MAX, si el millorValor está por debajo de alpha, podría ser ALFA
            // pero normalmente es EXACT si no hemos hecho poda.
            // Se puede matizar según tus convenciones.
        }
        if (millorvalor >= _beta) {
            tipo = nType.BETA;
        }
        guardarEnTransTable(hash, millorvalor, millorMov, profunditat, tipo, s);
        
        return millorvalor;
    }
    
    private List<Point> obtePossiblesMoviments(HexGameStatus s) {
        List<Point> moviments = new ArrayList<>();
        for (int i = 0; i < s.getSize(); ++i) {
            for (int j = 0; j < s.getSize(); ++j) {
                if (s.getPos(i, j) == 0) moviments.add(new Point(i,j));
                //moviments.add(new Point(i,j));
            }
        }
        
        
        return moviments;
    }
    
    
   /**
     * Ordena los movimientos para explorarlos primero usando el movimiento que ha funcionado mejor
     * (bestMoveIndex) en la transposition table, si existe.
     * 
     * @param s Estado actual
     * @param moviments la lista de movimientos a ordenar in-place
     */
    private void ordenaMovimentsSegunTT(HexGameStatus s, List<Point> moviments) {
        // Obtenemos el hash del estado actual
        long hash = z.calculateFullHash(s);
        MyStatus entry = tt.get(hash);

        if (entry != null && entry.bestMove != null) {
            // Reordenamos poniendo el bestMove en la cabeza de la lista
            reordenaSegunBestMove(moviments, entry.bestMove);
        }
    }

    
     /*   
     * Reordena la lista de movimientos colocando en primera posición
     * el índice bestMoveIndex, si es válido.
     */
    private void reordenaSegunBestMove(List<Point> possiblesMoviments, Point bestMove) {
        // Si el bestMove no está en la lista, no hace falta hacer nada
        if (bestMove != null && possiblesMoviments.contains(bestMove)) {
            // Reemplazamos el primer elemento de possiblesMoviments por bestMove
            int index = possiblesMoviments.indexOf(bestMove);
            // Intercambiamos el primer elemento con el bestMove
            possiblesMoviments.set(index, possiblesMoviments.get(0));
            possiblesMoviments.set(0, bestMove);
        }
    }

    
    /*
     * Guarda (o actualiza) la información en la transposition table.
     *
     * @param hash         El hash del estado
     * @param eval         El valor evaluado
     * @param bestMoveIdx  El índice del mejor movimiento que resultó
     * @param depth        La profundidad en la que se evaluó
     * @param type         El tipo de nodo (ALFA, BETA, EXACT)
     * @param original     El estado original (para copiar con MyStatus)
     */
    private void guardarEnTransTable(long hash, double eval, Point bestMove, int depth, nType type, HexGameStatus original) {
        // Crea un nuevo objeto MyStatus pasando el Point bestMove en lugar de bestMoveIndex
        MyStatus nouStatus = new MyStatus(original, z, eval, bestMove, depth, type);
        tt.put(hash, nouStatus); // Guarda en la transposition table
    }


    
    
}