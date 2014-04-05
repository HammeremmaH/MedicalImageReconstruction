package ua.in.hammer.MedicalImageReconstruction.VolumeRayCasting.RendererAlgorithm;

import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Scene;

/**
 * Created by Hammer on 30.03.2014.
 */
public interface RendererAlgorithm
{
	double[][] render(Scene scene, int fromY, int toY, int width, int height);
}
