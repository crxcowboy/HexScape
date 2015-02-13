package fr.lyrgard.hexScape.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.lyrgard.hexScape.model.CurrentUserInfo;
import fr.lyrgard.hexScape.model.SecondarySelectMarker;
import fr.lyrgard.hexScape.model.SelectMarker;
import fr.lyrgard.hexScape.model.Universe;
import fr.lyrgard.hexScape.model.game.Game;
import fr.lyrgard.hexScape.model.player.Player;

public class SelectMarkerService {

	private static SelectMarkerService INSTANCE;
	
	public static synchronized SelectMarkerService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SelectMarkerService();
		}
		return INSTANCE;
	}
	
	private SelectMarkerService() {
	}
	
	private Map<String, SelectMarker> markerByPlayerIds = new HashMap<String, SelectMarker>();
	
	public SelectMarker getSelectMarker(String playerId) {
		SelectMarker selectMarker = markerByPlayerIds.get(playerId);
		if (selectMarker == null) {
			Game game = Universe.getInstance().getGamesByGameIds().get(CurrentUserInfo.getInstance().getGameId());

			if (game != null) {
				Player player = game.getPlayer(playerId);

				if (player != null) {
					selectMarker = new SelectMarker(player);
					markerByPlayerIds.put(playerId, selectMarker);
				}
			}
		}
		
		return selectMarker;
	}
	
	public SecondarySelectMarker getNewSecondarySelectMarker(String playerId, PieceManager selectedPiece, PieceManager secondarySelectedPiece) {
		SecondarySelectMarker secondarySelectMarker = null;
		Game game = Universe.getInstance().getGamesByGameIds().get(CurrentUserInfo.getInstance().getGameId());

		if (game != null) {
			Player player = game.getPlayer(playerId);

			if (player != null) {
				secondarySelectMarker = new SecondarySelectMarker(player.getColor(), selectedPiece, secondarySelectedPiece);
			}
		}

		return secondarySelectMarker;
	}
	
	public Collection<SelectMarker> getSelectMarkers() {
		return markerByPlayerIds.values();
	}
}
