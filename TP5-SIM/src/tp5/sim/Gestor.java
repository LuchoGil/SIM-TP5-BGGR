package tp5.sim;

public class Gestor {

    private int contParquimetrosOcupados;
    private int contAutosSinLugar;
    private int contAutosSiEstacionaron;
    private int contAutosConInfraccion;
    private int iteraciones;

    private double[] horaFin; //la pos es el id del parquimetro
    private double proxLlegada;
    private double reloj;
    double[] tEntreLlegadas;

    private Parquimetro[] parquimetros;

    public Gestor(int cant) {
        this.contAutosSiEstacionaron = 0;
        this.contAutosSinLugar = 0;
        this.contParquimetrosOcupados = 0;
        this.contAutosConInfraccion = 0;
        this.iteraciones = cant;
        this.tEntreLlegadas = new double[2];
        this.horaFin = new double[24];
        this.parquimetros = new Parquimetro[24];
    }

    /*
    decide cual es el evento proximo y lo ejecuta
     */
    
    public void evento() {
        int posMin = 0; //la menor de las horas
        for (int i = 1; i < horaFin.length; i++) {
            if (horaFin[i] != 0) //si es 0 no comparo, significa que no esta en uso ese parq
            {
                if (horaFin[posMin] >= horaFin[i]) {
                    posMin = i;
                }
            }
        }
        if (proxLlegada < posMin) { //entonces genero una llegada
            //actualizar tiemposss
            reloj = proxLlegada;
            llegadaAuto();

        } else {
            //actualizar
            reloj = horaFin[posMin];
            this.contParquimetrosOcupados--;
            finEstacionamiento(posMin);
            horaFin[posMin] = 0; //cuando el parquimetro se desocupa, su horaFin esta en 0
        }
    }

    /*
    calculos para el evento llegada Auto
     */
    private void llegadaAuto() {
        int tiempoTurno;
        int posParquimetro; //parquimetro num
        if (!(this.contParquimetrosOcupados <= 25)) { //si no es menor o igual a 25 no tiene lugar
            this.contAutosSinLugar++;
        } else { //si tiene lugar
            tiempoTurno = this.turno();
            posParquimetro = this.buscarParquimetroLibre();
            switch (tipoUso()) {
                case 0: //no pone monedas
                    if (parquimetros[posParquimetro].getTiempoSobra() < tiempoTurno) {
                        this.contAutosConInfraccion++;
                    }
                    horaFin[posParquimetro] = reloj + tiempoTurno;
                    parquimetros[posParquimetro].ocupar();
                    this.contParquimetrosOcupados++;
                    break;
                case 1: //usa menos del tiempo del turno
                    double tiempo = Generador.uniforme(50 * tiempoTurno / 100, 95 * tiempoTurno / 100);
                    if (parquimetros[posParquimetro].getTiempoSobra() < tiempo) {
                        this.contAutosConInfraccion++;
                    }
                    parquimetros[posParquimetro].ocupar();
                    parquimetros[posParquimetro].actualizarTiempo(tiempo);
                    this.contParquimetrosOcupados++;
                    horaFin[posParquimetro] = reloj + tiempo;
                    break;
                case 2: //usa el tiempo exacto
                    parquimetros[posParquimetro].ocupar();
                    this.contParquimetrosOcupados++;
                    horaFin[posParquimetro] = reloj + tiempoTurno;
                    break;
                case 3: //usa mas del tiempo
                    double tExtra = Generador.uniforme(5 * tiempoTurno / 100, 15 * tiempoTurno / 100);
                    if (parquimetros[posParquimetro].getTiempoSobra() < (tiempoTurno + tExtra)) {
                        this.contAutosConInfraccion++;
                    }
                    parquimetros[posParquimetro].ocupar();
                    parquimetros[posParquimetro].actualizarTiempo(tiempoTurno + tExtra);
                    this.contParquimetrosOcupados++;
                    horaFin[posParquimetro] = reloj + tiempoTurno + tExtra;
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
    
    
    
    
    
}
