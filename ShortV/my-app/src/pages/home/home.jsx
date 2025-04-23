import React from 'react';
import { useState } from 'react';
import {btnList} from '../../static'
import './home.css'
import { Button,Card} from "antd";
import { CheckCircleFilled } from '@ant-design/icons';
import { useNavigate } from 'react-router';
import { getUserId } from '../../mock/api'
import { useSearchParams } from 'react-router';

export default function Home() {
    
    const [btnNewList, setBtnList] = useState(btnList);
    const selectedCount = btnNewList.filter((item) => item.selected).length;
    const [finalNums, setFinalNums] = useState([]);
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const phone = searchParams.get('phone');
    console.log(phone)
  
  const btnClick = (item) => {
    const updatedList = btnNewList.map((btn) =>
      btn.id === item.id ? { ...btn, selected: !btn.selected } : btn
    );
    setBtnList(updatedList);
  };

  
  const handleSubmit = async (selectedTags) => {
    if(selectedCount < 1 || selectedCount > 3){
        alert('请选择至少1-3个标签');
    }else{
    try {
        alert('提交成功');
        selectedTags = selectedNums.filter((value, index, self) => {
        return self.indexOf(value) === index;
          });
        const response = await getUserId(selectedTags,phone);
        console.log(response.status)
        console.log(response)
        if (response.status === 200) {
            const uid = response.data.userUid;
            const token= response.data.token;
            localStorage.setItem('uid', uid);
            localStorage.setItem('token', token);
            console.log(uid)
            // 跳转到video页面，并传递uid
            navigate(`/video?uid=${uid}`);
        }
        else{
          console.log("no 200")
        }
    } catch (error) {
        console.error('Error processing tags:', error);
    }}
}

    
    const selectedNums = btnNewList
      .filter((item) => item.selected)
      .map((item) => item.num);
    

  return (
    <div className="home">
      <div className="title">
        <div>选择1-3个你感兴趣的视频话题吧！</div>
      </div>
      <div className="btnList">
        <Card  className="card">
            {btnNewList.map((item,index)=>{
                return <Button disabled={selectedCount >= 3 && !item.selected} icon={item.selected &&<CheckCircleFilled />} style={item.selected && { backgroundColor: '#1890ff' }}
                onClick={()=>btnClick(item)} className="btn" key={item.id}>{item.title}</Button>
            })}
            
        </Card>
      </div>
      <div className="submit">
        <Button onClick={()=>handleSubmit()} className="btn" type="primary">确定</Button>
      </div>
    </div>
  );
}