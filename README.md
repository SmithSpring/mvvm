
## 1、准备工作
### 1.1、启用databinding
在主工程app的build.gradle的android {}中加入：
```gradle
buildFeatures{
        dataBinding = true
    }
```
### 1.2、依赖Library
从远程依赖：

在根目录的build.gradle中加入
```gradle
allprojects {
    repositories {
		...
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
在主项目app的build.gradle中依赖
```gradle
dependencies {
    ...
    implementation 'com.github.qqlixiong:mvvm-framework-android:v1.0.4'
}
```
或

下载例子程序，在主项目app的build.gradle中依赖例子程序中的**mvvm-framework**：
```gradle
dependencies {	
    ...
    implementation project(':mvvm-framework')
}
```

### 1.3、配置config.gradle
如果不是远程依赖，而是下载的例子程序，那么还需要将例子程序中的config.gradle放入你的主项目根目录中，然后在根目录build.gradle的第一行加入：

```gradle
apply from: "config.gradle"
```

**注意：** config.gradle中的 

android = [] 是你的开发相关版本配置，可自行修改

support = [] 是你的support相关配置，可自行修改

dependencies = [] 是依赖第三方库的配置，可以加新库，但不要去修改原有第三方库的版本号，不然可能会编译不过
### 1.4、配置AndroidManifest
添加权限：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
配置Application：

继承**mvvm-framework**中的BaseApplication，或者调用

```java
BaseApplication.setApplication(this);
```
来初始化你的Application

可以在你的自己AppApplication中配置

```java
//是否开启日志打印
KLog.init(true);
//配置全局异常崩溃操作
CaocConfig.Builder.create()
    .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
    .enabled(true) //是否启动全局异常捕获
    .showErrorDetails(true) //是否显示错误详细信息
    .showRestartButton(true) //是否显示重启按钮
    .trackActivities(true) //是否跟踪Activity
    .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
    .errorDrawable(R.mipmap.ic_launcher) //错误图标
    .restartActivity(LoginActivity.class) //重新启动后的activity
    //.errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
    //.eventListener(new YourCustomEventListener()) //崩溃后的错误监听
    .apply();
```

## 2、快速上手

### 2.1、第一个Activity
> 以大家都熟悉的登录操作为例：三个文件**LoginActivty.java**、**LoginViewModel.java**、**activity_login.xml**

##### 2.1.1、关联ViewModel
在activity_login.xml中关联LoginViewModel。
```xml
<layout>
    <data>
        <variable
            type="com.lx.framework.ui.login.LoginViewModel"
            name="viewModel"
        />
    </data>
    .....

</layout>
```

> variable - type：类的全路径 <br>variable - name：变量名

##### 2.1.2、继承BaseActivity

LoginActivity继承BaseActivity
```java

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> {
    //ActivityLoginBinding类是databinding框架自定生成的,对activity_login.xml
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_login;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public LoginViewModel initViewModel() {
        //View持有ViewModel的引用，如果没有特殊业务处理，这个方法可以不重写
        return ViewModelProviders.of(this).get(LoginViewModel.class);
    }
}
```
> 保存activity_login.xml后databinding会生成一个ActivityLoginBinding类。（如果没有生成，试着点击Build->Clean Project）

BaseActivity是一个抽象类，有两个泛型参数，一个是ViewDataBinding，另一个是BaseViewModel，上面的ActivityLoginBinding则是继承的ViewDataBinding作为第一个泛型约束，LoginViewModel继承BaseViewModel作为第二个泛型约束。

重写BaseActivity的二个抽象方法

initContentView() 返回界面layout的id<br>
initVariableId() 返回变量的id，对应activity_login中name="viewModel"，就像一个控件的id，可以使用R.id.xxx，这里的BR跟R文件一样，由系统生成，使用BR.xxx找到这个ViewModel的id。<br>

选择性重写initViewModel()方法，返回ViewModel对象
```java
@Override
public LoginViewModel initViewModel() {
    //View持有ViewModel的引用，如果没有特殊业务处理，这个方法可以不重写
    return ViewModelProviders.of(this).get(LoginViewModel.class);
}
```

**注意：** 不重写initViewModel()，默认会创建LoginActivity中第二个泛型约束的LoginViewModel，如果没有指定第二个泛型，则会创建BaseViewModel

##### 2.1.3、继承BaseViewModel

LoginViewModel继承BaseViewModel
```java
public class LoginViewModel extends BaseViewModel {
    public LoginViewModel(@NonNull Application application) {
        super(application);
    }
    ....
}
```
BaseViewModel与BaseActivity通过LiveData来处理常用UI逻辑，即可在ViewModel中使用父类的showDialog()、startActivity()等方法。在这个LoginViewModel中就可以尽情的写你的逻辑了！
> BaseFragment的使用和BaseActivity一样，详情参考Demo。

### 2.2、数据绑定
> 拥有databinding框架自带的双向绑定，也有扩展
##### 2.2.1、传统绑定
绑定用户名：

在LoginViewModel中定义
```java
//用户名的绑定
public ObservableField<String> userName = new ObservableField<>("");
```
在用户名EditText标签中绑定
```xml
android:text="@={viewModel.userName}"
```
这样一来，输入框中输入了什么，userName.get()的内容就是什么，userName.set("")设置什么，输入框中就显示什么。
**注意：** @符号后面需要加=号才能达到双向绑定效果；userName需要是public的，不然viewModel无法找到它。

点击事件绑定：

在LoginViewModel中定义
```java
//登录按钮的点击事件
public View.OnClickListener loginOnClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
            
    }
};
```
在登录按钮标签中绑定
```xml
android:onClick="@{viewModel.loginOnClick}"
```
这样一来，用户的点击事件直接被回调到ViewModel层了，更好的维护了业务逻辑

这就是强大的databinding框架双向绑定的特性，不用再给控件定义id，setText()，setOnClickListener()。

**但是，光有这些，完全满足不了我们复杂业务的需求啊！mvvm-framework闪亮登场：它有一套自定义的绑定规则，可以满足大部分的场景需求，请继续往下看。**

##### 2.2.2、自定义绑定
还拿点击事件说吧，不用传统的绑定方式，使用自定义的点击事件绑定。

在LoginViewModel中定义
```java
//登录按钮的点击事件
public BindingCommand loginOnClickCommand = new BindingCommand(new BindingAction() {
    @Override
    public void call() {
            
    }
});
```
在activity_login中定义命名空间
```xml
xmlns:binding="http://schemas.android.com/apk/res-auto"
```
在登录按钮标签中绑定
```xml
binding:onClickCommand="@{viewModel.loginOnClickCommand}"
```
这和原本传统的绑定不是一样吗？不，这其实是有差别的。使用这种形式的绑定，在原本事件绑定的基础之上，带有防重复点击的功能，1秒内多次点击也只会执行一次操作。如果不需要防重复点击，可以加入这条属性
```xml
binding:isThrottleFirst="@{Boolean.TRUE}"
```
那这功能是在哪里做的呢？答案在下面的代码中。
```java
//防重复点击间隔(秒)
public static final int CLICK_INTERVAL = 1;

/**
* requireAll 是意思是是否需要绑定全部参数, false为否
* View的onClick事件绑定
* onClickCommand 绑定的命令,
* isThrottleFirst 是否开启防止过快点击
*/
@BindingAdapter(value = {"onClickCommand", "isThrottleFirst"}, requireAll = false)
public static void onClickCommand(View view, final BindingCommand clickCommand, final boolean isThrottleFirst) {
    if (isThrottleFirst) {
                RxView.clicks(view)
                        .subscribe(new Consumer<Unit>() {
                            @Override
                            public void accept(Unit unit) throws Throwable {
                                if (clickCommand != null) clickCommand.execute();
                            }
                        });
            } else {
                RxView.clicks(view)
                        .throttleFirst(CLICK_INTERVAL, TimeUnit.SECONDS)//1秒钟内只允许点击1次
                        .subscribe(new Consumer<Unit>() {
                            @Override
                            public void accept(Unit unit) throws Throwable {
                                if (clickCommand != null) clickCommand.execute();
                            }
                        });
            }
}
```
onClickCommand方法是自定义的，使用@BindingAdapter注解来标明这是一个绑定方法。在方法中使用了RxView来增强view的clicks事件，.throttleFirst()限制订阅者在指定的时间内重复执行，最后通过BindingCommand将事件回调出去，就好比有一种拦截器，在点击时先做一下判断，然后再把事件沿着他原有的方向传递。

是不是觉得有点意思，好戏还在后头呢！
##### 2.2.3、自定义ImageView图片加载
绑定图片路径：

在ViewModel中定义
```java
public String imgUrl = "http://img0.imgtn.bdimg.com/it/u=2183314203,562241301&fm=26&gp=0.jpg";
```
在ImageView标签中
```xml
binding:url="@{viewModel.imgUrl}"
```
url是图片路径，这样绑定后，这个ImageView就会去显示这张图片，不限网络图片还是本地图片。

如果需要给一个默认加载中的图片，可以加这一句
```xml
binding:placeholderRes="@{R.mipmap.ic_launcher_round}"
```
> R文件需要在data标签中导入使用，如：`<import type="com.lx.framework.R" />`

BindingAdapter中的实现
```java
@BindingAdapter(value = {"url", "placeholderRes"}, requireAll = false)
public static void setImageUri(ImageView imageView, String url, int placeholderRes) {
    if (!TextUtils.isEmpty(url)) {
                //使用Glide框架加载图片
                Glide.with(imageView.getContext())
                        .load(url)
                        .apply(new RequestOptions().placeholder(placeholderRes))
                        .into(imageView);
            }else {
                imageView.setImageDrawable(placeholderRes);
            }
}
```
很简单就自定义了一个ImageView图片加载的绑定，学会这种方式，可自定义扩展。
> 如果你对这些感兴趣，可以下载源码，在binding包中可以看到各类控件的绑定实现方式

#### 2.2.4、RecyclerView绑定
##### 2.2.4.1 纯网络请求
> RecyclerView也是很常用的一种控件，传统的方式需要针对各种业务要写各种Adapter，如果你使用了mvvm-framework，则可大大简化这种工作量，从此告别setAdapter()。

在ViewModel中定义：
```java
//给RecyclerView添加items
public final ObservableList<NetWorkItemViewModel> observableList = new ObservableArrayList<>();
//给RecyclerView添加ItemBinding
public final ItemBinding<NetWorkItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_network);
```
ObservableList<>和ItemBinding<>的泛型是Item布局所对应的ItemViewModel

在xml中绑定
```xml
<androidx.recyclerview.widget.RecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    binding:itemBinding="@{viewModel.itemBinding}"
    binding:items="@{viewModel.observableList}"
    binding:layoutManager="@{LayoutManagers.linear()}"
    binding:lineManager="@{LineManagers.horizontal()}" />
```
layoutManager控制是线性(包含水平和垂直)排列还是网格排列，lineManager是设置分割线

网格布局的写法：`binding:layoutManager="@{LayoutManagers.grid(3)}`</br>
水平布局的写法：`binding:layoutManager="@{LayoutManagers.linear(LinearLayoutManager.HORIZONTAL,Boolean.FALSE)}"`</br>

使用到相关类，则需要导入该类才能使用，和导入Java类相似

> `<import type="com.lx.framework.binding.viewadapter.recyclerview.LayoutManagers" />`</br>
> `<import type="com.lx.framework.binding.viewadapter.recyclerview.LineManagers" />`</br>

这样绑定后，在ViewModel中调用ObservableList的add()方法，添加一个ItemViewModel，界面上就会实时绘制出一个Item。在Item对应的ViewModel中，同样可以以绑定的形式完成逻辑
> 可以在请求到数据后，循环添加`observableList.add(new NetWorkItemViewModel(NetWorkViewModel.this, entity));`详细可以参考例子程序中NetWorkViewModel类。

**注意：** 在以前的版本中，ItemViewModel是继承BaseViewModel，传入Context，新版本3.x中可继承ItemViewModel，传入当前页面的ViewModel

##### 2.2.4.2 下拉刷新和上拉加载更多请求
> RecyclerView下拉刷新和上拉加载更多。

在ViewModel中定义：
```java
//给RecyclerView添加items
public final ObservableList<NetWorkItemViewModel> observableList = new ObservableArrayList<>();
//给RecyclerView添加ItemBinding
public final ItemBinding<NetWorkItemViewModel> itemBinding = ItemBinding.of(BR.viewModel, R.layout.item_network);
//下拉刷新
public BindingCommand<Void> onRefreshCommand = new BindingCommand<Void>(() -> refreshEvent.setValue(BaseViewModel.REFRESH_STATUS));
//上拉加载更多
public BindingCommand<Void> onLoadMoreCommand = new BindingCommand<Void>(() -> refreshEvent.setValue(BaseViewModel.LOAD_MORE_STATUS));
```
ObservableList<>和ItemBinding<>的泛型是Item布局所对应的ItemViewModel

在xml中绑定
```xml
<androidx.recyclerview.widget.RecyclerView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    binding:itemBinding="@{viewModel.itemBinding}"
    binding:items="@{viewModel.observableList}"
    binding:onRefreshCommand="@{viewModel.onRefreshCommand}"
    binding:onLoadMoreCommand="@{viewModel.onLoadMoreCommand}"
    binding:layoutManager="@{LayoutManagers.linear()}"
    binding:lineManager="@{LineManagers.horizontal()}" />
```
##### 2.2.4.3 头和尾
> RecyclerView添加头部和尾部。

在ViewModel中定义：
```java
private HeaderItemViewModel headerViewModel = new HeaderItemViewModel(HeaderFooterViewModel.this);
    private HeaderItemViewModel footerViewModel = new HeaderItemViewModel(HeaderFooterViewModel.this);

    public HeaderFooterViewModel(@NonNull Application application) {
        super(application);
        headerViewModel.title.set("Header");
        footerViewModel.title.set("Footer");
    }

    public ObservableList<NetWorkItem2ViewModel> observableList = new ObservableArrayList<>();

    public MergeObservableList<Object> headerFooterItems = new MergeObservableList<>()
            .insertItem(headerViewModel)
            .insertList(observableList)
            .insertItem(footerViewModel);

    public final OnItemBind<Object> multipleItems = (itemBinding, position, item) -> {
        if (Objects.equals(HeaderItemViewModel.class,item.getClass())) {
            itemBinding.set(BR.item, R.layout.item_header_footer);
        } else if (Objects.equals(NetWorkItem2ViewModel.class,item.getClass())) {
            itemBinding.set(BR.viewModel, R.layout.item_network2);
        }
    };
```
ObservableList<>和ItemBinding<>的泛型是Item布局所对应的ItemViewModel

在xml中绑定
```xml
<androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rv_net"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:fitsSystemWindows="true"
            android:background="@color/white"
            binding:rvManager="@{LayoutManagers.linear()}"
            binding:itemBinding="@{viewModel.multipleItems}"
            binding:items="@{viewModel.headerFooterItems}"
            binding:lineManager="@{LineManagers.horizontal()}"/>
```
##### 2.2.4.4 多布局
> RecyclerView多item布局。

在ViewModel中定义：
```java
public static final String MultiRecycleType_01 = "type01";
    public static final String MultiRecycleType_02 = "type02";

    public MultiRecycleViewModel(@NonNull Application application) {
        super(application);

        //添加布局1
        MultiLayoutItemViewModel item = new MultiLayoutItemViewModel(this);

        item.title.set("我是第一个布局");
        item.multiItemType(MultiRecycleViewModel.MultiRecycleType_01);
        this.observableList.add(item);

        //添加布局2
        for (int i =0;i<10;i++){
            String text = "我是第" + i + "条";
            MultiLayoutItemViewModel item2 = new MultiLayoutItemViewModel(this);
            item2.title.set(text);
            item2.multiItemType(MultiRecycleViewModel.MultiRecycleType_02);
            this.observableList.add(item2);
        }

        //添加布局3
        MultiLayoutItemViewModel item3 = new MultiLayoutItemViewModel(this);
        item3.title.set("我是第三个布局");
        item3.multiItemType(MultiRecycleViewModel.MultiRecycleType_01);
        this.observableList.add(item3);
    }

    //给RecyclerView添加ObservableList
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();
    //RecyclerView多布局添加ItemBinding
    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of((itemBinding, position, item) -> {
        //通过item的类型, 动态设置Item加载的布局
        String itemType = (String) item.getItemType();
        if (MultiRecycleType_01.equals(itemType)) {
            itemBinding.set(BR.viewModel, R.layout.item_multi_layout1);
        } else if (MultiRecycleType_02.equals(itemType)) {
            itemBinding.set(BR.viewModel, R.layout.item_multi_rv_left);
        }
    });
```
ObservableList<>和ItemBinding<>的泛型是Item布局所对应的ItemViewModel

在xml中绑定
```xml
<androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rv_multi"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:background="@color/white"
            binding:rvManager="@{LayoutManagers.linear()}"
            binding:itemBinding="@{viewModel.itemBinding}"
            binding:items="@{viewModel.observableList}"
            binding:lineManager="@{LineManagers.horizontal()}" />
```
##### 2.2.4.4 多方向
> RecyclerView同时存在VERTICAL和HORIZONTAL的item。

```java
GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2, LinearLayoutManager.VERTICAL,false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                //TODO
            }
        });
binding.recycleview.setLayoutManager(gridLayoutManager);
```

更多RecyclerView、ListView、ViewPager等绑定方式，请参考 [https://github.com/evant/binding-collection-adapter](https://github.com/evant/binding-collection-adapter)

### 2.3、网络请求
> 网络请求一直都是一个项目的核心，现在的项目基本都离不开网络，一个好用网络请求框架可以让开发事半功倍。
#### 2.3.1、Retrofit+Okhttp+RxJava
> 现今，这三个组合基本是网络请求的标配，如果你对这三个框架不了解，建议先去查阅相关资料。

square出品的框架，用起来确实非常方便。**mvvm-framework**中引入了
```gradle
api "com.squareup.okhttp3:okhttp:3.11.0"
api "com.squareup.retrofit2:retrofit:2.9.0"
api "com.squareup.retrofit2:converter-gson:2.9.0"
api "com.squareup.retrofit2:adapter-rxjava3:2.9.0"
```
构建Retrofit时加入
```java
Retrofit retrofit = new Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
    .build();
```
或者直接使用例子程序中封装好的RetrofitClient
#### 2.3.2、网络拦截器
**LoggingInterceptor：** 全局拦截请求信息，格式化打印Request、Response，可以清晰的看到与后台接口对接的数据，
```java
LoggingInterceptor mLoggingInterceptor = new LoggingInterceptor
    .Builder()//构建者模式
    .loggable(true) //是否开启日志打印
    .setLevel(Level.BODY) //打印的等级
    .log(Platform.INFO) // 打印类型
    .request("Request") // request的Tag
    .response("Response")// Response的Tag
    .addHeader("version", BuildConfig.VERSION_NAME)//打印版本
    .build()
```
构建okhttp时加入
```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
    .addInterceptor(mLoggingInterceptor)
    .build();
```
**CacheInterceptor：** 缓存拦截器，当没有网络连接的时候自动读取缓存中的数据，缓存存放时间默认为3天。</br>
创建缓存对象
```java
//缓存时间
int CACHE_TIMEOUT = 10 * 1024 * 1024
//缓存存放的文件
File httpCacheDirectory = new File(mContext.getCacheDir(), "lx_cache");
//缓存对象
Cache cache = new Cache(httpCacheDirectory, CACHE_TIMEOUT);
```
构建okhttp时加入
```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
    .cache(cache)
    .addInterceptor(new CacheInterceptor(mContext))
    .build();
```
#### 2.3.3、Cookie管理
**mvvm-framework**提供两种CookieStore：**PersistentCookieStore** (SharedPreferences管理)和**MemoryCookieStore** (内存管理)，可以根据自己的业务需求，在构建okhttp时加入相应的cookieJar
```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
    .cookieJar(new CookieJarImpl(new PersistentCookieStore(mContext)))
    .build();
```
或者
```java
OkHttpClient okHttpClient = new OkHttpClient.Builder()
    .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
    .build();
```
#### 2.3.4、绑定生命周期
请求在ViewModel层。默认在BaseActivity中注入了LifecycleProvider对象到ViewModel，用于绑定请求的生命周期，View与请求共存亡。
```java
RetrofitClient.getInstance().create(DemoApiService.class)
    .demoGet()
    .compose(RxUtils.bindToLifecycle(getLifecycleProvider())) // 请求与View周期同步
    .compose(RxUtils.schedulersTransformer())  // 线程调度
    .compose(RxUtils.exceptionTransformer())   // 网络错误的异常转换
    .subscribe(new Consumer<BaseResponse<DemoEntity>>() {
        @Override
        public void accept(BaseResponse<DemoEntity> response) throws Exception {
                       
        }
    }, new Consumer<ResponseThrowable>() {
        @Override
        public void accept(ResponseThrowable throwable) throws Exception {
                        
        }
    });

```
在请求时关键需要加入组合操作符`.compose(RxUtils.bindToLifecycle(getLifecycleProvider()))`<br>
**注意：** 由于BaseActivity/BaseFragment都实现了LifecycleProvider接口，并且默认注入到ViewModel中，所以在调用请求方法时可以直接调用getLifecycleProvider()拿到生命周期接口。如果你没有使用 **mvvm-framework** 里面的BaseActivity或BaseFragment，使用自己定义的Base，那么需要让你自己的Activity继承RxAppCompatActivity、Fragment继承RxFragment才能用`RxUtils.bindToLifecycle(lifecycle)`方法。
#### 2.3.5、网络异常处理
网络异常在网络请求中非常常见，比如请求超时、解析错误、资源不存在、服务器内部错误等，在客户端则需要做相应的处理(当然，你可以把一部分异常甩锅给网络，比如当出现code 500时，提示：请求超时，请检查网络连接，此时偷偷将异常信息发送至后台(手动滑稽))。<br>

在使用Retrofit请求时，加入组合操作符`.compose(RxUtils.exceptionTransformer())`，当发生网络异常时，回调onError(ResponseThrowable)方法，可以拿到异常的code和message，做相应处理。<br>

> mvvm-framework中自定义了一个[ExceptionHandle](./mvvm-framework/src/main/java/com/lx/mvvm-framework/http/ExceptionHandle.java)，已为你完成了大部分网络异常的判断，也可自行根据项目的具体需求调整逻辑。<br>

**注意：** 这里的网络异常code，并非是与服务端协议约定的code。网络异常可以分为两部分，一部分是协议异常，即出现code = 404、500等，属于HttpException，另一部分为请求异常，即出现：连接超时、解析错误、证书验证失等。而与服务端约定的code规则，它不属于网络异常，它是属于一种业务异常。在请求中可以使用RxJava的filter(过滤器)，也可以自定义BaseSubscriber统一处理网络请求的业务逻辑异常。由于每个公司的业务协议不一样，所以具体需要你自己来处理该类异常。
## 3、辅助功能
> 一个完整的快速开发框架，当然也少不了常用的辅助类。下面来介绍一下**mvvm-framework**中有哪些辅助功能。
### 3.1、事件总线
> 事件总线存在的优点想必大家都很清楚了，android自带的广播机制对于组件间的通信而言，使用非常繁琐，通信组件彼此之间的订阅和发布的耦合也比较严重，特别是对于事件的定义，广播机制局限于序列化的类（通过Intent传递），不够灵活。
#### 3.3.1、RxBus
RxBus并不是一个库，而是一种模式。相信大多数开发者都使用过EventBus，对RxBus也是很熟悉。由于**mvvm-framework**中已经加入RxJava，所以采用了RxBus代替EventBus作为事件总线通信，以减少库的依赖。

使用方法：

在ViewModel中重写registerRxBus()方法来注册RxBus，重写removeRxBus()方法来移除RxBus
```java
//订阅者
private Disposable mSubscription;
//注册RxBus
@Override
public void registerRxBus() {
    super.registerRxBus();
    mSubscription = RxBus.getDefault().toObservable(String.class)
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

            }
        });
    //将订阅者加入管理站
    RxSubscriptions.add(mSubscription);
}

//移除RxBus
@Override
public void removeRxBus() {
    super.removeRxBus();
    //将订阅者从管理站中移除
    RxSubscriptions.remove(mSubscription);
}
```
在需要执行回调的地方发送
```java
RxBus.getDefault().post(object);
```
#### 3.3.2、Messenger
Messenger是一个轻量级全局的消息通信工具，在我们的复杂业务中，难免会出现一些交叉的业务，比如ViewModel与ViewModel之间需要有数据交换，这时候可以轻松地使用Messenger发送一个实体或一个空消息，将事件从一个ViewModel回调到另一个ViewModel中。

使用方法：

定义一个静态String类型的字符串token
```java
public static final String TOKEN_LOGINVIEWMODEL_REFRESH = "token_loginviewmodel_refresh";
```
在ViewModel中注册消息监听
```java
//注册一个空消息监听 
//参数1：接受人（上下文）
//参数2：定义的token
//参数3：执行的回调监听
Messenger.getDefault().register(this, LoginViewModel.TOKEN_LOGINVIEWMODEL_REFRESH, new BindingAction() {
    @Override
    public void call() {
	
    }
});

//注册一个带数据回调的消息监听 
//参数1：接受人（上下文）
//参数2：定义的token
//参数3：实体的泛型约束
//参数4：执行的回调监听
Messenger.getDefault().register(this, LoginViewModel.TOKEN_LOGINVIEWMODEL_REFRESH, String.class, new BindingConsumer<String>() {
    @Override
    public void call(String s) {
         
    }
});
```
在需要回调的地方使用token发送消息
```java
//发送一个空消息
//参数1：定义的token
Messenger.getDefault().sendNoMsg(LoginViewModel.TOKEN_LOGINVIEWMODEL_REFRESH);

//发送一个带数据回调消息
//参数1：回调的实体
//参数2：定义的token
Messenger.getDefault().send("refresh",LoginViewModel.TOKEN_LOGINVIEWMODEL_REFRESH);
```
> token最好不要重名，不然可能就会出现逻辑上的bug，为了更好的维护和清晰逻辑，建议以`aa_bb_cc`的格式来定义token。aa：TOKEN，bb：ViewModel的类名，cc：动作名（功能名）。

> 为了避免大量使用Messenger，建议只在ViewModel与ViewModel之间使用，View与ViewModel之间采用ObservableField去监听UI上的逻辑，可在继承了Base的Activity或Fragment中重写initViewObservable()方法来初始化UI的监听


注册了监听，当然也要解除它。在BaseActivity、BaseFragment的onDestroy()方法里已经调用`Messenger.getDefault().unregister(viewModel);`解除注册，所以不用担心忘记解除导致的逻辑错误和内存泄漏。
### 3.2、文件下载
文件下载几乎是每个app必备的功能，图文的下载，软件的升级等都要用到，mvvm-framework使用Retrofit+Okhttp+RxJava+RxBus实现一行代码监听带进度的文件下载。

下载文件
```java
String loadUrl = "你的文件下载路径";
String destFileDir = context.getCacheDir().getPath();  //文件存放的路径
String destFileName = System.currentTimeMillis() + ".apk";//文件存放的名称
DownLoadManager.getInstance().load(loadUrl, new ProgressCallBack<ResponseBody>(destFileDir, destFileName) {
    @Override
    public void onStart() {
        //RxJava的onStart()
    }

    @Override
    public void onCompleted() {
        //RxJava的onCompleted()
    }

    @Override
    public void onSuccess(ResponseBody responseBody) {
        //下载成功的回调
    }

    @Override
    public void progress(final long progress, final long total) {
        //下载中的回调 progress：当前进度 ，total：文件总大小
    }

    @Override
    public void onError(Throwable e) {
        //下载错误回调
    }
});
```
> 在ProgressResponseBody中使用了RxBus，发送下载进度信息到ProgressCallBack中，继承ProgressCallBack就可以监听到下载状态。回调方法全部执行在主线程，方便UI的更新，详情请参考例子程序。
### 3.3、ContainerActivity
一个盛装Fragment的一个容器(代理)Activity，普通界面只需要编写Fragment，使用此Activity盛装，这样就不需要每个界面都在AndroidManifest中注册一遍

使用方法：

在ViewModel中调用BaseViewModel的方法开一个Fragment
```java
startContainerActivity(你的Fragment类名.class.getCanonicalName())
```
在ViewModel中调用BaseViewModel的方法，携带一个序列化实体打开一个Fragment
```java
Bundle mBundle = new Bundle();
mBundle.putParcelable("entity", entity);
startContainerActivity(你的Fragment类名.class.getCanonicalName(), mBundle);
```
在你的Fragment中取出实体
```java
Bundle mBundle = getArguments();
if (mBundle != null) {
    entity = mBundle.getParcelable("entity");
}
```
### 3.4、6.0权限申请
> 对RxPermissions已经熟悉的朋友可以跳过。

使用方法：

例如请求相机权限，在Activity,Fragment,ViewModel中调用
```java
//请求打开相机和读写内存卡权限
requestPermissions(new IPermission() {
                                   @Override
                                   public void onGranted() {
                                       showToast("相机权限已经打开，直接跳入相机");
                                   }

                                   @Override
                                   public void onDenied(boolean denied) {
                                       showToast(denied ? "Permission denied, can't enable the camera" : "Denied permission without ask never again");
                                   }
                               }, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE));
```
更多权限申请方式请参考[RxPermissions原项目地址](https://github.com/tbruyelle/RxPermissions)
### 3.5、图片压缩
> 为了节约用户流量和加快图片上传的速度，某些场景将图片在本地压缩后再传给后台，所以特此提供一个图片压缩的辅助功能。

使用方法：

RxJava的方式压缩单张图片，得到一个压缩后的图片文件对象
```java
String filePath = "mnt/sdcard/1.png";
ImageUtils.compressWithRx(filePath, new Consumer<File>() {
    @Override
    public void accept(File file) throws Exception {
        //将文件放入RequestBody
        ...
    }
});
```
RxJava的方式压缩多张图片，按集合顺序每压缩成功一张，都将在onNext方法中得到一个压缩后的图片文件对象
```java
List<String> filePaths = new ArrayList<>();
filePaths.add("mnt/sdcard/1.png");
filePaths.add("mnt/sdcard/2.png");
ImageUtils.compressWithRx(filePaths, new Subscriber() {
    @Override
    public void onCompleted() {
	
    }
	
    @Override
    public void onError(Throwable e) {
	
    }
	
    @Override
    public void onNext(File file) {

    }
});
```
### 3.7、版本更新 （一行代码解决）
```java
        XUpdate.newInstance().update(apkUrl, updateTitle, updateContent, isForce,
                R.layout.custom_view_layout, R.mipmap.ic_launcher, null, null,
                (view, updateConfig, uiConfig) ->
                        {
                        //TODO  对自定义更新view的特殊操作
                        };
```
### 3.8、屏幕适配(框架已适配刘海屏)
请在布局（xml）文件里面使用
```java
android:layout_marginRight="@dimen/base20dp"
...
```
### 3.9、其他辅助类
**ToastUtils：** 吐司工具类

**SPUtils：** SharedPreferences工具类

**SDCardUtils：** SD卡相关工具类

**ConvertUtils：** 转换相关工具类

**StringUtils：** 字符串相关工具类

**RegexUtils：** 正则相关工具类

**KLog：** 日志打印，含json格式打印

## 4、附加

### 4.1、编译错误解决方法
> 使用databinding其实有个缺点，就是会遇到一些编译错误，而AS不能很好的定位到错误的位置，这对于刚开始使用databinding的开发者来说是一个比较郁闷的事。那么我在此把我自己在开发中遇到的各种编译问题的解决方法分享给大家，希望这对你会有所帮助。

##### 4.1.1、绑定错误
绑定错误是一个很常见的错误，基本都会犯。比如TextView的 `android:text=""` ，本来要绑定的是一个String类型，结果你不小心，可能绑了一个Boolean上去，或者变量名写错了，这时候编辑器不会报红错，而是在点编译运行的时候，在AS的Messages中会出现错误提示，如下图：

<img src="./img/error1.png" width="640" hegiht="640" align=center />

解决方法：把错误提示拉到最下面 (上面的提示找不到BR类这个不要管它)，看最后一个错误 ，这里会提示是哪个xml出了错，并且会定位到行数，按照提示找到对应位置，即可解决该编译错误的问题。

**注意：** 行数要+1，意思是上面报出第33行错误，实际是第34行错误，AS定位的不准确 (这可能是它的一个bug)

##### 4.1.2、xml导包错误
在xml中需要导入ViewModel或者一些业务相关的类，假如在xml中导错了类，那一行则会报红，但是res/layout却没有错误提示，有一种场景，非常特殊，不容易找出错误位置。就是你写了一个xml，导入了一个类，比如XXXUtils，后来因为业务需求，把那个XXXUtils删了，这时候res/layout下不会出现任何错误，而你在编译运行的时候，才会出现错误日志。苦逼的是，不会像上面那样提示哪一个xml文件，哪一行出错了，最后一个错误只是一大片的报错报告。如下图：

<img src="./img/error2.png" width="640" hegiht="640" align=center />

解决方法：同样找到最后一个错误提示，找到Cannot resolve type for **xxx**这一句 (xxx是类名)，然后使用全局搜索 (Ctrl+H) ，搜索哪个xml引用了这个类，跟踪点击进去，在xml就会出现一个红错，看到错误你就会明白了，这样就可解决该编译错误的问题。

##### 4.1.3、build错误
构建多module工程时，如出现【4.1.1、绑定错误】，且你能确定这个绑定是没有问题的，经过修改后出现下图错误：

<img src="./img/error3.png" width="640" hegiht="640" align=center />

解决方法：
这种是databinding比较大的坑，清理、重构和删build都不起作用，网上很难找到方法。经过试验，解决办法是手动创建异常中提到的文件夹，或者拷贝上一个没有报错的版本中对应的文件夹，可以解决这个异常

##### 4.1.4、自动生成类错误
有时候在写完xml时，databinding没有自动生成对应的Binding类及属性。比如新建了一个activity_login.xml，按照databinding的写法加入```<layout> <variable>```后，理论上会自动对应生成ActivityLoginBinding.java类和variable的属性，可能是as对databding的支持还不够吧，有时候偏偏就不生成，导致BR.xxx报红等一些莫名的错误。

解决方法：其实确保自己的写法没有问题，是可以直接运行的，报红不一定是你写的有问题，也有可能是编译器抽风了。或者使用下面的办法</br>
第一招：Build->Clean Project；</br>第二招：Build->Rebuild Project；</br>第三招：重启大法。

##### 4.1.5、gradle错误
如果遇到以下编译问题：

错误: 无法将类 BindingRecyclerViewAdapters中的方法 setAdapter应用到给定类型;
需要: RecyclerView,ItemBinding,List,BindingRecyclerViewAdapter,ItemIds<? super T>,ViewHolderFactory
找到: RecyclerView,ItemBinding,ObservableList,BindingRecyclerViewAdapter<CAP#1>,ItemIds,ViewHolderFactory
原因: 推断类型不符合等式约束条件
推断: CAP#1
等式约束条件: CAP#1,NetWorkItemViewModel
其中, T是类型变量:
T扩展已在方法 setAdapter(RecyclerView,ItemBinding,List,BindingRecyclerViewAdapter,ItemIds<? super T>,ViewHolderFactory)中声明的Object
其中, CAP#1是新类型变量:
CAP#1从?的捕获扩展Object

一般是由于gradle plugin版本3.5.1造成的，请换成gradle plugin 3.5.0以下版本

## 混淆
例子程序中给出了最新的【mvvm-framework混淆规则】，包含mvvm-framework中依赖的所有第三方library，可以将规则直接拷贝到自己app的混淆规则中。在此基础上你只需要关注自己业务代码以及自己引入第三方的混淆，【mvvm-framework混淆规则】请参考app目录下的[proguard-rules.pro](./app/proguard-rules.pro)文件。

## About
**qqlixiong：** 本人喜欢尝试新的技术，以后发现有好用的东西，我将会在企业项目中实战，没有问题了就会把它引入到**mvvm-framework**中，一直维护着这套框架，谢谢各位朋友的支持。如果觉得这套框架不错的话，麻烦点个 **star**，你的支持则是我前进的动力！

## License

    Copyright 2020 qqlixiong
 
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
        http://www.apache.org/licenses/LICENSE-2.0
 
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.