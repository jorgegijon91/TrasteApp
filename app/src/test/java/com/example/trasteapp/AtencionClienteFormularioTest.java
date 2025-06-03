package com.example.trasteapp;

import org.junit.Test;
import static org.junit.Assert.*;

public class AtencionClienteFormularioTest {

    static class FormularioAtencion {
        String asunto;
        String mensaje;

        FormularioAtencion(String asunto, String mensaje) {
            this.asunto = asunto != null ? asunto.trim() : "";
            this.mensaje = mensaje != null ? mensaje.trim() : "";
        }

        boolean esValido() {
            return !asunto.isEmpty() && !mensaje.isEmpty();
        }
    }

    @Test
    public void StestFormularioValido() {
        FormularioAtencion form = new FormularioAtencion("Problema con el contrato", "No puedo firmarlo");

        assertTrue(form.esValido());
        System.out.println("Formulario válido " + "\n" + "Asunto: " + form.asunto+ "\n"  + "Mensaje: " + form.mensaje);
    }

    @Test
    public void testFormularioConCamposVacios() {
        FormularioAtencion form1 = new FormularioAtencion("", "Mensaje");
        FormularioAtencion form2 = new FormularioAtencion("Asunto", "");

        assertFalse(form1.esValido());
        assertFalse(form2.esValido());
        System.out.println("Formulario inválido por campos vacíos");
    }

    @Test
    public void testFormularioEspaciosSolo() {
        FormularioAtencion form = new FormularioAtencion("   ", "   ");

        assertFalse(form.esValido());
        System.out.println("Formulario solo con espacios no es válido");
    }
}
