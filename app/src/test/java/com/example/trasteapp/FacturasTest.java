package com.example.trasteapp;

import org.junit.Test;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class FacturasTest {

    static class Factura {
        String id;
        Date fecha;
        double importe;
        String estado; // "pendiente", "pagada"
        boolean domiciliado;

        Factura(String id, Date fecha, double importe) {
            this.id = id;
            this.fecha = fecha;
            this.importe = importe;
            this.estado = "pendiente";
            this.domiciliado = false;
        }
    }

    @Test
    public void testGenerarFactura() {
        Factura factura = new Factura("factura_202505", new Date(), 40.0);

        System.out.println("Factura generada con estado: " + factura.estado);
        assertEquals("pendiente", factura.estado);
        assertEquals(40.0, factura.importe, 0.001);
        System.out.println("Importe: " + factura.importe);
    }

    @Test
    public void testPagoDeFactura() {
        Factura factura = new Factura("factura_202505", new Date(), 40.0);
        factura.estado = "pagada";

        System.out.println("Factura pagada. Estado actual: " + factura.estado + ". Importe: " + factura.importe);
        assertEquals("pagada", factura.estado);

    }

    @Test
    public void testDomiciliarFactura() {
        Factura factura = new Factura("factura_202505", new Date(), 40.0);
        factura.domiciliado = true;

        System.out.println("Factura domiciliada: " + factura.domiciliado);
        assertTrue(factura.domiciliado);
    }

    @Test
    public void testEvitarDoblePagoEnMismoMes() {
        List<Factura> historial = new ArrayList<>();
        historial.add(new Factura("factura_202505", new Date(), 40.0));
        historial.get(0).estado = "pagada";

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String mesActual = new SimpleDateFormat("yyyyMM").format(cal.getTime());

        boolean yaPagadaEsteMes = historial.stream()
                .anyMatch(f -> f.id.contains(mesActual) && "pagada".equalsIgnoreCase(f.estado));

        System.out.println("¿Ya hay una factura pagada este mes?: " + yaPagadaEsteMes);
        assertTrue(yaPagadaEsteMes);
    }
}
