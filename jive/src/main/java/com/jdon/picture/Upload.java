package com.jdon.picture;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.*;

/**
 * tool of upload file, by cos bean(com.oreilly.servlet), change upload bean in here
 * @author Sunny Peng
 *
 */

public class Upload{

  MultipartRequest mrequest=null;

  private String uploaddir="";
  public void setUploaddir(String uploaddir) { this.uploaddir = uploaddir; }

  private int size=500;
  public void setSize(int size) { this.size = size; }

    /**
     *
     *  @param String uploaddir
     *  @param int size
     *  @param HttpServletRequest request
     * */
  public void startUpload(HttpServletRequest request) throws Exception
  {
    if (uploaddir==null)
      throw new Exception("No Upload directory!");
    try{
      mrequest=new MultipartRequest(request,uploaddir,size * 1024);
    } catch (Exception ex) {
      throw new Exception("MultipartRequest()"+ex.getMessage());
    }

  }

  public Vector dealAllUploadImg() throws Exception
  {
    Vector vector=new Vector();

    Enumeration e=mrequest.getFileNames();
    while (e.hasMoreElements()) {
      String imgfile=mrequest.getFilesystemName((String)e.nextElement());
      if (imgfile!=null){
        vector.addElement(imgfile);
      }
    }
    return vector;
  }

  public Hashtable dealAllUploadImg2() throws Exception
  {
    Hashtable hashtable=new Hashtable();

    Enumeration e=mrequest.getFileNames();
    while (e.hasMoreElements()) {
      String imgname=(String)e.nextElement();
      String imgfile=mrequest.getFilesystemName(imgname);
      if (imgfile!=null){
        hashtable.put(imgname,imgfile);
        //     System.out.println(imgname+"="+imgfile);
      }
    }
    return hashtable;
  }

  public Hashtable dealAllPara() throws Exception
  {
    Hashtable hashtable=new Hashtable();

    Enumeration e=mrequest.getParameterNames();
    while (e.hasMoreElements()) {
      String paraname=(String)e.nextElement();
      String para=mrequest.getParameter(paraname);
      if ((paraname!=null) && (para!=null)){
        hashtable.put(paraname,para);
      }
      //System.out.println(paraname+"="+para);
    }
    return hashtable;
  }

    //删除所有上传文件*/
  public void deleteAllUploadImg() throws Exception
  {
    Enumeration e1=mrequest.getFileNames();
    while (e1.hasMoreElements()) {
      String imgfile=mrequest.getFilesystemName((String)e1.nextElement());
      if (imgfile!=null){
        File imgfile1=new File(uploaddir,imgfile);
        if (imgfile1.exists())
          imgfile1.delete();
      }
    }
  }

  public void clear()
  {
    uploaddir = "";
    mrequest=null;

  }
}

