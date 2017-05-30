package tp5.sim;

public class Parquimetro {
  
    private int estado;//1- ocupado 2-libre Sin saldo  3-libre con saldo
    private double tiempoSobra;
    private double horaFin;
    
    public Parquimetro(){
        estado=2;
        tiempoSobra=0.0;
        horaFin=0;
    }
    public double getTiempoSobra(){
        return tiempoSobra;
    }
    public void actualizarTiempo(double tiempoADescontar){
        tiempoSobra-=tiempoADescontar;
        if(tiempoSobra<0){
            tiempoSobra=0;
        }
    }
    public void ocupar(){
        estado=1;
    }
    public void desocupar(){
        if(tiempoSobra>0){
            estado=3;
        }
        else estado=2;
    }
    
    public boolean isLibreConTiempo(){
        if(estado==3)
            return true;
        return false;
    }
    
    public boolean isLibre(){
        if(estado==2)
            return true;
        return false;
    }
    
}
