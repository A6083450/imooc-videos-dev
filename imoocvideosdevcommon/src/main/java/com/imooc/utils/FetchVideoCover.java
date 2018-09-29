package com.imooc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author erpljq
 * @date 2018/9/19
 */
public class FetchVideoCover {

    //视频路径
    private String ffmpegEXE;

    public void getCover(String videoInputPath, String coverOutPutPath) throws IOException{
        //ffmpeg.exe -ss 00:00:01 -y -i spring.mp4 -vframes 1 bb.jpg
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        //指定截取第一秒
        command.add("-ss");
        command.add("00:00:01");

        command.add("-y");
        command.add("-i");

        command.add(videoInputPath);

        command.add("-vframes");
        command.add("1");

        command.add(coverOutPutPath);

//        for (String s : command) {
//            System.out.println(s + " ");
//        }
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null){

        }
        if (br != null){
            br.close();
        }
        if (inputStreamReader != null){
            inputStreamReader.close();
        }
        if (errorStream != null){
            errorStream.close();
        }

    }

    public String getFfmpegEXE(){
        return  ffmpegEXE;
    }

    public void setFfmpegEXE(String ffmpegEXE){
        this.ffmpegEXE = ffmpegEXE;
    }

    public FetchVideoCover() {
    }

    public FetchVideoCover(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public static void main(String[] args) {
        //获取视频信息
        FetchVideoCover videoInfo = new FetchVideoCover("E:\\imooc-video-dev\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            videoInfo.getCover("E:\\imooc-video-dev\\ffmpeg\\bin\\160319170918e837a85972328.mp4","E:\\imooc-video-dev\\ffmpeg\\bin\\new.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
