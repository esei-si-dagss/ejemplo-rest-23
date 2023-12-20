package es.uvigo.dagss.pedidos;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import es.uvigo.dagss.pedidos.entidades.Articulo;
import es.uvigo.dagss.pedidos.entidades.Cliente;
import es.uvigo.dagss.pedidos.entidades.Direccion;
import es.uvigo.dagss.pedidos.entidades.Familia;
import es.uvigo.dagss.pedidos.entidades.LineaPedido;
import es.uvigo.dagss.pedidos.entidades.Pedido;

public class PedidosTestUtils {
	// Referencias a entidades creadas para usar en los tests
    public static Cliente CLIENTE_CON_PEDIDOS;
    public static Cliente CLIENTE_SIN_PEDIDOS;

	public static String CLIENTE_DNI_EXISTE;
	public static String CLIENTE_DNI_NO_EXISTE;

    public static Long PEDIDO_ID_EXISTENTE;
    public static Long PEDIDO_ID_NO_EXISTENTE = 9999L;

    public static List<Pedido> PEDIDOS_CLIENTE_CON_PEDIDOS;

    public static Pedido PEDIDO_A_CREAR;

	public static List<Cliente> CLIENTES_CREADOS;
    public static Cliente CLIENTE_A_CREAR;
    public static Cliente CLIENTE_MODIFICADO;

    public static void crearDatosPruebaPedidos(TestEntityManager em) {
        // Insertar Entidades en BD
		Familia f1 = new Familia("familia1", "familia1");
		Familia f2 = new Familia("familia2", "familia2");
		em.persist(f1);
		em.persist(f2);

		Articulo a1 = new Articulo("articulo1", "articulo1", f1, 10.0);
		Articulo a2 = new Articulo("articulo2", "articulo2", f1, 100.0);
		Articulo a3 = new Articulo("articulo3", "articulo3", f2, 20.0);
		Articulo a4 = new Articulo("articulo4", "articulo4", f2, 200.0);
		em.persist(a1);
		em.persist(a2);
		em.persist(a3);
		em.persist(a4);


		Cliente c1 = new Cliente("12345678A", "Cliente1 Cliente1 Cliente1", new Direccion("calle1", "localidad1", "11111", "provincia1", "111111111"));
		Cliente c2 = new Cliente("12345678B", "Cliente2 Cliente2 Cliente2", new Direccion("calle2", "localidad2", "22222", "provincia2", "222222222"));
		Cliente c3 = new Cliente("12345678C", "Cliente3 Cliente3 Cliente3", new Direccion("calle3", "localidad3", "33333", "provincia3", "333333333"));
		em.persist(c1);
		em.persist(c2);
		em.persist(c3);


        Pedido p1 = new Pedido(Calendar.getInstance().getTime(), c1);
		p1.anadirLineaPedido(new LineaPedido(p1, 1, a1, a1.getPrecioUnitario()));
		p1.anadirLineaPedido(new LineaPedido(p1,2, a2, a2.getPrecioUnitario()));
		p1.anadirLineaPedido(new LineaPedido(p1, 3, a3, a3.getPrecioUnitario()));
		em.persist(p1);

		Pedido p2 = new Pedido(Calendar.getInstance().getTime(), c1);
		p2.anadirLineaPedido(new LineaPedido(p2, 1, a4, a4.getPrecioUnitario()));
		p2.anadirLineaPedido(new LineaPedido(p2, 2, a1, a1.getPrecioUnitario()));
		em.persist(p2);

        em.flush();


        // Mantener referencias para Tests
        CLIENTE_CON_PEDIDOS = c1;
        CLIENTE_SIN_PEDIDOS = c2;
		CLIENTES_CREADOS = Arrays.asList(c1, c2, c3);
		CLIENTE_DNI_EXISTE = CLIENTES_CREADOS.get(0).getDNI();
		CLIENTE_DNI_NO_EXISTE = "99999999Z";
		CLIENTE_MODIFICADO = new Cliente(c1.getDNI(), "Nuevo Nombre", c1.getDireccion());
        CLIENTE_A_CREAR = new Cliente("12345678D", "Cliente4 Cliente4 Cliente4", new Direccion("calle4", "localidad4", "44444", "provincia4", "444444444"));

        PEDIDO_ID_EXISTENTE = p1.getId();
        PEDIDO_ID_NO_EXISTENTE = 9999L;

        PEDIDOS_CLIENTE_CON_PEDIDOS = Arrays.asList(p1,p2);

		PEDIDO_A_CREAR = new Pedido(Calendar.getInstance().getTime(), c3);
		PEDIDO_A_CREAR.anadirLineaPedido(new LineaPedido(PEDIDO_A_CREAR, 1, a1, a1.getPrecioUnitario()));
		PEDIDO_A_CREAR.anadirLineaPedido(new LineaPedido(PEDIDO_A_CREAR, 2, a2, a2.getPrecioUnitario()));
    }
    
}
