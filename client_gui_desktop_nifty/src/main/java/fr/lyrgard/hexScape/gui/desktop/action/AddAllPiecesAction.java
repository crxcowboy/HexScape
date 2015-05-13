package fr.lyrgard.hexScape.gui.desktop.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import fr.lyrgard.hexScape.bus.CoreMessageBus;
import fr.lyrgard.hexScape.message.PlacePieceMessage;
import fr.lyrgard.hexScape.model.card.CardInstance;

public class AddAllPiecesAction extends AbstractAction {

	private static final long serialVersionUID = 6124817922902744899L;

	private String pieceModelId;
	private CardInstance card;
	private static final ImageIcon icon = new ImageIcon(ChooseMapAction.class.getResource("/gui/icons/addAllPieces.png"));
	
	public AddAllPiecesAction(String pieceModelId, CardInstance card) {
		super("Place all figures", icon);
		this.pieceModelId = pieceModelId;  
		this.card = card;
	}

	public void actionPerformed(ActionEvent paramActionEvent) {
		PlacePieceMessage message = new PlacePieceMessage(card.getId(), pieceModelId);
		CoreMessageBus.post(message);	
		
	}
}
