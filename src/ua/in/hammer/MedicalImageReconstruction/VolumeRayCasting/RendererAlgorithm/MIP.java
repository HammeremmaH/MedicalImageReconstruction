package ua.in.hammer.MedicalImageReconstruction.VolumeRayCasting.RendererAlgorithm;

import org.la4j.vector.Vector;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Viewport;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Ray;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Scene;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Volume;

/**
 * Created by Hammer on 30.03.2014.
 */
public class MIP implements RendererAlgorithm
{
	public double[][] render(Scene scene, int fromY, int toY, int width, int height)
	{
		double[][] pixels = new double[toY - fromY][width];

		Volume volume = scene.getVolume();
		Viewport viewport = scene.getViewport();
		Vector viewPoint = scene.getViewPoint();

		double distanceDelta = volume.getMinSpacing();

		for (int y = fromY; y < toY; y++)
		{
			for (int x = 0; x < width; x++)
			{
				Ray ray = new Ray(viewPoint, viewport.getPoint(x, y, width, height)).getTransformedRay(volume.getInverseTransformation());

				double[] distances = volume.getIntersectionDistances(ray);

				double pixel = 0;

				if (distances.length > 0)
				{
					for (double distance = distances[0]; distance <= distances[1]; distance += distanceDelta)
					{
						double voxel = volume.getVoxelLocal(ray.getPoint(distance));

						if (voxel > pixel)
						{
							pixel = voxel;
						}
					}
				}

				pixels[y - fromY][x] = pixel;
			}
		}

		return pixels;
	}
}