package com.dzics.data.appoint.changsha.mom.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author xnb
 * @date 2022/12/16 0016 14:31
 */
@Slf4j
@Component
public class MySqlTask {


    /**
     * 调用ProcessBuilder执行，相比Runtime方式，返回值不易丢失
     * @param command 命令
     * @return 执行结果
     */
    public static String execute(String command) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String result = null;
        try {
            File file = new File("E:\\daemonTmp");
            //新建一个存储结果的缓存文件
            File tmpFile = new File("E:\\logs\\test.txt");
            if (!file.exists()) {
                file.mkdirs();
            }
            if (!tmpFile.exists()) {
                tmpFile.createNewFile();
            }
            ProcessBuilder processBuilder = new ProcessBuilder().command("cmd.exe", "/c", command).inheritIO();
            // 把控制台中的红字变成了黑字，用通常的方法其实获取不到，控制台的结果是pb.start()方法内部输出的。
            processBuilder.redirectErrorStream(true);
            // 输出执行结果。
            processBuilder.redirectOutput(tmpFile);
            // 等待语句执行完成，否则可能会读不到结果。
            processBuilder.start().waitFor();
            InputStream inputStream = new FileInputStream(tmpFile);
            //设置编码
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            bufferedReader.close();
            bufferedReader = null;

            result = stringBuilder.toString();

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            String sql = "mysqlcheck -r -uroot -proot ";
            List<String> databases= Arrays.asList("ds0_xz","ds1_xz","ds2_xz","ds3_xz","ds4_xz","ds5_xz","ds6_xz","dzics_kanbanrouting_xz");
            for (String database : databases) {
                execute(sql+database);
            }
        }catch(Throwable throwable){
            throwable.printStackTrace();
        }
    }



}
