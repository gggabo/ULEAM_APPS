package views;

import java.io.Serializable;

import com.example.SGTA.MyUI;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
//import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
//import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import controllers.LoginController;
import de.steinwedel.messagebox.ButtonOption;
import de.steinwedel.messagebox.MessageBox;


public class vwLogin extends CssLayout implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private TextField username;
    private PasswordField password;
    private Button login;
    private Button forgotPassword;
	
   /* public ctrlLogin cLogin = new ctrlLogin();
    public vwPrincipal vwprincipal;*/
    
    public MainView mainView;
    
    private MyUI UIistance;
    
    public vwLogin(MyUI UI) {
    	this.UIistance = UI;
    	buildUI();
        username.focus();
	}
    
	public void buildUI() { 
		addStyleName("login-screen");

        // login form, centered in the available part of the screen
        Component loginForm = buildLoginForm();

        // layout to center login form when there is sufficient screen space
        // - see the theme for how this is made responsive for various screen
        // sizes
        VerticalLayout centeringLayout = new VerticalLayout();
        centeringLayout.setStyleName("centering-layout");
        centeringLayout.addComponent(loginForm);
        centeringLayout.setComponentAlignment(loginForm,
                Alignment.MIDDLE_CENTER);

        // information text about logging in
        CssLayout loginInformation = buildLoginInformation();

        addComponent(centeringLayout);
        addComponent(loginInformation);

	}
	
	private Component buildLoginForm() {
        VerticalLayout vForm = new VerticalLayout();
		
		vForm.addStyleName("login-form");
		vForm.setSizeUndefined();
		vForm.setMargin(false);
		
		HorizontalLayout hHeader = new HorizontalLayout();
		Image logo = new Image(null,new ThemeResource("images/arquitectura.jpg"));
		logo.setWidth("70px");
		
		Label lblHeader = new Label("<P ALIGN='justify'>Gestor de Actividades Docentes Facultad de Arquitectura ULEAM</p>",ContentMode.HTML);
		lblHeader.setWidth("160px");
		lblHeader.addStyleName(ValoTheme.LABEL_SMALL);
		
		hHeader.addComponents(logo,lblHeader); 
		 
		vForm.addComponent(hHeader);
		vForm.addComponent(username = new TextField("Usuario")); 
		
		username.setWidth("100%");
        username.setIcon(VaadinIcons.USER);
        username.setPlaceholder("gsalvatierra3575");
        username.setDescription("Digite su usuario");
        username.setStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        
        vForm.addComponent(password = new PasswordField("Clave"));
        password.setWidth("100%");
        password.setDescription("Digite su clave");
        password.setIcon(VaadinIcons.KEY);
        password.setPlaceholder("********");
        password.setStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        
        vForm.addComponent(login = new Button("Iniciar sesión")); 
        login.setWidth("100%");
        
        vForm.addComponent(forgotPassword = new Button("Descargar app Android"));
        forgotPassword.setWidth("100%");

        login.setDisableOnClick(true);
        login.addClickListener(new Button.ClickListener() { 
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    login();
                } finally {
                    login.setEnabled(true); 
                }
            }
        });
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        forgotPassword.addClickListener(new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(Button.ClickEvent event) {
				//message.normalMessage("Función no implementada");
				getUI().getPage().open("https://1drv.ms/u/s!AlVNK94rpQ4400WV0dAViMuGQZHl", "_blank");
                //showNotification(new Notification("Hint: Try anything"));
            }
        });
        forgotPassword.addStyleName(ValoTheme.BUTTON_LINK);
        
        
        
        return vForm;
    }

    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        Label loginInfoText = new Label(
                "<h1>Información de aplicación</h1>"
                        + "Esta aplicación tiene como objetivo gestionar las actividades realizadas por los docentes de la "
                        + "Facultad de Arquitectura de Universidad Laica Eloy Alfaro de Manabí",
                ContentMode.HTML);
        loginInfoText.setWidth("270px");
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }

    /*private void showNotification(Notification notification) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }*/
	
    private void login() {
    	
    	if(LoginController.login(username.getValue(), password.getValue())) { 
    	//if(username.getValue().equals("gsalvatierra3575") && password.getValue().equals("1313253575")) { 
    		mainView = new MainView(UIistance);
    		UIistance.setContent(mainView); 
    	}else {
    		/*ConfirmDialog.show(UIistance, "Información", "Usuario o clave incorrecta",
					"Aceptar","Cancelar", new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;

						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								
							} else {
								// User did not confirm
								// feedback(dialog.isConfirmed());
							}
						}
					});*/
    		MessageBox.createError()
    		.withCaption("Información")
    		.withMessage("Usuario o clave incorrecta")
    		.withOkButton(ButtonOption.caption("Aceptar"))
    		.open();
    		
    		
    	}
    	
    	//UI.showMainView();
    	
    	/*if(cLogin.login(username.getValue(), password.getValue())) {
    		vwprincipal = new vwPrincipal();
    		UI.getCurrent().setContent(vwprincipal);
    	}else {
    		ConfirmDialog.show(UI.getCurrent().getUI(), "Información", "Usuario o clave incorrecto",
					"Aceptar","Cancelar", new ConfirmDialog.Listener() {
						private static final long serialVersionUID = 1L;

						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								
							} else {
								// User did not confirm
								// feedback(dialog.isConfirmed());
							}
						}
					});
    	}*/
    }
	
	
	/*private TextField username;
    private PasswordField password;
    private Button login;
    private Button forgotPassword;
    
    public vwLogin() {
		// TODO Auto-generated constructor stub
    	
    	addComponent(buildUI());
    	
	}
    
    private Component buildUI() {
    	
    	
    	
    	return null;
    }*/
    
	
}
