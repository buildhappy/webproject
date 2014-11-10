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
public class UploadImg{


	private String fromdir="";
	public void setFromdir(String fromdir){
	       	this.fromdir=fromdir;
	}

	private String todir="";
	public void setTodir(String todir){
	       	this.todir=todir;
	}


	private String imgfile="";
	public void setImgfile(String imgfile){
	       	this.imgfile=imgfile;
	}

        //sysimgfile is no .gif or no .jpg or no.png's filename
        //it is different from imgfile, imgfile is full name,such as peng.gif
        //but sysimgfile is peng, no .gif
	private String sysimgfile="";
	public void setSysimgfile(String sysimgfile){
	       	this.sysimgfile=sysimgfile;
	}

    private String imgfileResult="";
	public String getImgfileResult(){return imgfileResult;}

    private int newwidth=0;
	public int getNewwidth(){return newwidth;}

    private int newheight=0;
	public int getNewheight(){return newheight;}

    /**
    *   @param imgfile1 dir1  is rename source;
    *   @param target is System's imgname
    *
    */


	public void init(String fromdir,String todir,String imgfile,String sysimgfile)
	{
		this.fromdir=fromdir;
		this.todir=todir;
		this.imgfile=imgfile;
		this.sysimgfile=sysimgfile;

	}

    /**改名*/
	public void renImgname(String ext)
	               throws Exception
	{
		FileDeal fd=new FileDeal();
		File filepic1=new File(fromdir,imgfile);
                if (!filepic1.isFile()) return;
      	        File filepic2=new File(todir,sysimgfile+ext);
		try {
			    fd.copy(filepic1.toString(), filepic2.toString());
		}catch (Exception ex) {
		         throw new Exception(" renImgname: "+ex.getMessage());
        }

	}
	/**
	* deciede if it is a picture
	* @param fromdir
	* @param imgfile
	* @param todir
	* @param sysimgfile
	* @return  system picture name; no dirtectry
	*/

	public String dealUploadImg() throws Exception
	{
            if ((imgfile.equals("")) || (fromdir.equals(""))
		    || (todir.equals(""))|| (sysimgfile.equals("")))
	    {
              return "";
	    }
            if (isGif(imgfile)){
           	renImgname(".gif");
           	imgfileResult=sysimgfile+".gif";
            }else if  (isJpg(imgfile)){
        	renImgname(".jpg");
        	imgfileResult=sysimgfile+".jpg";
            }else if (isPng(imgfile)){
                renImgname(".png");
                imgfileResult=sysimgfile+".png";
            }else {
                File tempfile=new File(fromdir,imgfile);
                if (!tempfile.delete())
      		   throw new Exception(fromdir+"/"+imgfile+" delete error!");
             }
             return imgfileResult;
	}

	public boolean isGif(String file)
	{
		if (file.toLowerCase().endsWith("gif")){
		    return true;
		}else{
			return false;
		}
	}

	public boolean isJpg(String file)
	{
		if (file.toLowerCase().endsWith("jpg")){
		    return true;
		}else{
			return false;
		}
	}

        public boolean isPng(String file)
        {
                if (file.toLowerCase().endsWith("png")){
                    return true;
                }else{
                        return false;
                }
        }



	/**
	  *  create a preview small picture
	  *  it has same function as dealUploadImg()
          *  gif --> png
          *  jpg --> jpg
	  * */

	public String CreateThumbnail(int maxwidth,int maxheight) throws Exception
        {
   	    String ext="";

        double Ratio=getImagSize(maxwidth,maxheight);

        File F = new File(fromdir,imgfile);
        if (!F.isFile())
            return "";
        BufferedImage Bi = ImageIO.read(F);
        AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(Ratio, Ratio), null);
        Image Itemp = op.filter(Bi, null);

        if (isJpg(imgfile)){
        	ext="jpg";
        }else{
            ext="png";
        }
        imgfileResult= sysimgfile+"."+ext;
        File ThF = new File(todir,imgfileResult);

        try {
            ImageIO.write((BufferedImage)Itemp, ext, ThF);
        }catch (Exception ex) {
		    throw new Exception(" ImageIo.write error in CreatThum.: "+ex.getMessage());
        }
        return imgfileResult;
   }


   /**获得在maxwidth maxheight规范下的*/
   public double getImagSize(int maxwidth,int maxheight) throws Exception{

   	     double Ratio=1.0;

   	     File F = new File(fromdir,imgfile);
         if (!F.isFile())
               throw new Exception(F+" is not image file error in CreateThumbnail!");

         BufferedImage Bi = ImageIO.read(F);
         Image Itemp = Bi.getScaledInstance (maxwidth,maxheight,Bi.SCALE_SMOOTH);


        newwidth=Bi.getWidth();
        newheight=Bi.getHeight();

        if (newwidth>maxwidth){
           Ratio = (new Integer(maxwidth)).doubleValue()/newwidth;
        }
        newwidth=(new Double(Ratio*Bi.getWidth())).intValue();
        newheight=(new Double(Ratio*Bi.getHeight())).intValue();

        if (newheight>maxheight){
           Ratio=Ratio*(new Integer(maxheight)).doubleValue()/newheight;
        }
        newwidth=(new Double(Ratio*Bi.getWidth())).intValue();
        newheight=(new Double(Ratio*Bi.getHeight())).intValue();

 //        if  ((ic.getHeight()>maxheight) || (ic.getWidth()>maxwidth)){
//        	  if (ic.getHeight()>ic.getWidth()){
//        	     Ratio =(new Integer(maxheight)).doubleValue()/ic.getHeight();
//        	  }else
//           	     Ratio = (new Integer(maxwidth)).doubleValue()/ic.getWidth();
//        }



        Bi=null;
        Itemp=null;
        return Ratio;
   }



}
