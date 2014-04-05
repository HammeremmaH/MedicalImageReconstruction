package ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel;

import com.pixelmed.dicom.DicomException;
import org.la4j.LinearAlgebra;
import org.la4j.matrix.Matrix;
import org.la4j.vector.Vector;
import org.la4j.vector.dense.BasicVector;
import ua.in.hammer.MedicalImageReconstruction.DICOM.DICOMFileSet;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Ray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Hammer on 29.03.2014.
 */
public class Volume extends Object
{
	private short[] voxelData;

	private short minValue;
	private short maxValue;

	private int columns;
	private int rows;
	private int slices;

	private double spacingBetweenColumns;
	private double spacingBetweenRows;
	private double spacingBetweenSlices;

	public Volume(DICOMFileSet dicomFileSet) throws DicomException
	{
		voxelData = dicomFileSet.getPixelData();

		minValue = voxelData[0];
		maxValue = voxelData[0];

		for (int i = 0; i < voxelData.length; i++)
		{
			if (voxelData[i] < minValue)
			{
				minValue = voxelData[i];
			}

			if (voxelData[i] > maxValue)
			{
				maxValue = voxelData[i];
			}
		}

		columns = dicomFileSet.getColumns();
		rows = dicomFileSet.getRows();
		slices = dicomFileSet.getSlices();

		spacingBetweenColumns = dicomFileSet.getSpacingBetweenColumns();
		spacingBetweenRows = dicomFileSet.getSpacingBetweenRows();
		spacingBetweenSlices = dicomFileSet.getSpacingBetweenSlices();

		modifyTransformation(Transformation.getTranslationMatrix(getDimensions().divide(2).multiply(-1)));

		//TODO: поворачивать "лицом" на основе параметров ориентации в DICOM-файле
		modifyTransformation(Transformation.getXRotationMatrix(Math.PI / 2));
	}

	public short[] getVoxelData()
	{
		return voxelData;
	}

	public short getMinValue()
	{
		return minValue;
	}

	public short getMaxValue()
	{
		return maxValue;
	}

	public int getColumns()
	{
		return columns;
	}

	public int getRows()
	{
		return rows;
	}

	public int getSlices()
	{
		return slices;
	}

	public double getSpacingBetweenColumns()
	{
		return spacingBetweenColumns;
	}

	public double getSpacingBetweenRows()
	{
		return spacingBetweenRows;
	}

	public double getSpacingBetweenSlices()
	{
		return spacingBetweenSlices;
	}

	public Vector getDimensions()
	{
		return new BasicVector(new double[] { columns * spacingBetweenColumns, rows * spacingBetweenRows, slices * spacingBetweenSlices, 0 });
	}

	private Vector getLocalPoint(Vector globalPoint)
	{
		return globalPoint.multiply(getInverseTransformation());
	}

	private double[] getVoxelDataIndexes(Vector localPoint)
	{
		return new double[] { localPoint.get(0) / spacingBetweenColumns , localPoint.get(1) / spacingBetweenRows, localPoint.get(2) / spacingBetweenSlices };
	}

	private boolean isInteriorIndexes(double[] indexes)
	{
		return !(Math.round(indexes[0]) < 0 || Math.round(indexes[0]) > columns
				|| Math.round(indexes[1]) < 0 || Math.round(indexes[1]) > rows
				|| Math.round(indexes[2]) < 0 || Math.round(indexes[2]) > slices);
	}

	private boolean isInterior(Vector point)
	{
		return isInteriorIndexes(getVoxelDataIndexes(getLocalPoint(point)));
	}

	public double[] getIntersectionDistances(Ray ray)
	{
		Vector localRayInitialPoint = getLocalPoint(ray.getInitialPoint());
		Vector localRayDirectionNormalVector = getLocalPoint(ray.getDirectionNormalVector());

		Vector[] planeNormals = new Vector[]
		{
			new BasicVector(new double[] { -1, 0, 0, 0 }),
			new BasicVector(new double[] { 0, -1, 0, 0 }),
			new BasicVector(new double[] { 0, 0, -1, 0 }),
			new BasicVector(new double[] { 1, 0, 0, 0 }),
			new BasicVector(new double[] { 0, 1, 0, 0 }),
			new BasicVector(new double[] { 0, 0, 1, 0 })
		};

		Vector[] planePoints = new Vector[]
		{
			new BasicVector(new double[] { 0, 0, 0, 1 }),
			getDimensions()
		};

		ArrayList<Double> distances = new ArrayList<>();

		for (int i = 0; i < 6; i++)
		{
			double distance = - (planeNormals[i].innerProduct(localRayInitialPoint) - planeNormals[i].innerProduct(planePoints[i / 3])) / planeNormals[i].innerProduct(localRayDirectionNormalVector);

			if (isInterior(ray.getPoint(distance)) == true)
			{
				distances.add(distance);
			}
		}

		return distances.size() > 0 ? new double[] { Collections.min(distances), Collections.max(distances) } : new double[] { };
	}

	public double getMinSpacing()
	{
		if (spacingBetweenColumns < spacingBetweenRows && spacingBetweenColumns < spacingBetweenSlices)
		{
			return spacingBetweenColumns;
		}
		else
		{
			if (spacingBetweenRows < spacingBetweenSlices)
			{
				return spacingBetweenRows;
			}
			else
			{
				return spacingBetweenSlices;
			}
		}
	}

	public double getVoxelLocal(Vector localPoint)
	{
		double[] indexes = getVoxelDataIndexes(localPoint);

		int column = (int) Math.floor(indexes[0]);
		int row = (int) Math.floor(indexes[1]);
		int slice = (int) Math.floor(indexes[2]);

		if (column < 0 || column >= columns
				|| row < 0 || row >= rows
				|| slice < 0 || slice >= slices)
		{
			return 0;
		}
		else
		{
			return (double) (voxelData[columns * rows * slice + columns * row + column] - minValue) / (maxValue - minValue);
		}
	}

	public double getVoxel(Vector point)
	{
		return getVoxelLocal(getLocalPoint(point));
	}
}
