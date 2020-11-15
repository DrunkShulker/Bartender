package org.newdawn.slick.command;


public class ControllerDirectionControl extends ControllerControl {
	
	public static final Direction LEFT = new Direction(LEFT_EVENT);
	
	public static final Direction UP = new Direction(UP_EVENT);
	
	public static final Direction DOWN = new Direction(DOWN_EVENT);
	
	public static final Direction RIGHT = new Direction(RIGHT_EVENT);
	
	
	public ControllerDirectionControl(int controllerIndex, Direction dir) {
		super(controllerIndex, dir.event, 0);
	}
	
	
	private static class Direction {
		
		private int event;
		
		
		public Direction(int event) {
			this.event = event;
		}
	}
}
