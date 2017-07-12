package component;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import entity.Regions;

@Repository
public class RespositoryRegiones {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public Regions read (int id_region)
	{
		Regions region = null;
		
			region = entityManager.find(Regions.class, new BigDecimal(id_region));
		
		return region;
	}
	
	public void update (Regions region)
	{
		
		entityManager.merge(region);
		
	}
	
	public void create (Regions region)
	{
		entityManager.persist(region);
	}
	
	public void delete (Integer id_region)
	{
		Regions region = null;
		
			region = read(id_region);
			entityManager.remove(region);//si intetamos borrar con null, dará una excepción de tipo IllegalArgumentException
	}	
	
	

}
