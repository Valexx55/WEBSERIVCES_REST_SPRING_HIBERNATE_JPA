package component;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import entity.Regions;

/**
 * EJEMPLO DE IMPLEMENTACÓN DE WEB SERVICE REST CON SPRING Y JPA, TRATANDO LA ENTIDAD REGIONS
 * DEL ESQUEMA HR DE ORACLE
 * GET --> LEE UNA REGIÓN Y LA DEVUELVE (json)
 * POST --> INSERTA UNA REGIÓN RECIBIDA EN EL CUERPO DE LA PETICIÓN (json)
 * PUT --> ACTUALIZA UN REGISTRO O CREA UNO NUEVO 
 * DELETE --> BORRA UN REGISTRO
 * 
 * Gestión de excepciones y datos devueltos
 * 
 * Hay cierto debate sobre qué debe devolverse en respuesta a la ejecución de un servicio WEB REST.
 * No hay realmente una "LEY" al respecto, aunque la lógica apunta a aprovechar la semántima del protocolo 
 * HTTP y sus códigos de estado (léase https://es.wikipedia.org/wiki/Anexo:C%C3%B3digos_de_estado_HTTP)
 * 
 * En este ejemplo, se decriben las posibles salidas para cada petición / método
 * 
 * getRegion: Se devuelve la región buscada en formato JSON y con un código de OK 
 * En caso de no existir, se devuelve un mensaje HTTP vacío con estado 204 - NO_CONTENT
 * Si el dato recibido no se ajusta al formato o tipo del ID, el servidor automáticamente devuelve un 400 PETICIÓN INCORRECTA
 * Si se produjese una excepción (error en el servidor de la base de datos o similar) se propagaría automáticamente al cliente
 * Se captura la excepción, se registra (printStackTrace -recordad que aquí debería ir un log) y se lanza throw e
 * 
 * postRegion: Se inserta una nueva región en la base de datos y se devuelve un 201 - CREATED
 * En caso de producirse una excepción por clave primaria repetida, se devuelve un conflict o 409
 * Si el dato recibido no se ajusta al formato de la región, el servidor automáticamente devuelve un 400 PETICIÓN INCORRECTA
 * Si se da un error de naturaleza distinta al anterior, simplemente se registra y se propaga mediante throw
 * 
 * putRegion: Se actualiza la región existente en la base de datos o se crea una nueva si no existía esa clave primaria 201 - CREATED
 * Si el dato recibido no se ajusta al formato de la región, el servidor automáticamente devuelve un 400 PETICIÓN INCORRECTA
 * Si se da un error de naturaleza distinta al anterior, simplemente se registra y se propaga mediante throw
 * 
 * 
 * deleteRegion: Se borra la región con el ID recibido y se devuelve OK - 200 si todo fue bien
 * Si el dato recibido no se ajusta al formato o tipo del ID, el servidor automáticamente devuelve un 400 PETICIÓN INCORRECTA
 * Si se ha demandado borrar un registro cuyo ID no existía en la base de datos se devuleve un 404 NOT FOUND
 * Si se da un error de naturaleza distinta al anterior, simplemente se registra y se propaga mediante throw
 * 
 * 
 * 
 * @author vale
 *
 */
@RestController //realmente, esto equivale a Controller + ResponseBody
public class ControllerRegiones {
	
	@Autowired
	ServiceRegiones serviceregiones;
	

	@RequestMapping(path = "/region/{idRegion}", produces="application/json", method=RequestMethod.GET)
	public ResponseEntity<Regions> getRegion(@PathVariable Integer idRegion) {
		
		ResponseEntity<Regions> respuesta = null;
		Regions region_obtenida = null;
			
		try {
			
		
			region_obtenida = serviceregiones.leerRegion(idRegion);
			if (region_obtenida == null) //la región con ese id no existía
			{
				respuesta = new ResponseEntity<Regions> (region_obtenida, HttpStatus.NO_CONTENT);//204
			} 
			else 
				{
					respuesta = new ResponseEntity<Regions> (region_obtenida, HttpStatus.OK);
				}
		}catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		
		
		return respuesta;
	}
	
	@RequestMapping(path = "/region", method=RequestMethod.POST)
	public ResponseEntity<Void> postRegion(@RequestBody Regions region_insertar) {
		
		ResponseEntity<Void> respuesta = null;
		
		try
		{
			serviceregiones.insertarRegion(region_insertar);
			respuesta = new ResponseEntity<Void> (HttpStatus.CREATED);
			
		}catch (Exception e)
		{
			e.printStackTrace();
			if ((e instanceof SQLIntegrityConstraintViolationException) || (e instanceof DataIntegrityViolationException))
			{
				respuesta = new ResponseEntity<Void> (HttpStatus.CONFLICT); //si está repetido el ID region que se pretende insertar (Excepción PK)
			} else throw e;//si no es un fallo de tipo violación clave primaria, progapo el error tal cual, para que el cliente pueda caonocer la causa (la base da datos está caida, lo que sea) : Otor tipo de error en definitiva
			
		}
		
		return respuesta;	
	}
	
	@RequestMapping(path = "/region", method=RequestMethod.PUT)
	public ResponseEntity<Void> putRegion(@RequestBody Regions region_actualizar) {
		ResponseEntity<Void> respuesta = null;
		
		try
		{
			serviceregiones.actualizarRegion(region_actualizar); //si existe se actualiza y si no se crea
			respuesta = new ResponseEntity<Void> (HttpStatus.CREATED);//si la instrucción anterior se ejecuto con éxito, la respuesta es HTTP 201 - CREATED
			
		}catch (Exception e)
		{
			e.printStackTrace();//registro el fallo 
			throw e;//y propago la excepción, para que le llegue al cliente
			
		}
		
		
		return respuesta;	
	}
	
	
	@RequestMapping(path = "/region/{idRegion}",  method=RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer idRegion) {
		ResponseEntity<Void> respuesta = null;
		
		try
		{
			serviceregiones.borrarRegion(idRegion);
			respuesta = new ResponseEntity<Void> (HttpStatus.OK);
		}catch (Exception e)
		{
			e.printStackTrace();
			if  (e instanceof IllegalArgumentException) {
				//si la región no existe, se quiere borrar una región que no existía, se lanza el NOT_FOUND
				respuesta = new ResponseEntity<Void> (HttpStatus.NOT_FOUND);
				
			} else throw e; //si se produce otro tipo de error, se propaga
			
		}
	return respuesta;
		
	}
	

}
