package com.dzm.apkqudao;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.tools.ant.filters.StringInputStream;

public class Main {

    public static void main(String[] args) {
        // write your code here

        StringBuilder buffer = new StringBuilder();
        BufferedReader br = null;
        OutputStreamWriter osw = null;
        try {

            File file_ = new File("C:\\Users\\user\\Downloads\\ApkQuDao-master\\ApkQuDao-master\\JiaMi.txt");
            if(!file_.exists()){
                System.out.println("缺少JiaMi.txt 配置文件");
                return;
            }
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file_),"UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String readLine = "";
            buffer.setLength(0);
            while((readLine = reader.readLine()) != null){
                buffer.append(readLine);
            }
            inputStreamReader.close();
            reader.close();
            String readTxt = buffer.toString();
            System.out.println(readTxt);
            String[] readTxts = readTxt.split(";");
            String srcPath = "";
            String keystore = "";
            String keyAlias = "";
            String content1 = "";
            //String apkName = "";
            String keyPassword = "";
            String storePassword = "";
            for (String txt:readTxts){
                if(txt.contains("app_path")){
                    srcPath = txt.split("=")[1];
                }else if(txt.contains("keystore")){
                    keystore = txt.split("=")[1];
                }else if(txt.contains("keyAlias")){
                    keyAlias = txt.split("=")[1];
                }else if(txt.contains("contents")){
                    content1 = txt.split("=")[1];
                }else if(txt.contains("keyPassword")){
                    keyPassword = txt.split("=")[1];
                }else if(txt.contains("storePassword")){
                    storePassword = txt.split("=")[1];
                }
            }

            File srcFile = new File(srcPath);
            if (!srcFile.exists()) {
                System.out.println("文件不存在");
                return;
            }
//            String parentPath = srcFile.getParent();    //源文件目录
            String fileName = srcFile.getName();        //源文件名称
            String prefixName = fileName.substring(0, fileName.lastIndexOf("."));
            buffer.setLength(0);
            String sourcePath = buffer.
                    append(prefixName).append("\\").toString();
            System.out.println("sourcePath:"+sourcePath);
            ZipUtil.unZip(srcPath, sourcePath);
            //------删除解压后的签名文件
            String signPathName = sourcePath + ZipUtil.SIGN_PATH_NAME;
            File signFile = new File(signPathName);
            if (signFile.exists()) {
                File sonFiles[] = signFile.listFiles();
                if (sonFiles != null && sonFiles.length > 0) {
                    //循环删除签名目录下的文件
                    for (File f : sonFiles) {
                        f.delete();
                    }
                }
                signFile.delete();
            }
            String[] contents = content1.split(",");

            //------打包
            String targetPath = "target";
            //判断创建文件夹
            File targetFile = new File(targetPath);
            if(!targetFile.exists()){
                targetFile.mkdir();
            }
            String sing = "sing";
            File filesing = new File(targetFile,sing);
            if(!filesing.exists()){
                filesing.mkdir();
            }
            String unsing = "unsing";
            File fileunsing = new File(targetFile,unsing);
            if(!fileunsing.exists()){
                fileunsing.mkdir();
            }
            String batStr = "";
            for (int i = 0;i<contents.length;i++) {
                //------修改内容
                String content = contents[i];
                buffer.setLength(0);
                String path = buffer
                        .append(prefixName).append(ZipUtil.UPDATE_PATH_NAME).toString();
                System.out.println("path："+path);
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
                while ((br.readLine()) != null) {
                    osw = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
                    //osw.write(content, 0, content.length());
                    String md5= getApkMd5(sourcePath+"\\classes.dex");
                    osw.write(md5, 0, md5.length());
                    osw.flush();
                }

                ZipUtil.compress(prefixName,targetPath+"\\"+unsing+"\\"+"unsin.apk");

                batStr+="jarsigner -verbose -keystore "+keystore+" -storepass "+storePassword+" -keypass "+keyPassword+" -signedjar "+targetPath+"\\"+sing+"\\"+"sin.apk "+targetPath+"\\"+unsing+"\\"+"unsin.apk"+" "+keyAlias+"\n";

                System.out.println("jarsigner -verbose -keystore "+keystore+" -storepass "+storePassword+" -keypass "+keyPassword+" -signedjar "+targetPath+"\\"+sing+"\\"+"sin.apk "+targetPath+"\\"+unsing+"\\"+"unsin.apk"+" "+keyAlias);
            }
            File file = new File("JiaMi.bat");
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos,1024);
            InputStream inputStream = new StringInputStream(batStr);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(bytes))!=-1){
                bos.write(bytes,0,len);
            }
            bos.close();
            fos.close();
            inputStream.close();
            runbat(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runbat(String batName) {
        String cmd = "cmd /c start "+ batName;// pass
        try {
            Process ps = Runtime.getRuntime().exec(cmd);
            ps.waitFor();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("child thread donn");
    }
    
    public static String getApkMd5(String apkPath) {
        MessageDigest msgDigest;
        try {
          //获取apk并计算MD5值
          msgDigest = MessageDigest.getInstance("MD5");
          byte[] bytes = new byte[4096];
          int count;
          FileInputStream fis;
          fis = new FileInputStream(new File(apkPath));

          while ((count = fis.read(bytes)) > 0) {
            msgDigest.update(bytes, 0, count);
          }
          //计算出MD5值
          BigInteger bInt = new BigInteger(1, msgDigest.digest());
          String md5 = bInt.toString(16);
          fis.close();
          return md5;
        } catch (NoSuchAlgorithmException e) {
          e.printStackTrace();
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
        return null;
      }

    
}
