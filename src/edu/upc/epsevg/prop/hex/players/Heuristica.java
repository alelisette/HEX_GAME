/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Dijkstra;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Alelisette
 */

public class Heuristica {
    //heuristica = h1 + h2 + h3 + h4
    private static double _heuristica;
    private static int _midaTauler;
    private static int _color;
    private static HexGameStatus _hgs;
    
    
    static public double getHeuristica(HexGameStatus hgs, PlayerType jugador) {
        _hgs = hgs;
        _midaTauler = hgs.getSize();
        _color = PlayerType.getColor(jugador);
        _heuristica = calculateHeuristica(hgs, jugador);
        return _heuristica;
    }
    
    static private double calculateHeuristica(HexGameStatus hgs, PlayerType jugador) {
        double h = calculaDiferencia(hgs, jugador);
        return h;
    }
    
    static private double calculaDiferencia(HexGameStatus hgs, PlayerType jugador) {
        //Dijkstra d = new Dijkstra();
        PlayerType rival = PlayerType.opposite(jugador);
        //int midaTauler = hgs.getSize();
        
        int d_jugador = Dijkstra.calculaDistanciaMinima(hgs, jugador, jugador);
        double m_jugador = Dijkstra.calculaMitjanaDistancies(hgs, jugador);
        
        int d_rival = Dijkstra.calculaDistanciaMinima(hgs, rival, jugador);
        double m_rival = Dijkstra.calculaMitjanaDistancies(hgs, rival);
        
        
        //double diferenciaDist = d_rival - d_jugador;
        
        //double diferenciaMitj = m_rival - m_jugador;
        //double diferencia = diferenciaDist - diferenciaMitj;
        //int enllacos_jugador = calculPesCasella(_color);
        //int enllacos_rival = calculPesCasella(-_color);
        //int diferenciaEnllacos = enllacos_jugador - enllacos_rival;
        
        //return d_rival - d_jugador;

        double d = d_rival - d_jugador;
        double m = m_rival - m_jugador;
        
        double h = d;
        if (d == 0) h = m;

        return h;
        // return m_rival - m_jugador; // guanya 5x5 si es primer
        //return (m_rival+enllacos_jugador) - (m_jugador+enllacos_rival);
    }
    

     
    
    /* Classe auxiliar: Casella */
    static class Casella {
        int fila;
        int columna;
        int color;
        private int enllacosVirtuals;
        
        Casella(int f, int c) {
            this.fila = f;
            this.columna = c;
            this.color = _hgs.getPos(f,c);
            this.enllacosVirtuals = 0;
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
        List<Casella> obteVeinesVirtuals() {
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
                if (compleixrang(filaNova, columnaNova) && _hgs.getPos(filaNova, columnaNova) != -_color ) { // pedra del rival = obstacle
                  //  veines.add(new Casella(filaNova, columnaNova));
                    if (_hgs.getPos(filaNova, columnaNova) == 0) { //veiem si el vei inmmediat esta lliure adjacent
                        veinslliuresinm[i] = true; //miramos si esa casilla esta libre i ponemos a true porque esta libre
                    }
                }
            }
            
            for (int i = 0; i<6; ++i) {   //AÑADE VECINOS VIRTUALES, se hace despues porque primero tiene que comprobar que los inmediatos esten libres
                int[] dirvv = dirvervirtuals[i];
                int filaNova = fila+dirvv[0];
                int columnaNova = columna+dirvv[1];
                if (compleixrang(filaNova, columnaNova) && _hgs.getPos(filaNova, columnaNova) != -_color  && veinslliuresinm[i%6] && veinslliuresinm[(i+1)%6]) { // pedra del rival = obstacle ja no cal mirar
                    //i es i+1 porque para anaizar el vecino virtual i se ha de explicar el vecino inmediato i i el i+1 (foto)
                    veines.add(new Casella(filaNova, columnaNova));
                    ++enllacosVirtuals;
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