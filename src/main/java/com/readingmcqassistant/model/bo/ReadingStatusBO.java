package com.readingmcqassistant.model.bo;

import com.readingmcqassistant.model.dao.JobDao;
import com.readingmcqassistant.model.dao.HistoryDAO;

public class ReadingStatusBO {

    private final JobDao jobDao = new JobDao();
    private final HistoryDAO historyDAO = new HistoryDAO();

    public Result checkStatus(long jobId, long userId) {
        JobDao.JobStatus st = jobDao.findStatus(jobId, userId);
        if (st == null) return null;

        Result r = new Result();
        r.status = st.status;
        r.error = st.error;
        r.historyId = st.historyId;

        if ("SUCCEEDED".equals(st.status) && st.historyId != null) {
            r.answerLetter = historyDAO.findAnswerLetter(st.historyId);
        }

        return r;
    }

    public static class Result {
        public String status;
        public String answerLetter;
        public Long historyId;
        public String error;
    }
}
