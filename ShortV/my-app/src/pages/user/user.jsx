import React from "react";
import './user.css'
import { useState, useEffect} from "react";
import { accountLogin,phoneCode,phoneLogin,phoneSetup } from "../../mock/api";
import { useNavigate } from 'react-router';

export default function User() {
    const [tabsValue, setTabsValue] = useState("phone");
    const [phone, setPhone] = useState("");
    const [code, setCode] = useState("");
    const [code2, setCode2] = useState("-1");
    const [account, setAccount] = useState("");
    const [password, setPassword] = useState("");
    const [signupPassword1,setSignupPassword1]= useState("");
    const [signupPassword2,setSignupPassword2]= useState("");
    const [isSignup, setIsSignup] = useState(false);

    //这里实现校验功能（手机号，密码格式）
    const [phoneError, setPhoneError] = useState("");
    const [passwordError, setPasswordError] = useState("");
    const [confirmPasswordError, setConfirmPasswordError] = useState("");
    const [codeError, setCodeError] = useState("");
    

    //验证码按钮60s功能
    const [countdown, setCountdown] = useState(0);
    const navigate = useNavigate();

    const startCountdown=()=>{
        setCountdown(60);
        const timer = setInterval(()=>{
            setCountdown((prevCount)=>{
                if(prevCount<=1){
                    clearInterval(timer);
                    return 0;
                }
                return prevCount-1;
            })
        },1000)
    }
    
    const handleGetCode = async() => {
        try{if(countdown>0||!phone)return;
        startCountdown();
        const res = await phoneCode(phone);
        setCode2(res);
        console.log(res);
        }catch(error){
            console.log(error);
        }
        
    }
    //账号密码登录签发token+uid（说明用户已经注册且选择好标签） ，要直接跳转到视频列表页
    const login= async()=>{
        try{
            const res = await accountLogin(account,password);
            if(res.token&&res.userUid){
            localStorage.setItem("token",res.token);
            localStorage.setItem("uid",res.userUid);
            alert("登录成功");
            navigate(`/video?uid=${res.userUid}`);
            }
            else{
                console.log("no")
            }
        
        }catch
        (err){
            console.log(err);

        }
    }
    //手机验证登录成功也签发token+uid（说明用户已经注册且选择好标签） ，要直接跳转到视频列表页
    const mblogin= async()=>{
        try{
        console.log("phone:"+phone)
        console.log("code:"+code)
        console.log("code2:"+code2)
        if(phoneError||codeError){alert("请输入正确的手机号或验证码") ;return;}
        
        if(code==code2){
            const res= await phoneLogin(phone,code);
            console.log(res);
            if(!res.isSetup){
                //测试如果是新用户，则将页面修改为注册样例
                setIsSignup(true)
                alert(res.message)
            }
            else{
                //这里说明是老用户，即api返回token+uid
                /* localStorage.setItem("token",res.token);
                localStorage.setItem("uid",res.uid);
                alert("登录成功");
                navigate(`/video?uid=${res.uid}`); */
            }
        }
        else{
            alert("验证码错误,请重试")
            return;
        }
        }
        catch(err) {
            console.log(err);
        }
    }
    //不直接签发token+uid,跳转到选择标签页面，选择完后再签发token+uid
    const signup=async ()=>{
        
        //注册中的登录按钮
        if(passwordError||confirmPasswordError){alert("请输入正确的密码") ;return;}
        if(signupPassword1===signupPassword2){
            //密码一致
            console.log("account:"+phone)
            console.log("password:"+signupPassword1)
            const res=await phoneSetup(phone,signupPassword1);
            if(res.nextStep){
                alert("注册成功")
                navigate(`/home?phone=${phone}`)
            }
            setAccount("")
            setPassword("")
            setSignupPassword1("")
            setSignupPassword2("")
            setIsSignup(false)
        }else{
            alert("两次密码不一致请重试")
        }
    }
    const renderForm=(isSignup,tabsValue)=>{
        
        if(isSignup&&tabsValue==="phone"){
            return(
                <div className="sign-input">
                <div className="input-group">
                    <input 
                        type="tel" 
                        placeholder="手机号"
                        value={phone}
                        onChange={
                            (e)=>{
                                setPhone(e.target.value);
                                
                            }
                        }
                    />
                </div>
                <input 
                    type="password"
                    placeholder="请输入密码"
                    value={ signupPassword1}
                    onChange={
                        (e)=>{setSignupPassword1(e.target.value)
                            validatePassword(e.target.value)
                        }}
                />
                {passwordError && <div className="pass1-error-text">{passwordError}</div>}
                <input 
                    type="password"
                    placeholder="请再次输入密码"
                    value={ signupPassword2}
                    onChange={
                        (e)=>{setSignupPassword2(e.target.value)
                            validateConfirmPassword(e.target.value, signupPassword1)
                        }}
                />
                {confirmPasswordError && <div className="pass2-error-text">{confirmPasswordError}</div>}
                <button className="submit-btn" onClick={()=>signup()} >注 册</button>
                {}
                 </div>
              )
          }
        else if(!isSignup&&tabsValue==="phone"){ 
            return(
                <div className="phone-inputs">
                <div className="input-group">
                    <input 
                        type="tel" 
                        placeholder="新用户将直接注册"
                        value={phone}
                        onChange={(e)=>{
                            setPhone(e.target.value);
                            validatePhone(e.target.value);
                        }}
                        
                    />
                    <button type="button" className="verify-code-btn"
                    onClick={()=>{handleGetCode(phone)}}
                    disabled={countdown>0}
                    style={{
                        cursor:countdown>0?'not-allowed':'pointer',
                        opacity:countdown>0?0.6:1
                        
                    }}
                    >
                        {countdown>0?`${countdown}s后重新获取`:'获取验证码'}
                    </button>
                    {phoneError && <div className="phoneerror-text">{phoneError}</div>}
                </div>
                <input 
                    type="password" 
                    placeholder="请输入验证码"
                    value={code}
                    onChange={(e)=>{
                        setCode(e.target.value)
                        validateCode(e.target.value);
                    }}
                />
                    {codeError && <div className="error-text">{codeError}</div>}
                <button className="submit-btn" onClick={()=>mblogin()}>登 录/注 册</button>
            </div>)
     }
        else  {
            return(
                <div className="account-inputs">
                    <input 
                        type="text" 
                        placeholder="请输入账号"
                        value={account}
                        onChange={
                            (e)=>{
                                setAccount(e.target.value)
                                }
                            
                        }
                    />
                    <input 
                        type="password" 
                        placeholder="请输入密码"
                        value={password}
                        onChange={(e)=>{
                            setPassword(e.target.value)
                            
                        }}
                    />
                    <button className="submit-btn" onClick={login}>登 录</button>
    
                </div>
             )
         }
      }
    
    /* console.log(accountLogin(18165622779,'123456'))
    console.log(phoneCode(18165622779))
    console.log(phoneLogin(18165622779,'123456'))
    console.log(phoneSetup(18165622779,'123456')) */

      useEffect(()=>{
          const token=localStorage.getItem('token');
          const uid = localStorage.getItem('uid');
          if(token&&uid){
              alert('已登录,为你直接跳转')
              navigate(`/video?uid=${uid}`)
          }
      })
      /* useEffect(() => {
        console.log("code2 更新为:", code2);
    }, [code2]);  */

    // 验证手机号
    const validatePhone = (value) => {
        const phoneRegex = /^\d{11}$/;  // 11位数字
        if (!value) {
            setPhoneError("手机号不能为空");
            return false;
        }
        if (!phoneRegex.test(value)) {
            console.log(value);
            setPhoneError("请输入11位手机号码");
            return false;
        }
        setPhoneError("");
        return true;
    };

    // 验证密码
    const validatePassword = (value) => {
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;  // 至少8位，包含字母和数字
        if (!value) {
            setPasswordError("密码不能为空");
            return false;
        }
        if (!passwordRegex.test(value)) {
            setPasswordError("密码必须至少8位，包含字母和数字");
            return false;
        }
        setPasswordError("");
        return true;
    };

    // 验证确认密码
    const validateConfirmPassword = (password, confirmPassword) => {
        if (!confirmPassword) {
            setConfirmPasswordError("请确认密码");
            return false;
        }
        if (password !== confirmPassword) {
            setConfirmPasswordError("两次输入的密码不一致");
            return false;
        }
        setConfirmPasswordError("");
        return true;
    };

    // 验证验证码
    const validateCode = (value) => {
        if (!value) {
            setCodeError("请输入验证码");
            return false;
        }
        if (value.length !== 6) {  // 假设验证码是6位
            setCodeError("验证码必须是6位");
            return false;
        }
        setCodeError("");
        return true;
    };

    return(
        <div className="container">
            <div className="header">欢迎来到MoeSee-Videos萌视-视频平台</div>
            <div className="card">
                <div className="title">欢迎使用MoeSee</div>
                
                    <div className="tabs">
                        <button className="btn" onClick={()=>{setTabsValue("phone") }}
                        style={{borderBottom: tabsValue === "phone" ? "2px solid black" : "2px solid #30e7dbed"}}>手机号</button>              
                        <button className="btn" onClick={()=>{setTabsValue("account")}}
                        style={{borderBottom: tabsValue === "account" ? "2px solid black" : "2px solid #30e7dbed"}}
                        >账号</button>
                    </div>
                    <div className="tabs-content">
                        <form onSubmit={(e)=>{e.preventDefault()}}>
                            { 
                                renderForm(isSignup, tabsValue)
                            }
                            
                        </form>
                    </div>
                </div>
            </div>
        
    )
}