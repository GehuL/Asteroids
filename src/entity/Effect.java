package entity;

import java.awt.Image;

import asteroids.Game;

public class Effect extends Item{

	protected Effect(Game game, float x, float y, int width, int height, Image image) {
		super(game, x, y, width, height, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean onCollision(Entity entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
