package component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entity.Regions;

@Service
public class ServiceRegiones {
	
	@Autowired
	private RespositoryRegiones repository_regiones;
	
	@Transactional
	public Regions leerRegion (int id_region)
	{
		Regions region_leida = null;
		
			region_leida = repository_regiones.read(id_region);
		
		return region_leida;
	}
	
	@Transactional
	public void actualizarRegion (Regions region_actualizar)
	{
		repository_regiones.update(region_actualizar);
	}
	
	@Transactional
	public void insertarRegion (Regions region_insertar)
	{
		repository_regiones.create(region_insertar);
		
	}
	
	@Transactional
	public void borrarRegion (Integer id_region)
	{
		repository_regiones.delete(id_region);
	}
}
