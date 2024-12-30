/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

/**
 *
 * @author Aleli
 */
public class MyStatus extends HexGameStatus{
  
    public int bestMoveIndex, depth;
    public double eval;
    public nType type;
    public enum nType{
        EXACT, ALFA, BETA
    }
    

    // Constructor de MyStatus
    public MyStatus(HexGameStatus status, double eval, int bestMoveIndex, int depth, nType type) {
        super(status); // Llama al constructor de HexGameStatus con un estado inicial
        this.bestMoveIndex = bestMoveIndex;
        this.depth = depth;
        this.eval = eval;
        this.type = type;
    }

    // Sobrescribir el método equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MyStatus other = (MyStatus) obj;
      /*  return this.bestMoveIndex == other.bestMoveIndex &&
               this.eval == other.eval &&
               this.depth == other.depth &&
               this.type == other.type;
    */
            if (this.bestMoveIndex != other.bestMoveIndex) {
            return false;
        }
        return this.eval == other.eval;
    
    }


    
    
    
}
