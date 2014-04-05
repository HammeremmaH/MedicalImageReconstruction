package ua.in.hammer.MedicalImageReconstruction.ThreeDimensionalModel;

import org.la4j.vector.Vector;

/**
 * Created by Hammer on 29.03.2014.
 */
public class Scene
{
	private Vector viewPoint;
	private Viewport viewport;
	private Volume volume;

	public Scene(Vector viewPoint, Viewport viewport, Volume volume)
	{
		this.viewPoint = viewPoint;
		this.viewport = viewport;
		this.volume = volume;
	}

	public Vector getViewPoint()
	{
		return viewPoint;
	}

	public Viewport getViewport()
	{
		return viewport;
	}

	public Volume getVolume()
	{
		return volume;
	}
}
