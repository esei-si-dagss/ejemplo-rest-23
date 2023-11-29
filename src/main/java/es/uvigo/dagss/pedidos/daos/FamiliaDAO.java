package es.uvigo.dagss.pedidos.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.uvigo.dagss.pedidos.entidades.Familia;

public interface FamiliaDAO extends JpaRepository<Familia, Long> {
	public List<Familia> findByNombre(String nombre);
    
	@Query("SELECT f FROM Familia f WHERE f.descripcion LIKE %:patron%")
	public List<Familia> findByPatronDescripcion(@Param("patron") String patron);
	// Podria haberse usado findByDescripcionContaining()
}
