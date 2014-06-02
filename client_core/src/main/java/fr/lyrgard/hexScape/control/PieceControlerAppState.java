package fr.lyrgard.hexScape.control;


import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import fr.lyrgard.hexScape.HexScapeCore;
import fr.lyrgard.hexScape.event.piece.PieceAddedEvent;
import fr.lyrgard.hexScape.event.piece.PieceMovedEvent;
import fr.lyrgard.hexScape.event.piece.PieceRemovedEvent;
import fr.lyrgard.hexScape.event.piece.PieceSelectedEvent;
import fr.lyrgard.hexScape.event.piece.PieceUnselectedEvent;
import fr.lyrgard.hexScape.listener.MapService;
import fr.lyrgard.hexScape.model.MoveablePiece;

public class PieceControlerAppState extends AbstractAppState implements ActionListener {

	private static final String CLICK_MAPPING = "ControlerAppState_click";
	private static final String CANCEL_MAPPING = "ControlerAppState_cancel";
	private static final String DELETE_MAPPING = "ControlerAppState_delete";
	private static final String MOUSE_WHEEL_UP_MAPPING = "ControlerAppState_mouseWheelUp";
	private static final String MOUSE_WHEEL_DOWN_MAPPING = "ControlerAppState_mouseWheelDown";
	
	private PlacePieceByMouseAppState placePieceByMouseAppState = new PlacePieceByMouseAppState();
	private SelectPieceByMouseAppState selectPieceByMouseAppState = new SelectPieceByMouseAppState();
	
	private State currentState = State.WAITING;
	
	private InputManager inputManager;
	
	private MapService mapService;
	
	private enum State {
		WAITING, ADDING_PIECE, MOVING_PIECE, SELECTING_PIECE
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		placePieceByMouseAppState.initialize(stateManager, app);
		selectPieceByMouseAppState.initialize(stateManager, app);
		this.mapService = HexScapeCore.getInstance().getMapService();
		
		changeStateTo(State.WAITING);
		
		inputManager = app.getInputManager();
		
		
		inputManager.addMapping(CLICK_MAPPING, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping(CANCEL_MAPPING, new KeyTrigger(KeyInput.KEY_ESCAPE));
		inputManager.addMapping(DELETE_MAPPING, new KeyTrigger(KeyInput.KEY_DELETE));
		inputManager.addMapping(MOUSE_WHEEL_UP_MAPPING, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
		inputManager.addMapping(MOUSE_WHEEL_DOWN_MAPPING, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
	
		inputManager.addListener(this, CLICK_MAPPING, CANCEL_MAPPING, DELETE_MAPPING, MOUSE_WHEEL_UP_MAPPING, MOUSE_WHEEL_DOWN_MAPPING);
	}
	
	public void addPiece(MoveablePiece piece) {
		placePieceByMouseAppState.setPieceToPlace(piece);
		changeStateTo(State.ADDING_PIECE);
	}
	
	public void moveSelectedPiece() {
		if (currentState == State.SELECTING_PIECE) {
			changeStateTo(State.MOVING_PIECE);
		}
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		if (currentState == State.ADDING_PIECE || currentState == State.MOVING_PIECE) {
			placePieceByMouseAppState.update(tpf);
		}
		if (selectPieceByMouseAppState.isEnabled()) {
			selectPieceByMouseAppState.update(tpf);
		}
	}

	@Override
	public void onAction(String name, boolean keyPressed, float tpf) {
		MoveablePiece piece;
	
		if (!isEnabled()) {
			return;
		}
		
		if (name.equals(CLICK_MAPPING)) {
			switch (currentState) {
			case WAITING:
				if (keyPressed && selectPieceByMouseAppState.getPieceUnderMouse() != null) {
					changeStateTo(State.SELECTING_PIECE);
					if (selectPieceByMouseAppState.getSelectedPiece() != null) {
						changeStateTo(State.MOVING_PIECE);
					}
				}
				break;
			case ADDING_PIECE:
				if (keyPressed) {
					changeStateTo(State.SELECTING_PIECE);
				}
				break;
			case MOVING_PIECE:
				changeStateTo(State.SELECTING_PIECE);
				break;
			case SELECTING_PIECE:
				changeStateTo(State.SELECTING_PIECE);
				if (keyPressed && selectPieceByMouseAppState.getSelectedPiece() == selectPieceByMouseAppState.getPieceUnderMouse()) {
					changeStateTo(State.MOVING_PIECE);
				}
				break;
			}

		} else if (name.equals(CANCEL_MAPPING) && keyPressed) {
			switch (currentState) {
			case WAITING:
				// Nothing to do here
				break;
			case ADDING_PIECE:
				changeStateTo(State.WAITING);
				break;
			case MOVING_PIECE:
				changeStateTo(State.SELECTING_PIECE);
				break;
			case SELECTING_PIECE:
				changeStateTo(State.WAITING);
				break;
			}
		} else if (name.equals(MOUSE_WHEEL_UP_MAPPING)) {
			switch (currentState) {
			case WAITING:
				// Nothing to do here
				break;
			case ADDING_PIECE:
				rotatePiece(placePieceByMouseAppState.getPieceToPlace(), true);
				break;
			case MOVING_PIECE:
				rotatePiece(placePieceByMouseAppState.getPieceToPlace(), true);
				break;
			case SELECTING_PIECE:
				rotatePiece(selectPieceByMouseAppState.getSelectedPiece(), true);
				break;
			}
		} else if (name.equals(MOUSE_WHEEL_DOWN_MAPPING)) {
			switch (currentState) {
			case WAITING:
				// Nothing to do here
				break;
			case ADDING_PIECE:
				rotatePiece(placePieceByMouseAppState.getPieceToPlace(), false);
				break;
			case MOVING_PIECE:
				rotatePiece(placePieceByMouseAppState.getPieceToPlace(), false);
				break;
			case SELECTING_PIECE:
				rotatePiece(selectPieceByMouseAppState.getSelectedPiece(), false);
				break;
			}
		} else if (name.equals(DELETE_MAPPING) && keyPressed) {
			switch (currentState) {
			case WAITING:
				// Nothing to do here
				break;
			case ADDING_PIECE:
				changeStateTo(State.WAITING);
				break;
			case MOVING_PIECE:
				piece = placePieceByMouseAppState.getPieceToPlace();
				if (piece != null) {
					mapService.removePiece(piece);
					HexScapeCore.getInstance().getEventBus().post(new PieceRemovedEvent(piece, HexScapeCore.getInstance().getPlayer()));
				}
				changeStateTo(State.WAITING);
				break;
			case SELECTING_PIECE:
				piece = selectPieceByMouseAppState.getSelectedPiece();
				if (piece != null) {
					mapService.removePiece(piece);
					HexScapeCore.getInstance().getEventBus().post(new PieceRemovedEvent(piece, HexScapeCore.getInstance().getPlayer()));
				}
				changeStateTo(State.WAITING);
				break;
			}
		}
	}
	
	public void rotatePiece(MoveablePiece piece, boolean clockwise) {
		piece.setDirection(piece.getDirection().rotate(clockwise));
		piece.getSpatial().setLocalRotation(new Quaternion().fromAngleAxis(piece.getDirection().getAngle(), Vector3f.UNIT_Y));
	}
	
	private void changeStateTo(State newState) {
		MoveablePiece piece;
		
		switch (newState) {
		case WAITING:
			switch (currentState) {
			case SELECTING_PIECE:
				piece = selectPieceByMouseAppState.getSelectedPiece();
				HexScapeCore.getInstance().getEventBus().post(new PieceUnselectedEvent(piece, HexScapeCore.getInstance().getPlayer()));
				selectPieceByMouseAppState.cancelSelection();
				break;
			case MOVING_PIECE:
				piece = selectPieceByMouseAppState.getSelectedPiece();
				HexScapeCore.getInstance().getEventBus().post(new PieceUnselectedEvent(piece, HexScapeCore.getInstance().getPlayer()));
				selectPieceByMouseAppState.cancelSelection();
				break;
			default:
				break;
			}
			placePieceByMouseAppState.setEnabled(false);
			selectPieceByMouseAppState.setEnabled(true);
			break;
		case ADDING_PIECE:
			switch (currentState) {
			case WAITING:
				// Nothing to do here
				break;
			case SELECTING_PIECE:
				piece = selectPieceByMouseAppState.getSelectedPiece();
				HexScapeCore.getInstance().getEventBus().post(new PieceUnselectedEvent(piece, HexScapeCore.getInstance().getPlayer()));
				selectPieceByMouseAppState.cancelSelection();
				break;
			case ADDING_PIECE:
				// Nothing to do here
				break;
			case MOVING_PIECE:
				// Nothing to do here
				break;
			default:
				break;
			}
			selectPieceByMouseAppState.setEnabled(false);
			placePieceByMouseAppState.setEnabled(true);
			break;
		case MOVING_PIECE:
			switch (currentState) {
			case SELECTING_PIECE:
				placePieceByMouseAppState.setPieceToPlace(selectPieceByMouseAppState.getSelectedPiece());
				break;
			default:
				break;
			}
			selectPieceByMouseAppState.setEnabled(true);
			placePieceByMouseAppState.setEnabled(true);
			break;
		case SELECTING_PIECE:
			switch (currentState) {
			case WAITING:
				if (selectPieceByMouseAppState.selectPiece()) {
					piece = selectPieceByMouseAppState.getSelectedPiece();
					HexScapeCore.getInstance().getEventBus().post(new PieceSelectedEvent(piece, HexScapeCore.getInstance().getPlayer()));
				}
				break;
			case SELECTING_PIECE:
				if (selectPieceByMouseAppState.selectPiece()) {
					piece = selectPieceByMouseAppState.getSelectedPiece();
					HexScapeCore.getInstance().getEventBus().post(new PieceSelectedEvent(piece, HexScapeCore.getInstance().getPlayer()));
				}
				break;
			case ADDING_PIECE:
				piece = placePieceByMouseAppState.getPieceToPlace();
				if (placePieceByMouseAppState.placePiece()) {
					HexScapeCore.getInstance().getEventBus().post(new PieceAddedEvent(piece, HexScapeCore.getInstance().getPlayer()));
					selectPieceByMouseAppState.selectPiece(piece);
					HexScapeCore.getInstance().getEventBus().post(new PieceSelectedEvent(piece, HexScapeCore.getInstance().getPlayer()));
				}
				break;
			case MOVING_PIECE:
				piece = placePieceByMouseAppState.getPieceToPlace();
				if (placePieceByMouseAppState.placePiece()) {
					HexScapeCore.getInstance().getEventBus().post(new PieceMovedEvent(placePieceByMouseAppState.getPieceToPlace(), HexScapeCore.getInstance().getPlayer()));
					selectPieceByMouseAppState.selectPiece(piece);
				}
				break;
			default:
				break;
			}
			placePieceByMouseAppState.setEnabled(false);
			selectPieceByMouseAppState.setEnabled(true);
			break;
		}
		
		currentState = newState;
	}
}
