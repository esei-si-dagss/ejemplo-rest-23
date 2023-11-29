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
import es.uvigo.dagss.pedidos.entidades.Familia;
import es.uvigo.dagss.pedidos.servicios.ArticuloService;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/familias", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
public class FamiliaController {
	@Autowired
	ArticuloService articuloService;

	@GetMapping()
	public ResponseEntity<List<Familia>> buscarTodos() {
		List<Familia> resultado = new ArrayList<>();
		resultado = articuloService.buscarFamilias();
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@RequestMapping(params = "descripcion", method = RequestMethod.GET)
	public ResponseEntity<List<Familia>> buscarPorDescripcion(
			@RequestParam(name = "descripcion", required = true) String descripcion) {
		List<Familia> resultado = new ArrayList<>();
		resultado = articuloService.buscarFamiliasPorDescripcion(descripcion);
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@GetMapping(path = "{id}")
	public ResponseEntity<Familia> buscarPorId(@PathVariable("id") Long id) {
		Optional<Familia> familia = articuloService.buscarFamiliaPorId(id);

		if (familia.isEmpty()) {
			throw new ResourceNotFoundException("Familia no encontrada");
		} else {
			return new ResponseEntity<>(familia.get(), HttpStatus.OK);
		}
	}

	@DeleteMapping(path = "{id}")
	public ResponseEntity<HttpStatus> eliminar(@PathVariable("id") Long id) {
		Optional<Familia> familia = articuloService.buscarFamiliaPorId(id);
		if (familia.isEmpty()) {
			throw new ResourceNotFoundException("Familia no encontrada");
		} else {
			articuloService.eliminarFamilia(familia.get());
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
	}

	@PutMapping(path = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Familia> modificar(@PathVariable("id") Long id, @RequestBody Familia familia) {
		Optional<Familia> familiaOptional = articuloService.buscarFamiliaPorId(id);

		if (familiaOptional.isEmpty()) {
			throw new ResourceNotFoundException("Familia no encontrada");
		} else {
			Familia nuevaFamilia = articuloService.modificarFamilia(familia);
			return new ResponseEntity<>(nuevaFamilia, HttpStatus.OK);
		} 
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Familia> crear(@Valid @RequestBody Familia familia) {
			Familia nuevaFamilia = articuloService.crearFamilia(familia);
			URI uri = crearURIFamilia(nuevaFamilia);

			return ResponseEntity.created(uri).body(nuevaFamilia);
	}

	// Construye la URI del nuevo recurso creado con POST
	private URI crearURIFamilia(Familia familia) {
		return ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{id}").buildAndExpand(familia.getId())
				.toUri();
	}

}
