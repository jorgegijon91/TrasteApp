package com.example.trasteapp;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class EmpresasPortesTest {

    static class Empresa {
        String nombre;
        String url;

        Empresa(String nombre, String url) {
            this.nombre = nombre;
            this.url = url;
        }
    }

    private final Map<String, List<Empresa>> empresasPorCiudad = new HashMap<>();

    @Before
    public void cargarEmpresas() {
        empresasPorCiudad.clear();

        empresasPorCiudad.put("gijón", Arrays.asList(
                new Empresa("Mudanzas Mario", "https://transportesymudanzasmario.com/"),
                new Empresa("Mudanzas Jose-Vicente", "https://www.jose-vicente.es/")
        ));
        empresasPorCiudad.put("oviedo", Arrays.asList(
                new Empresa("Mudanzas Trasnporteastur", "https://transporteastur.es/"),
                new Empresa("Mudanzas Abraham", "https://mudanzasabraham.com/")
        ));

        // Cargar "todas" sin incluirse a sí misma
        List<Empresa> todas = new ArrayList<>();
        for (Map.Entry<String, List<Empresa>> entry : empresasPorCiudad.entrySet()) {
            if (!entry.getKey().equals("todas")) {
                todas.addAll(entry.getValue());
            }
        }
        empresasPorCiudad.put("todas", todas);
    }


    @Test
    public void testBuscarEmpresasEnOviedo() {
        List<Empresa> lista = empresasPorCiudad.get("oviedo");
        System.out.println("Empresas en Oviedo:");
        for (Empresa e : lista) System.out.println("Mudanzas: " + e.nombre);

        assertNotNull(lista);
        assertEquals(2, lista.size());
    }

    @Test
    public void testBuscarEmpresasEnCiudadInexistente() {
        List<Empresa> lista = empresasPorCiudad.get("zaragoza");
        System.out.println("Empresas en Zaragoza: " + (lista == null ? "no hay empresas en esta ciudad" : lista.size()));

        assertNull(lista);
    }

    @Test
    public void testBuscarEmpresasEnTodas() {
        List<Empresa> lista = empresasPorCiudad.get("todas");
        System.out.println("Empresas en modo global:");
        for (Empresa e : lista) System.out.println("Empresa de mudanzas: " + e.nombre);

        assertNotNull(lista);
        assertTrue(lista.size() >= 4); // Gijón + Oviedo = 4
    }
}
