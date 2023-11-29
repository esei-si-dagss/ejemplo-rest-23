package es.uvigo.dagss.pedidos.controladores;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.uvigo.dagss.pedidos.entidades.Pedido;
import es.uvigo.dagss.pedidos.controladores.excepciones.ResourceNotFoundException;
import es.uvigo.dagss.pedidos.entidades.Cliente;
import es.uvigo.dagss.pedidos.servicios.PedidoService;
import es.uvigo.dagss.pedidos.servicios.ClienteService;

@RestController
@RequestMapping(path = "/api/pedidos", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class PedidoController {
	@Autowired
	PedidoService pedidoService;

	@Autowired
	ClienteService clienteService;

	@GetMapping()
	public ResponseEntity<List<Pedido>> buscarTodos() {
		List<Pedido> resultado = new ArrayList<>();
		resultado = pedidoService.buscarTodos();
		return new ResponseEntity<>(resultado, HttpStatus.OK);

	}

	@RequestMapping(params = "clienteDNI", method = RequestMethod.GET)
	public ResponseEntity<List<Pedido>> buscarPorClienteDNI(
			@RequestParam(name = "clienteDNI", required = true) String clienteDni) {
		List<Pedido> resultado = new ArrayList<>();

		Optional<Cliente> cliente = clienteService.buscarPorDNI(clienteDni);
		if (cliente.isEmpty()) {
			throw new ResourceNotFoundException("Cliente no encontrado");
		} else {
			resultado = pedidoService.buscarPorCliente(cliente.get());
			return new ResponseEntity<>(resultado, HttpStatus.OK);
		}
	}

	@GetMapping(path = "{id}")
	public ResponseEntity<Pedido> buscarPorId(@PathVariable("id") Long id) {
		Pedido pedido = pedidoService.buscarPorIdConLineas(id);

		if (pedido == null) {
			throw new ResourceNotFoundException("Pedido no encontrado");
		} else {
			return new ResponseEntity<>(pedido, HttpStatus.OK);
		}
	}

	@DeleteMapping(path = "{id}")
	public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id) {
		Optional<Pedido> pedido = pedidoService.buscarPorId(id);
		if (pedido.isEmpty()) {
			throw new ResourceNotFoundException("Pedido no encontrado");
		} else {
			pedidoService.eliminar(pedido.get());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Pedido> modificar(@PathVariable("id") Long id, @RequestBody Pedido pedido) {
		Optional<Pedido> pedidoOptional = pedidoService.buscarPorId(id);

		if (pedidoOptional.isEmpty()) {
			throw new ResourceNotFoundException("Pedido no encontrado");
		} else {
			Pedido nuevoPedido = pedidoService.modificar(pedido);
			return new ResponseEntity<>(nuevoPedido, HttpStatus.OK);
		}
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Pedido> crear(@RequestBody Pedido pedido) {
		Pedido nuevoPedido = pedidoService.crear(pedido);
		URI uri = crearURIPedido(nuevoPedido);

		return ResponseEntity.created(uri).body(nuevoPedido);
	}

	// Construye la URI del nuevo recurso creado con POST
	private URI crearURIPedido(Pedido pedido) {
		return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(pedido.getId()).toUri();
	}

}
