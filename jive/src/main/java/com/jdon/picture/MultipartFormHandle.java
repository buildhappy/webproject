package com.jdon.picture;


import com.jivesoftware.util.*;
import com.jivesoftware.forum.*;

import java.util.*;
import java.io.*;
import javax.servlet.http.*;

/**
  * this is used for html's MultiForam Submit param and picture handle;
  * this is like request.getParameter();
  * basic invoke:
  * 1. html write:
  *  <form name="form1" action="<%=request.getContextPath()%>/multipartformserv" method="post" enctype="multipart/form-data">
  *  <input type="hidden" name="FORWARDNAME" value="xxx.jsp" >  *
  *  </form>
  *
  * 2.the servlet multipartformserv will invoke this class and deal the request data.
  * then pass it back to xxx.jsp(the value of FORWARDNAME).
  *
  * 3. in the xxx.jsp , you need move the uploaded pictures to your directory.
  * by default it is saved in getUploaddir()
  *
  * if you need adjust the picture size:
  *		  <input type="hidden" name="maxwidth" value="120" >
  *		  <input type="hidden" name="maxheight" value="60" ><br>
  *
  *
  */

public class  MultipartFormHandle{

  public static final String FORWARDNAME="forward";
  public static final String MAXWIDTHPARAMNAME="maxwidth";
  public static final String MAXHEIGHTPARAMNAME="maxheight";
  public static final int SIZE=500;

  private Hashtable hashimg=null;
  private Hashtable hashpara=null;

  private String uploaddir="";

        //图片宿放处理后的最大尺寸
  private int maxwidth=0;
  private int maxheight=0;

  private ImgHandle imgHandle=new ImgHandle();
  private Upload upload=new Upload();


  public  String getUploaddir(){
    return  JiveGlobals.getJiveProperty("upload.dir");
  }



  /**  handle the form praram that include uploaded picture
   *  @param uploaddir
   *  @param request
   *  @param maxwidth maxheight
   */

  public void init(String uploaddir,HttpServletRequest request) throws Exception{
    this.uploaddir=uploaddir;
//    this.maxwidth=maxwidth;
//    this.maxheight=maxheight;

    upload.setUploaddir(uploaddir);
    try{
      upload.startUpload(request);
      hashimg=upload.dealAllUploadImg2();
      hashpara=upload.dealAllPara();
    }catch(Exception ex){
      throw new Exception(ex.getMessage());
    }finally{
      upload.clear();
    }
  }


  /**  get next step program name
   *  @param FORWARDNAME
   *
   */
  public String getForwardProgram(){

    String forword="";
    if (hashpara.get(FORWARDNAME)!=null)
      forword=(String)hashpara.get(FORWARDNAME);

    return forword;
  }

  private int getIntValue(String value){
    try{
      return Integer.parseInt(value);
    }catch(Exception ex){
      return 0;
    }

  }

  /**  get the Param from the Form, so that transfer send back to save them!
   *
   */
  public String getForwardProgramParam() throws Exception{

    StringBuffer buff=new StringBuffer();
    for (Enumeration e =  hashpara.keys(); e.hasMoreElements() ;)
    {
      String name=(String)e.nextElement();
      String value=(String)hashpara.get(name);

      if (name.equals(MAXWIDTHPARAMNAME))
        this.maxwidth=getIntValue(value);
      else if (name.equals(MAXHEIGHTPARAMNAME))
        this.maxheight=getIntValue(value);
      else if (!name.equals(FORWARDNAME))
        buff.append(name).append("=").append(value).append("&");

    }


    for (Enumeration e =  hashimg.keys(); e.hasMoreElements() ;)
    {
      String name=(String)e.nextElement();
      String value=(String)hashimg.get(name);
      try{
        value=HandleUploadImg(value,name);
        buff.append(name).append("=").append(value).append("&");
      }catch(Exception ex){
        throw new Exception(ex.getMessage());
      }

    }
    return buff.toString();

  }


  private String HandleUploadImg(String fromfile,String tofilePrex) throws Exception
  {
    ImageFilter imageFilter=new ImageFilter();

    Picture oldpicture=new Picture(uploaddir,fromfile);

    String newfile=tofilePrex+imageFilter.getPostfixname(fromfile);
    Picture newpicture=new Picture(uploaddir,newfile);


    try{
      if ((maxwidth!=0) && (maxheight!=0))
        imgHandle.CreateThumbnail(oldpicture,newpicture,maxwidth,maxheight);
      else
        imgHandle.copyImg(oldpicture,newpicture);
    }catch(Exception ex){
      throw new Exception(ex.getMessage());
    }finally{
      File fromfile2=new File(uploaddir,fromfile);
      if (fromfile2.exists())
        fromfile2.delete();
    }
     return newpicture.getImgfile();
  }


  public void clear()
  {
    hashimg=null;
    hashpara=null;
    maxwidth=0;
    maxheight=0;
  }

  public  void mvToUserUploaddir(String picfilename,String newlocationDir) throws Exception{
    File uploadedfile=new File(getUploaddir(),picfilename);
    if (!uploadedfile.isFile())
      return;
    try{
      String newlocation=newlocationDir+"/"+picfilename;
      FileDeal.move(uploadedfile.toString(),newlocation);
    }catch(Exception ex){
      throw new Exception("can't move upload pic to user dirtectory!"+ex.getMessage());
    }
  }


  private MultipartFormHandle() {
  }


  private static MultipartFormHandle mf = new MultipartFormHandle();
  public static MultipartFormHandle getInstance() {
    return mf;
  }

}
