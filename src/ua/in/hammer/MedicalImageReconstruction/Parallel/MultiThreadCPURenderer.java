package ua.in.hammer.MedicalImageReconstruction.Parallel;

import ua.in.hammer.MedicalImageReconstruction.VolumeRayCasting.RendererAlgorithm.RendererAlgorithm;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Scene;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Hammer on 30.03.2014.
 */
public class MultiThreadCPURenderer
{
	private Scene scene;
	private RendererAlgorithm rendererAlgorithm;


	public MultiThreadCPURenderer(Scene scene, RendererAlgorithm rendererAlgorithm)
	{
		this.scene = scene;
		this.rendererAlgorithm = rendererAlgorithm;
	}

	public BufferedImage render(int width, int height) throws InterruptedException
	{
		int numberOfCores = Runtime.getRuntime().availableProcessors();
		//int numberOfCores = 1;
		CPURendererWorker[] cpuRendererWorkers = new CPURendererWorker[numberOfCores];

		int lastRows = 0;
		int rows;

		for (int i = 0; i < numberOfCores; i++)
		{
			rows = height / numberOfCores + (i < height % numberOfCores ? 1 : 0);

			cpuRendererWorkers[i] = new CPURendererWorker(scene, rendererAlgorithm, lastRows, lastRows + rows, width, height);
			cpuRendererWorkers[i].start();

			lastRows += rows;
		}

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < numberOfCores; i++)
		{
			cpuRendererWorkers[i].join();

			int fromY = cpuRendererWorkers[i].getFromY();
			int toY = cpuRendererWorkers[i].getToY();
			double[][] pixels = cpuRendererWorkers[i].getPixels();

			for (int y = fromY; y < toY; y++)
			{
				for (int x = 0; x < width; x++)
				{
					double value = pixels[y - fromY][x];

					bufferedImage.setRGB(x, y, new Color((float) value, (float) value, (float) value).getRGB());
				}
			}
		}

		return bufferedImage;
	}
}
