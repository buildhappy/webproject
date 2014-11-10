package com.jdon.picture;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Picture {

  private String imgDir="";
  public String getImgDir(){
    return imgDir;
  }

  private String imgfile="";
  public void setImgfile(String imgfile){
    this.imgfile=imgfile;
  }
  public String getImgfile(){
    return imgfile;
  }

  public String getImgPrefix(){
      ImageFilter imageFilter=new ImageFilter();
      int length=imgfile.length()-imageFilter.getPostfixname(imgfile).length();
    return imgfile.substring(0,length);
  }



  private int width=0;
  public void setWidth(int width){
    this.width=width;
  }
  public int getWidth(){
    return width;
  }

  private int height=0;
  public void setHeight(int height){
    this.height=height;
  }
  public int getHeight(){
    return height;
  }



  public Picture(String imgDir,String imgfile){
    this.imgDir=imgDir;
    this.imgfile=imgfile;


  }
}