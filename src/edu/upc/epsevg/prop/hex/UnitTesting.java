/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus2;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus3;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus3.Result;
/**
 *
 * @author bernat
 */
public class UnitTesting {
    
     //CLASSE PROVA, ES IMPORTANT crear 15 taulers i comprovar que nostra heuristica es tal i com creem perque es molt complicat fer-ho
    //quin es el fonament principal del joc? quina distancia tinc fins a arribar a l'altre canto
    //mitjançant pel algoritme de Dijsktra, hi ha una configuracio inicial perque es una configuracio virual i
    //virtualment aixo es un virtual guanyada, hem de fer funcions a part i no esborrar els casos i si provem 10 taulers diferents 
    //aixi podem testejar-los i que el codi funciona be
    
    public static void main(String[] args) {
    
        
        byte[][] board = {
        //X   0  1  2  3  4  5  6  7  8
            { 0, 0, 0, 0, 0, 0, 0, 0, 1},                     // 0   Y
              { 0, 0, 0, 0, 0, 0, -1, 0, 0},                    // 1
                { 0, -1, 0, 0, 1, 0, 0, 0, 0},                  // 2
                  { 0, 0, -1, 0, 0, 0, 0, 0, 0},                // 3
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0},              // 4 
                      { 0, -1, 0, 0, 0, 0, 0, 0, 0},            // 5    
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0},          // 6      
                          { 0, 0, 0, 0, 0, 0, 0, 0, 0},        // 7       
                            { 0, 0, 0, 0, 0, 0, 0, 0, 0}       // 8    Y         
        };

        HexGameStatus gs = new HexGameStatus(board, PlayerType.PLAYER1);        
        int d = Dijkstra.calculaDistanciaMinima(gs, PlayerType.PLAYER1);
        System.out.println("Distancia minima: " + d);
 
        //fila i columna = 4,4
        //vecinos virtuales: 2-5, 3-3, 3-6, 5-2, 5-5, 6-3
        //fila x i columna y
        //vecinos virtuales seran: {x-2, y+1}, {x-1, y-1}, {x-1, y+2}, {x+1, y-2}, {x+1, y+1}, {x+2, y-1}
        //x = 6, y = 3
        //4,4    5,2,     5,5   7,1   7,4  8,2 
        //____________________________________COMPLEIX VERTEX VIRTUAL_________________________________________
        
        
    }
    
}






















