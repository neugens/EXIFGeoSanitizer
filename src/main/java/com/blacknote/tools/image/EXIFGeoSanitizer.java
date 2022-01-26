/*
 * This file is part of EXIFGeoSanitizer.
 *
 * EXIFGeoSanitizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EXIFGeoSanitizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PurePerspective.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.blacknote.tools.image;

import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class EXIFGeoSanitizer {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please specify an input file");
            return;
        }

        var imageFile = new File(args[0]);

        try {

            var targetName = "geosanitized-" + imageFile.toPath().getFileName();
            var targetFile = new File(imageFile.getParentFile().getCanonicalPath(), targetName);
            System.err.println(targetFile);

            final JpegImageMetadata metadata = (JpegImageMetadata) Imaging.getMetadata(imageFile);
            final TiffImageMetadata exif = metadata.getExif();
            if (exif == null) {
                return;
            }

            var outputSet = exif.getOutputSet();
            if (outputSet == null) {
                outputSet = new TiffOutputSet();
            } else {
                outputSet.setGPSInDegrees(0, 0);
            }

            try (FileOutputStream outStream = new FileOutputStream(targetFile);
                 OutputStream os = new BufferedOutputStream(outStream)) {

                new ExifRewriter().updateExifMetadataLossless(imageFile, os, outputSet);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
