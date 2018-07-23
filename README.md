# AndroidResUpdatePlugin

## 0 背景

凡是涉及界面的移动端开发，必要会和各种资源文件打交道，如颜色、文本、尺寸、图片等。对于图片资源，一般的开发流程如下：

![ks_manage_res_old_work_flow.png](https://upload-images.jianshu.io/upload_images/2282248-195abf5e4627dc34.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

>常规图片管理使用流程

1. 开发不修改图片文件的命名，全部图片文件完全由视觉定义

	由于图片名称名不能反应该图片在 app 中的位置或功能，因此当项目工程进展到一定程度，图片数量变多，图片的维护就容易变的混乱，如出现重复图片、图片不合理的复用等问题

2. 开发根据模块、功能等修改图片文件的命名

	开发接收到图片之后，人肉修改图片名，自己维护一份资源，而视觉们也需要维护一份资源，容易造成 2 份资源的不同步

## 1 实践

* 保证图片资源只存在一份

	视觉维护一份图片资源，开发不允许`人肉`重命名。

* 如何保证视觉后期维护

	为保证视觉保证图片和功能模块对应，则视觉通过文件夹的方式管理图片。相关模块的文件，放置在对应模块名的文件夹下。如地址模块的图片放置在 address 文件夹下，购物车模块的图片放置在 shoppingcart 文件夹下，全局图片放置在 all 文件夹下。另外不同倍率的图片分别放置在 mipmap-xhdpi、mipmap-xxhdpi 等文件夹下。

* 视觉通过什么途径给开发资源

	为避免单对单的 popo 传输和邮件发送，我们为图片资源建立了 git 仓库，视觉们上传 git，开发拉取图片(不允许上传)。同时，使用 git 仓库，也使得图片的增删改，有记录可查。

* 开发如何使用图片

	android 工程中，图片资源是平铺放置的，而视觉维护的图片是分别放置在对应模块名的文件夹下（不同文件夹内可能存在同名文件）。然后，我们也规定了不允许`人肉`修改文件名。那么，开发该如何将图片集成到工程中呢？很简单，写个脚本程序，将文件夹内的图片拼接上文件夹名，再拷贝出来就能解决了，如 address/back.png → address_back.png

## 2 git 图片同步自动化

到这里，已经基本解决以往图片维护的各种问题了，不过上述流程还是存在不便利的地方：
	
1. 脚本程序拷贝出来的文件，程序员需要`人肉`选择出里面需要的图片，并移动到工程中
2. 视觉删除的不用的图片，开发需要根据 git 记录`人肉`地删除工程中的文件
3. 由于存在`人肉`才做难免还是会出现图片 git 仓库和工程中的图片存在不一致的情况，如项目工程中存在冗余图片未删除的情况
4. app 工程中图片资源无历史更新记录
5. 由于 app 是多分支开发，同时部分分支是在将来的版本上线，而视觉图片仓库是单分支（不指望视觉同学管理 git 仓库分支），此时 app 工程容易出现冗余图片

对于上述问题，我们需要有工具完成以下目标

1. 自动更新图片 git 仓库
2. 自动比对 app 工程图片和图片 git 仓库，输出各文件夹`增`、`删`、`改`的图片信息，并支持预览
3. 支持开发选择和取消改动的图片操作，并同步到 app 工程

![ks_manage_res_work_flow_plugin_new.jpg](https://upload-images.jianshu.io/upload_images/2282248-1b096df9293e623e.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
>插件安装界面

![gif](https://upload-images.jianshu.io/upload_images/2282248-cbb8f0aab3f55594.gif?imageMogr2/auto-orient/strip)
>插件操作示例

Android Studio 控制台输出文本示例：

```
[Ljava.lang.String;@22bdb9f8
Updating a9b339b..321f2ff
Fast-forward
 android/mipmap-xhdpi/coupon/sem_circle.png   | Bin 0 -> 14966 bytes
 android/mipmap-xxhdpi/coupon/semi_circle.png | Bin 0 -> 15004 bytes
 2 files changed, 0 insertions(+), 0 deletions(-)
 create mode 100644 android/mipmap-xhdpi/coupon/sem_circle.png
From git.mail.netease.com:yanxuan_gui/yanxuan_gui
 create mode 100644 android/mipmap-xxhdpi/coupon/semi_circle.png
   a9b339b..321f2ff  dev        -> origin/dev
===================
Wed Feb 22 19:04:30 CST 2017
add /Users/.../yanxuan_gui/android/mipmap-xhdpi/coupon/sem_circle.png to /Users/.../YanXuan/app/src/main/res/mipmap-xhdpi/coupon_sem_circle.png
add /Users/.../yanxuan_gui/android/mipmap-xxhdpi/coupon/semi_circle.png to /Users/.../YanXuan/app/src/main/res/mipmap-xxhdpi/coupon_semi_circle.png
```

## 3 新的管理流程

![ks_manage_res_new_work_flow.png](https://upload-images.jianshu.io/upload_images/2282248-0a7702970e12b510.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

以上流程，相比原有的图片管理方式，更加简单高效且易于维护