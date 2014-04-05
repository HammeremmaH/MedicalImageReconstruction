package ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

/**
 * Created by Hammer on 29.03.2014.
 */
public class Viewport
{
	private Matrix cornerPoints;

	public Viewport(Matrix cornerPoints)
	{
		this.cornerPoints = cornerPoints;
	}

	public Matrix getCornerPoints()
	{
		return cornerPoints;
	}

	public Vector getPoint(int x, int y, int width, int height)
	{
		Vector leftSidePoint = cornerPoints.getRow(0).add(cornerPoints.getRow(1).subtract(cornerPoints.getRow(0)).multiply((double) y / (height - 1)));
		Vector rightSidePoint = cornerPoints.getRow(3).add(cornerPoints.getRow(2).subtract(cornerPoints.getRow(3)).multiply((double) y / (height - 1)));

		return leftSidePoint.add(rightSidePoint.subtract(leftSidePoint).multiply((double) x / (width - 1)));
	}
}
