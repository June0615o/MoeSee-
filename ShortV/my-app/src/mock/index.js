/*
import Mock from 'mockjs'
//Mock可以拦截前端发出的请求，来模拟接口

//返回uid的请求
Mock.mock('/api/getUserId', 'post',(option)=>{
    const tags=JSON.parse(option.body).tags;
    return {
        code:200,
        message:'success',
        data:{
            uid: '011111' //测试数据
        }
    }
});
// 根据uid获取视频推荐数据
Mock.mock('/api/getRecommendVideos', 'post', (options) => {
    const { uid } = JSON.parse(options.body);
    
    // 从static.jsx导入的数据
    const mockData = {
        videoList: [
            {
                "videoCoverImage": "//i0.hdslb.com/bfs/archive/503f632d894b5a250efbef9568474d8f017aa3f6.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1w14y1r7fD",
                "videoTitle": "公益筹到的款，用到位了吗？我们辗转2000多公里，就是为了得到一个答案"
            },
            {
                "videoCoverImage": "//i0.hdslb.com/bfs/archive/d1d8fe99547107260b0bf2eb28c659496fa8a159.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV12gAXeoEh4",
                "videoTitle": "动物与人类无障碍交流"
            },
            {
                "videoCoverImage": "//i0.hdslb.com/bfs/archive/92e1888754d3b7e0705f7809895e32bbd3cd7f91.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV18e411i7QV",
                "videoTitle": "我的快乐正在帮我做好事？"
            },
            {
                "videoCoverImage": "//i1.hdslb.com/bfs/archive/0356f92520899a835357271f9818f53b436dae7f.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV19F411k77r",
                "videoTitle": "特厨探店 |来扬州几天吃过最正经的淮扬菜馆子！怡园"
            },
            {
                "videoCoverImage": "//i1.hdslb.com/bfs/archive/247a943a28f476f3e2def980e87b3182f6d5d2a1.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1f34y1o72a",
                "videoTitle": "糕 辅 导"
            },
            {
                "videoCoverImage": "//i2.hdslb.com/bfs/archive/27a21e8131098c738ff2bd7c6d971bb02d8bc700.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1MPNJe6EGZ",
                "videoTitle": "第一集差点把主角全炸了，这剧玩儿这么大的吗？《白色橄榄树》"
            },
            {
                "videoCoverImage": "//i1.hdslb.com/bfs/archive/c1d6526c1190c8cc247406c1514f0a546aa6cf3a.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1hW4y1T7ao",
                "videoTitle": "我发现我们村真的很穷！"
            },
            {
                "videoCoverImage": "//i1.hdslb.com/bfs/archive/9f73ac1a03fecae3b848c924d479a0835fabe4f1.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1Nm411Z7HG",
                "videoTitle": "履职日记：守好生猪“中国芯” 吃上更美味猪肉【主播说三农】"
            },
            {
                "videoCoverImage": "//i1.hdslb.com/bfs/archive/ede46cd6f9bc532d804346ecdc4ce9508cb1f1f8.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1FZcoesEM9",
                "videoTitle": "《明日方舟》「灶暖味常」特别公益PV"
            },
            {
                "videoCoverImage": "//i0.hdslb.com/bfs/archive/239c6db87795d5b67ff16be750d2bcf912ca20a1.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1cz4y167Ra",
                "videoTitle": "「全球变暖」公益短片"
            },
            {
                "videoCoverImage": "//i0.hdslb.com/bfs/archive/b96dc40bc18267d3a5a68aee57daad89f6dac8eb.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1Ld4y1W7VL",
                "videoTitle": "奇哥、一数、黄夫人、陶大...... 我搜集了B站高中各科的免费公益up主 | 去大数据把这个视频推给迷茫的中学生"
            },
            {
                "videoCoverImage": "//i0.hdslb.com/bfs/archive/f34265ca40d9bc89717d14007c79e24ffda09361.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1qwAmeZEDX",
                "videoTitle": "ai的下一步风口在哪？都明牌了...可别问了"
            },
            {
                "videoCoverImage": "//i2.hdslb.com/bfs/archive/1b97de8c8dc0bbccf1bcda9c190b8f0b17aef9b6.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV18DDgYjEMY",
                "videoTitle": "两个小妾同时生产，比谁先生出儿子，结果花魁大着肚子看笑话"
            },
            {
                "videoCoverImage": "//i1.hdslb.com/bfs/archive/d3acab255edc6a494a97a8fd91af8f7b87eb5181.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1nw4m1S7cT",
                "videoTitle": "(电视剧)《倚天屠龙记》陈钰琪专访,泡雨无际拍摄 &quot;挠脚心&quot;的故事"
            },
            {
                "videoCoverImage": "//i1.hdslb.com/bfs/archive/b65bc1c55234b062b32aac4a929428d6f087f956.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1dh411q7mu",
                "videoTitle": "这是她的第一部女主剧 现在导演都破产了，剧还没播....."
            },
            {
                "videoCoverImage": "//i1.hdslb.com/bfs/archive/5e0ec74b61d1975d923ef5c2bf529f836235a056.jpg",
                "videoUrl": "https://www.bilibili.com/video/BV1DY9FYfEAt",
                "videoTitle": "三大平台的剧终于要憋不住啦，这三部古装大男主剧你最期待哪部？ 成毅《赴山海》 肖战 《藏海传》杨洋《凡人修仙传》"
            }
        ],
    };

    return {
        code: 200,
        message: 'success',
        data: mockData
    }
});
//根据uid获取用户推荐数据
Mock.mock('/api/getRecommendUser', 'post', (options) => {
    const { uid } = JSON.parse(options.body);
    
    // 从static.jsx导入的数据
    const mockData = {
        similarUsers:[{
                    "userId": 7891,
                    "userUid": 107890,
                    "userPreferredTags": "家装房产,三农",
                    "userClusterId": "23,28"
                },
                {
                    "userId": 8410,
                    "userUid": 108409,
                    "userPreferredTags": "美食,家装房产",
                    "userClusterId": "6,23"
                },
                {
                    "userId": 607,
                    "userUid": 100606,
                    "userPreferredTags": "健身,家装房产",
                    "userClusterId": "23,25"
                },
                {
                    "userId": 5848,
                    "userUid": 105847,
                    "userPreferredTags": "科技数码,家装房产",
                    "userClusterId": "5,23"
                }]
    };

    return {
        code: 200,
        message: 'success',
        data: mockData
    }
});
*/
import Mock from 'mockjs';
//拦截用户密码登录api
Mock.mock('http://localhost:8080/api/auth/login', 'post', (option) => {
    const { params } = JSON.parse(option.body);  // 解构 params
    console.log(params);
    const { userAccount,password } = params;    // 从 params 中获取值
    return {
        token: "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDAwMDAiLCJleHAiOjE3NDQ2NDM5NjN9.27lmSUe0pQZUhLEwiSV1y1tmV2NJq7DCYEtkov_wt3o",
        uid:"10001"
    }
});

//拦截获取验证码api
Mock.mock('http://localhost:8080/api/auth/code', 'post', (option) => {
    const { params } = JSON.parse(option.body);  // 解构 params
    const { phone } = params;    // 从 params 中获取值
    return {
        code:"123456"
    }
});

//拦截手机号+验证码登录的api 这里的默认要进行下一步
Mock.mock('http://localhost:8080/api/auth/mblogin', 'post', (option) => {
    const { params } = JSON.parse(option.body);  // 解构 params
    const { phone,code } = params;    // 从 params 中获取值
    return {
        message: "手机号未注册,请继续进行下一步以完成注册.",
        nextStep: "/api/auth/setPassword",
        isSetup:false
    }
});
//设置密码登录后的api
Mock.mock('http://localhost:8080/api/auth/setPassword', 'post', (option) => {
    const { params } = JSON.parse(option.body);  // 解构 params
    const { phone,password } = params;    // 从 params 中获取值
    return {
       message: "密码设置成功,请选择您感兴趣的词条.",
       nextStep: "/api/register"
    }
});