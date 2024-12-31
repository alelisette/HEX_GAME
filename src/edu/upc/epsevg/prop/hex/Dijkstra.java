package edu.upc.epsevg.prop.hex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.awt.Point;

/**
 * Implementació de l'algorisme de Dijkstra per calcular distàncies en el joc Hex.
 * 
 * @author AleLisette
 */
public class Dijkstra {
    /** Matriu per emmagatzemar les distàncies mínimes des de cada posició inicial (node imaginari incial). */
    private static int[][] distancies;

    /**
     * Calcula la distància mínima (nombre mínim de pedres necessàries per connectar)
     * des d'un punt inicial (node imaginari inicial) fins al marge oposat (node imaginari final).
     * 
     * @param gs Estat actual del joc.
     * @param jugador Tipus de jugador que inicia el càlcul.
     * @param jugadorHex Jugador associat amb les pedres en el taulell.
     * @return Distància mínima calculada.
     */
    public static int calculaDistanciaMinima(HexGameStatus gs, PlayerType jugador, PlayerType jugadorHex) {
        inicialitza(gs.getSize());

        PriorityQueue<Node> cua = new PriorityQueue<>(Comparator.comparingInt(n -> n.distancia));
        inicialitzaNodeImaginariInicial(gs, jugador, cua, jugadorHex); // node imaginari inical: nodes de la primera vorera

        // Algorisme de Dijkstra
        while (!cua.isEmpty()) {
            Node actual = cua.poll();

            // Si aquest node ja té una distància calculada menor, ignorar.
            if (actual.distancia > distancies[actual.punt.x][actual.punt.y]) continue;

            for (Point vei : obteVeins(gs, actual.punt)) {
                int novaDistancia = actual.distancia + calculaCost(gs, vei, jugador);

                if (novaDistancia < distancies[vei.x][vei.y]) {
                    distancies[vei.x][vei.y] = novaDistancia;
                    cua.add(new Node(vei, novaDistancia));
                }
            }
        }

        return obteDistanciaMinimaFinal(gs, jugador);
    }

    /**
     * Calcula la mitjana de totes les distàncies mínimes des de cada punt del taulell.
     * 
     * @param gs Estat actual del joc.
     * @param jugador Tipus de jugador.
     * @return Mitjana de les distàncies mínimes.
     */
    public static double calculaMitjanaDistancies(HexGameStatus gs, PlayerType jugador) {
        int size = distancies.length;
        int suma = 0, count = 0;
        for (int[] fila : distancies) {
            for (int d : fila) {
                if (d != Integer.MAX_VALUE) suma += d;
                else suma += (size*size); // per tenir en compte els infinits (no hi ha camí possible)
                count++;
            }
        }
        return count > 0 ? (double) suma / count : size*size;
    }

    /**
     * Inicialitza la matriu de distàncies i la configura a valors per defecte (infinit).
     * 
     * @param size Mida del taulell.
     */
    private static void inicialitza(int size) {
        distancies = new int[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(distancies[i], Integer.MAX_VALUE);
        }
    }

    /**
     * Configura els nodes inicials en la cua de prioritat segons els marges rellevants
     * per al jugador.
     * 
     * @param gs Estat actual del joc.
     * @param jugador Tipus de jugador.
     * @param cua Cua de prioritat per als nodes.
     * @param jugadorHex Jugador associat amb les pedres en el taulell.
     */
    private static void inicialitzaNodeImaginariInicial(HexGameStatus gs, PlayerType jugador, PriorityQueue<Node> cola, PlayerType jugadorHex) {
        int size = gs.getSize();
        
        if (jugador == PlayerType.PLAYER1) { // Blanc: marge superior. (veure UnitTesting)
            for (int x = 0; x < size; x++) {
                Point p = new Point(0, x);
                int casella = gs.getPos(p);
                if (casella == 0) {
                    distancies[0][x] = 1;
                    cola.add(new Node(p, 1));
                }
                else if (casella == 1) {
                    distancies[0][x] = 0;
                    cola.add(new Node(p, 0));
                }
                else cola.add(new Node(p, Integer.MAX_VALUE));
            }
        } else {  // Negre: marge esquerre.
            for (int y = 0; y < size; y++) {
                Point p = new Point(y, 0);
                int casella = gs.getPos(p);
                if (casella == 0) {
                    distancies[y][0] = 1;
                    cola.add(new Node(p, 1));
                }
                else if (casella == 1) { // hauria de ser -1, pero això fa que el comportament sigui més agressiu, adient a taulers petits
                    distancies[y][0] = 0;
                    cola.add(new Node(p, 0));
                }
                else cola.add(new Node(p, Integer.MAX_VALUE));
            }
        }
    }

    /**
     * Calcula el cost de moure's a una casella específica.
     * 
     * @param gs Estat actual del joc.
     * @param p Punt destí.
     * @param jugador Tipus de jugador.
     * @return Cost associat al moviment.
     */
    private static int calculaCost(HexGameStatus gs, Point p, PlayerType jugador) {
        int size = gs.getSize();
        int color = gs.getPos(p);
        if (color == 0) return 1; // casella buida
        return (color == PlayerType.getColor(jugador)) ? 0 : size*size; // size^2 -> penalització de rivals
    }

    /**
     * Obté els veïns vàlids d'un punt en el taulell.
     * 
     * @param gs Estat actual del joc.
     * @param p Punt d'origen.
     * @return Llista de punts veïns accessibles.
     */
    private static List<Point> obteVeins(HexGameStatus gs, Point p) {
        int[][] direccions = {{-1, 1}, {-1, 0}, {0, -1}, {1, -1}, {1, 0}, {0, 1}};
        List<Point> veins = new ArrayList<>();
        boolean[] veinsLliures = {false, false, false, false, false, false};
        for (int i = 0; i < 6; ++i) {
            int[] d = direccions[i];
            int nx = p.x + d[0], ny = p.y + d[1];
            if (nx >= 0 && ny >= 0 && nx < gs.getSize() && ny < gs.getSize()) {
                veins.add(new Point(nx, ny));
                if (gs.getPos(nx, ny) == 0) veinsLliures[i] = true;
            }
        }
        
        int[][] direccionsVirtuals = {{-2, 1}, {-1, -1}, {1, -2}, {2, -1}, {1, 1}, {-1, 2}};
        for (int i = 0; i < 6; ++i) {
            int[] d = direccionsVirtuals[i];
            int nx = p.x + d[0], ny = p.y + d[1];
            if (nx >= 0 && ny >= 0 && nx < gs.getSize() && ny < gs.getSize() 
                && veinsLliures[i%6] && veinsLliures[(i+1)%6]) {
                veins.add(new Point(nx, ny));
            }
        }
      
        return veins;
    }

    /**
     * Calcula la distància mínima necessària per connectar un marge segons
     * el jugador.
     * 
     * @param gs Estat actual del joc.
     * @param jugador Tipus de jugador.
     * @return Distància mínima al marge oposat.
     */
    private static int obteDistanciaMinimaFinal(HexGameStatus gs, PlayerType jugador) {
        int size = gs.getSize();
        int minDistancia = Integer.MAX_VALUE;

        if (jugador == PlayerType.PLAYER1) { // Blanc: marge inferior. 
            for (int x = 0; x < size; x++) {
                minDistancia = Math.min(minDistancia, distancies[size-1][x]);
            }
        } else { // Negre: marge dret.
            for (int y = 0; y < size; y++) {
                minDistancia = Math.min(minDistancia, distancies[y][size-1]);
            }
        }
        return minDistancia;
    }

    /**
     * Classe interna per representar nodes a la cua de prioritat.
     */
    private static class Node {
        Point punt;
        int distancia;

        Node(Point punt, int distancia) {
            this.punt = punt;
            this.distancia = distancia;
        }
    }
}