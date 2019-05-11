<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="https://cdn.bootcss.com/flv.js/1.3.3/flv.min.js"></script>
<body>
<div id="container">
    <div id="bg">
        <%--<img src="images/background.jpg">--%>
    </div>
    <div id="video">
        <video id="videoElement" controls></video>
    </div>
</div>
<script>
// if (flvjs.isSupported()) {//检查flvjs能否正常使用
//     var videoElement = document.getElementById('videoElement');//使用id选择器找到第二步设置的dom元素
//     var flvPlayer = flvjs.createPlayer({//创建一个新的flv播放器对象
//         type: 'flv',//类型flv
//         url: 'https://jingyan.baidu.com/asyncreq?method=getApp&title=%E5%A6%82%E4%BD%95%E6%89%93%E5%BC%80.flv%E6%96%87%E4%BB%B6&word=%E6%89%93%E5%BC%80flv'//flv文件地址
//     });
//     flvPlayer.attachMediaElement(videoElement);//将flv视频装载进video元素内
//     flvPlayer.load();//载入视频
//     flvPlayer.play();//播放视频，如果不想要自动播放，去掉本行
// }
</script>
</body>
</html>
