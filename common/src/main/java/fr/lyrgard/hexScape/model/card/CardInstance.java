package fr.lyrgard.hexScape.model.card;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.lyrgard.hexScape.model.marker.MarkerInstance;
import fr.lyrgard.hexScape.model.marker.StackableMarkerInstance;
import fr.lyrgard.hexScape.model.piece.PieceInstance;

public class CardInstance {
	
	private String id;
	
	private String cardTypeId;
	
	private int number;
	
	private TreeSet<MarkerInstance> markers;
	
	private Queue<String> pieceLeftToPlace = new LinkedList<String>();
	
	private List<PieceInstance> pieces = new ArrayList<PieceInstance>();

	@JsonCreator
	public CardInstance(
			@JsonProperty("id") String id, 
			@JsonProperty("cardTypeId") String cardTypeId, 
			@JsonProperty("number") int number) {
		super();
		this.id = id;
		this.cardTypeId = cardTypeId;
		this.number = number;
	}
	
	public void addPiece(PieceInstance piece) {
		piece.setCard(this);
		pieces.add(piece);
	}
	
	public PieceInstance getPiece(String pieceId) {
		return pieces.stream().filter(piece -> piece.getId().equals(pieceId)).findFirst().orElse(null);
	}
	
	public void addMarker(MarkerInstance marker) {
		if (marker instanceof StackableMarkerInstance) {
			for (MarkerInstance markerOnCard : getMarkers()) {
				if (markerOnCard.getMarkerDefinitionId().equals(marker.getMarkerDefinitionId())) {
					// a marker of this type is already on the card. Add "number" to it
					((StackableMarkerInstance)markerOnCard).setNumber(((StackableMarkerInstance)markerOnCard).getNumber() + number);				
					return;
				}
			}
			markers.add(marker);
		} else {
			markers.add(marker);
		}
	}
	
	public MarkerInstance getMarker(String markerId) {
		return markers.stream().filter(m -> m.getId().equals(markerId)).findFirst().orElse(null);
	}
	
	public MarkerInstance getMarkerByType(String markerTypeId) {
		return markers.stream().filter(m -> m.getMarkerDefinitionId().equals(markerTypeId)).findFirst().orElse(null);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	

	public TreeSet<MarkerInstance> getMarkers() {
		if (markers == null) {
			markers = new TreeSet<>();
		}
		return markers;
	}

	public String getCardTypeId() {
		return cardTypeId;
	}

	public void setCardTypeId(String cardTypeId) {
		this.cardTypeId = cardTypeId;
	}

	public Queue<String> getPieceLeftToPlace() {
		return pieceLeftToPlace;
	}

	public List<PieceInstance> getPieces() {
		return pieces;
	}

	public void setPieces(List<PieceInstance> pieces) {
		if (pieces != null) {
			for (PieceInstance piece : pieces) {
				addPiece(piece);
			}
		}
	}

}
