package ua.in.hammer.MedicalImageReconstruction.OpenCL;

import com.nativelibs4java.opencl.*;
import com.nativelibs4java.util.*;
import org.bridj.Pointer;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Scene;
import ua.in.hammer.MedicalImageReconstruction.Util.Array;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Created by Hammer on 22.03.2014.
 */
public class MIPRenderer
{
	private Scene scene;

	private CLContext context;
	private CLQueue queue;
	private CLKernel kernel;

	Pointer<Double> viewPortPointer;
	Pointer<Short> volumePointer;

	CLBuffer<Double> viewPort;
	CLBuffer<Short> volume;

	public MIPRenderer(Scene scene) throws IOException
	{
		this.scene = scene;

		context = JavaCL.createBestContext(CLPlatform.DeviceFeature.GPU);
		queue = context.createDefaultQueue();

		String source = IOUtils.readText(MIPRenderer.class.getResource("kernels.cl"));
		CLProgram program = context.createProgram(source).build();

		kernel = program.createKernel("mip");

		viewPortPointer = Pointer.pointerToDoubles(Array.toArray(((Basic2DMatrix) scene.getViewport().getCornerPoints()).toArray()));
		volumePointer = Pointer.pointerToShorts(scene.getVolume().getVoxelData());

		viewPort = context.createBuffer(CLMem.Usage.Input, viewPortPointer);
		volume = context.createBuffer(CLMem.Usage.Input, volumePointer);
	}

	public BufferedImage render(int width, int height)
	{
		Pointer<Double> volumeInverseTransformationPointer = Pointer.pointerToDoubles(Array.toArray(((Basic2DMatrix) scene.getVolume().getInverseTransformation()).toArray()));

		CLBuffer<Double> volumeInverseTransformation = context.createBuffer(CLMem.Usage.Input, volumeInverseTransformationPointer);
		CLBuffer<Double> pixels = context.createBuffer(CLMem.Usage.Output, Double.class, width * height);

		kernel.setArgs(
				((BasicVector) scene.getViewPoint()).toArray(),
				viewPort,
				volume,
				new int[] { scene.getVolume().getColumns(), scene.getVolume().getRows(), scene.getVolume().getSlices(), 0 },
				new double[] { scene.getVolume().getSpacingBetweenColumns(), scene.getVolume().getSpacingBetweenRows(), (float) scene.getVolume().getSpacingBetweenSlices(), 0 },
				volumeInverseTransformation,
				scene.getVolume().getMinValue(),
				scene.getVolume().getMaxValue(),
				width,
				height,
				pixels
		);

		for (int i = 0; i < height; i += 128)
		{
			for (int j = 0; j < width; j += 128)
			{
				kernel.enqueueNDRange(queue, new long[] { i, j }, new long[] { 128, 128 }, new long[] { 8, 8 });
			}
		}

		Pointer<Double> pixelsPointer = pixels.read(queue);

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				double value = pixelsPointer.get(y * width + x);

				bufferedImage.setRGB(x, y, new Color((float) value, (float) value, (float) value).getRGB());
			}
		}

		volumeInverseTransformation.release();
		pixels.release();

		volumeInverseTransformationPointer.release();
		pixelsPointer.release();

		/*viewPort.release();
		volume.release();

		viewPortPointer.release();
		volumePointer.release();*/

		return bufferedImage;
	}

	/*
		CLImageFormat imageFormat = new CLImageFormat(CLImageFormat.ChannelOrder.INTENSITY, CLImageFormat.ChannelDataType.UnsignedInt16);

		CLSampler sampler = context.createSampler(true, CLSampler.AddressingMode.Repeat, CLSampler.FilterMode.Linear);
		CLImage3D image3D = context.createImage3D(CLMem.Usage.Input, imageFormat, 100, 100, 100);
		*/
}
