package org.fordes.subview.entity;

import java.io.File;

public class FileSet {

    private File file;
    private int type;

    public FileSet(File file, int type) {
        this.file = file;
        this.type = type;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public File getFile() {
        return file;
    }
}
