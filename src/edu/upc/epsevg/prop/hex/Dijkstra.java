package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author AleLisette
 */

import java.awt.Point;
import java.util.*;

public class Dijkstra {

    // Matriz para almacenar las distancias mínimas desde el inicio.
    private static int[][] distancias;

    // Método principal para calcular la distancia mínima (piedras que faltan).
    public static int calculaDistanciaMinima(HexGameStatus gs, PlayerType jugador, PlayerType jugadorHex) {
        inicializa(gs.getSize());
        
        // Cola de prioridad para implementar Dijkstra.
        PriorityQueue<Node> cola = new PriorityQueue<>(Comparator.comparingInt(n -> n.distancia));

        // Añadir nodos iniciales al considerar los bordes del jugador.
        inicializarNodosIniciales(gs, jugador, cola, jugadorHex);

        // Algoritmo de Dijkstra.
        while (!cola.isEmpty()) {
            Node actual = cola.poll();

            // Si este nodo ya tiene una distancia calculada menor, ignorar.
            if (actual.distancia > distancias[actual.punto.x][actual.punto.y]) continue;

            // Explorar los vecinos del nodo actual.
            for (Point vecino : obtenerVecinos(gs, actual.punto)) {
                int nuevaDistancia = actual.distancia + calcularCosto(gs, vecino, jugador);

                if (nuevaDistancia < distancias[vecino.x][vecino.y]) {
                    distancias[vecino.x][vecino.y] = nuevaDistancia;
                    cola.add(new Node(vecino, nuevaDistancia));
                }
            }
        }

        // Calcular y retornar la distancia mínima para ganar.
        return obtenerDistanciaMinimaFinal(gs, jugador);
    }

    // Calcula la media de las distancias mínimas encontradas.
    public static double calculaMitjanaDistancies(HexGameStatus gs, PlayerType jugador) {
        int size = distancias.length;
        int suma = 0, count = 0;
        for (int[] fila : distancias) {
            for (int d : fila) {
                //int d = distancias[x][y];
                if (d != Integer.MAX_VALUE) suma += d;
                else suma += (size*size); // per tenir en compte els infinits (no hi ha camí possible)
                count++;
            }
        }
        return count > 0 ? (double) suma / count : size*size;
    }

    // Inicializa estructuras necesarias.
    private static void inicializa(int size) {
        distancias = new int[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(distancias[i], Integer.MAX_VALUE);
        }
    }

    // Añade nodos iniciales según los bordes relevantes para el jugador.
    private static void inicializarNodosIniciales(HexGameStatus gs, PlayerType jugador, PriorityQueue<Node> cola, PlayerType jugadorHex) {
        int size = gs.getSize();
        //int valor = 1;
        //if (jugadorHex == PlayerType.PLAYER2) valor = -1;
        
        if (jugador == PlayerType.PLAYER1) { // Blanco: conecta filas superior e inferior.
            for (int x = 0; x < size; x++) {
                Point p = new Point(0, x);
                int casella = gs.getPos(p);
                if (casella == 0) {
                    distancias[0][x] = 1;
                    cola.add(new Node(p, 1));
                }
                else if (casella == 1) {
                    distancias[0][x] = 0;
                    cola.add(new Node(p, 0));
                }
                else {
                    //distancias[0][x] = Integer.MAX_VALUE;
                    cola.add(new Node(p, Integer.MAX_VALUE));
                }
            }
        } else { // Negro: conecta columnas izquierda y derecha.
            for (int y = 0; y < size; y++) {
                Point p = new Point(y, 0);
                int casella = gs.getPos(p);
                if (casella == 0) {
                    distancias[y][0] = 1;
                    cola.add(new Node(p, 1));
                }
                else if (casella == 1) {
                    distancias[y][0] = 0;
                    cola.add(new Node(p, 0));
                }
                else {
                   // distancias[y][0] = Integer.MAX_VALUE;
                    cola.add(new Node(p, Integer.MAX_VALUE));
                }
            }
        }
    }

    // Calcula el costo de moverse a una casilla (enemigos tienen más peso).
    private static int calcularCosto(HexGameStatus gs, Point p, PlayerType jugador) {
        int size = gs.getSize();
        int color = gs.getPos(p);
        if (color == 0) return 1; // Casilla vacía.
        return (color == PlayerType.getColor(jugador)) ? 0 : size*size; // size^2 penaliza rivales.
    }

    // Obtiene los vecinos válidos para un punto.
    private static List<Point> obtenerVecinos(HexGameStatus gs, Point p) {
        int[][] direcciones = {{-1, 1}, {-1, 0}, {0, -1}, {1, -1}, {1, 0}, {0, 1}};
        List<Point> vecinos = new ArrayList<>();
        boolean[] vecinosLibres = {false, false, false, false, false, false};
        for (int i = 0; i < 6; ++i) {
            int[] d = direcciones[i];
            int nx = p.x + d[0], ny = p.y + d[1];
            if (nx >= 0 && ny >= 0 && nx < gs.getSize() && ny < gs.getSize()) {
                vecinos.add(new Point(nx, ny));
                if (gs.getPos(nx, ny) == 0) vecinosLibres[i] = true;
            }
        }
       
        
        int[][] direccionesVirtuales = {{-2, 1}, {-1, -1}, {1, -2}, {2, -1}, {1, 1}, {-1, 2}};
        for (int i = 0; i < 6; ++i) {
            int[] d = direccionesVirtuales[i];
            int nx = p.x + d[0], ny = p.y + d[1];
            if (nx >= 0 && ny >= 0 && nx < gs.getSize() && ny < gs.getSize() 
                && vecinosLibres[i%6] && vecinosLibres[(i+1)%6]) {
                vecinos.add(new Point(nx, ny));
            }
        }
      
        return vecinos;
    }

    // Obtiene la distancia mínima para ganar según el borde final.
    private static int obtenerDistanciaMinimaFinal(HexGameStatus gs, PlayerType jugador) {
        int size = gs.getSize();
        int minDistancia = Integer.MAX_VALUE;

        if (jugador == PlayerType.PLAYER1) { // Blanco: borde inferior.
            for (int x = 0; x < size; x++) {
                minDistancia = Math.min(minDistancia, distancias[size-1][x]);
            }
        } else { // Negro: borde derecho.
            for (int y = 0; y < size; y++) {
                minDistancia = Math.min(minDistancia, distancias[y][size-1]);
            }
        }
        return minDistancia;
    }

    // Clase interna para nodos en la cola de prioridad.
    private static class Node {
        Point punto;
        int distancia;

        Node(Point punto, int distancia) {
            this.punto = punto;
            this.distancia = distancia;
        }
    }
}