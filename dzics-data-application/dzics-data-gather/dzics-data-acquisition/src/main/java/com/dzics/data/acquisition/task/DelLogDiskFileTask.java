package com.dzics.data.acquisition.task;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 清除日志操作
 *
 * @author ZhangChengJun
 * Date 2021/11/24.
 * @since
 */
@Service
@Slf4j
public class DelLogDiskFileTask implements SimpleJob {


    @Value("${del.diskfile.log.day}")
    private static Integer delDiskFilesLog;

    @Value("${dzics.mysql.data.backUps.path}")
    private String pscUrl;

    @Value("${dzics.java.logs.path}")
    private String logsUrl;

    /**
     * 清理磁盘日志文件
     */
    public void delDiskFile(String url) {
        List<String> files = new ArrayList<>();
        List<String>dirs = new ArrayList<>();
        File file = new File(url);
        if(file.exists()==true){
            File [] array = file.listFiles();
            if(array!=null){
                for (int i = 0; i < array.length; i++) {
                    if (array[i].isFile()){
                        files.add(array[i].getPath());
                    }
                    if (array[i].isDirectory()){
                        dirs.add(array[i].getPath());
                    }
                }
            }
            if (!files.isEmpty()){
                delFile(files,delDiskFilesLog);
            }
            if (!dirs.isEmpty()){
                analysisFile(dirs);
            }
        }
    }

    //文件判断是否需要删除
    private static void delFile(List<String>list,Integer days){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //定义30天前的时间戳
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,- days);
        //指定历史删除时间  （小于删，大于留）
        long queryTime = calendar.getTime().getTime();
        for(int i=0;i<list.size();i++){
            File file = new File(list.get(i));
            long upTime = new Date(file.lastModified()).getTime();
            if(upTime<queryTime){
                file.delete();
                log.debug("删除日志文件{},最后修改时间为{}",file.getName(),dateFormat.format(new Date(upTime)));
            }
        }
    }

    //文件夹对象继续解刨
    private static void analysisFile(List<String>dirList){
        List<String>dirsList = dirList;
        List<String>newDirList = new ArrayList<>();
        List<String>filesList = new ArrayList<>();
        for (int i=0;i<dirsList.size();i++)
        {
            File file = new File(dirsList.get(i));
            File [] files = file.listFiles();
            if (files!=null){
                for (int j=0;j<files.length;j++){
                    if (files[j].isFile()){
                        filesList.add(files[j].getPath());
                    }
                    if (files[j].isDirectory()){
                        newDirList.add(files[j].getPath());
                    }
                }
            }
        }
        if(!filesList.isEmpty()){
            delFile(filesList,delDiskFilesLog);
        }
        if (!newDirList.isEmpty()){
            analysisFile(newDirList);
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.warn("开始清除日志文件..........");
        delDiskFile(logsUrl);
        log.warn("清除日志文件结束..........");

        log.warn("开始清除磁盘psc文件..........");
        delDiskFile(pscUrl);
        log.warn("清除磁盘psc文件结束..........");
    }
}
