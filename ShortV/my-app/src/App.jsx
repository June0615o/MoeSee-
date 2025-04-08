import { useState } from 'react'
import  Home from './pages/home/home'
import Video from './pages/video/video'

import { BrowserRouter ,Routes,Route} from 'react-router'
import './App.css'

function App() {
  return (
   <>
   
   <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home/>} />
        <Route path="/home" element={<Home/>} />
        <Route path="/video" element={<Video/>} />
      </Routes>
    </BrowserRouter>
   
   </>

  )
}

export default App
