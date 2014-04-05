package ua.in.hammer.MedicalImageReconstruction.DICOM;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.sun.deploy.util.ArrayUtil;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.util.*;

/**
 * Created by Hammer on 22.03.2014.
 */
public class DICOMFileSet
{
	private ArrayList<DICOMFile> dicomFiles;

	public DICOMFileSet(File path) throws IOException, DicomException
	{
		File[] files = path.listFiles();

		dicomFiles = new ArrayList<>();

		for (File file : files)
		{
			if (file.isFile() && FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("dcm"))
			{
				dicomFiles.add(new DICOMFile(file));
			}
		}

		Collections.sort(dicomFiles);
	}

	public short[] getPixelData() throws DicomException
	{
		int columns = getColumns();
		int rows = getRows();
		int slices = getSlices();

		short[] pixelData = new short[columns * rows * slices];

		for (int i = 0; i < dicomFiles.size(); i++)
		{
			System.arraycopy(dicomFiles.get(i).getPixelData(), 0, pixelData, i * columns * rows, columns * rows);
		}

		return pixelData;
	}

	public int getColumns() throws DicomException
	{
		return dicomFiles.get(0).getColumns();
	}

	public int getRows() throws DicomException
	{
		return dicomFiles.get(0).getRows();
	}

	public int getSlices()
	{
		return dicomFiles.size();
	}

	public double getSpacingBetweenColumns() throws DicomException
	{
		return dicomFiles.get(0).getSpacingBetweenColumns();
	}

	public double getSpacingBetweenRows() throws DicomException
	{
		return dicomFiles.get(0).getSpacingBetweenRows();
	}

	public double getSpacingBetweenSlices() throws DicomException
	{
		return Math.abs(dicomFiles.get(1).getImagePositionPatient()[2] - dicomFiles.get(0).getImagePositionPatient()[2]);
	}
}
