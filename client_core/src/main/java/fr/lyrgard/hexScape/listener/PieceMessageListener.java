package fr.lyrgard.hexScape.listener;

import java.util.concurrent.Callable;

import com.google.common.eventbus.Subscribe;

import fr.lyrgard.hexScape.HexScapeCore;
import fr.lyrgard.hexScape.bus.CoreMessageBus;
import fr.lyrgard.hexScape.bus.GuiMessageBus;
import fr.lyrgard.hexScape.message.PieceMovedMessage;
import fr.lyrgard.hexScape.message.PiecePlacedMessage;
import fr.lyrgard.hexScape.message.PieceRemovedMessage;
import fr.lyrgard.hexScape.message.PieceSelectedMessage;
import fr.lyrgard.hexScape.message.PieceUnselectedMessage;
import fr.lyrgard.hexScape.message.PlacePieceMessage;
import fr.lyrgard.hexScape.model.Universe;
import fr.lyrgard.hexScape.model.card.CardInstance;
import fr.lyrgard.hexScape.model.piece.PieceInstance;
import fr.lyrgard.hexScape.model.player.Player;
import fr.lyrgard.hexScape.service.PieceManager;
import fr.lyrgard.hexscape.client.network.ClientNetwork;

public class PieceMessageListener extends AbstractMessageListener {

	private static PieceMessageListener INSTANCE = new PieceMessageListener();
	
	public static void start() {
		CoreMessageBus.register(INSTANCE);
	}
	
	private PieceMessageListener() {
	}
	
	@Subscribe public void onPlacePieceMessage(PlacePieceMessage message) {
		final String playerId = message.getPlayerId();
		final String pieceModelId = message.getPieceModelId();
		final String cardInstanceId = message.getCardInstanceId();
		
		final Player player = Universe.getInstance().getPlayersByIds().get(playerId);
		if (player != null && player.getArmy() != null && player.getArmy().getCardsById().keySet().contains(cardInstanceId)) {
			final CardInstance card = player.getArmy().getCardsById().get(cardInstanceId);
			
			HexScapeCore.getInstance().getHexScapeJme3Application().enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					String pieceId = playerId + "-" + player.getPiecesById().size();
					PieceInstance piece = new PieceInstance(pieceId, pieceModelId, card);
					HexScapeCore.getInstance().getMapManager().placePiece(new PieceManager(piece));
					player.getPiecesById().put(piece.getId(), piece);
					return null;
				}
			});		
		}
	}
	
	@Subscribe public void onPiecePlaced(PiecePlacedMessage message) {
//		final String playerId = message.getPlayerId();
//		final String pieceModelId = message.getModelId();
//		final String cardInstanceId = message.getCardInstanceId();
			
		// TODO others players
		GuiMessageBus.post(message);		
	}
	
	@Subscribe public void onPieceMoved(PieceMovedMessage message) {
		if (HexScapeCore.getInstance().isOnline()) {
			ClientNetwork.getInstance().send(message);
		}
		GuiMessageBus.post(message);
	}
	
	@Subscribe public void onPieceRemoved(PieceRemovedMessage message) {
		if (HexScapeCore.getInstance().isOnline()) {
			ClientNetwork.getInstance().send(message);
		}
		GuiMessageBus.post(message);
	}
	
	@Subscribe public void onPieceSelected(PieceSelectedMessage message) {
		if (HexScapeCore.getInstance().isOnline()) {
			ClientNetwork.getInstance().send(message);
		}
		GuiMessageBus.post(message);
	}
	
	@Subscribe public void onPieceUnselected(PieceUnselectedMessage message) {
		if (HexScapeCore.getInstance().isOnline()) {
			ClientNetwork.getInstance().send(message);
		}
		GuiMessageBus.post(message);
	}
}
