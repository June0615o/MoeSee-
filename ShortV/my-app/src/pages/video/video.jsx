import React from "react";
//import { videoList,userList } from "../../static";
import {useState,useEffect} from "react"
import { SyncOutlined ,FireFilled,FireTwoTone,UserOutlined} from "@ant-design/icons";
import { Carousel,Card,Button,FloatButton,} from 'antd';
import { useLocation } from "react-router";
import { getRecommendVideos,getRecommendUser,getFirstRecommendVideos } from "../../mock/api";
import './video.css'

export default function Video() {
   const [videoList,setVideoList] = useState([]);
   const [similarUsers,setSimilarUsers] = useState([]);
   const [displayVideos,setDisplayVideos] = useState([]);
   const [displayUsers,setDisplayUsers] = useState([]);
   const location = useLocation();

   //从url中获得uid
   const uid = new URLSearchParams(location.search).get("uid");
   

   //初始化数据
   useEffect(()=>{
       fetchRecommendations();
       
   },[uid])

   //获取推荐视频
   const fetchRecommendations = async () => {
       try{
        const responseVideo = await getRecommendVideos(uid);
        const responseUser = await getRecommendUser(uid);
        console.log(responseVideo)
        console.log(responseUser)
        
           
            const videoList = responseVideo;
            const similarUsers = responseUser;
            setVideoList(videoList);
            const shuffled = [...similarUsers].sort(() => 0.5 - Math.random());
            setSimilarUsers(shuffled.slice(0, 4));
            console.log("测试"+similarUsers);
            
            refreshDisplayVideos();
            refreshDisplayUsers();
        
       }catch(error){
           console.log(error);
       }
   }
   //视频换一批
   const refreshDisplayVideos= async()=>{
    const responseVideo = await getRecommendVideos(uid);
    const videoList = responseVideo;
    setDisplayVideos(videoList);
    console.log(displayVideos)
   }
   //用户换一批
   const refreshDisplayUsers = async() => {
    const responseUser = await getRecommendUser(uid);
    const similarUsers = responseUser;
    const shuffled = [...similarUsers].sort(() => 0.5 - Math.random());
    setDisplayUsers(shuffled.slice(0, 4)); // 或者你需要的数量
    console.log(displayUsers)
    }
    
    return(
        <div className="content">
            <button onClick={()=>{refreshDisplayUsers()}} className="changeButton"
            >
                <SyncOutlined className="icon" />
                换一换
            </button>
            <div className="userList">
                {displayUsers.map((user,index)=>{
                    return(
                        <div className="item" key={index}>
                            <div> <UserOutlined style={{paddingRight: '5px'}} /> UserUid:{user.userUid} </div>
                            <div>感兴趣：{user.userPreferredTags}</div>
                        </div>
                    )
                })}
                
            </div>
            <div className="title">看看这些视频是否符合你的胃口吧！</div>
            <div className="video">
                <div className="card">
                    <div className="left">
                        <Carousel autoplay autoplaySpeed={3000} arrows dotPosition="top" dots={true}>
                            {videoList.slice(0, 8).map((video, index) => (
                                <div className="carousel-item" key={index}>
                                    <a href={video.videoUrl} target="_blank" >
                                        <img src={video.videoCoverImage} alt={`视频${index + 1}`} />
                                        <div className="carousel-title">
                                            <h3>{video.videoTitle}</h3>
                                        </div>
                                    </a>
                                </div>
                            ))}
                        </Carousel>
                    </div>
                    <div className="right">
                        {displayVideos.map((video, index) => (
                            <div className="videoList" key={index}>
                                <div className="thumbnail">
                                    <a href={video.videoUrl} target="_blank" rel="noopener noreferrer">
                                        <img className="pic" src={video.videoCoverImage} alt="视频封面" />
                                    </a>
                                </div>
                                <div className="txt">
                                    <a href={video.videoUrl} target="_blank" rel="noopener noreferrer">
                                       {video.videoTitle}
                                    </a>
                                    <div className="fire-container">
                                        <FireTwoTone className="fire" twoToneColor="#eb2f96" />
                                        <div className="tooltip">
                                            <div>未来7天播放量:{video.futureViews}</div>
                                            <div>未来7天热度: {video.futureHeat}</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                    <button onClick={()=>{refreshDisplayVideos()}} className="changeButton"
                        >
                        <SyncOutlined className="icon"/>
                       换一换
                    </button>
                </div>
            </div>
            
        </div>
    )
}