package com.moesee.moeseedemo.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

public class RollbackGenerator {
    public static void main(String[] args) {
        String inputFile = "C:\\Users\\Ayaka\\Desktop\\detailed_output.sql"; // 日志文件路径
        String outputFile = "C:\\Users\\Ayaka\\Desktop\\reverse_output.sql"; // 输出恢复文件路径

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            String line;
            Map<Integer, String> originalValues = new HashMap<>();
            boolean isWhereSection = false;
            Pattern fieldPattern = Pattern.compile(".*@(\\d+)=(.+)"); // 关键修复点

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("### UPDATE")) {
                    // 提交前一个记录
                    if (!originalValues.isEmpty()) {
                        writeRollback(writer, originalValues);
                        originalValues.clear();
                    }
                    isWhereSection = false;
                } else if (line.startsWith("### WHERE")) {
                    isWhereSection = true;
                } else if (line.startsWith("### SET")) {
                    isWhereSection = false;
                } else if (isWhereSection) {
                    // 关键修复：允许字段行包含其他前缀
                    Matcher matcher = fieldPattern.matcher(line);
                    if (matcher.find()) {
                        int field = Integer.parseInt(matcher.group(1));
                        String rawValue = matcher.group(2).trim();

                        // 处理字符串值
                        if (field == 3 || field == 4) {
                            if (rawValue.startsWith("'") && rawValue.endsWith("'")) {
                                rawValue = rawValue.substring(1, rawValue.length() - 1);
                            }
                            rawValue = rawValue.replace("'", "''");
                        }

                        originalValues.put(field, rawValue);
                    }
                }
            }

            // 处理最后一个记录
            if (!originalValues.isEmpty()) {
                writeRollback(writer, originalValues);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void writeRollback(BufferedWriter writer, Map<Integer, String> values) throws IOException {
        String sql = String.format(
                "UPDATE `db02`.`users`\n" +
                        "SET user_uid = %s,\n" +
                        "    user_preferred_tags = '%s',\n" +
                        "    user_cluster_id = '%s'\n" +
                        "WHERE user_id = %s;\n",
                values.get(2), values.get(3), values.get(4), values.get(1)
        );
        writer.write(sql);
        writer.newLine();
    }
}