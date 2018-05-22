package com.ecnu.zz.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * @author : Bruce Zhao
 * @email : zhzh402@163.com
 * @date : 18-5-7 下午1:03
 * @desc :
 */
public class AgentLogUtil {

//    private ArrayList<String> agentDirTree = new ArrayList<>();
    private static LinkedHashSet<String> agentDirTree = new LinkedHashSet();
    public static LinkedHashSet<String> getAgentDirTree(){
        return agentDirTree;
    }

    public void addAgentDirTree(ArrayList<String> logs){
        for(String log : logs){
            /*if(!agentDirTree.contains(log)){
                agentDirTree.add(log);
            }*/
            agentDirTree.add(log);
        }

        System.out.println("AgentLogUtil.addAgentDirTree: " + agentDirTree);
    }

    public static void rebuildAgentDirTree(){
        if(agentDirTree.size() != 0)
            return;
        BufferedReader brLog = null;

        try {
            brLog = new BufferedReader(new FileReader("/opt/rdfs/rdma_dirtree_log_file"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        String line;
        try {
            while ((line = brLog.readLine()) != null) {
                agentDirTree.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                brLog.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("AgentLogUtil.rebuildAgentDirTree rebuild agent's log structure: " + agentDirTree);
    }

    public static void appendDeleteLog(ArrayList<String> deletedList) {
        BufferedWriter bwDeleteLog = null;

        try {
            bwDeleteLog = new BufferedWriter(new FileWriter("/opt/rdfs/rdma_delete_log_file"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for(String str : deletedList){
                bwDeleteLog.write(str+"\n");
            }
            bwDeleteLog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bwDeleteLog.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void rebuildDirTreeLog(LinkedHashSet<String> agentDirTree) {
        BufferedWriter bwDirTree = null;

        try {
            bwDirTree = new BufferedWriter(new FileWriter("/opt/rdfs/rdma_dirtree_log_file"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for(String str : agentDirTree){
                bwDirTree.write(str+"\n");
            }
            bwDirTree.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bwDirTree.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
    Agent收到client发送的消息(所有传输的文件路径信息)之后,记录到本地的log文件中,重复的不记录
     */
    public static void logFileSimple(ArrayList<String> filePaths) {
        File rdmaLogFile = new File("/opt/rdfs/rdma_dirtree_log_file");
        if(!rdmaLogFile.exists()){
            try {
                rdmaLogFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bwLog = null;
        try {
            bwLog = new BufferedWriter(new FileWriter(rdmaLogFile, true));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            LinkedHashSet<String> agentLogs = AgentLogUtil.getAgentDirTree();
            for (String filePath : filePaths) {
                if(!agentLogs.contains(filePath))
                    bwLog.write(filePath + "\n");
            }
            bwLog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bwLog.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //到这里, 存放到server的文件路径信息都写入了Agent自己的rdma_log_file中,但是为了效率的问题,同时写入一个内存结构,方便查找
        AgentLogUtil agentLogUtil = new AgentLogUtil();
        agentLogUtil.addAgentDirTree(filePaths);

    }
}