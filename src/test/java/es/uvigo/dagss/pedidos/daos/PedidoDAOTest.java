package es.uvigo.dagss.pedidos.daos;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import es.uvigo.dagss.pedidos.PedidosTestUtils;
import es.uvigo.dagss.pedidos.entidades.LineaPedido;
import es.uvigo.dagss.pedidos.entidades.Pedido;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PedidoDAOTest {

    @Autowired
    private TestEntityManager em;

    // Componente a probar
    @Autowired
    private PedidoDAO pedidoDAO;


    @BeforeEach
    public void cargarContextoPrueba() {
        PedidosTestUtils.crearDatosPruebaPedidos(em);
    }


    @Test
    @DisplayName("findPedidoConLineas sin resultados")
    public void testFindPedidoConLineasSinResultados() {
        // Given
        Long pedidoId = PedidosTestUtils.PEDIDO_ID_NO_EXISTENTE;

        // When
        Pedido foundPedido = pedidoDAO.findPedidoConLineas(pedidoId);

        // Then
        assertThat(foundPedido).isNull();
    }


    @Test
    @DisplayName("findPedidoConLineas con resultados")
    public void testFindPedidoConLineas() {
        // Given
        Long pedidoId = PedidosTestUtils.PEDIDO_ID_EXISTENTE;
        Pedido pedidoConLineas = em.find(Pedido.class, pedidoId);
        int numLineas = pedidoConLineas.getLineas().size();

        // When
        Pedido foundPedido = pedidoDAO.findPedidoConLineas(pedidoId);

        // Then
        assertThat(foundPedido).isNotNull();
        List<LineaPedido>  lineas = foundPedido.getLineas();
        assertThat(lineas).isNotNull();
        assertThat(lineas).isNotEmpty();
        assertThat(lineas.size()).isEqualTo(numLineas);
    }

    @Test
    @DisplayName("findByClienteDNI sin resultados")
    public void testFindByClienteDNISinResutlados() {
        // Given
        String dni = PedidosTestUtils.CLIENTE_SIN_PEDIDOS.getDNI();  

        // When
        List<Pedido> pedidos = pedidoDAO.findByClienteDNI(dni);

        // Then
        assertThat(pedidos).isEmpty();
    }

    @Test
    @DisplayName("findByClienteDNI con resultados")
    public void testFindByClienteDNI() {
        // Given
        String dni = PedidosTestUtils.CLIENTE_CON_PEDIDOS.getDNI();  
        int numPedidos = PedidosTestUtils.PEDIDOS_CLIENTE_CON_PEDIDOS.size();

        // When
        List<Pedido> pedidos = pedidoDAO.findByClienteDNI(dni);

        // Then
        assertThat(pedidos).isNotEmpty();
        assertThat(pedidos.size()).isEqualTo(numPedidos);
    }

    @Test
    @DisplayName("crear Pedidos con Lineas de Pedido")
    public void testCrearPedidoConLineasPedido() {
        // Given
        Pedido pedidoACrear = PedidosTestUtils.PEDIDO_A_CREAR;
        int numLineas = pedidoACrear.getLineas().size();

        // When
                Pedido pedidoCreado = pedidoDAO.save(pedidoACrear);

        // Then
        Optional<Pedido> pedidoLeido = pedidoDAO.findById(pedidoCreado.getId());
        assertThat(pedidoLeido).isPresent();
        assertThat(pedidoLeido.get().getLineas().size()).isEqualTo(numLineas);
    }

}

