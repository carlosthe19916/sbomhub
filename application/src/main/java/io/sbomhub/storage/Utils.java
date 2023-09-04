
package io.sbomhub.storage;

import org.apache.camel.dataformat.zipfile.ZipFileDataFormat;

public class Utils {

    public static ZipFileDataFormat getZipFileDataFormat() {
        ZipFileDataFormat zipFile = new ZipFileDataFormat();
        zipFile.setUsingIterator(true);
        return zipFile;
    }

}
