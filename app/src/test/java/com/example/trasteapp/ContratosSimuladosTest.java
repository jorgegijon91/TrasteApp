package com.example.trasteapp;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

public class ContratosSimuladosTest {

    static class Contrato {
        String ciudad;
        boolean firmado;

        Contrato(String ciudad, boolean firmado) {
            this.ciudad = ciudad;
            this.firmado = firmado;
        }
    }

    @Test
    public void testHayContratosSinFirmar() {
        List<Contrato> contratos = Arrays.asList(
                new Contrato("Gijón", true),
                new Contrato("Oviedo", false),
                new Contrato("Avilés", false)
        );

        long pendientes = contratos.stream().filter(c -> !c.firmado).count();

        System.out.println("Contratos sin firmar: " + pendientes);
        assertTrue("Debe haber al menos un contrato sin firmar", pendientes > 0);
    }

    @Test
    public void testFirmarTodosLosContratos() {
        List<Contrato> contratos = Arrays.asList(
                new Contrato("Gijón", true),
                new Contrato("Oviedo", false),
                new Contrato("Avilés", false)
        );

        System.out.println(" Iniciando firma de contratos...");
        for (Contrato c : contratos) {
            if (!c.firmado) {
                System.out.println("Firmando contrato en " + c.ciudad);
                c.firmado = true;
            }
        }

        boolean todosFirmados = contratos.stream().allMatch(c -> c.firmado);

        System.out.println("Verificación final de contratos:");
        for (Contrato c : contratos) {
            System.out.println("Contrato en " + c.ciudad + " firmado: " + c.firmado);
        }

        assertTrue("Todos los contratos deben estar firmados", todosFirmados);
    }
}
