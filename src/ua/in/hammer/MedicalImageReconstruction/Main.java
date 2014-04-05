package ua.in.hammer.MedicalImageReconstruction;

import com.pixelmed.dicom.*;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;
import ua.in.hammer.MedicalImageReconstruction.DICOM.DICOMFileSet;
import ua.in.hammer.MedicalImageReconstruction.OpenCL.MIPRenderer;
import ua.in.hammer.MedicalImageReconstruction.Parallel.MultiThreadCPURenderer;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Viewport;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Scene;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Transformation;
import ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel.Volume;
import ua.in.hammer.MedicalImageReconstruction.VolumeRayCasting.RendererAlgorithm.MIP;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * Created by Hammer on 22.03.2014.
 */
public class Main
{
	public static void main(String[] args) throws IOException, DicomException, InterruptedException
	{
		String path;

		if (args.length > 0)
		{
			path = args[0];
		}
		else
		{
			path = "./dataset";
		}

		DICOMFileSet dicomFileSet = new DICOMFileSet(new File(path));

		Viewport viewport = new Viewport(new Basic2DMatrix(new double[][]
		{
			new double[] { -100, 100, -300, 1 },
			new double[] { -100, -100, -300, 1 },
			new double[] { 100, -100, -300, 1 },
			new double[] { 100, 100, -300, 1 }
		}));

		Volume volume = new Volume(dicomFileSet);

		Scene scene = new Scene(new BasicVector(new double[] { 0, 0, -900, 1 }), viewport, volume);

		//MultiThreadCPURenderer multiThreadCPURenderer = new MultiThreadCPURenderer(scene, new MIP());
		MIPRenderer mipRenderer = new MIPRenderer(scene);

		//BufferedImage bufferedImage = multiThreadCPURenderer.render(1024, 1024);

		for (int i = 0; i < 64; i++)
		{
			System.out.println(i);

			long startTime = System.nanoTime();

			BufferedImage bufferedImage = mipRenderer.render(1024, 1024);

			System.out.println("GPU: " + (System.nanoTime() - startTime) / 1000000000.0);

			ImageIO.write(bufferedImage, "jpg", new File("out" + i + ".jpg"));
			//ImageIO.write(bufferedImage, "jpg", new File("out.jpg"));

			//volume.modifyTransformation(Transformation.getYRotationMatrix(2 * Math.PI / 64));
		}



		//short[] pixelData = dicomFileSet.getPixelData();


		//DICOMFile dicomFile = dicomFileSet.get(0);


		//Ray ray1 = new Ray(new BasicVector(new double[] { 0, 10, -20 }), new BasicVector(new double[] { 0, 0, -10 }));

		/*

		Viewport pp = new Viewport(
				new BasicVector(new double[] { -5, 5, 0 }),
				new BasicVector(new double[] { -5, -5, 0 }),
				new BasicVector(new double[] { 5, -5, 1 }),
				new BasicVector(new double[] { 5, 5, 0 })
		);

		Vector p = pp.getPoint(50, 50, 101, 101);




/*
		Dimension d = sourceImage.getDimension();

		Raster raster = bufferedImage.getData();
		int bitDepth = bufferedImage.getColorModel().getComponentSize()[0];

		DataBuffer db = raster.getDataBuffer();

		short[] dbus = ((DataBufferUShort) db).getData();

		BufferedImage newBufferedImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);

		int[] pixel = new int[1];

		for (int i = 0; i < 512; i++)
		{
			for (int j = 0; j < 512; j++)
			{
				pixel = raster.getPixel(i, j, pixel);

				Color c = new Color(bufferedImage.getRGB(i, j));

				Color c1 = new Color(pixel[0] / 256, pixel[0] / 256, pixel[0] / 256);
				newBufferedImage.setRGB(i, j, c1.getRGB());
			}
		}

		boolean b = ImageIO.write(newBufferedImage, "bmp", new File("123.bmp"));



	*/
	}
}

