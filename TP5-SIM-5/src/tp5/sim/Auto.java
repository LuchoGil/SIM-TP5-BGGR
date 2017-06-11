/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tp5.sim;

/**
 *
 * @author Luciano
 */
public class Auto {
    
    private int estado;//1- estacionado sin infraccion 2- estacionado con infraccion 3-sin estacionar
    private int numero;
    private double horaLlegada;
    private double horaSalida;

    public Auto(){
        this.estado=3;
        this.horaLlegada=0.0;
        this.horaSalida=0.0;
        this.numero=0;                
    }
    public Auto(int estado, int numero, double horaLlegada, double horaSalida) {
        this.estado = estado;
        this.numero = numero;
        this.horaLlegada = horaLlegada;
        this.horaSalida = horaSalida;
    }

    public int getEstado() {
        return estado;
    }

    public String getEstadoLetras()
    {
        String est="";
        
        if(estado==1)
            est="Estacionado sin Infracción";
        
        if(estado==2)
            est="Estacionado con Infracción";
        
        if(estado==3)
            est="Sin estacionar";
        
        return est;
    }
    
    public int getNumero() {
        return numero;
    }

    public double getHoraLlegada() {
        return horaLlegada;
    }

    public double getHoraSalida() {
        return horaSalida;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setHoraLlegada(double horaLlegada) {
        this.horaLlegada = horaLlegada;
    }

    public void setHoraSalida(double horaSalida) {
        this.horaSalida = horaSalida;
    }

    @Override
    public String toString() {
        return "Auto N°" + numero + " - Hora llegada: "+horaLlegada+" - Hora salida: "+horaSalida ;
    }
        
    
    
}
