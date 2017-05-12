package com.maxplus1;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

/**
 * Created by xiaolong.qiu on 2017/4/12.
 */
public class buildStormCmd {

    private final static String PATH_NAME = "target/libs";
    private final static String FINAL_NAME = "StormDemo.jar";

    @Test
    public void test(){
        Collection<File> files = FileUtils.listFiles(new File(PATH_NAME), new String[]{"jar"}, false);
        StringBuilder cmd = new StringBuilder("storm --jars \"");
        files.forEach(file -> {
            cmd.append("libs/").append(file.getName()).append(",");
        });
        cmd.deleteCharAt(cmd.length()-1);
        cmd.append("\" jar ").append(FINAL_NAME)
                .append(" com.maxplus1.DemoApplication ");
        System.out.println("====================================");
        System.out.println(cmd.toString());
        System.out.println("====================================");
    }
}
