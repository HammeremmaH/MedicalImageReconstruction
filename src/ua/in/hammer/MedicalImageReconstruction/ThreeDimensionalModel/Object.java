package ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel;

import org.la4j.LinearAlgebra;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;

/**
 * Created by Hammer on 02.04.2014.
 */
public abstract class Object
{
	private Matrix transformation;
	private Matrix inverseTransformation;

	public Object()
	{
		transformation = Transformation.getIdentityMatrix();
		inverseTransformation = Transformation.getIdentityMatrix();
	}

	public Matrix getTransformation()
	{
		return transformation;
	}

	public final Matrix getInverseTransformation()
	{
		return inverseTransformation;
	}

	public void modifyTransformation(Matrix transformation)
	{
		this.transformation = this.transformation.multiply(transformation);
		this.inverseTransformation = this.transformation.withInverter(LinearAlgebra.InverterFactory.GAUSS_JORDAN).inverse();
	}

	public abstract Vector getDimensions();
}
