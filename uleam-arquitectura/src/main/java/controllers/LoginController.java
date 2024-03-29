package controllers;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.codec.digest.DigestUtils;

import com.vaadin.flow.server.VaadinSession;

import models.Rol;
import models.Usuario;
import services.JPAService;

public class LoginController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static Usuario u;
	public static Query query;
	public static List<Rol> roles;
	public static boolean loginStatus = false;
	
	
	public static boolean login(String usuario, String clave) {

		JPAService.runInTransaction(em ->{
			query = em.createQuery("SELECT u from Usuario u where u.nombre_usuario = ?1 and u.clave = ?2 and u.estado = 1");
			query.setParameter(1, usuario); 
			query.setParameter(2, DigestUtils.shaHex(clave)); 

			if(query.getResultList().size()>0) {
				u = (Usuario) query.getSingleResult();
				
				VaadinSession.getCurrent().setAttribute("ID_USUARIO", u.getId());
				VaadinSession.getCurrent().setAttribute("NOMBRE_USUARIO", u.getNombre_usuario());
				VaadinSession.getCurrent().setAttribute("NOMBRE_PERSONA", u.getApellido_paterno()+" "+u.getNombre_uno());
				VaadinSession.getCurrent().setAttribute("TIPO_USUARIO", u.getRoles());
				VaadinSession.getCurrent().setAttribute("CLAVE", u.getClave());
				VaadinSession.getCurrent().setAttribute("LOGIN", u.getNombre_usuario());
				roles = u.getRoles();
			
				System.out.println(VaadinSession.getCurrent().getAttribute("NOMBRE_USUARIO")
						+" "+VaadinSession.getCurrent().getAttribute("ID_USUARIO")+" "
				+VaadinSession.getCurrent().getAttribute("NOMBRE_PERSONA")+" "+VaadinSession.getCurrent().getAttribute("TIPO_USUARIO"));		
				
				loginStatus = true;
			}else {
				loginStatus = false;
			}
			
			
			return null;
		}
        ); 
		
		return loginStatus;
	}
	
	/*static StreamResource stream;
	
	public static StreamResource getImgUser(long idPersonal) {	
		JPAService.runInTransaction(em ->{		
			u = em.find(Usuario.class, idPersonal);
			stream = uploadUtils.byteToImg(u.getImagen());
			return null;
		});
		
		return stream;	
	}*/
}
