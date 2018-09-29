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
public class FFMpegTest {

    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void converter(String videoInputPath, String videoOutPutPath) throws IOException {
        //ffmpeg -i input.mp4 output.avi
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add(videoOutPutPath);

        for (String s : command) {
            System.out.print(s + " ");
        }

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
        FFMpegTest ffmpeg = new FFMpegTest("E:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffmpeg.converter("E:\\ffmpeg\\bin\\160319170918e837a85972328.mp4","E:\\ffmpeg\\bin\\mp4ToAvi.avi");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
