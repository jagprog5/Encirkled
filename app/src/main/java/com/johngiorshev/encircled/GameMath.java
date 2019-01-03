package com.johngiorshev.encircled;

import java.util.Random;

public class GameMath {
	public static final Random random = new Random();

	private GameMath() {
	}

	/**
	 * @param x1
	 *            X value of first coordinate.
	 * @param y1
	 *            Y value of first coordinate.
	 * @param x2
	 *            X value of second coordinate.
	 * @param y2
	 *            Y value of second coordinate.
	 * @return The euclidean distance between the points (x1, y1) and (x2, y2).
	 */
	public static float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * @param shooterX
	 *            The shooter's X position
	 * @param shooterY
	 *            The shooter's Y position.
	 * @param targetX
	 *            The target's X position.
	 * @param targetY
	 *            The target's Y position.
	 * @param projectileSpeed
	 *            The total speed for the projectile. The distance between the point
	 *            (0, 0) and the output point (delta X, delta Y) will be equal to
	 *            this input.
	 * @return A float[] containing the delta X and delta Y value for a projectile
	 *         to hit a stationary target at the position (targetX, targetY) when
	 *         being fired from (shooterX, shooterY). [0] is the deltaX, [1] is the
	 *         deltaY. Special Case: If the points (shooterX, shooterY) and
	 *         (targetX, targetY) are in the exact same location, float[] {0, 0}
	 *         will be returned.
	 */
	public static float[] projectileSpeed(float shooterX, float shooterY, float targetX, float targetY,
			float projectileSpeed) {
		// got to love grade 10 similar triangles
		float zVal = projectileSpeed / distance(shooterX, shooterY, targetX, targetY);
		if (Float.isInfinite(zVal) || Float.isNaN(zVal)) {
			return new float[] { 0f, 0f };
		}
		return new float[] { zVal * (targetX - shooterX), zVal * (targetY - shooterY) };
	}

	/**
	 * Intended to be used for spawn points. Transform the output as desired from
	 * (0, 0).
	 * 
	 * @param minRadius
	 *            The minimum distance for the returned point, inclusively.
	 * @param maxRadius
	 *            The maximum distance for the returned point, exclusively.
	 * @return A float[] containing a random point so that the distance between the
	 *         returned point and the point at (0, 0) is greater than minRadius and
	 *         less than maxRadius. [0] is the X value, at [1] is the Y value.
	 */
	public static float[] spawnPoint(float minRadius, float maxRadius) {
		double theta = random.nextFloat() * (Math.PI * 2);
		float radius = nextFloat(minRadius, maxRadius);
		return new float[] { (float) Math.cos(theta) * radius, (float) Math.sin(theta) * radius };
	}

	/**
	 * @param min
	 *            Minimum returned value, inclusively.
	 * @param max
	 *            Maximum returned value, exclusively.
	 * @return A random float greater than or equal to min and less than max.
	 */
	public static float nextFloat(float min, float max) {
		return random.nextFloat() * (max - min) + min;
	}
}
