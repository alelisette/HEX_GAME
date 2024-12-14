/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerMove;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aleli
 */

/**
 * Implementación del jugador Hexmastery usando el algoritmo MIN-MAX con poda alfa-beta.
 * @author Aleli
 */
public class Hexmastery implements IPlayer {

    private static final int INFINIT = Integer.MAX_VALUE;
    private static final int MENYS_INFINIT = Integer.MIN_VALUE;

    private String _name; // Nombre del jugador
    private int _profMax; // Profundidad máxima del árbol de búsqueda
    private int _nJugades;

    public Hexmastery(String name, int profunditatMaxima) {
        this._name = name;
        this._profMax = profunditatMaxima;
        this._nJugades = 0;
    }

    @Override
    public PlayerMove move(HexGameStatus s) {
        Point millorMoviment = null;
        int millorValor = MENYS_INFINIT;
        int colorAct = s.getCurrentPlayerColor();
        ++_nJugades;
        
        List<Point> possiblesMoviments = convertirMoves(s.getMoves()); // Obtener movimientos posibles
        for (Point moviment : possiblesMoviments) {
            HexGameStatus nouEstat = new HexGameStatus(s);
            nouEstat.placeStone(moviment);

            int valor = MIN(nouEstat, 1, colorAct, MENYS_INFINIT, INFINIT);
            if (valor > millorValor) {
                millorValor = valor;
                millorMoviment = moviment;
            }
        }
        System.out.println("Nombre de jugades:" + _nJugades);
        return new PlayerMove(millorMoviment, _nJugades, _profMax, null);
    }

    @Override
    public void timeout() {
        System.out.println("Temps esgontant..");
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
        if (profunditat == _profMax || s.isGameOver()) {
            return 0; // Sin heurística
        }

        int millorValor = INFINIT;
        List<Point> possiblesMoviments = convertirMoves(s.getMoves());

        for (Point moviment : possiblesMoviments) {
            HexGameStatus nouEstat = new HexGameStatus(s);
            nouEstat.placeStone(moviment);

            int valor = MAX(nouEstat, profunditat + 1, colorAct, _alpha, _beta);
            millorValor = Math.min(millorValor, valor);
            _beta = Math.min(_beta, millorValor);

            if (_beta <= _alpha) {
                break; // Poda alfa-beta
            }
        }

        return millorValor;
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
        if (profunditat == _profMax || s.isGameOver()) {
            return 0; // Sin heurística
        }

        int millorValor = MENYS_INFINIT;
        List<Point> possiblesMoviments = convertirMoves(s.getMoves());

        for (Point moviment : possiblesMoviments) {
            HexGameStatus nouEstat = new HexGameStatus(s);
            nouEstat.placeStone(moviment);

            int valor = MIN(nouEstat, profunditat + 1, colorAct, _alpha, _beta);
            millorValor = Math.max(millorValor, valor);
            _alpha = Math.max(_alpha, millorValor);

            if (_beta <= _alpha) {
                break; // Poda alfa-beta
            }
        }

        return millorValor;
    }

    /**
     * Convierte una lista de MoveNode a una lista de Point.
     * @param moves Lista de MoveNode
     * @return Lista de Point extraída de MoveNode
     */
    private List<Point> convertirMoves(List<MoveNode> moves) {
        List<Point> points = new ArrayList<>();
        for (MoveNode move : moves) {
            points.add(move.getPoint());
        }
        return points;
    }
}
