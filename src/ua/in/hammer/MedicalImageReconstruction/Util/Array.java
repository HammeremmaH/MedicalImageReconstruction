package ua.in.hammer.MedicalImageReconstruction.Util;

/**
 * Created by Hammer on 02.04.2014.
 */
public class Array
{
	public static double[] toArray(double[][] matrix)
	{
		double[] array = new double[matrix.length * matrix[0].length];

		for (int i = 0; i < matrix.length; i++)
		{
			for (int j = 0; j < matrix[0].length; j++)
			{
				array[i * matrix.length + j] = (float) matrix[i][j];
			}
		}

		return array;
	}
}
