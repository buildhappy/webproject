package com.jdon.picture;

import com.jivesoftware.util.*;

import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.image.AffineTransformOp;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

/**
 * tool of handle image,include rename; change big picture to small picture
 * @author Sunny Peng
 */
public class ImgHandle{


  private Picture oldpicture;

  private Picture newpicture;


  private ImageFilter imageFilter=new ImageFilter();
  private FileDeal fd=new FileDeal();



  public void ImgHandle(){}


  public void copyImg(Picture oldpicture,Picture newpicture) throws Exception
  {
    String newimgname="";
    try{
      File originfile=new File(oldpicture.getImgDir(),oldpicture.getImgfile());
      if (!originfile.isFile()) {
        throw new Exception(" there is no this image file:"+originfile.toString());
      }

      File targetfile=new File(newpicture.getImgDir(),newpicture.getImgfile());

      fd.copy(originfile.toString(), targetfile.toString());

    }catch (Exception ex) {
      throw new Exception(" dealUploadImg error: "+ex.getMessage());
    }

  }




  /**
   *  create a preview small picture
   *  it has same function as dealUploadImg()
   *  gif --> png
   *  jpg --> jpg
   * */
  public void CreateThumbnail(Picture oldpicture,Picture newpicture,int maxwidth,int maxheight) throws Exception
  {
    String ext="";
    if (imageFilter.isGif(oldpicture.getImgfile())){
      newpicture.setImgfile(newpicture.getImgPrefix()+".png");
      ext="png";
    }else if (imageFilter.isJpg(oldpicture.getImgfile())){
      ext="jpg";
    }else if (imageFilter.isPng(oldpicture.getImgfile())){
      ext="png";
    }

    try {
      File F = new File(oldpicture.getImgDir(),oldpicture.getImgfile());
      if (!F.isFile())
        return;

      getImagSize(oldpicture);
      double Ratio=getRadio(oldpicture,maxwidth,maxheight);

      BufferedImage Bi = ImageIO.read(F);
      AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(Ratio, Ratio), null);
      Image Itemp = op.filter(Bi, null);

      File ThF = new File(newpicture.getImgDir(),newpicture.getImgfile());
      ImageIO.write((BufferedImage)Itemp, ext, ThF);

    }catch (Exception ex) {
      throw new Exception(" ImageIo.write error in CreatThum.: "+ex.getMessage());
    }

  }

  public double getRadio(Picture picture,int maxwidth,int maxheight){
    double Ratio=1.0;

    if (picture.getWidth()>picture.getHeight()){
      Ratio = (new Integer(maxwidth)).doubleValue()/picture.getWidth();
    }else if (picture.getWidth()<picture.getHeight()){
      Ratio=(new Integer(maxheight)).doubleValue()/picture.getHeight();
    }else if (picture.getWidth()==picture.getHeight()){
       if (picture.getWidth()>maxwidth){
           Ratio = (new Integer(maxwidth)).doubleValue()/picture.getWidth();
       }

    }
    return Ratio;
  }


  public void getImagSize(Picture picture) throws Exception{

    double Ratio=1.0;

    try{
      File F = new File(picture.getImgDir(),picture.getImgfile());
      if (!F.isFile())
        throw new Exception(F+" is not image file error !");

      BufferedImage Bi = ImageIO.read(F);
//      Image Itemp = Bi.getScaledInstance (maxwidth,maxheight,Bi.SCALE_SMOOTH);

      picture.setWidth(Bi.getWidth());
      picture.setHeight(Bi.getHeight());
    }catch (Exception ex) {
      throw new Exception(" getImagSize: "+ex.getMessage());
    }

  }

  public List picList(String picDir,int maxwidth,int maxheight)  throws Exception{
     List list=new ArrayList();
     try{
        File directory=new File(picDir);
        String[] images = directory.list(new ImageFilter());
        String fromdir=picDir;
        for (int i = 0; i < images.length; i++){
          String imgfile=images[i];
          Picture picture=new Picture(fromdir,imgfile);
          getImagSize(picture);
          list.add(picture);
        }
        return list;
     }catch (Exception ex) {
        throw new Exception("picList error:"+ex.getMessage());
     }
  }


}
