package com.example.trasteapp;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class BuscarTrasterosTest {

    static class Trastero {
        String ciudad;
        boolean alquilado;
        boolean reservado;
        long horasDesdeReserva;

        Trastero(String ciudad, boolean alquilado, boolean reservado, long horasDesdeReserva) {
            this.ciudad = ciudad;
            this.alquilado = alquilado;
            this.reservado = reservado;
            this.horasDesdeReserva = horasDesdeReserva;
        }
    }

    private boolean estaDisponible(Trastero t) {
        return !t.alquilado && (!t.reservado || t.horasDesdeReserva >= 24);
    }

    @Test
    public void testFiltroTrasterosDisponibles() {
        List<Trastero> lista = Arrays.asList(
                new Trastero("Oviedo", false, false, 0),
                new Trastero("Gijón", true, false, 0),
                new Trastero("Avilés", false, true, 10),
                new Trastero("Mieres", false, true, 30)
        );

        List<Trastero> disponibles = new ArrayList<>();
        for (Trastero t : lista) {
            if (estaDisponible(t)) {
                disponibles.add(t);
            }
        }

        System.out.println("Trasteros disponibles encontrados: " + disponibles.size());
        for (Trastero t : disponibles) {
            System.out.println("Trastero disponible: " + t.ciudad);
        }

        assertEquals(2, disponibles.size());
        assertEquals("Oviedo", disponibles.get(0).ciudad);
        assertEquals("Mieres", disponibles.get(1).ciudad);
    }
}
