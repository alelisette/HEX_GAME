/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.util.Objects;
import java.awt.Point;

/**
 *
 * @author Aleli
 */
public class MyStatus extends HexGameStatus{
  
    public Point bestMove;
    public int depth;
    public double eval;
    public nType type;
    public enum nType{
        EXACT, ALFA, BETA
    }
    public long zobristHash;
    public boolean avaluat;

    // Constructor de MyStatus
    public MyStatus(HexGameStatus status, zobrist z, double eval, Point bestMove, int depth, nType type) {
        super(status); // Llama al constructor de HexGameStatus con un estado inicial
        this.bestMove = bestMove;
        this.depth = depth;
        this.eval = eval;
        this.type = type;
        this.zobristHash = z.calculateFullHash(status);
    }

    // Sobrescribir el método equals

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MyStatus other = (MyStatus) obj;

        // Compara el bestMove directamente usando equals
        if (this.bestMove != null ? !this.bestMove.equals(other.bestMove) : other.bestMove != null) {
            return false;
        }

        // Compara el resto de los campos
        if (Double.compare(this.eval, other.eval) != 0) {
            return false;
        }
        if (this.depth != other.depth) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.zobristHash != other.zobristHash) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.bestMove);;
        hash = 59 * hash + this.depth;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.eval) ^ (Double.doubleToLongBits(this.eval) >>> 32));
        hash = 59 * hash + Objects.hashCode(this.type);
        hash = 59 * hash + (int) (this.zobristHash ^ (this.zobristHash >>> 32));
        hash = 59 * hash + (this.avaluat ? 1 : 0);
        return hash;
    }


    
    
    
}
