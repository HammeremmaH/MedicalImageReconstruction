package ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel;

import org.la4j.matrix.Matrix;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;

/**
 * Created by Hammer on 30.03.2014.
 */
public class Transformation
{
	public static Matrix getIdentityMatrix()
	{
		return new Basic2DMatrix(
			new double[][]
			{
				new double[] { 1, 0, 0, 0 },
				new double[] { 0, 1, 0, 0 },
				new double[] { 0, 0, 1, 0 },
				new double[] { 0, 0, 0, 1 }
			});
	}

	public static Matrix getTranslationMatrix(Vector deltaVector)
	{
		return new Basic2DMatrix(
			new double[][]
			{
				new double[] { 1, 0, 0, 0 },
				new double[] { 0, 1, 0, 0 },
				new double[] { 0, 0, 1, 0 },
				new double[] { deltaVector.get(0), deltaVector.get(1), deltaVector.get(2), 1 }
			});
	}

	public static Matrix getScaleMatrix(Vector deltaVector)
	{
		return new Basic2DMatrix(
			new double[][]
			{
				new double[] { deltaVector.get(0), 0, 0, 0 },
				new double[] { 0, deltaVector.get(1), 0, 0 },
				new double[] { 0, 0, deltaVector.get(2), 0 },
				new double[] { 0, 0, 0, 1 }
			});
	}

	public static Matrix getXRotationMatrix(double delta)
	{
		return new Basic2DMatrix(
			new double[][]
			{
				new double[] { 1, 0, 0, 0 },
				new double[] { 0, Math.cos(delta), Math.sin(delta), 0 },
				new double[] { 0, - Math.sin(delta), Math.cos(delta), 0 },
				new double[] { 0, 0, 0, 1 }
			});
	}

	public static Matrix getYRotationMatrix(double delta)
	{
		return new Basic2DMatrix(
			new double[][]
			{
				new double[] { Math.cos(delta), 0, Math.sin(delta), 0 },
				new double[] { 0, 1, 0, 0 },
				new double[] { - Math.sin(delta), 0, Math.cos(delta), 0 },
				new double[] { 0, 0, 0, 1 }
			});
	}

	public static Matrix getZRotationMatrix(double delta)
	{
		return new Basic2DMatrix(
			new double[][]
			{
				new double[] { Math.cos(delta), Math.sin(delta), 0, 0 },
				new double[] { - Math.sin(delta), Math.cos(delta), 0, 0 },
				new double[] { 0, 0, 1, 0 },
				new double[] { 0, 0, 0, 1 }
			});
	}
}
