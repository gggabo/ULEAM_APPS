package views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

import com.vaadin.data.Binder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import controllers.RolController;
import controllers.UsuarioController;
import models.Rol;
import models.Usuario;
import utils.UploadImage;
import utils.dialogWindow;
import utils.message;
import utils.uploadXls;
public class VwUsuarios extends VerticalLayout implements View, Serializable{
	private static final long serialVersionUID = 1L;

	public VwUsuarios() {
		addComponent(buildUI()); 
		setCss();
		setPlaceHolder();
		setMaxLengthText();
		addValidation();
		//cargarDatos();
		buildUser();
		initComponents();
		
		//addStyleName("custom-margin-layout");
	}
	
	public VerticalLayout mainLayout = new VerticalLayout();
	public Panel pnlPrincipal = new Panel();
	public FormLayout mainFrm;
	public HorizontalLayout toolbar = new HorizontalLayout();
	public VerticalLayout usuarioLayout = new VerticalLayout();
	public TextField nombre_uno = new TextField("Primer nombre");
	public TextField nombre_dos = new TextField("Segundo nombre");
	public TextField apellido_paterno = new TextField("Apellido paterno");
	public TextField apellido_materno = new TextField("Apellido materno");
	public TextField correo = new TextField("Correo electrónico");
	public TextField telefono = new TextField("Teléfono");
	public TextField cedula = new TextField("Cedula/Pasaporte");
	public TextField nombre_usuario = new TextField("Usuario");
	public PasswordField clave = new PasswordField("Clave");
	public Button btnGuardar = new Button("Guardar");
	public MenuBar mainMenu = new MenuBar();
	public UploadImage uploadField = new UploadImage();
	public uploadXls uploadXls = new uploadXls();
	public String accion = "guardar"; 
	
	public ComboBox<Rol> cmbRol = new ComboBox<>();
	public List<Rol> listRol = new ArrayList<>();
	public Grid<Rol> gridRol = new Grid<>();
	public List<Rol> listGridRol = new ArrayList<>();
	
	public CssLayout filtering = new CssLayout();
	public Button clearFilter = new Button(VaadinIcons.CLOSE_CIRCLE);
	public TextField filtertxt = new TextField();
	public Grid<Usuario> gridUsuario = new Grid<>();
	public List<Usuario> listUsuarios = new ArrayList<>();
		
	public Grid<Usuario> gridUsuarioImport = new Grid<>();
	public List<Usuario> listUsuariosImport = new ArrayList<>();
	
	/*public TwinColSelect<Laboratorio> twLaboratorio = new TwinColSelect<>();
	public List<Laboratorio> listLabs = new ArrayList<>();
	public List<Laboratorio> listLabsTw = new ArrayList<>();
	*/
	
	public Component buildUI() {
				
		toolbar.setWidth("100%");
		toolbar.setSpacing(true);
		toolbar.setStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
		toolbar.addStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		toolbar.setResponsive(true);
		toolbar.addComponents(mainMenu);
		
		mainMenu.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
		mainMenu.addStyleName(ValoTheme.MENUBAR_SMALL);
		mainMenu.setResponsive(true);
		mainMenu.addItem("Nuevo usuario", VaadinIcons.USER_CHECK, new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				userNewEdit(null);
				accion="guardar";
			}
		});		
		
	//	mainMenu.addItem("Imprimir", VaadinIcons.PRINT, null);		
		
		mainMenu.addItem("Importar usuarios", VaadinIcons.INSERT, new Command() {
			private static final long serialVersionUID = 1L;
			@Override
			public void menuSelected(MenuItem selectedItem) {
				importUserView();
				
				/*userNewEdit(null);
				accion="guardar";*/
			}
		});
		
		filtertxt.setPlaceholder("Buscar por nombres o cedula");
		filtertxt.setValueChangeMode(ValueChangeMode.LAZY);
		filtertxt.setSizeFull();
		filtertxt.addValueChangeListener(e ->{
			listUsuarios.clear();
			listUsuarios.addAll(UsuarioController.search(filtertxt.getValue()));
			gridUsuario.setItems(listUsuarios);
		}); 
		
		clearFilter.addClickListener(e->{
			filtertxt.clear();
		});
		
		filtering.addComponents(filtertxt,clearFilter);
		filtering.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		filtering.addStyleName("custom-margins");
				   
		gridUsuario.addColumn(Usuario::getCedula).setCaption("CÉDULA/DNI");
		gridUsuario.addColumn(Usuario -> Usuario.getApellido_paterno() +" "+ Usuario.getApellido_materno() +" "+
		Usuario.getNombre_uno() +" "+ Usuario.getNombre_dos()
				).setCaption("NOMBRES Y APELLIDOS").setId("NOMBRES");
		gridUsuario.addColumn(Usuario::getNombre_usuario).setCaption("USUARIO");
		
		gridUsuario.setWidth("100%");
		gridUsuario.setSelectionMode(SelectionMode.NONE);
		gridUsuario.addComponentColumn(Usuario -> {
	 
			Button b = new Button("Editar");
			b.addClickListener(clickb ->{  
				userNewEdit(Usuario);
				cedula.setValue(Usuario.getCedula());
				apellido_paterno.setValue(Usuario.getApellido_paterno());
				apellido_materno.setValue(Usuario.getApellido_materno());
				nombre_uno.setValue(Usuario.getNombre_uno());
				nombre_dos.setValue(Usuario.getNombre_dos());
				correo.setValue(Usuario.getCorreo()); 
				telefono.setValue(Usuario.getTelefono());
				nombre_usuario.setValue(Usuario.getNombre_usuario());
				clave.setValue("");
				uploadField.setValue(Usuario.getImagen());	
				
				listGridRol.addAll(Usuario.getRoles()); 
				
			/*	listLabsTw.addAll(UsuarioController.getAllLabsByRol(Usuario.getId()));
				HashSet<Laboratorio> labsSelect = new HashSet<Laboratorio>(listLabsTw);
				
				twLaboratorio.setValue(labsSelect);
				
				Iterator<Rol> rolIterator = listGridRol.iterator(); 
				Rol r; 
				while(rolIterator.hasNext()) {
					r=rolIterator.next();
					if(r.getIdRol()==4) { 
						twLaboratorio.setVisible(true);
					}
				}*/
				
				gridRol.setItems(listGridRol);
				accion = "modificar";
				
			});
			b.setStyleName(ValoTheme.BUTTON_FRIENDLY);
			b.addStyleName(ValoTheme.BUTTON_SMALL);
			b.setIcon(VaadinIcons.EDIT);
			
			Button b2 = new Button("Eliminar");
			b2.addClickListener(clickb2 ->{
				listUsuarios.remove(Usuario);
				gridUsuario.setItems(listUsuarios);
				Usuario.setEstado(0);
				UsuarioController.update(Usuario);
				message.warringMessage("Usuario eliminado");
			});
			b2.setStyleName(ValoTheme.BUTTON_DANGER);
			b2.addStyleName(ValoTheme.BUTTON_SMALL);
			b2.setIcon(VaadinIcons.ERASER);
			
			HorizontalLayout hl = new HorizontalLayout();
			hl.addComponents(b,b2);
			return hl;			
		}).setCaption("Opciones");

		usuarioLayout.addComponents(toolbar,filtering,gridUsuario);
		usuarioLayout.setMargin(false);
		
		pnlPrincipal.setCaption("Gestión de usuarios");
		pnlPrincipal.setIcon(VaadinIcons.USERS);
		pnlPrincipal.setContent(usuarioLayout);
		
		mainLayout.addComponents(pnlPrincipal);
		return mainLayout;
	}
	
	public void setCss() {
		apellido_paterno.setStyleName(ValoTheme.TEXTFIELD_SMALL);
		apellido_materno.setStyleName(ValoTheme.TEXTFIELD_SMALL);
		nombre_uno.setStyleName(ValoTheme.TEXTFIELD_SMALL);
		nombre_dos.setStyleName(ValoTheme.TEXTFIELD_SMALL);
		correo.setStyleName(ValoTheme.TEXTFIELD_SMALL); 
		telefono.setStyleName(ValoTheme.TEXTFIELD_SMALL);
		cedula.setStyleName(ValoTheme.TEXTFIELD_SMALL);
		nombre_usuario.setStyleName(ValoTheme.TEXTFIELD_SMALL);
		clave.setStyleName(ValoTheme.TEXTFIELD_SMALL);
		cmbRol.setStyleName(ValoTheme.COMBOBOX_TINY);
		btnAddRol.addStyleName(ValoTheme.BUTTON_SMALL);
		btnAddRol.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		gridRol.addStyleName(ValoTheme.TABLE_SMALL);
		gridRol.addStyleName(ValoTheme.TABLE_COMPACT); 
		filtertxt.addStyleName(ValoTheme.TEXTFIELD_SMALL);
		filtertxt.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		filtertxt.setIcon(VaadinIcons.SEARCH);
		clearFilter.addStyleName(ValoTheme.BUTTON_SMALL);
	} 
	
	private void initComponents() {
		uploadField.setFileSizeLimit(1.0F);
		
		gridRol.addColumn(Rol::getNombre).setCaption("ROL ASIGNADO");
		gridRol.addComponentColumn(Rol -> {
			Button b = new Button();
			b.addClickListener(clickb2 ->{
				listGridRol.remove(Rol);
				/*if(Rol.getIdRol()==4) {
					twLaboratorio.setVisible(false);
					twLaboratorio.clear();
				}*/
				gridRol.setItems(listGridRol);
			});
			b.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
			b.addStyleName(ValoTheme.BUTTON_SMALL);
			b.setIcon(VaadinIcons.CLOSE_CIRCLE);
			return b;
		}).setStyleGenerator(Rol -> "v-align-left");
		
		gridRol.setSelectionMode(SelectionMode.NONE);
		gridRol.setWidth("200px"); 
		gridRol.setHeight("100px");
		
		gridUsuarioImport.addColumn(Usuario::getCedula).setCaption("CÉDULA/DNI");
		gridUsuarioImport.addColumn(Usuario -> Usuario.getNombre_uno() +" "+ Usuario.getNombre_dos()+" "+
				Usuario.getApellido_paterno() +" "+ Usuario.getApellido_materno()).setCaption("NOMBRES Y APELLIDOS");
		gridUsuarioImport.addColumn(Usuario::getNombre_usuario).setCaption("USUARIO");
		
		gridUsuarioImport.setWidth("600px");
		gridUsuarioImport.setSelectionMode(SelectionMode.NONE);
		
	}
	
	String varApellido="";
	String varNombreUno="";
	String varCedula="";
	
	HorizontalLayout layoutRol = new HorizontalLayout();
	VerticalLayout layoutRolGrid = new VerticalLayout();
	Button btnAddRol = new Button(VaadinIcons.ARROW_CIRCLE_RIGHT_O);
	
	public void userNewEdit(Usuario user) {
		limpiar(); 
		
		dialogWindow dialogWindow = new dialogWindow("Ingreso de usuarios", VaadinIcons.USERS);
		
		btnAddRol.setHeight("24px");
		btnAddRol.addClickListener(e->{
			if(cmbRol.getValue()==null) {
				message.warringMessage("Debe seleccionar un rol de la lista");
				return;
			}
			addRol();
			dialogWindow.center();
		});
		
		
		cmbRol.setWidth("160px");
		
		layoutRol.addComponents(cmbRol,btnAddRol);
		layoutRol.setMargin(false);
		layoutRol.setSpacing(false);
		
		layoutRolGrid.setCaption("Rol de usuario");
		layoutRolGrid.setMargin(false);
		layoutRolGrid.setSpacing(false);
		layoutRolGrid.addComponents(layoutRol);
		
		mainFrm = new FormLayout();
		mainFrm.addComponents(cedula, apellido_paterno, apellido_materno,nombre_uno, nombre_dos, 
				correo, telefono, nombre_usuario, clave, layoutRolGrid);
		mainFrm.setMargin(false);
		mainFrm.setSpacing(false);	
		
		VerticalLayout layoutUpload = new VerticalLayout();
		layoutUpload.addComponents(uploadField, gridRol);
		layoutUpload.setComponentAlignment(uploadField, Alignment.MIDDLE_CENTER);
		layoutUpload.setCaption("Foto de perfil (Max 3Mb)");
		layoutUpload.setSpacing(true);
		layoutUpload.setMargin(false);
		
		/*twLaboratorio.setRows(4);
		twLaboratorio.setWidth("530px");*/
		
		HorizontalLayout layoutFormImg = new HorizontalLayout();
		layoutFormImg.setStyleName(ValoTheme.LAYOUT_HORIZONTAL_WRAPPING);
		layoutFormImg.addComponents(mainFrm,layoutUpload);
		
		VerticalLayout vl = new VerticalLayout();
		vl.addComponents(layoutFormImg);
		vl.setMargin(false);
		vl.setSpacing(false);
		
		dialogWindow.getOkButton().addClickListener(new ClickListener() {	
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
								
				if(accion.equals("guardar")) {
					
					if (!validator.isValid()) {
						validator.validate();
						
						message.warringMessage("Hay errores en los campos de texto");
						return;
					}
					
					if(UsuarioController.DBcontainsUser(cedula.getValue())) {
						message.warringMessage("El usuario ya se encuentra registrado");
						return;
					}
					
					Usuario us = new Usuario(cedula.getValue().toUpperCase().trim(),nombre_uno.getValue().toUpperCase().trim(), 
							nombre_dos.getValue().toUpperCase().trim(), apellido_paterno.getValue().toUpperCase().trim(), 
							apellido_materno.getValue().toUpperCase().trim(),correo.getValue().trim(), telefono.getValue().trim(), 
							uploadField.getValue(),nombre_usuario.getValue().toLowerCase().trim(), DigestUtils.sha1Hex(clave.getValue().trim()),1);
					
				//	List<Laboratorio> lab = new ArrayList<>(twLaboratorio.getValue());
					
				//	user.setLaboratorios(lab);
					
					us.setRoles(listGridRol);
					
					UsuarioController.save(us); 
				}else {
					user.setCedula(cedula.getValue().toUpperCase().trim());
					user.setApellido_paterno(apellido_paterno.getValue().toUpperCase().trim());
					user.setApellido_materno(apellido_materno.getValue().toUpperCase().trim());
					user.setNombre_uno(nombre_uno.getValue().toUpperCase().trim());
					user.setNombre_dos(nombre_dos.getValue().toUpperCase().trim());
					user.setCorreo(correo.getValue().trim());
					user.setTelefono(telefono.getValue().trim());
					user.setImagen(uploadField.getValue());
					user.setNombre_usuario(nombre_usuario.getValue().toLowerCase().trim());
										
					if(!clave.getValue().isEmpty()) {
						user.setClave(DigestUtils.sha1Hex(clave.getValue().trim()));
					}
					
				//	List<Laboratorio> lab = new ArrayList<>(twLaboratorio.getValue());
				//	user.setLaboratorios(lab);
					
					user.setRoles(listGridRol);
					
					UsuarioController.update(user);
				}
				
				message.normalMessage("Acción realizada con éxito");
				
				cargarDatos();
				dialogWindow.close();
			}
		});
		
		dialogWindow.getCancelButton().addClickListener(e->{
			dialogWindow.close();
		});
		
		dialogWindow.setResponsive(true);
		dialogWindow.addComponentBody(vl); 
		UI.getCurrent().addWindow(dialogWindow);
	}
	
	private void importUserView() {
		
		uploadXls.clear();
		
		dialogWindow dialogWindow = new dialogWindow("Importación de usuarios", VaadinIcons.USERS);
		dialogWindow.setResponsive(true);
		
		uploadXls.setGridUsuarioImport(gridUsuarioImport);
		//uploadXls.setListUsuariosImport(listUsuariosImport);
		
		VerticalLayout rootLayout = new VerticalLayout();
	    rootLayout.setMargin(false);
		rootLayout.addComponents(uploadXls,gridUsuarioImport);
		
		dialogWindow.getOkButton().addClickListener(e ->{
			
			//System.out.println(uploadXls.getListUsuariosImport());
			/*dialogWindow dialogProcess = new dialogWindow("Procesando", VaadinIcons.FLASK);;
			getUI().access(()->{
				UI.getCurrent().addWindow(dialogProcess);
				ProgressBar progress = new ProgressBar();
				progress.setIndeterminate(true);
				dialogProcess.addComponentBody(progress);
			});
			*/
			Iterator<Usuario> u = uploadXls.getListUsuariosImport().iterator();
			Usuario ureg, udis;
			
			while(u.hasNext()) {
				ureg = u.next();
				
				if(!UsuarioController.DBcontainsUser(ureg.getCedula())) {
					UsuarioController.save(ureg);
					
				}	
				
				udis = UsuarioController.getSpecificUserDisable(ureg.getCedula());
				if(udis != null) {
					if(udis.getEstado()==0 ) {
						//System.out.println(UsuarioController.getSpecificUserDisable(ureg.getCedula()));
						udis.setEstado(1); 
						UsuarioController.update(udis);
					}
				}
				
				 
			}
			
			message.normalMessage("Importación realizada con éxito");
			uploadXls.clear();
			
			cargarDatos();
			dialogWindow.close();
			/*getUI().access(()->{
				dialogProcess.close();
			});*/
		});
		
		dialogWindow.addComponentBody(rootLayout);
		
		//dialogWindow.addComponentBody(layoutFormImg);
		UI.getCurrent().addWindow(dialogWindow);
	}
	
	public void cargarDatos() {
		listUsuarios = UsuarioController.findAll();
		gridUsuario.setItems(listUsuarios);
		
		listRol = RolController.findAll();
		cmbRol.setItems(listRol);
	    cmbRol.setItemCaptionGenerator(Rol::getNombre);
	    
	  /*  listLabs = LabotatorioController.findAll();
	    twLaboratorio.setItems(listLabs);
	    twLaboratorio.setItemCaptionGenerator(Laboratorio::getNombre);*/
		
	} 
	
	private void addRol() {
				
		Rol rol = cmbRol.getValue();
		boolean c = false;
		
		for(Rol r:listGridRol) {
			if(r.getIdRol() == rol.getIdRol()) {
				c=true;
				break;
			}
		}
		if(!c) {
			listGridRol.add(rol);
			/*if(rol.getIdRol()==4) {
				twLaboratorio.setVisible(true);
			}*/
			gridRol.setItems(listGridRol);
		}
		
	}
	
	private void setPlaceHolder() {
		nombre_uno.setPlaceholder("Kevin");
		nombre_dos.setPlaceholder("Rodrigo");
		apellido_paterno.setPlaceholder("Bravo");
		apellido_materno.setPlaceholder("Moreira");
		correo.setPlaceholder("usuario@mail.com"); 
		telefono.setPlaceholder("09999999999");
		cedula.setPlaceholder("1313253658");
		nombre_usuario.setPlaceholder("kbravo1234");
		clave.setPlaceholder("123456789");	
	}
	
	private void setMaxLengthText() {
		nombre_uno.setMaxLength(20);
		nombre_dos.setMaxLength(20);
		apellido_paterno.setMaxLength(20);
		apellido_materno.setMaxLength(20);
		correo.setMaxLength(50);
		telefono.setMaxLength(20);
		cedula.setMaxLength(20);
		nombre_usuario.setMaxLength(20);
		clave.setMaxLength(20);
	}
	
	private void limpiar() {
		nombre_uno.setValue("");
		nombre_dos.setValue("");
		apellido_paterno.setValue("");
		apellido_materno.setValue("");
		correo.setValue(""); 
		telefono.setValue("");
		cedula.setValue("");
		nombre_usuario.setValue("");
		clave.setValue("");
		uploadField.clear();
		cmbRol.setValue(null);
		listGridRol.clear();
		gridRol.setItems(listGridRol);
		/*twLaboratorio.setVisible(false);
		twLaboratorio.clear();
		listLabsTw.clear();*/
	}
	
	Binder<Usuario> validator = new Binder<>();
	
	private void addValidation() {
		
		validator.forField(cedula)
		.asRequired("Dato requerido")
		.bind(Usuario::getCedula, Usuario::setCedula);
		
		validator.forField(apellido_paterno)
		.asRequired("Dato requerido")
		.bind(Usuario::getApellido_paterno, Usuario::setApellido_paterno);
		
		validator.forField(nombre_uno)
		.asRequired("Dato requerido")
		.bind(Usuario::getNombre_uno, Usuario::setNombre_uno);
				
	}
	
	private void buildUser() {
		cedula.addValueChangeListener(e ->{
			clave.setValue(e.getValue());
			
			if(!nombre_uno.getValue().isEmpty() && nombre_uno.getValue().trim().length() >=1) {
				varNombreUno =  nombre_uno.getValue().substring(0,1);
			}else {
				varNombreUno = "";
			}
			
			if(!cedula.getValue().isEmpty() && cedula.getValue().length() >=4) {
				varCedula = cedula.getValue().substring(cedula.getValue().trim().length() - 4 , cedula.getValue().trim().length());//4 ULTIMOS DÍGITOS DE CEDULA
			}else {
				varCedula = "";
			}
			
			varApellido = apellido_paterno.getValue().trim().replace(" ", "");
					
			nombre_usuario.setValue(varNombreUno+varApellido+varCedula);
		});
		
		apellido_paterno.addValueChangeListener(e->{
			if(!nombre_uno.getValue().isEmpty() && nombre_uno.getValue().trim().length() >=1) {
				varNombreUno =  nombre_uno.getValue().substring(0,1);
			}else {
				varNombreUno = "";
			}
			
			if(!cedula.getValue().isEmpty() && cedula.getValue().length() >=4) {
				varCedula = cedula.getValue().substring(cedula.getValue().trim().length() - 4 , cedula.getValue().trim().length());//4 ULTIMOS DÍGITOS DE CEDULA
			}else {
				varCedula = "";
			}
			
			varApellido = apellido_paterno.getValue().trim().replace(" ", "");;
					
			nombre_usuario.setValue(varNombreUno+varApellido+varCedula);
		});
		
		nombre_uno.addValueChangeListener(e->{
			if(!nombre_uno.getValue().isEmpty() && nombre_uno.getValue().trim().length() >=1) {
				varNombreUno =  nombre_uno.getValue().substring(0,1);
			}else {
				varNombreUno = "";
			}
			
			if(!cedula.getValue().isEmpty() && cedula.getValue().length() >=4) {
				varCedula = cedula.getValue().substring(cedula.getValue().trim().length() - 4 , cedula.getValue().trim().length());//4 ULTIMOS DÍGITOS DE CEDULA
			}else {
				varCedula = "";
			}
			
			varApellido = apellido_paterno.getValue().trim().replace(" ", "");;
					
			nombre_usuario.setValue(varNombreUno+varApellido+varCedula);
		});
	}

}
