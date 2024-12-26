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
 * @author AleLisette
 */
public class Dijkstra {
    static private HexGameStatus _hgs;
    static private int _colorJugador;
    static private int _midaTauler;
    
    static private double _mitjanaDistancies;
   
    /*static int getHeuristica(HexGameStatus gs, PlayerType playerType) {
    return 0;
    }*/
    
    // retorna la MitjanaDelsCamins
    static public int calculaDistanciaMinima(HexGameStatus gs, PlayerType jugador) {
        _hgs = gs;
        _colorJugador = PlayerType.getColor(jugador);
        _midaTauler = gs.getSize();
        
        Queue<Casella> nodesJugador = new LinkedList<>();
        Set<Casella> nodesInicials = new HashSet<>();
        Set<Casella> nodesFinals = new HashSet<>(); 
        for (int i=0; i<_midaTauler; ++i) {
            for (int j=0; j<_midaTauler; ++j) {
                Casella casella = new Casella(i, j);
                
                if (gs.getPos(i, j) == _colorJugador) nodesJugador.add(casella);
                
                /* El jugador blanc vol connectar la primera i última columna del tauler */
                if (_colorJugador == 1) { 
                    if (i == 0) nodesInicials.add(casella);
                    else if (i == _midaTauler-1) nodesFinals.add(casella);
                } else { /* El jugador negre vol connectar la primera i última fila del tauler */
                    if (j == 0) nodesInicials.add(casella);
                    else if (j == _midaTauler-1) nodesFinals.add(casella);
                }
            }
        }
        
        int distanciaMinima = Integer.MAX_VALUE; //infinit
        _mitjanaDistancies = Integer.MAX_VALUE;
        int sumaDistancies = 0;
        int comptadorDistancies = 0;
        int infinits = 0;
        while (!nodesJugador.isEmpty()) {
            Casella node = nodesJugador.poll();
            
            /*int distancia;
            if (nodesInicials.contains(node)) distancia = dijkstra(node, nodesFinals);
            else if (nodesFinals.contains(node)) distancia = dijkstra(node, nodesInicials);
            else distancia = dijkstra(node, nodesInicials) + dijkstra(node, nodesFinals);*/
            
            int distancia = dijkstra(node, nodesInicials) + dijkstra(node, nodesFinals);
            
            distanciaMinima = Math.min(distanciaMinima, distancia);
            if (distancia < Integer.MAX_VALUE) {
                sumaDistancies += distancia;
                ++comptadorDistancies;
            }
            else sumaDistancies += (_midaTauler*_midaTauler);
            //else ++infinits;
        }
        
        int totalCamins = comptadorDistancies + infinits;
        if (totalCamins != 0) {
            double penalitzacioPerCamíImpossible = 100;
            double sumaFinal = sumaDistancies + infinits*penalitzacioPerCamíImpossible;
            _mitjanaDistancies = sumaFinal / totalCamins;
        }
        
        return distanciaMinima;
    }
    
    static public double calculaMitjanaDistancies() {
        return _mitjanaDistancies;
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
                        if (_hgs.getPos(veina.fila, veina.columna) == 0) novaDistancia += 1; // Cost constant, solo se le sumara un 1 cuando esa casilla esta libre pero para ir cuando hay un vertice virtual para ir ahi se le suma 0
                        else if (_hgs.getPos(veina.fila, veina.columna) == -_colorJugador) novaDistancia = Integer.MAX_VALUE;
                                
                        if (novaDistancia < veina.distancia) {
                            veina.distancia = novaDistancia;
                            obertes.add(veina);
                        }
                    }
                }
            }
        }
        
        return Integer.MAX_VALUE; //Retorna infinit // No s'ha trobat cap camí
    }
    
    public static int calculaPonts(HexGameStatus gs, PlayerType jugador) {
        int midaTauler = gs.getSize();
        int colorJugador = PlayerType.getColor(jugador);
        int ponts = 0;

        for (int i = 0; i < midaTauler; i++) {
            for (int j = 0; j < midaTauler; j++) {
                // Només considerem caselles buides com a possibles ponts
                if (gs.getPos(i, j) == 0) {
                    Casella casellaBuida = new Casella(i, j);

                    // Mira els veïns immediats
                    List<Casella> veinesJugador = new ArrayList<>();
                    for (Casella veina : casellaBuida.obteVeines()) {
                        if (gs.getPos(veina.fila, veina.columna) == colorJugador) {
                            veinesJugador.add(veina);
                        }
                    }

                    // Si hi ha dues o més caselles del mateix color adjacents, és un pont potencial
                    if (veinesJugador.size() >= 2) {
                        ponts++;
                    }
                }
            }
        }
        return ponts;
    }
    
    public static int calculaConnexions(HexGameStatus gs, PlayerType jugador) {
        int midaTauler = gs.getSize();
        int colorJugador = PlayerType.getColor(jugador);
        Set<Casella> visitades = new HashSet<>();
        int connexions = 0;

        for (int i = 0; i < midaTauler; i++) {
            for (int j = 0; j < midaTauler; j++) {
                if (gs.getPos(i, j) == colorJugador) {
                    Casella casella = new Casella(i, j);
                    if (!visitades.contains(casella)) {
                        // Marca la casella com visitada
                        visitades.add(casella);

                        // Mira les caselles veïnes del mateix color
                        for (Casella veina : casella.obteVeines()) {
                            if (gs.getPos(veina.fila, veina.columna) == colorJugador) {
                                connexions++;
                            }
                        }
                    }
                }
            }
        }
        return connexions / 2; // Dividim entre 2 perquè comptem cada connexió dues vegades
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
            int hash = 17; // Valor base inicial
            hash = 31*hash + fila; // Combina la fila en el hash
            hash = 31*hash + columna; // Combina la columna en el hash
            return hash;
        }
        
        /* Retorna de les caselles veïnes BUIDES de la Casella p.i. */
        List<Casella> obteVeines() {
            int [][] direccions = {
                //vecinos inmediatos básicos 
                {-1, 1}, {-1, 0}, {0, -1}, {1, -1}, {1,0}, {0,1} //direccions veins inmediats ordenats 
            };
            int[][] dirvervirtuals = { // vertices de los vecinos virtuales 
                //{-2, 1}, {-1, -1}, {-1, 2}, {1, -2}, {1, 1}, {2, -1}
                //direccions dels veins virtuals:                 {x-2, y+1}, {x-1, y-1}, {x-1, y+2}, {x+1, y-2}, {x+1, y+1}, {x+2, y-1}
                //versio ordenada:
                {-2, 1}, {-1, -1}, {1, -2}, {2, -1}, {1, 1}, {-1, 2}
            };

            boolean [] veinslliuresinm = {
                false, false, false, false, false, false
            };
            
            List<Casella> veines = new ArrayList<>();
            for (int i=0; i<6; ++i) { //veins inmediats o sigui al voltant del node  AÑADE VECINOS INMEDIATOS 
                int[] direccio = direccions[i];
                int filaNova = fila+direccio[0];
                int columnaNova = columna+direccio[1];
                if (compleixrang(filaNova, columnaNova) /*&& _hgs.getPos(filaNova, columnaNova) != -_colorJugador*/) { // pedra del rival = obstacle
                    veines.add(new Casella(filaNova, columnaNova));
                    if (_hgs.getPos(filaNova, columnaNova) == 0) { //veiem si el vei inmmediat esta lliure adjacent
                        veinslliuresinm[i] = true; //miramos si esa casilla esta libre i ponemos a true porque esta libre
                    }
                }
            }
            
            for (int i = 0; i<6; ++i) {   //AÑADE VECINOS VIRTUALES, se hace despues porque primero tiene que comprobar que los inmediatos esten libres
                int[] dirvv = dirvervirtuals[i];
                int filaNova = fila+dirvv[0];
                int columnaNova = columna+dirvv[1];
                if (compleixrang(filaNova, columnaNova) /*&& _hgs.getPos(filaNova, columnaNova) != -_colorJugador*/  && veinslliuresinm[i%6] && veinslliuresinm[(i+1)%6]) { // pedra del rival = obstacle ja no cal mirar
                    //i es i+1 porque para anaizar el vecino virtual i se ha de explicar el vecino inmediato i i el i+1 (foto)
                    veines.add(new Casella(filaNova, columnaNova));
                }
            }
            
            return veines;
        }
        
        public boolean compleixrang(int x, int y) {
            boolean compleix = false;
            if (x<_midaTauler && x>=0 && y<_midaTauler && y>=0 ) { //si compleix aixo es que esta dins del rang del tauler
                compleix = true;
            }
            return compleix;
        }
        
    }
    
}
