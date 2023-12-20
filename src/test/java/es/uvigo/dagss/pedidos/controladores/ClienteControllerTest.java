package es.uvigo.dagss.pedidos.controladores;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.mockito.Mockito;


import es.uvigo.dagss.pedidos.entidades.Cliente;
import es.uvigo.dagss.pedidos.entidades.Direccion;
import es.uvigo.dagss.pedidos.servicios.ClienteService;

@WebMvcTest(controllers = ClienteController.class)
@AutoConfigureMockMvc
public class ClienteControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean  // Inyecta una implementacion de ClienteService simulada
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("buscarTodos() con GET sobre /api/clientes")
    void buscarTodos() throws Exception {
        // Given (preparar contexto con Mock de ClienteService)
        List<Cliente> clientes = Arrays.asList(
            new Cliente("11111111A", "Cliente1", new Direccion()),
            new Cliente("11111111B", "Cliente2", new Direccion()),
            new Cliente("11111111C", "Cliente3", new Direccion())
        );
        String[] dnis = {"11111111A", "11111111B", "11111111C"};
        Mockito.when(clienteService.buscarTodos()).thenReturn(clientes);

        // When (perform() GET sobre /api/clientes)
        // Then (comprobaciones)
        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(clientes.size()))
            .andExpect(jsonPath("$[?(@.dni)].dni", containsInAnyOrder(dnis)));
    }

    @Test
    @DisplayName("buscarTodos() con GET sobre /api/clientes sin resultados")
    void buscarTodosSinResultado() throws Exception {
        // Given (preparar contexto con Mock de ClienteService)
        List<Cliente> clientes = Collections.emptyList();
        Mockito.when(clienteService.buscarTodos()).thenReturn(clientes);

        // When (perform() GET sobre /api/clientes)
        // Then (comprobaciones)
        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("buscarPorDNI() con GET sobre /api/clientes/{dni} con resultados")
    void buscarPorDNIConResultado() throws Exception {
        // Given (preparar contexto con Mock de ClienteService)
        String dni = "11111111A";
        String nombre = "Cliente1";
        Cliente cliente = new Cliente(dni, nombre, new Direccion());
        Mockito.when(clienteService.buscarPorDNI(dni)).thenReturn(Optional.of(cliente));

        // When (perform() GET sobre /api/clientes/{dni})
        // Then (comprobaciones)
        mockMvc.perform(get("/api/clientes/{dni}", dni))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.dni").value(dni))
            .andExpect(jsonPath("$.nombre").value(nombre));
    }

    @Test
    @DisplayName("buscarPorDNI() con GET sobre /api/clientes/{dni} sin resultados")
    void buscarPorDNISinResultado() throws Exception {
        // Given (preparar contexto con Mock de ClienteService)
        String dni = "00000000A";
        Mockito.when(clienteService.buscarPorDNI(dni)).thenReturn(Optional.ofNullable(null));

        // When (perform() GET sobre /api/clientes/{dni})
        // Then (comprobaciones)
        mockMvc.perform(get("/api/clientes/{dni}", dni))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.detail").value("Cliente no encontrado"));
    }


    @Test
    @DisplayName("eliminar() con DELETE sobre /api/clientes/{dni} con DNI inexistente")
    void eliminarClienteNoExiste() throws Exception {
        // Given (preparar contexto con Mock de ClienteService)
        String dni = "00000000A";
        Mockito.when(clienteService.buscarPorDNI(dni)).thenReturn(Optional.ofNullable(null));

        // When (perform() DELETE sobre /api/clientes/{dni})
        // Then (comprobaciones)
        mockMvc.perform(delete("/api/clientes/"+dni))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.detail").value("Cliente no encontrado"));
    }

    @Test
    @DisplayName("eliminar() con DELETE sobre /api/clientes/{dni} con DNI existente")
    void eliminar() throws Exception {
        // Given (preparar contexto con Mock de ClienteService)
        String dni = "11111111A";
        String nombre = "Cliente1";
        Cliente cliente = new Cliente(dni, nombre, new Direccion());
        Mockito.when(clienteService.buscarPorDNI(dni)).thenReturn(Optional.of(cliente));
        // Por defecto los metodos void, como ClienteService.eliminar(), no "hacen nada" en Mockito => no es necesario especificarlos

        // When (perform() DELETE sobre /api/clientes/{dni})
        // Then (comprobaciones)
        mockMvc.perform(delete("/api/clientes/{dni}", dni))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("modificar() con PUT sobre /api/clientes/{dni} con DNI inexistente")
    void modificarClienteNoExiste() throws Exception {
        // Given (preparar contexto con Mock de ClienteService)
        String dni = "00000000A";
        Cliente clienteEditado = new Cliente(dni, "", new Direccion());

        Mockito.when(clienteService.buscarPorDNI(dni)).thenReturn(Optional.ofNullable(null));

        // When (perform() PUT sobre /api/clientes/{dni})
        // Then (comprobaciones)
        mockMvc.perform(put("/api/clientes/{dni}", dni)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(clienteEditado)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.detail").value("Cliente no encontrado"));
    }

    @Test
    @DisplayName("modificar() con PUT sobre /api/clientes/{dni} con DNI existente")
    void modificar() throws Exception {
        // Given (preparar contexto con Mock de ClienteService)
        String dni = "11111111A";
        String nombre = "Cliente1";
        String nuevoNombre = "NuevoCliente1";

        Cliente clienteAnterior = new Cliente(dni, nombre, new Direccion());
        Cliente clienteEditado = new Cliente(dni, nuevoNombre, new Direccion());
        Mockito.when(clienteService.buscarPorDNI(dni)).thenReturn(Optional.of(clienteAnterior));
        Mockito.when(clienteService.modificar(clienteEditado)).thenReturn(clienteEditado);

        // When (perform() PUT sobre /api/clientes/{dni})
        // Then (comprobaciones)
        mockMvc.perform(put("/api/clientes/{dni}", dni)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteEditado)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.dni").value(dni))
            .andExpect(jsonPath("$.nombre").value(nuevoNombre));
    }
}
