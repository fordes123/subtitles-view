package org.fordes.subview.util.FileIOUtil;

import java.io.File;

/**
 * 工具类，检测选择的文件是否受支持
 */

public class FileUtil {

    private String suffix;

    /**
     * 获取文件扩展名
     * @param file
     * @return
     */
    public String getFileExtensionName(File file){
        return file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
    }


    /**
     * 校验文件支持
     * @param file
     * @return
     */
    public Boolean Do(File file){
        suffix=getFileExtensionName(file);
        if(suffix.equals("srt")||suffix.equals("mp4")||suffix.equals("ts")||suffix.equals("flv"))
            return true;
        return false;
    }


    /**
     * 返回文件类型
     * @param file
     * @return ，1-字幕；2-视频；0-暂不支持
     */
    public int getFileType(File file){

        switch (getFileExtensionName(file)){
            case "srt":
                return 1;
            case "ass":
                return 2;
            case "lrc":
                return 3;
            case "mp4":
                return 10;
            case "ts":
                return 11;
            case "flv":
                return 12;
            default:
                return 0;
        }
    }



    /**
     * 删除指定文件夹下所有文件
     * @param path，文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {

        File file = new File(path);
        if (!file.exists()) {
            System.out.println("文件夹"+path+" ,不存在");
            return false;
        }
        if (!file.isDirectory()) {
            System.out.println("文件夹"+path+" ,不是文件夹");
            return false;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);
                delFolder(path + "/" + tempList[i]);
            }
        }
        return true;
    }

    /**
     * 删除文件夹
     * @param folderPath ,文件夹完整绝对路径
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
