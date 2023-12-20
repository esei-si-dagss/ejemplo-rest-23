package es.uvigo.dagss.pedidos.daos;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import es.uvigo.dagss.pedidos.entidades.Cliente;
import es.uvigo.dagss.pedidos.entidades.Direccion;

@DataJpaTest
class ClienteDAOTest {

    @Autowired
    private TestEntityManager em;

    // Componente a probar
    @Autowired
    private ClienteDAO clienteDAO;

    @Test
    @DisplayName("findByNombreContaining sin resultados")
    void findByNombreContainingSinResultados() {
        // Given

        // When
        List<Cliente> found = clienteDAO.findByNombreContaining("Apellido0");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findByNombreContaining con resultados")
    void findByNombreContaining() {
        // Given
        String nombre = "Nombre Apellido1 Apellido2";
        Cliente cliente = new Cliente();
        cliente.setDNI("dni1");
        cliente.setNombre(nombre);
        em.persist(cliente);
        em.flush();

        // When
        List<Cliente> found = clienteDAO.findByNombreContaining("Apellido1");

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getNombre()).isEqualTo(nombre);
    }

    @Test
    @DisplayName("findByDireccionLocalidad sin resultados")
    void findByDireccionLocalidadSinResultados() {
        // Given

        // When
        List<Cliente> found = clienteDAO.findByDireccionLocalidad("Localidad0");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findByDireccionLocalidad con resultados")
    void findByDireccionLocalidad() {
        // Given
        String localidad = "Localidad1";
        Cliente cliente = new Cliente();
        cliente.setDNI("dni1");
        cliente.setDireccion(new Direccion("Domicilio1", localidad, "CodigoPostal1", "Provincia1", "Telefono1"));
        em.persist(cliente);
        em.flush();

        // When
        List<Cliente> found = clienteDAO.findByDireccionLocalidad(localidad);

        // Then
        assertThat(found).isNotEmpty();
        Cliente c = found.get(0);
        Direccion d = c.getDireccion();
        assertThat(d).isNotNull();
        assertThat(d.getLocalidad()).isEqualTo(localidad);
    }
}