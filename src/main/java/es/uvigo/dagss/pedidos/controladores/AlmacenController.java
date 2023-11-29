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
import es.uvigo.dagss.pedidos.controladores.excepciones.WrongParameterException;
import es.uvigo.dagss.pedidos.entidades.Almacen;
import es.uvigo.dagss.pedidos.entidades.Articulo;
import es.uvigo.dagss.pedidos.entidades.ArticuloAlmacen;
import es.uvigo.dagss.pedidos.servicios.AlmacenService;
import es.uvigo.dagss.pedidos.servicios.ArticuloService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/almacenes", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class AlmacenController {
	@Autowired
	AlmacenService almacenService;
	@Autowired
	ArticuloService articuloService;

	@GetMapping()
	public ResponseEntity<List<Almacen>> buscarTodos() {
		List<Almacen> resultado = new ArrayList<>();
		resultado = almacenService.buscarTodos();
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@RequestMapping(params = "localidad", method = RequestMethod.GET)
	public ResponseEntity<List<Almacen>> buscarPorLocalidad(
			@RequestParam(name = "localidad", required = true) String localidad) {
		List<Almacen> resultado = new ArrayList<>();
		resultado = almacenService.buscarPorLocalidad(localidad);
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@RequestMapping(params = "articuloId", method = RequestMethod.GET)
	public ResponseEntity<List<Almacen>> buscarPorArticuloId(
			@RequestParam(name = "articuloId", required = true) Long articuloId) {
		List<Almacen> resultado = new ArrayList<>();
		resultado = almacenService.buscarPorArticuloId(articuloId);
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@GetMapping(path = "{id}")
	public ResponseEntity<Almacen> buscarPorId(@PathVariable("id") Long id) {
		Optional<Almacen> almacen = almacenService.buscarPorId(id);

		if (almacen.isEmpty()) {
			throw new ResourceNotFoundException("Almacen no encontrado");
		} else {
			return new ResponseEntity<>(almacen.get(), HttpStatus.OK);
		}
	}

	@DeleteMapping(path = "{id}")
	public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id) {
		Optional<Almacen> almacen = almacenService.buscarPorId(id);

		if (almacen.isEmpty()) {
			throw new ResourceNotFoundException("Almacen no encontrado");
		} else {
			almacenService.eliminar(almacen.get());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Almacen> modificar(@PathVariable("id") Long id, @Valid @RequestBody Almacen almacen) {
		Optional<Almacen> almacenOptional = almacenService.buscarPorId(id);

		if (almacenOptional.isEmpty()) {
			throw new ResourceNotFoundException("Almacen no encontrado");
		} else {
			Almacen nuevoAlmacen = almacenService.modificar(almacen);
			return new ResponseEntity<>(nuevoAlmacen, HttpStatus.OK);
		}
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Almacen> crear(@Valid @RequestBody Almacen almacen) {
		Almacen nuevoAlmacen = almacenService.crear(almacen);
		URI uri = crearURIAlmacen(nuevoAlmacen);

		return ResponseEntity.created(uri).body(nuevoAlmacen);
	}

	// GET {id}/articulos
	// Articulos de un Almacen
	@GetMapping(path = "{idAlmacen}/articulos")
	public ResponseEntity<List<ArticuloAlmacen>> buscarArticulosAlmacen(
			@PathVariable("idAlmacen") Long idAlmacen) {
		List<ArticuloAlmacen> resultado = new ArrayList<>();
		resultado = almacenService.buscarArticulosAlmacenPorAlmacenId(idAlmacen);

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	// GET {id}/articulos/{id}
	// Recuperar datos de un Articulo en el Almacén indicado
	@GetMapping(path = "{idAlmacen}/articulos/{idArticulo}")
	public ResponseEntity<ArticuloAlmacen> buscarArticuloAlmacenPorId(
			@PathVariable("idAlmacen") Long idAlmacen, @PathVariable("idArticulo") Long idArticulo) {
		Optional<ArticuloAlmacen> articuloAlmacen = almacenService
				.buscarArticuloAlmacenPorArticuloIdAlmacenId(idArticulo, idAlmacen);

		if (articuloAlmacen.isEmpty()) {
			throw new ResourceNotFoundException("Articulo no existe en Almacen");
		} else {
			return new ResponseEntity<>(articuloAlmacen.get(), HttpStatus.OK);
		}
	}

	// GET {id}/articulos/{id}/stock
	// Recuperar directamente el stock (como entero) de un Articulo en el Almacén
	// indicado
	@GetMapping(path = "{idAlmacen}/articulos/{idArticulo}/stock")
	public ResponseEntity<Integer> leerStockArticuloAlmacenPorId(
			@PathVariable("idAlmacen") Long idAlmacen, @PathVariable("idArticulo") Long idArticulo) {
		Optional<ArticuloAlmacen> articuloAlmacen = almacenService
				.buscarArticuloAlmacenPorArticuloIdAlmacenId(idArticulo, idAlmacen);

		if (articuloAlmacen.isEmpty()) {
			throw new ResourceNotFoundException("Articulo no existe en Almacen");
		} else {
			return new ResponseEntity<>(articuloAlmacen.get().getStock(), HttpStatus.OK);
		}
	}

	// PUT {id}/articulos/{id}
	// Actualizar los datos de Stock de un Artículo en el Almacén indicado")
	@PutMapping(path = "{idAlmacen}/articulos/{idArticulo}")
	public ResponseEntity<ArticuloAlmacen> modificarArticuloAlmacen(
			@PathVariable("idAlmacen") Long idAlmacen, @PathVariable("idArticulo") Long idArticulo,
			@Valid @RequestBody ArticuloAlmacen articuloAlmacen) {
		// Recuperar nuevo stock
		Integer nuevoStock = articuloAlmacen.getStock();
		return _modificarStockArticuloAlmacen(idAlmacen, idArticulo, nuevoStock);
	}

	// PUT {id}/articulos/{id}/stock
	// Actualizar directamente el stock de un Articulo en el Almacén indicado
	@PutMapping(path = "{idAlmacen}/articulos/{idArticulo}/stock")
	public ResponseEntity<ArticuloAlmacen> modificarArticuloAlmacenDirecto(
			@PathVariable("idAlmacen") Long idAlmacen, @PathVariable("idArticulo") Long idArticulo,
			@RequestBody Integer nuevoStock) {
		return _modificarStockArticuloAlmacen(idAlmacen, idArticulo, nuevoStock);
	}

	private ResponseEntity<ArticuloAlmacen> _modificarStockArticuloAlmacen(Long idAlmacen, Long idArticulo,
			Integer stock) {
		Optional<ArticuloAlmacen> articuloAlmacenOptional = almacenService
				.buscarArticuloAlmacenPorArticuloIdAlmacenId(idArticulo, idAlmacen);

		if (articuloAlmacenOptional.isEmpty()) {
			throw new ResourceNotFoundException("Articulo no existe en Almacen");
		} else {
			ArticuloAlmacen articuloAlmacenAModificar = articuloAlmacenOptional.get();
			articuloAlmacenAModificar.setStock(stock);
			ArticuloAlmacen nuevoArticuloAlmacen = almacenService.modificarArticuloAlmacen(articuloAlmacenAModificar);
			return new ResponseEntity<>(nuevoArticuloAlmacen, HttpStatus.OK);
		}
	}

	// DELETE {id}/articulos/{id}
	// Eliminar los datos de Stock de un Artículo en el Almacén indicado
	@DeleteMapping(path = "{idAlmacen}/articulos/{idArticulo}")
	public ResponseEntity<HttpStatus> eliminarArticuloAlmacen(@PathVariable("idAlmacen") Long idAlmacen,
			@PathVariable("idArticulo") Long idArticulo) {
		Optional<ArticuloAlmacen> articuloAlmacen = almacenService
				.buscarArticuloAlmacenPorArticuloIdAlmacenId(idArticulo, idAlmacen);
		if (articuloAlmacen.isEmpty()) {
			throw new ResourceNotFoundException("Articulo no existe en Almacen");
		} else {
			almacenService.eliminarArticuloAlmacen(articuloAlmacen.get());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	// POST {id}/articulos/
	// Crear los datos de Stock de un Artículo nuevo en el Almacén indicado
	@PostMapping(path = "{idAlmacen}/articulos")
	public ResponseEntity<ArticuloAlmacen> crearArticuloAlmacen(@PathVariable("idAlmacen") Long idAlmacen,
			@Valid @RequestBody ArticuloAlmacen articuloAlmacen) {
		Long idArticulo = articuloAlmacen.getArticulo().getId();
		Integer stock = articuloAlmacen.getStock();
		return _crearArticuloAlmacen(idAlmacen, idArticulo, stock);
	}

	// POST {id}/articulos/{id}/stock
	// Crear directamente el stock de un Articulo nuevo en el Almacén indicado
	@PostMapping(path = "{idAlmacen}/articulos/{idArticulo}/stock")
	public ResponseEntity<ArticuloAlmacen> crearArticuloAlmacenDirecto(
			@PathVariable("idAlmacen") Long idAlmacen, 
			@PathVariable("idArticulo") Long idArticulo,
			@RequestBody Integer stock) {
		return _crearArticuloAlmacen(idAlmacen, idArticulo, stock);
	}

	private ResponseEntity<ArticuloAlmacen> _crearArticuloAlmacen(Long idAlmacen, Long idArticulo,
			Integer stock) {
		Optional<ArticuloAlmacen> articuloAlmacenOptional = almacenService
				.buscarArticuloAlmacenPorArticuloIdAlmacenId(idArticulo, idAlmacen);
		if (articuloAlmacenOptional.isPresent()) {
			throw new WrongParameterException("El Articulo indicado ya existe en el Almacen");
		} else {
			Optional<Almacen> almacen = almacenService.buscarPorId(idAlmacen);
			if (almacen.isEmpty()) {
				throw new WrongParameterException("El Almacen indicado no existe");
			}

			Optional<Articulo> articulo = articuloService.buscarPorId(idArticulo);
			if (articulo.isEmpty()) {
				throw new WrongParameterException("El Articulo indicado no existe");
			}
			ArticuloAlmacen nuevoArticuloAlmacen = almacenService.crearArticuloAlmacen(articulo.get(),
					almacen.get(), stock);
			URI uri = crearURIArticuloAlmacen(nuevoArticuloAlmacen);

			return ResponseEntity.created(uri).body(nuevoArticuloAlmacen);
		}
	}

	// Construye la URI del nuevo recurso creado con POST
	private URI crearURIAlmacen(Almacen almacen) {
		return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(almacen.getId())
				.toUri();
	}

	// Construye la URI del nuevo recurso creado con POST
	private URI crearURIArticuloAlmacen(ArticuloAlmacen articuloAlmacen) {
		Long idArticulo = articuloAlmacen.getArticulo().getId();
		return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(idArticulo).toUri();
	}

}
