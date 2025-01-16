package model;

public class Carrera {
    private int numVueltas;
    private int longitudVuelta;
    private int longitudTotal;

    public Carrera(int nVueltas, int longitudVuelta) {
        this.numVueltas = nVueltas;
        this.longitudVuelta = longitudVuelta;
        setLongitudTotal(); // Calcular automáticamente la longitud de carrera
    }

    public int getNumVueltas() {
        return numVueltas;
    }

    public int getLongitudVuelta() {
        return longitudVuelta;
    }

    public int getLongitudTotal() {
        return longitudTotal;
    }

    public void setLongitudTotal() {
        this.longitudTotal = this.numVueltas * this.longitudVuelta;
    }
}
