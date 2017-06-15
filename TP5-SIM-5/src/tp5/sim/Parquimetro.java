package tp5.sim;

public class Parquimetro {
  
    private int estado;//1- ocupado 2-libre Sin saldo  3-libre con saldo
    private double horaFin;
    
    public Parquimetro(){
        estado=2;
        horaFin=0;
    }

    public double getHoraFin() {
        return horaFin;
    }   
    
    public void actualizarTiempo(double horaFin){
        double resta= horaFin-this.horaFin;
        if(resta<0)
            resta=0;
        this.horaFin =horaFin+resta;
    }
    
    public void ocupar(){
        estado=1;
    }
    public void desocupar(int estado){
        this.estado=estado;
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
    
    public String getEstado()
    {
        String est="";
        
        if(estado==1)
            est="Ocupado";
        
        if(estado==2)
            est="Libre sin saldo";
        
        if(estado==3)
            est="Libre con saldo";
        
        return est;
    }
}
