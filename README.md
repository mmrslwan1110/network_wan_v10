<!-- wp:heading -->
<h2><strong>视频通信APP</strong><strong>的设计</strong></h2>
<!-- /wp:heading -->

<!-- wp:heading -->
<h2><strong>1       </strong>功能选择界面</h2>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p>通过程序设计，首先构建用户功能选择界面，其中包括用户名和该用户的个人签名、个人头像，本处签名选用为个人网站，新的个人网站：<a href="https://www.codekp.cn/">https://www.codekp.cn/</a>。向下依次是Home主界面，主要是APP的地图功能。第二项Gallery为第二个界面，为APP的蓝牙信道检测功能，第三项Slideshow为第三界面，为APP的视频通话功能。点击三个界面选项框可以跳转到自己的界面。</p>
<!-- /wp:paragraph -->

<!-- wp:image {"id":254,"sizeSlug":"large","linkDestination":"none"} -->
<figure class="wp-block-image size-large"><img src="http://www.mmrsl.cn/wp-content/uploads/2021/01/1.png" alt="" class="wp-image-254"/></figure>
<!-- /wp:image -->

<!-- wp:paragraph {"align":"center"} -->
<p class="has-text-align-center">图1 APP功能选择界面图</p>
<!-- /wp:paragraph -->

<!-- wp:heading -->
<h2><strong>2       </strong> 定位界面</h2>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p><a>定位界面，主要由上方的底层用户数据显示界面，和下方的定位地图界面，地图界面采用浏览器形式，并对定位点用红色鲜明坐标在图中进行了标记。</a></p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>首先，通过调用安卓底层接口，读取手机的经度、纬度等信息，然后设计了一个浏览器界面，由于时间关系没有调用高德地图的api，直接调用的web静态页面。通过修改URL的页面地址，即可获得如图所示的地图信息显示图。</p>
<!-- /wp:paragraph -->

<!-- wp:image {"id":255,"sizeSlug":"large","linkDestination":"none"} -->
<figure class="wp-block-image size-large"><img src="http://www.mmrsl.cn/wp-content/uploads/2021/01/2-514x1024.png" alt="" class="wp-image-255"/></figure>
<!-- /wp:image -->

<!-- wp:paragraph {"align":"center"} -->
<p class="has-text-align-center">图2 APP定位界面</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>其次，由于本文中获取的为GPS原始数据，采用的是国际坐标系(WGS84)，而中国地图采用的为火星坐标系(GCJ02)，其中进行了经纬度的转换，将原始数据转到精准的经纬度数据，并实现精确定位。</p>
<!-- /wp:paragraph -->

<!-- wp:heading -->
<h2><strong>3       </strong>视频通话界面</h2>
<!-- /wp:heading -->

<!-- wp:paragraph -->
<p>视频通话界面，界面左侧为通话对方的实时视频图像，右方为己方的实时视频图像，下方为对方的端口号输入栏，右侧为设置，点击后会连接进对方的视频数据中。</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>首先，先编写程序，读取安卓手机的相机接口。其中的原理是，首先将调用安卓手机的相机功能，然后将拍摄到的视频保存在存储器中的某个地方，然后读取该位置的图片，在右侧视频端进行显示，调用相机接口如图3所示。</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>然后采用socket通信，其中有两种选择，TCP通信和UDP通信两种方式，其各自在自己的使用场景下有各自的优点，但是考虑到UDP不用握手可以完成双向传递，于是选择UDP，同时还可以提高通信速率。</p>
<!-- /wp:paragraph -->

<!-- wp:image {"id":258,"sizeSlug":"large","linkDestination":"none"} -->
<figure class="wp-block-image size-large"><img src="http://www.mmrsl.cn/wp-content/uploads/2021/01/3-1-626x1024.png" alt="" class="wp-image-258"/></figure>
<!-- /wp:image -->

<!-- wp:paragraph {"align":"center"} -->
<p class="has-text-align-center">图3 APP视频通话相机功能</p>
<!-- /wp:paragraph -->

<!-- wp:paragraph -->
<p>获取到本地的图像数据后，对图像进行编码，通过UDP通讯协议进行传输，传输到本地IP：192.168.201.217，端口：8888上。然后在另对方手机中输入己方IP地址，点击START按钮即可开始视频通话，视频通话完成图像如下图所示。</p>
<!-- /wp:paragraph -->

<!-- wp:image {"id":259,"sizeSlug":"large","linkDestination":"none"} -->
<figure class="wp-block-image size-large"><img src="http://www.mmrsl.cn/wp-content/uploads/2021/01/4-1-524x1024.png" alt="" class="wp-image-259"/></figure>
<!-- /wp:image -->

<!-- wp:paragraph {"align":"center"} -->
<p class="has-text-align-center">图4 APP视频通话界面图</p>
<!-- /wp:paragraph -->

<!-- wp:heading -->
<h2><strong>4      APP</strong>视频demo</h2>
<!-- /wp:heading -->

<!-- wp:video -->
<figure class="wp-block-video"><video controls src="blob:http://www.mmrsl.cn/386ea6cf-f2e7-47b7-9cf8-8346885f1ddd"></video></figure>
<!-- /wp:video -->
