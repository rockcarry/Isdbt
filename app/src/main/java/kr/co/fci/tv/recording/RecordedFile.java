package kr.co.fci.tv.recording;

import android.graphics.Bitmap;

/**
 * Created by eddy.lee on 2015-09-22.
 */
public class RecordedFile {

    private String date;
    private String fileName;
    private int fileSize;
    private Bitmap thumbNail;
    private int index;
    private String filePath;

    public RecordedFile(String _filePath, String _fileName, String _date,int _fileSize)
    {
        fileName = _fileName;
        date = _date;
        fileSize = _fileSize;
        filePath = _filePath;
    }

    String getFilePath()
    {
        return filePath;
    }

    String getFileName()
    {
        return fileName;
    }

    String getDate()
    {
        return date;
    }

    int getfileSize()
    {
        return fileSize;
    }

    int getindex()   {  return index;}


   void setFileName(String _newName)
    {
        fileName = _newName;
        //to do  boradcast
    }

}
