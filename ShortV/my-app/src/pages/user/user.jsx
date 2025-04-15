import React from "react";
import './user.css'
import { useState } from "react";

export default function User() {
    const [tabsValue, setTabsValue] = useState("phone");
    const [phone, setPhone] = useState("");
    const [code, setCode] = useState("");
    const [account, setAccount] = useState("");
    const [password, setPassword] = useState("");
    const [signupPassword1,setSignupPassword1]= useState("");
    const [signupPassword2,setSignupPassword2]= useState("");
    const [isSignup, setIsSignup] = useState(false);
    const login= ()=>{
        //账号密码登录

    }
    const mblogin=()=>{
        //手机验证码登录
        console.log("phone:"+phone)
        console.log("code:"+code)
        setPhone("")
        setCode("")
        alert("登录成功")
        //测试如果是新用户，则将页面修改为注册样例
        setIsSignup(true)
    }
    const signup=()=>{
        
        //注册中的登录按钮
        if(signupPassword1===signupPassword2){
            //密码一致
            console.log("account:"+account)
            console.log("password:"+signupPassword1)
            setAccount("")
            setPassword("")
            setSignupPassword1("")
            setSignupPassword2("")
            setIsSignup(false)
        }else{
            alert("两次密码不一致")
        }
    }
    const renderForm=(isSignup,tabsValue)=>{
        console.log("isSignup:"+isSignup)
        console.log("tabsValue:"+tabsValue)
        if(isSignup&&tabsValue==="phone"){
            return(
                <div className="sign-input">
                <div className="input-group">
                    <input 
                        type="tel" 
                        placeholder="手机号"
                        value={phone}
                        onChange={(e)=>{setPhone(e.target.value)}}
                    />
                   
                </div>
                <input 
                    type="password"
                    placeholder="请输入密码"
                    value={ signupPassword1}
                    onChange={(e)=>{setSignupPassword1(e.target.value)}}
                />
                <input 
                    type="password"
                    placeholder="请再次输入密码"
                    value={ signupPassword2}
                    onChange={(e)=>{setSignupPassword2(e.target.value)}}
                />
                <button className="submit-btn" onClick={()=>signup()} >注 册</button>
                
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
                        onChange={(e)=>{setPhone(e.target.value)}}
                    />
                    <button type="button" className="verify-code-btn">
                        获取验证码
                    </button>
                </div>
                <input 
                    type="text" 
                    placeholder="请输入验证码"
                    value={code}
                    onChange={(e)=>{setCode(e.target.value)}}
                />
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
                        onChange={(e)=>{setAccount(e.target.value)}}
                    />
                    <input 
                        type="password" 
                        placeholder="请输入密码"
                        value={password}
                        onChange={(e)=>{setPassword(e.target.value)}}
                    />
                    <button className="submit-btn" onClick={()=>login()}>登 录</button>
                </div>
             )
         }
      }
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