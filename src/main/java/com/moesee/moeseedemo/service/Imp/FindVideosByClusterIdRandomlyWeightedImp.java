package com.moesee.moeseedemo.service.Imp;

import com.moesee.moeseedemo.mapper.VideoMapper;
import com.moesee.moeseedemo.pojo.Video;
import com.moesee.moeseedemo.service.FindVideosByClusterIdRandomlyWeighted;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class FindVideosByClusterIdRandomlyWeightedImp implements FindVideosByClusterIdRandomlyWeighted {

    @Autowired
    private VideoMapper videoMapper;

    private Random random =new Random();
    @Override
    public Video findVideosByClusterIdRandomlyWeighted(int clusterId) {
        List<Video> videos=videoMapper.findVideosByClusterId(clusterId,null,100);
        if (videos.isEmpty()){
            return null;
        }
        double totalWeight = videos.stream()
                                   .mapToDouble(video -> Math.log(video.getVideoViews()+1))
                                   .sum();
        /*
        使用对数函数来计算权重，适当平滑差距 +1是为了防止对数为0时函数抛出错误
        现在获得了视频的总权重作为一个“轴”，随机出投掷点投掷
        */

        double randomPoint = random.nextDouble() * totalWeight;

        double cumulativeWeight = 0.0;
        for(Video video : videos){
            cumulativeWeight += Math.log(video.getVideoViews()+1);
            if(randomPoint <= cumulativeWeight){
                return video;
            }
        }
        /*
        该算法说明:
        (例如ABC播放量10/100/1000. 则取对数后的权重为2.398/4.615/6.908)
        生成一个随机点，例如 randomPoint = 7.0。
        加权累积：
        A 的权重累积：2.398
        B 的权重累积：2.398 + 4.615 = 7.013
        C 的权重累积：7.013 + 6.908 = 13.921

        randomPoint = 7.0 落在 B 的区间内，因此选中 B。
        */
        return null; //如果抛出异常，说明算法有问题
    }

}
