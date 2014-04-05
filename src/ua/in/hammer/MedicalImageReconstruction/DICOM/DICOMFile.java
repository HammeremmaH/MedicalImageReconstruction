package ua.in.hammer.MedicalImageReconstruction.DICOM;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;

import java.io.File;
import java.io.IOException;

/**
 * Created by Hammer on 24.03.2014.
 */
public class DICOMFile implements Comparable<DICOMFile>
{
	private AttributeList attributeList;

	private short[] pixelData;
	private Integer columns;
	private Integer rows;
	private Double spacingBetweenColumns;
	private Double spacingBetweenRows;
	private double[] imagePositionPatient;
	private Integer sliceNumber;

	public DICOMFile(File file) throws IOException, DicomException
	{
		attributeList = new AttributeList();
		attributeList.read(file);
	}

	public short[] getPixelData() throws DicomException
	{
		if (pixelData == null)
		{
			pixelData = attributeList.get(TagFromName.PixelData).getShortValues();
		}

		return pixelData;
	}

	public int getColumns() throws DicomException
	{
		if (columns == null)
		{
			columns = attributeList.get(TagFromName.Columns).getIntegerValues()[0];
		}

		return columns;
	}

	public int getRows() throws DicomException
	{
		if (rows == null)
		{
			rows = attributeList.get(TagFromName.Rows).getIntegerValues()[0];
		}

		return rows;
	}

	public double getSpacingBetweenColumns() throws DicomException
	{
		if (spacingBetweenColumns == null)
		{
			spacingBetweenColumns = attributeList.get(TagFromName.PixelSpacing).getDoubleValues()[1];
		}

		return spacingBetweenColumns;
	}

	public double getSpacingBetweenRows() throws DicomException
	{
		if (spacingBetweenRows == null)
		{
			spacingBetweenRows = attributeList.get(TagFromName.PixelSpacing).getDoubleValues()[0];
		}

		return spacingBetweenRows;
	}

	public double[] getImagePositionPatient() throws DicomException
	{
		if (imagePositionPatient == null)
		{
			imagePositionPatient = attributeList.get(TagFromName.ImagePositionPatient).getDoubleValues();
		}

		return imagePositionPatient;
	}

	public int getSliceNumber() throws DicomException
	{
		if (sliceNumber == null)
		{
			sliceNumber = attributeList.get(TagFromName.InstanceNumber).getIntegerValues()[0];
		}

		return sliceNumber;
	}

	private int getSliceNumberAnyway()
	{
		try
		{
			return getSliceNumber();
		}
		catch (DicomException e)
		{
			return -1;
		}
	}

	@Override
	public int compareTo(DICOMFile o)
	{
		return Integer.compare(this.getSliceNumberAnyway(), o.getSliceNumberAnyway());
	}
}
