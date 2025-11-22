package com.readingmcqassistant.bo;

import com.readingmcqassistant.dao.JobDao;

public class ReadingBO {

    private final JobDao jobDao = new JobDao();

    public long submitJob(long userId, String passage, String question,
                          String a, String b, String c, String d) {

        String payload = buildPayloadJson(passage, question, a, b, c, d);

        return jobDao.enqueue(userId, payload); 
    }

    private String buildPayloadJson(String passage, String question,
                                    String a, String b, String c, String d) {

        return """
        {"passage": %s, "question": %s, "options": [%s,%s,%s,%s]}
        """.formatted(
                toJson(passage), toJson(question),
                toJson(a), toJson(b), toJson(c), toJson(d)
        );
    }


    // tiny JSON escaper
    private static String toJson(String s) {
        if (s == null) return "null";
        return "\"" + s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                + "\"";
    }
}
