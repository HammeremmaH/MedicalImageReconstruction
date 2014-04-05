package ua.in.hammer.MedicalImageReconstruction.Parallel;

import ua.in.hammer.MedicalImageReconstruction.VolumeRayCasting.RendererAlgorithm.RendererAlgorithm;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Scene;

/**
 * Created by Hammer on 30.03.2014.
 */
public class CPURendererWorker extends Thread
{
	private Scene scene;
	private RendererAlgorithm rendererAlgorithm;

	private int fromY;
	private int toY;
	private int width;
	private int heigth;

	private double[][] pixels;

	public CPURendererWorker(Scene scene, RendererAlgorithm rendererAlgorithm, int fromY, int toY, int width, int height)
	{
		this.scene = scene;
		this.rendererAlgorithm = rendererAlgorithm;

		this.fromY = fromY;
		this.toY = toY;
		this.width = width;
		this.heigth = height;
	}

	public int getFromY()
	{
		return fromY;
	}

	public int getToY()
	{
		return toY;
	}

	public double[][] getPixels()
	{
		return pixels;
	}

	@Override
	public void run()
	{
		pixels = rendererAlgorithm.render(scene, fromY, toY, width, heigth);
	}
}
