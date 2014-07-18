package fr.lyrgard.hexScape.gui.desktop.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import fr.lyrgard.hexScape.HexScapeCore;
import fr.lyrgard.hexScape.bus.CoreMessageBus;
import fr.lyrgard.hexScape.message.ConnectToServerMessage;
import fr.lyrgard.hexScape.service.ConfigurationService;

public class ConnectToServerAction extends AbstractAction {

	private static final long serialVersionUID = -8567383319786101832L;
	
	private static final ImageIcon icon = new ImageIcon(ChooseMapAction.class.getResource("/gui/icons/connect.png"));
	
	public ConnectToServerAction() {
		super("Connect to server", icon);
	}

	public void actionPerformed(ActionEvent e) {
		String playerId = HexScapeCore.getInstance().getPlayerId();
		
		ConnectToServerMessage message = new ConnectToServerMessage(playerId, ConfigurationService.getInstance().getServerHost());
		CoreMessageBus.post(message);
	}
	
	
	
}
