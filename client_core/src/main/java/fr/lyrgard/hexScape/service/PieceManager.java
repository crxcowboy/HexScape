package fr.lyrgard.hexScape.service;

import java.util.Iterator;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import fr.lyrgard.hexScape.HexScapeCore;
import fr.lyrgard.hexScape.model.SecondarySelectMarker;
import fr.lyrgard.hexScape.model.SelectMarker;
import fr.lyrgard.hexScape.model.map.Direction;
import fr.lyrgard.hexScape.model.map.Tile;
import fr.lyrgard.hexScape.model.model3d.TileMesh;
import fr.lyrgard.hexScape.model.piece.PieceInstance;
import fr.lyrgard.hexScape.utils.CoordinateUtils;

public class PieceManager {

	private PieceInstance piece;
	
	private Node pieceNode;
	
	private Spatial pieceModelSpatial;
	
	private boolean selected = false;
	
	private boolean secondarySelected = false;

	public PieceManager(PieceInstance piece) {
		super();
		this.piece = piece;
	}



	public PieceInstance getPiece() {
		return piece;
	}
	
	public Spatial getSpatial() {
		if (pieceNode == null) {
			pieceNode = new Node();
			pieceModelSpatial = ExternalModelService.getInstance().getModel(piece.getModelId());
			pieceNode.attachChild(pieceModelSpatial);
		}
		return pieceNode;
	}
	
	public void rotate(Direction direction) {
		getPiece().setDirection(direction);
		
		float angle = DirectionService.getInstance().getAngle(direction);
		pieceModelSpatial.setLocalRotation(new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y));
	}
	
	public void moveTo(int x, int y, int z, Direction direction) {
		Tile nearestTile = HexScapeCore.getInstance().getMapManager().getNearestTile(x, y, z);
		if (nearestTile != null) {
			Vector3f spacePos = CoordinateUtils.toSpaceCoordinate(nearestTile.getX(), nearestTile.getY(), nearestTile.getZ());

			if (nearestTile.isHalfSize()) {
				spacePos.y += TileMesh.HEX_SIZE_Y / 2;
			} else {
				spacePos.y += TileMesh.HEX_SIZE_Y;
			}
			getSpatial().setLocalTranslation(spacePos);
			piece.setX(x);
			piece.setY(y);
			piece.setZ(z);
		}
		if (direction != piece.getDirection()) {
			rotate(direction);
		}
	}
	
	public void select(String playerId) {
		SelectMarker selectMarker = SelectMarkerService.getInstance().getSelectMarker(playerId);
		pieceNode.attachChild(selectMarker.getSpatial());
		selectMarker.getSpatial().setLocalTranslation(0, 0.3f, 0);
		selected = true;
	}
	
	public void unselect(String playerId) {
		SelectMarker selectMarker = SelectMarkerService.getInstance().getSelectMarker(playerId);
		pieceNode.detachChild(selectMarker.getSpatial());
		Iterator<SecondarySelectMarker> it = selectMarker.getSecondarySelectMarkers().iterator();
		while (it.hasNext()) {
			SecondarySelectMarker secondarySelectMarker = it.next();
			secondarySelectMarker.getSecondarySelectedPiece().switchSecondarySelect(playerId, this);
		}
		selected = false;
	}
	
	public boolean isSelected() {
		return selected;
	}



	public void switchSecondarySelect(String playerId, PieceManager selectedPiece) {
		SelectMarker selectMarker = SelectMarkerService.getInstance().getSelectMarker(playerId);
		Iterator<SecondarySelectMarker> it = selectMarker.getSecondarySelectMarkers().iterator();
		boolean secondarySelectMarkerFound = false;
		while (it.hasNext()) {
			SecondarySelectMarker secondarySelectMarker = it.next();
			if (pieceNode.hasChild(secondarySelectMarker.getSpatial())) {
				secondarySelectMarkerFound = true;
				pieceNode.detachChild(secondarySelectMarker.getSpatial());
				it.remove();
				secondarySelected = false;
				break;
			}
		}
		if (!secondarySelectMarkerFound) {
			SecondarySelectMarker secondarySelectMarker = SelectMarkerService.getInstance().getNewSecondarySelectMarker(playerId, selectedPiece, this);
			selectMarker.getSecondarySelectMarkers().add(secondarySelectMarker);
			pieceNode.attachChild(secondarySelectMarker.getSpatial());
			secondarySelectMarker.getSpatial().setLocalTranslation(0, 0.3f, 0);
			secondarySelected = true;
		}
	}



	public boolean isSecondarySelected() {
		return secondarySelected;
	}
	
	
}
