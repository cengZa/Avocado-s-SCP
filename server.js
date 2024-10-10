const express = require('express');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors()); // 启用 CORS，以允许跨域请求

// 模拟数据库中的课程数据
const coursesData = {
    '2023-10-01': [
        { id: 1, name: '数学课', time: '09:00 - 10:30', location: '教室101' },
        { id: 2, name: '物理课', time: '11:00 - 12:30', location: '教室102' }
    ],
    '2023-10-02': [
        { id: 3, name: '化学课', time: '13:00 - 14:30', location: '实验室201' }
    ]
};

// 定义 API 路由以获取特定日期的课程
app.get('/api/courses', (req, res) => {
    const date = req.query.date; // 从查询参数中获取日期

    if (coursesData[date]) {
        return res.json(coursesData[date]); // 返回该日期下的课程数据
    } else {
        return res.json([]); // 如果没有找到对应的课程，则返回空数组
    }
});

// 启动服务器
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
