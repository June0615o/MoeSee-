package com.moesee.moeseedemo.service.Imp;

import com.moesee.moeseedemo.service.AnalyticsService;
import com.moesee.moeseedemo.utils.DateUtils;
import com.moesee.moeseedemo.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsServiceImp implements AnalyticsService {

    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private DateUtils dateUtils;

    @Override
    public Map<String,Object> analyzeVideoHeat(String videoUrl, String dateStr){
        Integer videoId = videoMapper.getVideoIdByUrl(videoUrl);
        if(videoId<0){
            throw new IllegalArgumentException("视频未分类");
        }
        LocalDate date = dateUtils.stringToLocalDate(dateStr);
        //获取最近一周的播放量和点赞量 并且计算出下周预期播放量和周热度
        int lastWeekViews=predictPastDaysViews(videoId,date,3);
        int lastWeekLikes=predictPastDaysLikes(videoId,date,3);
        int futureViews = lastWeekViews;
        double heat = calculateHeat(lastWeekViews,lastWeekLikes,3);

        Map<String,Object> result=new HashMap<>();
        result.put("videoId:", videoId);
        result.put("futureViews:", futureViews);
        result.put("futureHeat:", heat);
        result.put("analyticsDate:", date);
        return result;
    }
    private double calculateHeat(int views,int likes,int days){
        final double a = 7; //播放量权重
        final double b = 8; //点赞量权重
        final double c = 1; //时间衰减系数
        return a*Math.log(views +1)+b*Math.log(likes+1)-c*days;
    }

    private int predictPastDaysViews(int videoId,LocalDate date,int days){
        int viewsCount=0;
        for(int i=0;i<days;i++){
            viewsCount+=videoMapper.getVideoViewsByVideoIdAndDate(videoId,date.minusDays(i));
        }
        return viewsCount;
    }
    private int predictPastDaysLikes(int videoId,LocalDate date,int days){
        int viewsCount=0;
        for(int i=0;i<days;i++){
            viewsCount+=videoMapper.getVideoLikesByVideoIdAndDate(videoId,date.minusDays(i));
        }
        return viewsCount;
    }
}
