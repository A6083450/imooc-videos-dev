package com.imooc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author erpljq
 * @date 2018/9/18
 */
public class MergeVideoMp3 {

    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void converter(String videoInputPath, String mp3InputPath,
                          double seconds, String videoOutPutPath) throws IOException {
        //ffmpeg -i input.mp4 -i audio.mp3 -t 7 -y 新的视频.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");
        command.add(String.valueOf(seconds));

        command.add("-y");
        command.add(videoOutPutPath);

        /*for (String s : command) {
            System.out.print(s + " ");
        }*/

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

    public static void main(String[] args) {
        MergeVideoMp3 ffmpeg = new MergeVideoMp3("E:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffmpeg.converter("E:\\ffmpeg\\bin\\160319170918e837a85972328.mp4",
                    "E:\\ffmpeg\\bin\\M11.mp3", 10,"E:\\ffmpeg\\bin\\javaNew.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
