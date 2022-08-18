package asteroids;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import entity.Entity;

public interface Collision {

	public final Rectangle2D hitbox = new Rectangle();

	public boolean onCollision(Entity entity);

}
