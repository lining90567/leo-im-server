package org.leo.im.api.dto;

/**
 * 文件dto类
 * 
 * @author Leo
 * @date 2018/6/13
 */
public class FileDTO {
    
    private String id;
    
    private String name;
    
    private String extension;
    
    private int size;
    
    private String mimeType;
    
    private int width;
    
    private int height;
    
    private short thumbWidth;
    
    private short thumbHeight;
    
    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public short getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(short thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public short getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(short thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "FileDTO [id=" + id + ", name=" + name + ", extension=" + extension + ", size=" + size + ", mimeType="
                + mimeType + ", width=" + width + ", height=" + height + ", thumbWidth=" + thumbWidth + ", thumbHeight="
                + thumbHeight + ", path=" + path + "]";
    }

}
