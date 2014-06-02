package fr.lyrgard.hexScape.gui.desktop.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import fr.lyrgard.hexScape.HexScapeCore;
import fr.lyrgard.hexScape.model.ServerConstant;

public class ConnectToServerAction extends AbstractAction {

	private static final long serialVersionUID = -8567383319786101832L;
	
	private static final ImageIcon icon = new ImageIcon(ChooseMapAction.class.getResource("/gui/icons/connect.png"));

	private String url = "localhost";
	
	private int port = ServerConstant.SERVER_PORT;
	
	public ConnectToServerAction() {
		super("Connect to server", icon);
	}
	
	public ConnectToServerAction(String url) {
		this();
		this.url = url;
	}
	
	public ConnectToServerAction(String url, int port) {
		this();
		this.url = url;
		this.port = port;
	}

	public void actionPerformed(ActionEvent e) {
		HexScapeCore.getInstance().getMultiplayerService().connectToServer(url, port);
		
	}
	
	
	
}
