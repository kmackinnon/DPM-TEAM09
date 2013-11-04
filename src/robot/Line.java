package robot;

public class Line {

	private Coordinates c1;
	private Coordinates c2;
	private double lengthSquared;

	public Line(Coordinates c1, Coordinates c2) {

		this.c1 = c1;
		this.c2 = c2;
		this.lengthSquared = Math.pow((c1.getX() - c2.getX()), 2)
				+ Math.pow((c1.getY() - c2.getY()), 2);

	}

	public double shortestDistance(Coordinates a) {

		if (lengthSquared == 0.0) {
			return distanceBetweenTwoPoints(a, c1);
		}

		double parameter = projectionOntoLine(a, c1, c2) / lengthSquared;

		if (parameter < 0.0) {
			return distanceBetweenTwoPoints(a, c2);
		}

		else if (parameter > 1.0) {
			return distanceBetweenTwoPoints(a, c1);
		}

		
		
		Coordinates projection = new Coordinates(c2.getX() + parameter
				* (c1.getX() - c2.getX()), c2.getY() + parameter
				* (c1.getY() - c2.getY()));
		
		
		return distanceBetweenTwoPoints(a, projection);

	}

	private double distanceBetweenTwoPoints(Coordinates a, Coordinates b) {

		return Math.sqrt(Math.pow((a.getX() - b.getX()), 2)
				+ Math.pow((a.getY() - b.getY()), 2));

	}

	private double projectionOntoLine(Coordinates a, Coordinates b,
			Coordinates c) {

		double x1 = a.getX() - c.getX();
		double y1 = a.getY() - c.getY();

		double x2 = b.getX() - c.getX();
		double y2 = b.getY() - c.getY();

		return x1 * x2 + y1 * y2;

	}

}
