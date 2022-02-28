# Weather
红岩寒假考核------------一个较完整的app。
本软件是一个查看天气的软件，上下滑动可查看当天天气信息，左右可查看你所添加的城市。由于接口信息较少原因，详情页面为假数据，目前只是查看当天小时的天气，因为我实在没找到7天或者一个月的天气接口。
--------------------已用kotlin重写
### 一、软件介绍

### 1.视觉上
(1)使用了随天气和时间变化的背景,例如：
<br>晴天：<img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215132141.jpg" width="200" height="400" /> 雾霾：<img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215132103.jpg" width="200" height="400" />雪天：<img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215132149.jpg" width="200" height="400" /></br>
等等，这里就不一一展示了。

(2)还使用了下滑背景逐渐透明，便于查看信息，如：<br><img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215140627.jpg" width="200" height="400" /> <img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215140621.jpg" width="200" height="400" /> <img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215140615.jpg" width="200" height="400" /> <img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215140608.jpg" width="200" height="400" /></br>
逐渐下滑逐渐透明。

(3)还有城市管理页面也实现了item 背景随天气变化，如：
<br><img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215141228.jpg" width="200" height="400"/></br>

(4)还实现了白天，夜晚背景变化，刚刚展示的都是白天，这是晚上的情况：可以看到进入夜晚的城市item背景已经变暗了
<br><img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215181117.jpg" width="200" height="400"/><img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215181124.jpg" width="200" height="400"/></br>

### 2.使用上
(1)在城市管理页面长按item可选择删除单个城市，点击可跳转至对应城市。例：
<br><img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215145443.gif" width="200" height="400"/> <img src="https://github.com/RQ527/pictures/blob/master/QQ%E5%9B%BE%E7%89%8720220215145512.gif" width="200" height="400"/></br>

(2)然后我自定义的温度曲线可以左右滑动查看温度和小时天气，到最后可以点击查看温度，而且天气图标也是随时间和天气变化的。
自定义的温度曲线图也是支持惯性滑动和弹性回弹（就是只会有五个item出现在屏幕中）。

(3)点击一些图标可以进入详情页面。

### 二、使用的技术
用的语言是java，kotlin使用了一丢丢。

数据库使用的是room

网络请求使用的是okhttp

一些自定义view

gson解析数据

### 三、亮点讲解
1.自定义view---MyDiagram

继承于viewgroup,实现左右滑动，惯性滑动，弹性回弹

因为惯性滑动比较耗时使用的是重新开启线程进行滑动，然后在里面用Scroller帮助类进行滑动

圆环位置的计算是开启线程监听滚动然后进行计算圆环位置的逻辑。这里在之前遇到过难题，之前我是直接在onTouchEvent里面直接进行计算位置的逻辑，然后发现很卡，体验感很差，之后在郭神的帮助下改成了这个思路。

下面的天气展示是一个item,然后根据数据动态添加进去进行measure,layout。

弹性回弹用的Scroller进行矫正位置，每次up或者在惯性滑动结束后用adjustUi()方法进行校正。

2.自定义的menu,继承自view

官方提供的menu满足不了自己的需求，于是自己写了一个。

用bitmap+popuwindow+listview

实现圆角背景

然后用anim实现了弹出和消失的逐渐变大变小的动画

3.自定义的tablayout

用两层LnearLayout，外层确定位置，内层动态添加圆点并调整位置，通过ViewPger2的监听使得当前的圆点高亮

4.MyScrollView继承自ScrollView

因为我需要监听ScrollView来改变背景的透明度，但是官方的ScrollView的监听方法是protected，所以只能重写然后把监听方法暴露出来

### 四、滑动冲突

解决了SwipeRefreshLayout和MyScrollView和Viewpager2和MyDiagram的滑动冲突

滑动MyDiagram会响应Viewpager2的左右滑动，上下滑动MyScrollView和有时滑动ViewPager2时会响应SwipeRefreshLayout以至于滑动不流畅，体验极差。

花了三天时间，上网查资料，看书，总算解决了，呜呜呜。

### 五、心得体会

一个月的时间，算是边玩边写吧，完成了自己独立开发的第一款比较完整有使用价值的app。期间也收获很多，加深了对java语言的理解，对面对对象和接口回调理解更深，学会了自定义View的一些方法和技巧，学会了合理处理滑动冲突，学会了okhttp,gson,room,anim动画等的用法，实用的代码会封装成工具类，以及对类进行分包等等等等。我想，如果我没有进入红岩，我不会学到这么多，感谢红岩，感谢学长们。

### 六、未来展望

希望之后学会使用kotlin进行编程，学会jecpack，retrofit的使用，和mvvm架构，以及更多新技术，让自己实力变强变强再变强。加油！

