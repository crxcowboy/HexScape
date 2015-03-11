package fr.lyrgard.hexScape.server.listener;

import com.google.common.eventbus.Subscribe;

import fr.lyrgard.hexScape.bus.CoreMessageBus;
import fr.lyrgard.hexScape.bus.GuiMessageBus;
import fr.lyrgard.hexScape.message.CardInstanceChangedOwnerMessage;
import fr.lyrgard.hexScape.message.ChangeCardInstanceOwnerMessage;
import fr.lyrgard.hexScape.message.MarkerPlacedMessage;
import fr.lyrgard.hexScape.message.MarkerRemovedMessage;
import fr.lyrgard.hexScape.message.MarkerRevealedMessage;
import fr.lyrgard.hexScape.message.PlaceMarkerMessage;
import fr.lyrgard.hexScape.message.RemoveMarkerMessage;
import fr.lyrgard.hexScape.message.RevealMarkerMessage;
import fr.lyrgard.hexScape.model.IdGenerator;
import fr.lyrgard.hexScape.model.Universe;
import fr.lyrgard.hexScape.model.card.CardInstance;
import fr.lyrgard.hexScape.model.game.Game;
import fr.lyrgard.hexScape.model.marker.UnknownTypeMarkerInstance;
import fr.lyrgard.hexScape.model.player.Player;
import fr.lyrgard.hexScape.model.player.User;
import fr.lyrgard.hexscape.server.network.ServerNetwork;

public class CardMessageListener {

	private static CardMessageListener instance;

	public static void start() {
		if (instance == null) {
			instance = new CardMessageListener();
			CoreMessageBus.register(instance);
		}
	}

	private CardMessageListener() {
	}


	@Subscribe public void onPlaceMarker(PlaceMarkerMessage message) {
		String userId = message.getSessionUserId();
		String cardId = message.getCardId();
		String markerTypeId = message.getMarkerTypeId();
		String hiddenMarkerTypeId = message.getHiddenMarkerTypeId();
		int number = message.getNumber();
		boolean stackable = message.isStackable();

		User user = Universe.getInstance().getUsersByIds().get(userId); 

		if (user != null && user.getGame() != null) {
			if (user != null && user.getGame() != null && user.getPlayer() != null) {
				for (Player owner : user.getGame().getPlayers()) {
					if (owner.getArmy() != null) {
						CardInstance card = owner.getArmy().getCard(cardId);
						if (card != null) {
							UnknownTypeMarkerInstance markerInfo = (UnknownTypeMarkerInstance)card.getMarkerByType(markerTypeId);
							String markerId = null;
							if (stackable && markerInfo != null) {
								markerId = markerInfo.getId();
								markerInfo.setNumber(markerInfo.getNumber() + number);
							} else {
								markerId = IdGenerator.getInstance().getNewMarkerId();
								markerInfo = new UnknownTypeMarkerInstance(markerId, markerTypeId, hiddenMarkerTypeId, number);

								card.addMarker(markerInfo);
							}

							MarkerPlacedMessage resultMessageForEmitter = new MarkerPlacedMessage(owner.getId(), cardId, markerId, markerTypeId, hiddenMarkerTypeId, number);
							ServerNetwork.getInstance().sendMessageToUser(resultMessageForEmitter, userId);

							// Hide the hiddenMarkerTypeId for others players
							MarkerPlacedMessage resultMessageForOthers = new MarkerPlacedMessage(owner.getId(), cardId, markerId, markerTypeId, null, number);
							ServerNetwork.getInstance().sendMessageToGameExceptUser(resultMessageForOthers, user.getGame().getId(), userId);

							return;
						}
					}
				}
			}
		}
	}

	@Subscribe public void onRemoveMarker(RemoveMarkerMessage message) {
		String userId = message.getSessionUserId();
		String cardId = message.getCardId();
		String markerId = message.getMarkerId();
		int number = message.getNumber();


		User user = Universe.getInstance().getUsersByIds().get(userId); 

		if (user != null && user.getGame() != null) {
			Game game = user.getGame();
			for (Player owner : user.getGame().getPlayers()) {
				if (owner.getArmy() != null) {
					CardInstance card = owner.getArmy().getCard(cardId);
					if (card != null) {
						UnknownTypeMarkerInstance markerInfo = (UnknownTypeMarkerInstance)card.getMarker(markerId);
						if (markerInfo != null) {
							markerInfo.setNumber(markerInfo.getNumber() - number);
							if (markerInfo.getNumber() <= 0) {
								card.getMarkers().remove(markerInfo);
							}

							MarkerRemovedMessage resultMessage = new MarkerRemovedMessage(user.getPlayerId(), cardId, markerId, number);
							ServerNetwork.getInstance().sendMessageToGame(resultMessage, game.getId());

							return;
						}
					}
				}
			}
		}
	}

	@Subscribe public void onRevealMarker(RevealMarkerMessage message) {
		String userId = message.getSessionUserId();
		String cardId = message.getCardId();
		String markerId = message.getMarkerId();

		User user = Universe.getInstance().getUsersByIds().get(userId); 

		if (user != null && user.getGame() != null && user.getPlayer() != null) {
			Game game = user.getGame();
			for (Player owner : user.getGame().getPlayers()) {
				if (owner.getArmy() != null) {
					CardInstance card = owner.getArmy().getCard(cardId);
					if (card != null) {
						UnknownTypeMarkerInstance markerInfo = (UnknownTypeMarkerInstance)card.getMarker(markerId);
						if (markerInfo != null) {
							if (markerInfo.getHiddenMarkerTypeId() != null) {
								String hiddenMarkerTypeId = markerInfo.getHiddenMarkerTypeId();

								markerInfo.setMarkerDefinitionId(hiddenMarkerTypeId);
								markerInfo.setHiddenMarkerTypeId(null);

								MarkerRevealedMessage resultMessage = new MarkerRevealedMessage(user.getPlayerId(), cardId, markerId, hiddenMarkerTypeId);
								ServerNetwork.getInstance().sendMessageToGame(resultMessage, game.getId());

								return;
							}
						}
					}
				}
			}
		}
	}

	@Subscribe public void onChangeCardInstanceOwner(ChangeCardInstanceOwnerMessage message) {
		String userId = message.getSessionUserId();
		String cardId = message.getCardId();
		String newOwnerId = message.getNewOwnerId();

		User user = Universe.getInstance().getUsersByIds().get(userId); 

		if (user != null && user.getGame() != null && user.getPlayer() != null) {

			Game game = user.getGame();

			Player currentOwner = game.getCardOwner(cardId);
			Player newOwner = game.getPlayer(newOwnerId);
			CardInstance card = game.getCard(cardId);

			if (currentOwner != null && newOwner != null && newOwner.getArmy() != null && card != null) {
				String newCardId = newOwnerId + "-" + newOwner.getArmy().getCards().size();

				currentOwner.getArmy().getCards().remove(card);
				card.setId(newCardId);
				newOwner.getArmy().addCard(card);

				CardInstanceChangedOwnerMessage resultMessage = new CardInstanceChangedOwnerMessage(cardId, currentOwner.getId(), newCardId, newOwnerId);

				ServerNetwork.getInstance().sendMessageToGame(resultMessage, game.getId());
			}
		}
	}

}
