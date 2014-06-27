package fr.lyrgard.hexScape.listener;

import java.util.UUID;

import com.google.common.eventbus.Subscribe;

import fr.lyrgard.hexScape.HexScapeCore;
import fr.lyrgard.hexScape.bus.CoreMessageBus;
import fr.lyrgard.hexScape.bus.GuiMessageBus;
import fr.lyrgard.hexScape.message.ErrorMessage;
import fr.lyrgard.hexScape.message.MarkerPlacedMessage;
import fr.lyrgard.hexScape.message.MarkerRemovedMessage;
import fr.lyrgard.hexScape.message.MarkerRevealedMessage;
import fr.lyrgard.hexScape.message.PlaceMarkerMessage;
import fr.lyrgard.hexScape.message.RemoveMarkerMessage;
import fr.lyrgard.hexScape.message.RevealMarkerMessage;
import fr.lyrgard.hexScape.model.Universe;
import fr.lyrgard.hexScape.model.card.Army;
import fr.lyrgard.hexScape.model.card.CardInstance;
import fr.lyrgard.hexScape.model.marker.HiddenMarkerInstance;
import fr.lyrgard.hexScape.model.marker.MarkerDefinition;
import fr.lyrgard.hexScape.model.marker.MarkerInstance;
import fr.lyrgard.hexScape.model.marker.MarkerType;
import fr.lyrgard.hexScape.model.marker.RevealableMarkerInstance;
import fr.lyrgard.hexScape.model.marker.StackableMarkerInstance;
import fr.lyrgard.hexScape.model.player.Player;
import fr.lyrgard.hexScape.service.MarkerService;
import fr.lyrgard.hexscape.client.network.ClientNetwork;

public class MarkerMessageListener extends AbstractMessageListener {

	private static MarkerMessageListener instance;
	
	public static void start() {
		if (instance == null) {
			instance = new MarkerMessageListener();
			CoreMessageBus.register(instance);
		}
	}
	
	public static void stop() {
		if (instance != null) {
			CoreMessageBus.unregister(instance);
			instance = null;
		}
	}
	
	private MarkerMessageListener() {
	}
	
	@Subscribe public void onPlaceMarkerMessage(PlaceMarkerMessage message) {
		if (HexScapeCore.getInstance().isOnline()) {
			ClientNetwork.getInstance().send(message);
		} else {
			String playerId = message.getPlayerId();
			String gameId = message.getGameId();
			String cardId = message.getCardId();
			String markerId = UUID.randomUUID().toString();
			int number = message.getNumber();
			String markerTypeId = message.getMarkerTypeId();
			String hiddenMarkerTypeId = message.getHiddenMarkerTypeId();
			MarkerPlacedMessage resultMessage = new MarkerPlacedMessage(playerId, gameId, cardId, markerId, markerTypeId, hiddenMarkerTypeId, number);
			CoreMessageBus.post(resultMessage);
		}
		
	}
	
	@Subscribe public void onMarkerPlaced(MarkerPlacedMessage message) {
		String playerId = message.getPlayerId();
		String gameId = message.getGameId();
		String cardId = message.getCardId();
		String markerId = message.getMarkerId();
		String markerTypeId = message.getMarkerTypeId();
		int number = message.getNumber();
		String hiddenMarkerTypeId = message.getHiddenMarkerTypeId();
		
		Player player = Universe.getInstance().getPlayersByIds().get(playerId);
		if (player == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find player " + playerId));
			return;
		} 
		
		Army army = player.getArmy();
		if (army == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find army for player " + playerId));
			return;
		}
		
		CardInstance card = army.getCardsById().get(cardId);
		if (card == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find card " + cardId + " for player " + playerId + " in game " + gameId));
			return;
		}
		
		MarkerDefinition markerDefinition = MarkerService.getInstance().getMarkersByIds().get(markerTypeId);
		if (markerDefinition == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find marker type " + markerTypeId));
			return;
		}
		
		MarkerInstance marker = null;
		
		switch (markerDefinition.getType()) {
		case NORMAL:
			marker = new MarkerInstance(markerDefinition.getId());
			marker.setId(markerId);
			card.getMarkers().add(marker);
			break;
		case STACKABLE:
			addStackableMarkerToCard(card, markerDefinition, number, markerId);
			break;
		case REVEALABLE:
			marker = new RevealableMarkerInstance(markerDefinition.getId());
			marker.setId(markerId);
			card.getMarkers().add(marker);
			break;
		case HIDDEN:
			marker = new HiddenMarkerInstance(markerDefinition.getId(), hiddenMarkerTypeId);
			marker.setId(markerId);
			card.getMarkers().add(marker);
			break;
		}
		
		GuiMessageBus.post(message);
	}

	private void addStackableMarkerToCard(CardInstance card, MarkerDefinition markerDefinition, int number, String markerId) {
		for (MarkerInstance markerOnCard : card.getMarkers()) {
			if (markerOnCard.getMarkerDefinitionId().equals(markerDefinition.getId())) {
				// a marker of this type is already on the card. Add "number" to it
				((StackableMarkerInstance)markerOnCard).setNumber(((StackableMarkerInstance)markerOnCard).getNumber() + number);				
				return;
			}
		}
		StackableMarkerInstance marker = new StackableMarkerInstance(markerDefinition.getId(), number);
		marker.setId(markerId);
		card.getMarkers().add(marker);
		
	}

	@Subscribe public void onRemoveMarkerMessage(RemoveMarkerMessage message) {
		
		if (HexScapeCore.getInstance().isOnline()) {
			ClientNetwork.getInstance().send(message);
		} else {
			String playerId = message.getPlayerId();
			String gameId = message.getGameId();
			String cardId = message.getCardId();
			String markerId = message.getMarkerId();
			int number = message.getNumber();
			boolean allMarkers = message.isAllMarkers();
			MarkerRemovedMessage resultMessage = new MarkerRemovedMessage(playerId, gameId, cardId, markerId, number, allMarkers);
			CoreMessageBus.post(resultMessage);
		}
	}
	
	@Subscribe public void onMarkerRemoved(MarkerRemovedMessage message) {
		String playerId = message.getPlayerId();
		String gameId = message.getGameId();
		String cardId = message.getCardId();
		String markerId = message.getMarkerId();
		int number = message.getNumber();
		boolean allMarkers = message.isAllMarkers();
		
		
		Player player = Universe.getInstance().getPlayersByIds().get(playerId);
		if (player == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find player " + playerId));
			return;
		} 
		
		Army army = player.getArmy();
		if (army == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find army for player " + playerId));
			return;
		}
		
		CardInstance card = army.getCardsById().get(cardId);
		if (card == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find card " + cardId + " for player " + playerId + " in game " + gameId));
			return;
		}
		
		if (allMarkers) {
			if (!card.getMarkers().isEmpty()) {
				card.getMarkers().clear();
			}
		} else {
			
			for (MarkerInstance marker : card.getMarkers()) {
				if (marker.getId().equals(markerId)) {
					
					MarkerDefinition markerDefinition = MarkerService.getInstance().getMarkersByIds().get(marker.getMarkerDefinitionId());
					if (markerDefinition == null) {
						CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find marker type " + markerId));
						return;
					}
					
					if (markerDefinition.getType() == MarkerType.STACKABLE) {
						int currentNumber = ((StackableMarkerInstance)marker).getNumber();
						if (currentNumber <= number) {
							card.getMarkers().remove(marker);
						} else {
							((StackableMarkerInstance)marker).setNumber(currentNumber - number);
						}
					} else {
						card.getMarkers().remove(marker);
					}
					break;
				}
			}
		}
		GuiMessageBus.post(message);
	}

	@Subscribe public void onRevealMarkerMessage(RevealMarkerMessage message) {
		
		if (HexScapeCore.getInstance().isOnline()) {
			ClientNetwork.getInstance().send(message);
		} else {
			String playerId = message.getPlayerId();
			String gameId = message.getGameId();
			String cardId = message.getCardId();
			String markerId = message.getMarkerId();
			
			Player player = Universe.getInstance().getPlayersByIds().get(playerId);
			if (player == null) {
				CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find player " + playerId));
				return;
			} 
			
			Army army = player.getArmy();
			if (army == null) {
				CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find army for player " + playerId));
				return;
			}
			
			CardInstance card = army.getCardsById().get(cardId);
			if (card == null) {
				CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find card " + cardId + " for player " + playerId + " in game " + gameId));
				return;
			}
			
			for (MarkerInstance marker : card.getMarkers()) {
				if (marker.getId().equals(markerId)) {
					if (!(marker instanceof HiddenMarkerInstance)) {
						return;
					}
					String hiddenMarkerTypeId = ((HiddenMarkerInstance)marker).getHiddenMarkerDefinitionId();
					MarkerRevealedMessage resultMessage = new MarkerRevealedMessage(playerId, gameId, cardId, markerId, hiddenMarkerTypeId);
					CoreMessageBus.post(resultMessage);
				}
			}
		}
	}
	
	@Subscribe public void onMarkerRevealed(MarkerRevealedMessage message) {
		String playerId = message.getPlayerId();
		String gameId = message.getGameId();
		String cardId = message.getCardId();
		String markerId = message.getMarkerId();
		String hiddenMarkerTypeId = message.getHiddenMarkerTypeId();
		
		Player player = Universe.getInstance().getPlayersByIds().get(playerId);
		if (player == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find player " + playerId));
			return;
		} 
		
		Army army = player.getArmy();
		if (army == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find army for player " + playerId));
			return;
		}
		
		CardInstance card = army.getCardsById().get(cardId);
		if (card == null) {
			CoreMessageBus.post(new ErrorMessage(playerId, "Unable to find card " + cardId + " for player " + playerId + " in game " + gameId));
			return;
		}
		
		for (MarkerInstance marker : card.getMarkers()) {
			if (marker.getId().equals(markerId)) {
				if (!(marker instanceof HiddenMarkerInstance)) {
					return;
				}
				
				RevealableMarkerInstance revealedMarker = new RevealableMarkerInstance(hiddenMarkerTypeId);
				revealedMarker.setId(markerId);
				card.getMarkers().remove(marker);
				card.getMarkers().add(revealedMarker);
				
				GuiMessageBus.post(message);
				break;
			}
		}
	}

}
