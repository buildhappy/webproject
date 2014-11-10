package com.jivesoftware.util;


import java.io.*;

/**
* tool of file and directory operation
*
* @author Sunny Peng
*/

public class FileDeal {

        /**
    *   This class moves an input file to output file
    *
    *   @param String input file to move from
    *   @param String output file
    *
    */
    public static void move (String input, String output) throws Exception{
        File inputFile = new File(input);
        File outputFile = new File(output);
        try{
             inputFile.renameTo(outputFile);
        }catch (Exception ex) {
                     throw new Exception("Can not mv"+input+" to "+output+ex.getMessage());
       }
    }


    /**
    *  This class copies an input file to output file
    *
    *  @param String input file to copy from
    *  @param String output file
    */
    public static boolean copy(String input, String output) throws Exception{
        int BUFSIZE = 65536;
        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);

        try{
            int s;
            byte[] buf = new byte[BUFSIZE];
            while ((s = fis.read(buf)) > -1 ){
                    fos.write(buf, 0, s);
            }
       }catch (Exception ex) {
                        throw new Exception("makehome"+ex.getMessage());
       }finally{
           fis.close();
           fos.close();
       }
        return true;
    }

    public static void makehome(String home) throws Exception
        {
                File homedir=new File(home);
            if (!homedir.exists())
        {
                        try
                        {
                                homedir.mkdirs();
            }catch (Exception ex) {
                        throw new Exception("Can not mkdir :"+ home+" Maybe include special charactor!");
            }
                }
        }

    /**
    *  This class copies an input files of a directory to another directory not include subdir
    *
    *  @param String sourcedir   the directory to copy from such as:/home/bqlr/images
    *  @param String destdir      the target directory
    */
        public static void CopyDir(String sourcedir,String destdir) throws Exception
    {
        File dest = new File(destdir);
        File source = new File(sourcedir);

        String [] files= source.list();
        try
        {
              makehome(destdir);
        }catch (Exception ex) {
                      throw new Exception("CopyDir:"+ex.getMessage());
        }


        for (int i = 0; i < files.length; i++)
            {
                  String sourcefile = source+File.separator+files[i];
                  String  destfile = dest+File.separator+files[i];
              File temp = new File(sourcefile);
              if (temp.isFile()){
                              try{
                         copy(sourcefile,destfile);
                  }catch (Exception ex) {
                             throw new Exception("CopyDir:"+ex.getMessage());
                  }
                      }
        }
    }


    /**
    *  This class del a directory recursively,that means delete all files and directorys.
    *
    *  @param File directory      the directory that will be deleted.
    */
    public static void recursiveRemoveDir(File directory) throws Exception
    {
            if (!directory.exists())
                throw new IOException(directory.toString()+" do not exist!");

        String[] filelist = directory.list();
        File tmpFile = null;
        for (int i = 0; i < filelist.length; i++) {
            tmpFile = new File(directory.getAbsolutePath(),filelist[i]);
            if (tmpFile.isDirectory()) {
                  recursiveRemoveDir(tmpFile);
            } else if (tmpFile.isFile()) {
                  try{
                    tmpFile.delete();
                  }catch (Exception ex) {
                             throw new Exception(tmpFile.toString()+" can not be deleted "+ex.getMessage());
                  }
            }
        }
        try{
            directory.delete();
        }catch (Exception ex) {
                    throw new Exception(directory.toString()+" can not be deleted "+ex.getMessage());
        }finally{
            filelist=null;
        }
    }

}
