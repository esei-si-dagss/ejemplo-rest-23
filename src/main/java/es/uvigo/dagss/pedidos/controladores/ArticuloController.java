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

import es.uvigo.dagss.pedidos.controladores.excepciones.ResourceNotFoundException;
import es.uvigo.dagss.pedidos.entidades.Articulo;
import es.uvigo.dagss.pedidos.servicios.ArticuloService;

@RestController
@RequestMapping(path = "/api/articulos", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class ArticuloController {
	@Autowired
	ArticuloService articuloService;

	@GetMapping()
	public ResponseEntity<List<Articulo>> buscarTodos() {
		List<Articulo> resultado = new ArrayList<>();
		resultado = articuloService.buscarTodos();
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@RequestMapping(params = "familiaId", method = RequestMethod.GET)
	public ResponseEntity<List<Articulo>> buscarPorFamiliaId(
			@RequestParam(name = "familiaId", required = true) Long familiaId) {
		List<Articulo> resultado = new ArrayList<>();
		resultado = articuloService.buscarPorFamilia(familiaId);
		return new ResponseEntity<>(resultado, HttpStatus.OK);

	}

	@RequestMapping(params = "descripcion", method = RequestMethod.GET)
	public ResponseEntity<List<Articulo>> buscarPorDescripcion(
			@RequestParam(name = "descripcion", required = true) String descripcion) {
		List<Articulo> resultado = new ArrayList<>();
		resultado = articuloService.buscarPorDescripcion(descripcion);

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@GetMapping(path = "{id}")
	public ResponseEntity<Articulo> buscarPorId(@PathVariable("id") Long id) {
		Optional<Articulo> articulo = articuloService.buscarPorId(id);

		if (articulo.isEmpty()) {
			throw new ResourceNotFoundException("Articulo no encontrado");
		} else {
			return new ResponseEntity<>(articulo.get(), HttpStatus.OK);
		}
	}

	@DeleteMapping(path = "{id}")
	public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id) {
		Optional<Articulo> articulo = articuloService.buscarPorId(id);
		if (articulo.isEmpty()) {
			throw new ResourceNotFoundException("Articulo no encontrado");
		} else {
			articuloService.eliminar(articulo.get());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Articulo> modificar(@PathVariable("id") Long id, @RequestBody Articulo articulo) {
		Optional<Articulo> articuloOptional = articuloService.buscarPorId(id);

		if (articuloOptional.isEmpty()) {
			throw new ResourceNotFoundException("Articulo no encontrado");
		} else {
			Articulo nuevoArticulo = articuloService.modificar(articulo);
			return new ResponseEntity<>(nuevoArticulo, HttpStatus.OK);
		}
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Articulo> crear(@RequestBody Articulo articulo) {
		Articulo nuevoArticulo = articuloService.crear(articulo);
		URI uri = crearURIArticulo(nuevoArticulo);

		return ResponseEntity.created(uri).body(nuevoArticulo);
	}

	// Construye la URI del nuevo recurso creado con POST
	private URI crearURIArticulo(Articulo articulo) {
		return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(articulo.getId())
				.toUri();
	}

}
