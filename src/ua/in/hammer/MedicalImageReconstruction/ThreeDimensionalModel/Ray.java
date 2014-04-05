package ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel;

import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.Vectors;

/**
 * Created by Hammer on 29.03.2014.
 */
public class Ray
{
	private Vector initialPoint;
	private Vector directionNormalVector;

	private Ray()
	{

	}

	public Ray(Vector viewPoint, Vector viewPortPoint)
	{
		initialPoint = viewPortPoint;

		Vector directionVector = viewPortPoint.subtract(viewPoint);
		directionNormalVector = directionVector.divide(directionVector.fold(Vectors.mkEuclideanNormAccumulator()));
	}

	public Vector getInitialPoint()
	{
		return initialPoint;
	}

	public Vector getDirectionNormalVector()
	{
		return directionNormalVector;
	}

	public Vector getPoint(double distance)
	{
		return initialPoint.add(directionNormalVector.multiply(distance));
	}

	public Ray getTransformedRay(Matrix transformation)
	{
		Ray ray = new Ray();

		ray.initialPoint = initialPoint.multiply(transformation);
		ray.directionNormalVector = directionNormalVector.multiply(transformation);

		return ray;
	}
}
