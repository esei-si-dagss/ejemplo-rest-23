package es.uvigo.dagss.pedidos.integracion;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import es.uvigo.dagss.pedidos.PedidosTestUtils;
import es.uvigo.dagss.pedidos.entidades.Cliente;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class ClienteTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired 
    private TestEntityManager em;

    @BeforeEach
    public void cargarContextoPrueba() {
        PedidosTestUtils.crearDatosPruebaPedidos(em);
    }

    @Test
    @DisplayName("buscarTodos() con GET sobre /api/clientes")
    void buscarTodos() throws Exception {
        // Given 
        List<Cliente> clientes = PedidosTestUtils.CLIENTES_CREADOS;
        List<String> dnis = new ArrayList<>();
        for (Cliente c : clientes) {
            dnis.add(c.getDNI());
        }

        // When (perform() GET sobre /api/clientes)
        // Then (comprobaciones)
        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(clientes.size()))
            .andExpect(jsonPath("$[?(@.dni)].dni", containsInAnyOrder(dnis.toArray())));
    }

    @Test
    @DisplayName("buscarPorLocalidadSinResultados() con GET sobre /api/clientes?localidad=")
    void buscarPorLocalidadSinResultados() throws Exception {
        // Given 
        String localidad = "Nulo";

        // When (perform() GET sobre /api/clientes?localidad=)
        // Then (comprobaciones)
        mockMvc.perform(get("/api/clientes?localidad="+localidad))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("buscarPorNimbreSinResultados() con GET sobre /api/clientes?localidad=")
    void buscarPorNombreSinResultados() throws Exception {
        // Given 
        String nombre = "Nulo";

        // When (perform() GET sobre /api/clientes?nombre=)
        // Then (comprobaciones)
        mockMvc.perform(get("/api/clientes?nombre="+nombre))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("buscarPorDNI() con GET sobre /api/clientes/{dni} con resultados")
    void buscarPorDNIConResultado() throws Exception {
        // Given 
        Cliente clienteExiste = PedidosTestUtils.CLIENTES_CREADOS.get(0);
        String dni = clienteExiste.getDNI();
        String nombre = clienteExiste.getNombre();
 
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
        // Given
        String dni = PedidosTestUtils.CLIENTE_DNI_NO_EXISTE;

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
        String dni = PedidosTestUtils.CLIENTE_DNI_NO_EXISTE;

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
        Cliente clienteExiste = PedidosTestUtils.CLIENTES_CREADOS.get(0);
        String dni = clienteExiste.getDNI();

        // When (perform() DELETE sobre /api/clientes/{dni})
        // Then (comprobaciones)
        mockMvc.perform(delete("/api/clientes/{dni}", dni))
            .andExpect(status().isNoContent());

        // Then (verificar borrado real)  
        Cliente clienteLeido = em.find(Cliente.class, dni);  
        assertThat(clienteLeido).isNull(); 
    }

    @Test
    @DisplayName("modificar() con PUT sobre /api/clientes/{dni} con DNI inexistente")
    void modificarClienteNoExiste() throws Exception {
        // Given 
        String dni = PedidosTestUtils.CLIENTE_DNI_NO_EXISTE;
        Cliente clienteEditado = PedidosTestUtils.CLIENTE_MODIFICADO;

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
        Cliente clienteModificado = PedidosTestUtils.CLIENTE_MODIFICADO;
        String dni = clienteModificado.getDNI();
        String nuevoNombre = clienteModificado.getNombre();

        // When (perform() PUT sobre /api/clientes/{dni})
        // Then (comprobaciones)
        mockMvc.perform(put("/api/clientes/{dni}", dni)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteModificado)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.dni").value(dni))
            .andExpect(jsonPath("$.nombre").value(nuevoNombre));

        // Then (verificar modificacion real)
        Cliente clienteLeido = em.find(Cliente.class, dni);
        assertThat(clienteLeido).isEqualTo(clienteModificado);
    }
}
