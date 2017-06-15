package tp5.sim;

import java.util.ArrayList;

public class Gestor {

    private int contParquimetrosOcupados;
    private int contAutosSinLugar;
    private int contAutosSiEstacionaron;
    private int contAutosConInfraccion;
    private double tiempoSimulacion;
    private double tiempoEstacionado;
    private double proxLlegada;
    private double reloj;
    double[] tEntreLlegadas;
    double rndTiempo1;
    double tiempo1;
    private int numeroAuto = 0;
    private Parquimetro[] parquimetros;
    private Auto[] autos;
    private boolean termino;
    private double tInicialAMostrar;
    private int turno;
    private double rndTurno;
    private double rndTipo;
    private String condicion;
    private int posParquimetro;
    private int posMin;
    private boolean banLlegada = true;
    private boolean banInfracciones = false;
    private ArrayList autosConInfraccionesEnEsteEvento; //creo un array ist de ints que que son as posiciones de vector que se producieron infraccion

    public Gestor(double tiempoLimite, int cant, double tInicialAMostrar) { //tiempo de corte, y cantidad de parquimetros  
        this.contAutosSiEstacionaron = 0;
        this.contAutosSinLugar = 0;
        this.contParquimetrosOcupados = 0;
        this.contAutosConInfraccion = 0;
        this.tiempoSimulacion = tiempoLimite;
        this.tEntreLlegadas = Generador.normalBM(3, 10);
        this.autos = new Auto[cant];
        this.parquimetros = new Parquimetro[cant];
        termino = false;
        this.tInicialAMostrar = tInicialAMostrar;
        for (int i = 0; i < autos.length; i++) { //crea los objetos necesarios en los vectores
            autos[i] = new Auto();
            parquimetros[i] = new Parquimetro();
        }
        this.autosConInfraccionesEnEsteEvento = new ArrayList();
    }

    /*
     decide cual es el evento proximo y lo ejecuta
     */
    private String evento() {
        this.banInfracciones = false;
        this.autosConInfraccionesEnEsteEvento.clear();

        String evento = "";
        int posMin = 0; //la menor de las horas // ESTABA COMENTANDO POR ESO CREO QUE FALLABA

        for (int i = 1; i < autos.length; i++) {
            if (autos[i] != null) {
                if (autos[i].getEstado() != 3) { //si es igual a 3 no comparo, significa que esta libree
                    if (autos[posMin].getHoraSalida() >= autos[i].getHoraSalida()) {
                        posMin = i;
                    }
                }
                this.controlarParquimetros(i); //para ahorrar un for, controla mientras busca el menor
            }
        }

        if (autos[posMin].getEstado() == 3 || proxLlegada <= autos[posMin].getHoraSalida()) { //entonces genero una llegada
            reloj = proxLlegada;
            if (reloj >= tiempoSimulacion) {
                termino = true;
            } else {
                llegadaAuto();
                evento = "Llegada Auto";
            }
        } else {
            reloj = autos[posMin].getHoraSalida();
            if (reloj >= tiempoSimulacion) {
                termino = true;
            } else {
                finEstacionamiento(posMin);
                evento = "Fin Estacionamiento";
            }
        }
        return evento;
    }

    public void simular(Ventana v, int cantAMostrar) { //este metodo tiene que llamar la ventana
        String evento = "";
        int i = 1;
        this.proxLlegada = this.tEntreLlegadas[0];
        this.rndTiempo1 = this.tEntreLlegadas[2];
        this.tiempo1 = this.tEntreLlegadas[0];
        tEntreLlegadas[0] = 0;
        tEntreLlegadas[2] = 0;
        v.escribirInicioSimulacion(reloj, "Inicio", rndTiempo1, tEntreLlegadas[3], tiempo1, tEntreLlegadas[1], proxLlegada, contAutosSinLugar, contAutosConInfraccion, contAutosSiEstacionaron);
        while (!termino) { //cuando termino esta en true, finaliza la simulación
            evento = this.evento();
            if (reloj >= tInicialAMostrar && i < cantAMostrar) {
                i++;
                System.out.println("\nreloj= " + reloj);
                System.out.println("evento= " + evento);
                System.out.println("rnd1 llegada= " + tEntreLlegadas[2]);
                System.out.println("rnd2 llegada= " + tEntreLlegadas[3]);
                System.out.println("tiempo llegada1= " + tEntreLlegadas[0]);
                System.out.println("tiempo llegada2= " + tEntreLlegadas[1]);
                System.out.println("proximaLLegada= " + proxLlegada);
                System.out.println("rnd t previsto= " + this.rndTurno);
                System.out.println("rnd condicion= " + this.rndTipo);
                System.out.println("condicion= " + condicion);
                System.out.println("Autos " + autos);
                System.out.println("Parquimetros " + parquimetros);
                System.out.println("Autos con infraccion" + this.contAutosConInfraccion);
                System.out.println("autos que si estacionaron " + this.contAutosSiEstacionaron);
                System.out.println("Autos sin lugar " + this.contAutosSinLugar);
                System.out.println("Parquimetros ocupados " + this.contParquimetrosOcupados);

                if (evento == "Llegada Auto") {
                    v.escribirLlegadaAuto(reloj, evento, rndTiempo1, tEntreLlegadas[3], tiempo1, tEntreLlegadas[1], proxLlegada, rndTurno, turno, rndTipo, condicion, tiempoEstacionado, autos, parquimetros, contAutosSinLugar, contAutosConInfraccion, contAutosSiEstacionaron, numeroAuto, posParquimetro, banLlegada, autosConInfraccionesEnEsteEvento);
                    //los indices de los autos que cambiaron estan en autosConInfraccionesEnEsteEvento
                }
                if (evento == "Fin Estacionamiento") {
                    v.escribirFinEstacionamiento(reloj, evento, rndTiempo1, tEntreLlegadas[3], tiempo1, tEntreLlegadas[1], proxLlegada, autos, parquimetros, contAutosSinLugar, contAutosConInfraccion, contAutosSiEstacionaron, posMin, autosConInfraccionesEnEsteEvento);
                    //los indices de los autos que cambiaron estan en autosConInfraccionesEnEsteEvento
                }
            }

        }
    }

    /*
     calculos para el evento llegada Auto
     */
    private void llegadaAuto() {
        numeroAuto++;
        double tiempoTurno;
        if (this.contParquimetrosOcupados >= parquimetros.length) { //no tiene lugar
            this.contAutosSinLugar++;
            banLlegada = false;
        } else { //si tiene lugar
            banLlegada = true;
            tiempoTurno = this.turno();
            posParquimetro = this.buscarParquimetroLibre();

            Auto a = new Auto(1, numeroAuto, reloj, reloj + tiempoTurno);//creo el auto
            autos[posParquimetro] = a;
            this.contParquimetrosOcupados++;
            switch (tipoUso()) {
                case 0: //no pone monedas                   
                    parquimetros[posParquimetro].ocupar();
                    condicion = "No pone monedas";
                    tiempoEstacionado = turno;
                    break;
                case 1: //usa menos del tiempo del turno
                    double tiempo = Generador.uniforme(50, 95);
                    double tTurno = tiempoTurno;
                    tTurno = (tTurno * tiempo) / 100;
                    autos[posParquimetro].setHoraSalida(reloj + tTurno);
                    parquimetros[posParquimetro].ocupar();
                    parquimetros[posParquimetro].actualizarTiempo(reloj + tTurno);
                    condicion = "Usa menos tiempo";
                    tiempoEstacionado = tTurno;
                    break;
                case 2: //usa el tiempo exacto                    
                    parquimetros[posParquimetro].ocupar();
                    parquimetros[posParquimetro].actualizarTiempo(reloj + tiempoTurno);
                    condicion = "Usa tiempo exacto";
                    tiempoEstacionado = turno;
                    break;
                case 3: //usa mas del tiempo
                    double tExtra = Generador.uniforme(5, 15);
                    double tiTurno = tiempoTurno;
                    tiTurno = tiTurno + (tiTurno * tExtra) / 100;
                    autos[posParquimetro].setHoraSalida(reloj + tiTurno);
                    parquimetros[posParquimetro].ocupar();
                    parquimetros[posParquimetro].actualizarTiempo(reloj + tiTurno);
                    condicion = "Usa mas tiempo";
                    tiempoEstacionado = tiTurno;
                    break;
            }

        }
        if (tEntreLlegadas[0] != 0) {
            proxLlegada = reloj + tEntreLlegadas[0];
            this.rndTiempo1 = this.tEntreLlegadas[2];
            this.tiempo1 = this.tEntreLlegadas[0];
            tEntreLlegadas[0] = 0;
            tEntreLlegadas[2] = 0;
        } else if (tEntreLlegadas[1] != 0) {
            proxLlegada = reloj + tEntreLlegadas[1];
            tEntreLlegadas[1] = 0;
            tEntreLlegadas[3] = 0;
        } else {
            tEntreLlegadas = Generador.normalBM(3, 10);
            proxLlegada = reloj + tEntreLlegadas[0];
            this.rndTiempo1 = this.tEntreLlegadas[2];
            this.tiempo1 = this.tEntreLlegadas[0];
            tEntreLlegadas[0] = 0;
            tEntreLlegadas[2] = 0;
        }
    }

    /*
     busca un parquimetro, teniendo en cuenta la prioridad
     */
    private int buscarParquimetroLibre() {
        int i;
        boolean encontro = false;
        for (i = 0; i < parquimetros.length; i++) {
            if (parquimetros[i].isLibreConTiempo()) {
                encontro = true;
                break;
            }
        }
        if (!encontro) {
            for (i = 0; i < parquimetros.length; i++) {
                if (parquimetros[i].isLibre()) {
                    break;
                }
            }
        }
        return i;
    }

    private double rnd() {
        double numero = Math.random();
        int trun = (int) (numero * 100);
        return (double) ((double) (trun) / 100);
    }

    private int tipoUso() {

        rndTipo = rnd();
        if (rndTipo >= 0.00 && rndTipo <= 0.02) {
            return 0;
        }
        if (rndTipo >= 0.03 && rndTipo <= 0.39) {
            return 1;
        }
        if (rndTipo >= 0.40 && rndTipo <= 0.79) {
            return 2;
        }
        return 3;

    }

    private void finEstacionamiento(int id) {
        this.contAutosSiEstacionaron++;
        this.contParquimetrosOcupados--;
        if (parquimetros[id].getHoraFin() < reloj) {
            parquimetros[id].desocupar(3); //libre con saldo
        } else {
            parquimetros[id].desocupar(2); //libre sin saldo
        }
        autos[id].setEstado(3);
    }

    private int turno() {
        rndTurno = rnd();
        turno = 120;

        if (rndTurno >= 0.00 && rndTurno <= 0.39) {
            turno = 60;
            return turno; //en minutos
        }
        return turno;
    }

    private void controlarParquimetros(int i) //controla infracciones y cambia de estadode los atos infractores 
    {
        if (parquimetros[i].getHoraFin() < reloj) {
            if ((autos[i].getEstado() == 1) && (autos[i].getHoraSalida() > reloj)) {
                this.contAutosConInfraccion++;
                autos[i].setEstado(2);
                this.banInfracciones = true;
                this.autosConInfraccionesEnEsteEvento.add(i);
            }
        }

    }

}
