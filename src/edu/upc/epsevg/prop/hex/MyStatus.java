/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.util.Objects;
import java.awt.Point;

/**
 * Classe que hereta de HexGameStatus per afegir informació addicional sobre l'estat del joc.
 * Inclou informació sobre el millor moviment, la profunditat de l'avaluació, el tipus d'avaluació,
 * la clau de hash de Zobrist i si l'estat ha estat avaluat.
 * 
 * @author AleLisette
 */
public class MyStatus extends HexGameStatus{
    /** El millor moviment a fer des de l'estat actual. */
    public Point _bestMove;
    
    /** La profunditat de l'estat actual en l'arbre de cerca. */
    public int _depth;
    
    /** La valoració associada a aquest estat. */
    public double _eval;
    
    /** Tipus de valoració (Exacta, Alfa o Beta). */
    public nType _type;
    
    /** Tipus d'avaluació per diferenciar els mètodes de càlcul. */
    public enum nType{
        EXACT, ALFA, BETA
    }
    
    /** La clau de hash de Zobrist per a aquest estat. */
    public long _zobristHash;
    
    /** Indica si l'estat ha estat avaluat. */
    public boolean _avaluat;

    /**
     * Constructor de la classe MyStatus.
     * 
     * @param status Estat original del joc (HexGameStatus).
     * @param z Objecte per calcular el hash de Zobrist.
     * @param eval Valoració de l'estat.
     * @param bestMove El millor moviment a partir de l'estat.
     * @param depth La profunditat de l'estat en l'arbre de cerca.
     * @param type Tipus d'avaluació (Exact, Alfa o Beta).
     */
    public MyStatus(HexGameStatus status, zobrist z, double eval, Point bestMove, int depth, nType type) {
        super(status); // crida a la constructor de HexGameStatus com estat inicial
        this._bestMove = bestMove;
        this._depth = depth;
        this._eval = eval;
        this._type = type;
        this._zobristHash = z.calculateFullHash(status);
        this._avaluat = true;
    }

    /**
     * Sobreescriu el mètode equals per comparar dos objectes MyStatus.
     * Compara tots els camps rellevants de l'estat.
     * 
     * @param obj Objecte a comparar.
     * @return True si els objectes són iguals, false en cas contrari.
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

        if (this._bestMove != null ? !this._bestMove.equals(other._bestMove) : other._bestMove != null) {
            return false;
        }
        if (Double.compare(this._eval, other._eval) != 0) {
            return false;
        }
        if (this._depth != other._depth) {
            return false;
        }
        if (this._type != other._type) {
            return false;
        }
        if (this._zobristHash != other._zobristHash) {
            return false;
        }

        return true;
    }
    
    /**
     * Sobreescriu el mètode hashCode per generar un codi de hash únic basat en els camps de l'estat.
     * 
     * @return Codi de hash generat a partir de les propietats de l'estat.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this._bestMove);;
        hash = 59 * hash + this._depth;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this._eval) ^ (Double.doubleToLongBits(this._eval) >>> 32));
        hash = 59 * hash + Objects.hashCode(this._type);
        hash = 59 * hash + (int) (this._zobristHash ^ (this._zobristHash >>> 32));
        hash = 59 * hash + (this._avaluat ? 1 : 0);
        return hash;
    }   
}