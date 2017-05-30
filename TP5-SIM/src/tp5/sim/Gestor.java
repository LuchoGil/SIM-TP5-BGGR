package tp5.sim;

public class Gestor {

    private int contParquimetrosOcupados;
    private int contAutosSinLugar;
    private int contAutosSiEstacionaron;
    private int contAutosConInfraccion;
    private int iteraciones;

       
    private double proxLlegada;
    private double reloj;
    double[] tEntreLlegadas;
    private int numeroAuto = 1;

    private Parquimetro[] parquimetros;
    private Auto[] autos;
    
    public Gestor(int cant) {
        this.contAutosSiEstacionaron = 0;
        this.contAutosSinLugar = 0;
        this.contParquimetrosOcupados = 0;
        this.contAutosConInfraccion = 0;
        this.iteraciones = cant;
        this.tEntreLlegadas = new double[2];
        
        this.autos = new Auto[25];
        this.parquimetros = new Parquimetro[25];
        
    }

    /*
    decide cual es el evento proximo y lo ejecuta
     */
    
    public void evento() {
        
        int posMin = 0; //la menor de las horas
        for (int i = 1; i < autos.length; i++) {
            if (autos[i].getHoraSalida() >= reloj) //si es 0 no comparo, significa que no esta en uso ese parq
            {
                if (autos[posMin].getHoraSalida() >= autos[i].getHoraSalida()) {
                    posMin = i;
                }
            }
        }
        
        if (proxLlegada <  autos[posMin].getHoraSalida()) { //entonces genero una llegada
            //actualizar tiemposss
            reloj = proxLlegada;
            llegadaAuto();

        } else {
            //actualizar
            reloj = autos[posMin].getHoraSalida();
            this.contParquimetrosOcupados--;
            finEstacionamiento(posMin);            
        }
    }

    /*
    calculos para el evento llegada Auto
     */
    private void llegadaAuto() {
        
        controlarParquimetros();//recorre el vector y verifica las hroas de los autos y el tiempo de los parquimetros.
        
        int tiempoTurno;
        int posParquimetro; //parquimetro num
        if (!(this.contParquimetrosOcupados <= 25)) { //si no es menor o igual a 25 no tiene lugar
            this.contAutosSinLugar++;
        } else { //si tiene lugar
            tiempoTurno = this.turno();            
            posParquimetro = this.buscarParquimetroLibre();
            
            Auto a = new Auto(1,numeroAuto,reloj,reloj+tiempoTurno);//creo el auto
            
            switch (tipoUso()) {
                case 0: //no pone monedas                    
                    autos[posParquimetro].setHoraSalida(reloj + tiempoTurno);
                    parquimetros[posParquimetro].ocupar();
                    this.contParquimetrosOcupados++;
                    break;
                case 1: //usa menos del tiempo del turno
                    double tiempo = Generador.uniforme(50 * tiempoTurno / 100, 95 * tiempoTurno / 100);
                    a.setHoraSalida(reloj+tiempo);                   
                    parquimetros[posParquimetro].ocupar();
                    parquimetros[posParquimetro].actualizarTiempo(tiempo);
                    this.contParquimetrosOcupados++;
                    break;
                case 2: //usa el tiempo exacto                    
                    parquimetros[posParquimetro].ocupar();
                    parquimetros[posParquimetro].actualizarTiempo(tiempoTurno);
                    this.contParquimetrosOcupados++;                    
                    break;
                case 3: //usa mas del tiempo
                    double tExtra = Generador.uniforme(5 * tiempoTurno / 100, 15 * tiempoTurno / 100);
                    a.setHoraSalida(reloj+tiempoTurno+tExtra);                   
                    parquimetros[posParquimetro].ocupar();
                    parquimetros[posParquimetro].actualizarTiempo(tiempoTurno);
                    this.contParquimetrosOcupados++;                    
                    break;
            }

            //ahora genero un rnd para guardar en proxLlegada (tener en cuenta que es NORMAL)
            if (tEntreLlegadas[0] != 0) {
                proxLlegada = tEntreLlegadas[0];
                tEntreLlegadas[0] = 0;
            } else if (tEntreLlegadas[1] != 0) {
                proxLlegada = tEntreLlegadas[1];
                tEntreLlegadas[1] = 0;
            } else {
                tEntreLlegadas = Generador.normalBM(3, 10);
            }

        }

    }


/*
    busca un parquimetro, teniendo en cuenta la prioridad
 */
private int buscarParquimetroLibre(){
        int i;
            boolean encontro=false;
            for (i = 0; i < parquimetros.length; i++) {
                if(parquimetros[i].isLibreConTiempo())
                {
                    encontro=true;
                    break;
                }
            }
            if(!encontro)
                for (i = 0; i < parquimetros.length; i++) {
                    if(parquimetros[i].isLibre())
                        break;
                }
        return i;
    }
    
    private double rnd(){
        double numero = Math.random();
        int trun = (int) (numero * 100);
        return (double) ((double) (trun) / 100);
    }
    
    
    private int tipoUso(){
       
        double random = rnd();


        if (random >= 0.00 && random <= 0.02) {
            return 0;
        }
        if (random >= 0.03 && random <= 0.39) {
            return 1;
        }
        if (random >= 0.40 && random <= 0.79) {
            return 2;
        }
        return 3;
    }

    private void finEstacionamiento(int id) {
        this.contAutosSiEstacionaron++;
        parquimetros[id].desocupar();
    }
    
    private int turno(){
        double random = rnd();
    
        if(random >= 0.00 && random <= 0.39) 
            return 1;
        return 2;               
    }

    private void controlarParquimetros() //controla infracciones y cambia de estadode los atos infractores 
    { 
        for (int i = 0; i <= 25 ; i++) {
            if(parquimetros[i].getHoraFin() < reloj){
                if((autos[i].getEstado() == 1) && (autos[i].getHoraSalida() > reloj)) {                
                    this.contAutosConInfraccion++;
                    autos[i].setEstado(2);
                }
            }            
            
        }
        
    }
    
    
    
    
    
}
