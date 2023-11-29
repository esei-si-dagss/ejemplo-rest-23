package es.uvigo.dagss.pedidos.servicios;

import java.util.List;
import java.util.Optional;

import es.uvigo.dagss.pedidos.entidades.Pedido;
import es.uvigo.dagss.pedidos.entidades.Cliente;

public interface PedidoService {
	public Pedido crear(Pedido pedido);
	public Pedido modificar(Pedido pedido);
	public void eliminar(Pedido pedido);
	public Optional<Pedido> buscarPorId(Long id);
	public Pedido buscarPorIdConLineas(Long id);	
	public List<Pedido> buscarTodos();
	public List<Pedido> buscarPorCliente(Cliente cliente);

}
