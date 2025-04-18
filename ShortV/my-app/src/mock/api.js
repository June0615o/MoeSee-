import axios from 'axios';

//用户标签选择
export const getUserId=(tags)=>{
    return axios.post('http://localhost:8080/api/register',{
        tags:tags
    })
}

/*// 获取推荐数据
export const getRecommendVideos = (uid) => {
    return axios.post('http://localhost:8080/api/recommend', {
        uid: uid
    });
}

// 获取推荐数据
export const getRecommendUser = (uid) => {
    return axios.post('http://localhost:8080/api/recommendusers', {
        uid: uid
    });
}*/

//第一次获取视频数据 新版

export const getFirstRecommendVideos = async (userUid,token) => {
  try {
    const response = await axios.get('http://localhost:8080/api/firstrecommend', {
        headers: {
            Authorization: `Bearer ${token}`
        },
        userUid: userUid
      
    });
    console.log(response.data);
    return response.data;
  } catch (error) {
    console.error('获取推荐用户失败:', error);
  }
};

//换一批
export const getRecommendVideos = async (userUid,token) => {
  try {
    const response = await axios.get('http://localhost:8080/api/recommend', {
      headers: {
        Authorization: `Bearer ${token}`
    },
    userUid: userUid
    });
    console.log(response.data);
    return response.data;
  } catch (error) {
    console.error('获取推荐用户失败:', error);
  }
};
//获取用户数据
export const getRecommendUser = async (userUid,token) => {
  try {
    const response = await axios.get('http://localhost:8080/api/recommendusers', {
      headers: {
        Authorization: `Bearer ${token}`
    },
    userUid: userUid
    });
    console.log(response.data);
    return response.data;
  } catch (error) {
    console.error('获取推荐用户失败:', error);
  }
}




//账号密码登录api
export const accountLogin= async(account,password)=>{
  try{
    const response = await axios.post('http://localhost:8080/api/auth/login',{
      
        userAccount:account,
        password:password
      
    })
    console.log(response.data);
    return response.data;
  }catch(error){
    console.error('登录失败:',error);
  }
}
//发送验证码的api
export const phoneCode= async(phone)=>{
  try{
    const response = await axios.post('http://localhost:8080/api/auth/code',{
      
        phone:phone
      
    })
    console.log(response.data);
    return response.data;
  }catch(error){
    console.error('获取验证码失败:',error);
  }
}
//手机号+验证码登录的api
export const phoneLogin= async(phone,code)=>{
  try{
    const response = await axios.post('http://localhost:8080/api/auth/mblogin',{
      
        phone:phone,
        code:code
      
    })
    console.log(response.data);
    return response.data;
  }catch(error){
    console.error('手机号登录失败:',error);
  }
}
//手机登录设置密码后的api
export const phoneSetup= async(phone,password)=>{
  try{
    const response = await axios.post('http://localhost:8080/api/auth/setPassword',{
      
        phone:phone,
        password:password

      }
    )
    console.log(response.data);
    return response.data;
  }catch(error){
    console.error('手机号登录失败:',error);
  }
}