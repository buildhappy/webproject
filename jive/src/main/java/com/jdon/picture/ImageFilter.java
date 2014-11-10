package com.jdon.picture;

import java.io.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ImageFilter  implements FilenameFilter
{



  public boolean isGif(String file)
  {
    if (file.toLowerCase().endsWith(".gif")){
      return true;
    }else{
      return false;
    }
  }

  public String getPostfixname(String file)
  {
     if (file.toLowerCase().endsWith(".jsp"))
        return ".txt";
     else
        return file.substring(file.indexOf("."));
  }


  public boolean isJpg(String file)
  {
    if (file.toLowerCase().endsWith(".jpg")){
      return true;
    }else{
      return false;
    }
  }

  public boolean isPng(String file)
  {
    if (file.toLowerCase().endsWith(".png")){
      return true;
    }else{
      return false;
    }
  }


  public boolean accept(File dir,String fname){
    return (isGif(fname) || isJpg(fname) || isPng(fname));

  }

}