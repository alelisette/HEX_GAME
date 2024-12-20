package edu.upc.epsevg.prop.hex;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 *
 * @author AleLisettes
 */
public class Dijkstra {
    static private HexGameStatus _hgs;
    static private int _colorJugador;
    static private int _midaTauler;
    
   
    /*static int getHeuristica(HexGameStatus gs, PlayerType playerType) {
    return 0;
    }*/

    static public int calculaDistanciaMinima(HexGameStatus gs, PlayerType jugador) {
        _hgs = gs;
        _colorJugador = PlayerType.getColor(jugador);
        _midaTauler = gs.getSize();
        
        Queue<Casella> nodesJugador = new LinkedList<>();
        Set<Casella> nodesInicials = new HashSet<>();
        Set<Casella> nodesFinals = new HashSet<>(); 
        for (int i=0; i<gs.getSize(); ++i) {
            for (int j=0; j<gs.getSize(); ++j) {
                Casella casella = new Casella(i, j);
                
                if (gs.getPos(i, j) == _colorJugador) nodesJugador.add(casella);
                
                /* El jugador blanc vol connectar la primera i última columna del tauler */
                if (_colorJugador == 1) { 
                    if (i == 0) nodesInicials.add(casella);
                    else if (i == gs.getSize()-1) nodesFinals.add(casella);
                } else { /* El jugador negre vol connectar la primera i última fila del tauler */
                    if (j == 0) nodesInicials.add(casella);
                    else if (j == gs.getSize()-1) nodesFinals.add(casella);
                }
            }
        }
        
        int distanciaMinima = Integer.MAX_VALUE;
        while (!nodesJugador.isEmpty()) {
            Casella node = nodesJugador.poll();
            
            int distancia;
            if (nodesInicials.contains(node)) distancia = dijkstra(node, nodesFinals);
            else if (nodesFinals.contains(node)) distancia = dijkstra(node, nodesInicials);
            else distancia = dijkstra(node, nodesInicials) + dijkstra(node, nodesFinals);
            
            distanciaMinima = Math.min(distanciaMinima, distancia);
        }
        
        
        return distanciaMinima;
    }
    
    static private int dijkstra(Casella inici, Set<Casella> objectiu) {
        PriorityQueue<Casella> obertes = new PriorityQueue<>(Comparator.comparingInt(c -> c.distancia));
        inici.distancia = 0;
        obertes.add(inici);
        
        Set<Casella> tancades = new HashSet<>(); // Nodes ja visitats 
        
        while (!obertes.isEmpty()) {
            Casella actual = obertes.poll();
    
            if (!tancades.contains(actual)) { // Si ja l'hem visitat, la saltem
                tancades.add(actual);
                
                if (objectiu.contains(actual)) { // Ja hem arribat al node objectiu
                    return actual.distancia;
                }
                
                for (Casella veina : actual.obteVeines()) { // Explorem les caselles veïnes
                    if (!tancades.contains(veina)) { // Evitem obstacles (ocupades) i caselles ja visitades
                        int novaDistancia = actual.distancia; 
                        if (_hgs.getPos(veina.fila, veina.columna) == 0) novaDistancia += 1; // Cost constant
                      
                        if (novaDistancia < veina.distancia) {
                            veina.distancia = novaDistancia;
                            obertes.add(veina);
                        }
                    }
                }
            }
        }
        
        return 666; // No s'ha trobat cap camí
    }
    
    
    /* Classe auxiliar: Casella */
    static class Casella {
        int fila;
        int columna;
        int distancia;
        
        Casella(int f, int c) {
            this.fila = f;
            this.columna = c;
            this.distancia = Integer.MAX_VALUE;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Casella casella = (Casella) obj;
            return fila == casella.fila && columna == casella.columna;
        }
        
        @Override
        public int hashCode() {
            int hash = 17;
            hash = 31*hash + fila;
            hash = 31*hash + columna;
            return hash;
        }
        
        /* Retorna de les caselles veïnes BUIDES de la Casella p.i. */
        List<Casella> obteVeines() {
            int [][] direccions = { 
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {-1, 1}
                /*a baix  a dalt dreta  esquerra  abaixEsq  adaltDre*/
            };
            
            List<Casella> veines = new ArrayList<>();
            for (int i=0; i<6; ++i) {
                int[] direccio = direccions[i];
                int filaNova = fila+direccio[0];
                int columnaNova = columna+direccio[1];
                if (filaNova < _midaTauler && 0 <= filaNova && 
                        columnaNova < _midaTauler && 0 <= columnaNova &&
                        _hgs.getPos(filaNova, columnaNova) != -_colorJugador) { // pedra del rival = obstacle
                    veines.add(new Casella(filaNova, columnaNova));
                }
            }
            
            return veines;
        }
    }
}
