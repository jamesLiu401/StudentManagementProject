import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

// 获取根元素
const root = ReactDOM.createRoot(document.getElementById('root'));

// 渲染应用
root.render(<App />);

// 等待React应用完全加载后隐藏加载动画
setTimeout(() => {
    const loadingElement = document.getElementById('loading');
    if (loadingElement) {
        loadingElement.style.display = 'none';
    }
}, 100); // 短暂延迟确保React应用已渲染