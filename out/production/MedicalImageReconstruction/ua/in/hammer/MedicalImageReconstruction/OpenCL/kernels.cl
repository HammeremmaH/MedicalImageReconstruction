#pragma OPENCL EXTENSION cl_khr_fp64 : enable

inline double4 multiplyVectorByMatrix(double4 vector, __global double4 *matrix)
{
    double4 result;

    result.s0 = vector.s0 * matrix[0].s0 + vector.s1 * matrix[1].s0 + vector.s2 * matrix[2].s0 + vector.s3 * matrix[3].s0;
    result.s1 = vector.s0 * matrix[0].s1 + vector.s1 * matrix[1].s1 + vector.s2 * matrix[2].s1 + vector.s3 * matrix[3].s1;
    result.s2 = vector.s0 * matrix[0].s2 + vector.s1 * matrix[1].s2 + vector.s2 * matrix[2].s2 + vector.s3 * matrix[3].s2;
    result.s3 = vector.s0 * matrix[0].s3 + vector.s1 * matrix[1].s3 + vector.s2 * matrix[2].s3 + vector.s3 * matrix[3].s3;

    return result;
}

__kernel void mip(double4 viewPoint,
    __global double4 *viewportCornerPoints,
    __global const short *volume, const int4 volumeSize, const double4 volumeSpacing, __global double4 *volumeInverseTransformation, short minValue, short maxValue,
    int width, int height,
    __global double *pixels
    )
{
    int y = get_global_id(0);
    int x = get_global_id(1);

    //view port

    double4 viewportLeftSidePoint = viewportCornerPoints[0] + (viewportCornerPoints[1] - viewportCornerPoints[0]) * ((double) y / (height - 1));
    double4 viewportRightSidePoint = viewportCornerPoints[3] + (viewportCornerPoints[2] - viewportCornerPoints[3]) * ((double) y / (height - 1));
    double4 viewportPoint = viewportLeftSidePoint + (viewportRightSidePoint - viewportLeftSidePoint) * ((double) x / (width - 1));

    //ray

    double4 rayInitialPoint = viewportPoint;
    double4 rayDirectionNormalVector = normalize(viewportPoint - viewPoint);

    //intersection distances

    double4 localRayInitialPoint = multiplyVectorByMatrix(rayInitialPoint, volumeInverseTransformation);
    double4 localRayDirectionNormalVector = multiplyVectorByMatrix(rayDirectionNormalVector, volumeInverseTransformation);

    double4 planeNormals[] =
    {
        (double4) ( -1, 0, 0, 0 ),
        (double4) ( 0, -1, 0, 0 ),
        (double4) ( 0, 0, -1, 0 ),
        (double4) ( 1, 0, 0, 0 ),
        (double4) ( 0, 1, 0, 0 ),
        (double4) ( 0, 0, 1, 0 )
    };

    double4 planePoints[] =
    {
        (double4) ( 0, 0, 0, 1 ),
        volumeSpacing * convert_double4(volumeSize)
    };

    double distances[6];
    int numberOfDistances = 0;

    for (int i = 0; i < 6; i++)
    {
        double distance = - (dot(planeNormals[i], localRayInitialPoint) - dot(planeNormals[i], planePoints[i / 3])) / dot(planeNormals[i], localRayDirectionNormalVector);

        double4 localRayPoint = localRayInitialPoint + localRayDirectionNormalVector * distance;

        double4 roundedVolumeIndex = round(localRayPoint / volumeSpacing);

        if (!(roundedVolumeIndex.s0 < 0 || roundedVolumeIndex.s0 > volumeSize.s0
            || roundedVolumeIndex.s1 < 0 || roundedVolumeIndex.s1 > volumeSize.s1
            || roundedVolumeIndex.s2 < 0 || roundedVolumeIndex.s2 > volumeSize.s2))
        {
            distances[numberOfDistances++] = distance;
        }
    }

    if (numberOfDistances < 2)
    {
        pixels[y * width + x] = 0;
    }
    else
    {
        double minDistance = distances[0];
        double maxDistance = distances[0];

        for (int i = 0; i < numberOfDistances; i++)
        {
            if (distances[i] < minDistance)
            {
                minDistance = distances[i];
            }

            if (distances[i] > maxDistance)
            {
                maxDistance = distances[i];
            }
        }

        double distanceDelta;

        if (volumeSpacing.s0 < volumeSpacing.s1 && volumeSpacing.s0 < volumeSpacing.s2)
        {
            distanceDelta = volumeSpacing.s1;
        }
        else
        {
            if (volumeSpacing.s1 < volumeSpacing.s2)
            {
                distanceDelta = volumeSpacing.s1;
            }
            else
            {
                distanceDelta = volumeSpacing.s2;
            }
        }

        //ray trace

        double pixel = 0;

        for (double distance = minDistance; distance <= maxDistance; distance += distanceDelta)
        {
            double4 localRayPoint = localRayInitialPoint + localRayDirectionNormalVector * distance;

            int4 volumeIndex = convert_int4(floor(localRayPoint / volumeSpacing));

            double voxel = 0;

            if (!(volumeIndex.s0 < 0 || volumeIndex.s0 >= volumeSize.s0
                || volumeIndex.s1 < 0 || volumeIndex.s1 >= volumeSize.s1
                || volumeIndex.s2 < 0 || volumeIndex.s2 >= volumeSize.s2))
            {
                voxel = (double) (volume[volumeSize.s0 * volumeSize.s1 * volumeIndex.s2 + volumeSize.s0 * volumeIndex.s1 + volumeIndex.s0] - minValue) / (maxValue - minValue);
            }

            if (voxel > pixel)
            {
                pixel = voxel;
            }
        }

        pixels[y * width + x] = pixel;
    }
}


