#PatatiumWebUi
 **该web自动化测试框架是用java语言编写的，基于selenium webdriver 开源自动化测试工具编写的，结合了testng 等工具。该框架实现了关键字驱动技术，无需掌握多少编程知识即可编写脚本，同时实现了数据与代码分离的功能：1、元素定位信息保存在对象库文件中 2、测试用例数据可以存储在excel中。从而实现，页面元素位置变化，无需改动脚本，只需修改对应的元素定位信息即可。
目前框架还不是特别完善，还需要写一些脚本实现自动化；学习该框架需要熟悉一定的HTML 和java基础，后续可以考虑自动编码的实现。** 

首先给大家展示一下用该框架编写的一个简单的自动化用例脚本。
@Test(description=’主页--检查医院管理菜单’)
Public void checkHospiatalMenue()
{  
    ElementAction action=new ElementAction();  ---创建元素操作对象
    HomePage homePage=new HomePage();---创建主页对象
    action.click(homePage.HospitalMenue());---单击主页下的医院管理菜单
    action.sleep(2);---暂停2秒
    Assertion.verityTextPresent(“医院基本信息”，“点击医院信息管理菜单，检查是否进入医院管理页面”)；--设置检查点
    Assertion.verityError();
}

下面给大家简单讲解下，该框架的使用。（使用该框架之前首先要做的是环境搭建，环境搭建比较简单，在此就不介绍了）

第一步：创建XML对象库（编写xml对象库文件）
<?xml version="1.0" encoding="UTF-8"?>
<map>	
	<page pagename="net.hk515.PageObject.LoginPage"value="http://192.168.0.21:8086/User/Login" desc="华康运营后台登录页面">
		<locator type="id" timeout="3" value="userName"  desc="用户名">userName</locator>
		<locator type="id" timeout="3" value="password"  desc="密码">password</locator>
		<locator type="id" timeout="3" value="loginButton"  desc="登录">loginButton</locator>
	</page>
</map>
说明：
1、<map>标签是整个对象库文件的根目录，管理整个项目的对象。
2、<page>标签管理一个页面的元素（webelement：input,select,textare,a,li等标签）。一个page包含多个locator对象
3、<locator> 标签管理一个元素对象的信息。一个locator对象包含 定位方式(type),元素加载等待时间（timeout,秒），元素定位信息（value）,元素对象描述（desc）,元素对象名称（locator标签的文本值）
4、<locator>标签对象属性详解：
 Type：定位方式，包含id,name,class,linktext,xpath,css等，定位元素的时候灵活使用，一般可以统一用xpath 代替id,name,class，linktext的定位方式
 Timeout：元素加载时间，有些页面元素，可能要等待一段时间才能加载过来，为了查找元素的稳定性，需加等待时间。
 Value:元素定位信息，如果是id,name,class，linktext直接把网页元素对应的这些属性值写上即可，如果是xpath定位方式，需要填写正确的xpath语法格式(后续给大家详解)
 Desc:元素的描述，元素的中文描述信息
 5、<page>标签对象属性详解：
Pagename:page对象名字，格式：net.hk515.PageObject.xxxPage;最后面那位才是真正的页面名字，前面的是java对象库路径；另外注意，页面名字是头个单词大写；例如主页：名字定义为 net.hk515.PageObject.HomePage
Value：页面对象的URL，可不填
Desc:页面对象中文描述


第二步：运行PageObjectAutoCodeForJar类，把xml对象库转化为java文件对象库


第三步：编写测试脚本。具体步骤如下
  1、在net.hk515.Test包目录下，创建测试类XXXTest，一般以模块划分，比如：登录测试：LoginTest.java
主页测试：HomePageTest.java  账户管理测试：AccountMangerTest.java
  2、编写测试代码
 
结构如下：
   Public HomePageTest extends TestBaseCase  ---测试类名
   {
      HomePage  homePage =new HomePage();---创建HomPage对象，用于后面调用HomePage对象库的元素信息
      ElementAction action =new ElmentAction();---创建操作页面元素的对象，用于后面调用操作元素的方法
      @BeforeClass()---运行本类用例之前必须执行的方法
	  @Parameters({"Base_Url","UserName","PassWord"})---读取testng.xml配置的项目地址，用户名和密码
	  public void beforetest(String Base_Url,String UserName,String PassWord)
	 {
		log.info("输入用户名密码进入后台主页");	---控制台日志输出	
		CommonAction.Login(Base_Url+"/login/login", UserName, PassWord);---调用公共登录方法
		action.sleep(5);---等待暂停操作3秒
	 }
    @Test(description="主页--预约挂号菜单",priority=1)---用例描述
	public void checkAppiont_memue()---用例方法名
	{
		action.click(homePage.appiont_memue());---单击主页面的预约挂号菜单（appiont_memue）
		action.sleep(3);---等待暂停操作3秒
		Assertion.VerityTextPresent("按医院挂号","点击预约挂号菜单是否成功进入预约挂号页面");---设置检查点，检查单击预约挂号菜单后，页面是否出现按医院挂号文字。如果页面报错，无法访问，那么就找不到该文字，用例就会标记为失败(failed)
		Assertion.VerityError();---判断用例是否有错误的检查点，包含1个以上的错误检查点，用例就标记为失败(failed)
		
	}
   }

第四步：配置用例执行顺序testng.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd">
<suite name="Suite" parallel="none">
	<parameter name="driver" value="FirefoxDriver"></parameter>---设置浏览器类型
	<parameter name="nodeURL" value=""></parameter>---设置运行浏览器的机器IP地址
    //项目URL
	<parameter name="Base_Url" value="http://shenzhen.call.hk515.com/login/login"></parameter>
	<parameter name="UserName" value="hljadmin"></parameter>--项目登录的用户名
	<parameter name="PassWord" value="111111"></parameter>---项目登录的密码
	<listeners>---监听器，固定填写
        <listener class-name="net.hk515.utils.TestListener"></listener>
        <listener class-name="net.hk515.utils.TestReport"></listener>
    </listeners>
   <test name="账号注册/查询/编辑/注销模块类">--测试集名字
    <classes>
      <class name="net.hk515.Test.AppointmentTest">---测试类名
      	     <methods preserve-order="true">
      	  <include name="checkAppiont_memue" desc="检查预约挂号菜单"/>--测试类方法，检查预约挂号菜单
            </methods>
       </class>
    </classes>
  </test> <!-- Test -->  
</suite> <!-- Suite -->

第五步：运行testng.xml,执行用例

第六步：查看运行结果


用例运行错误自动截图




Xpath 详解：
注：可通过火狐浏览器安装,firebug,firepath插件校验xpath的正确性
先举个xpah例子://div[@id=’abc’]/form/div/input/span
//：从匹配选择的当前节点，选择文档中的节点，不考虑它的具体位置，例如：//div[@name=‘abc’]
查找页面中name属性为abc的div标签
/：从根节点选取元素，例如：/html/body/div[@id='myModalex'] 
可以是文档最根节点开始查找元素，也可以是配陪得节点为根节点往下找
例如：//*[@id='loginForm']/div[1]/label
@：@表示属性 属性可以用and,or运算符
例如：//label[@class='col-sm-2 control-label' and @for='userName'] 在定位中，如果一个属性还不能精确定位某个元素那么则可以再组合增加一个元素，使定位达到唯一性
Text（）：通过元素的文本值查找元素，例：//h2[text()='华康移动医疗客服后台']
Contains();//input[contains(@id,'nt')] 模糊匹配，查找id包含nt的input标签
//h2[contains(text(),'华康移动医疗客服后台')] 查找文本值包含华康移动医疗客服后台的元素
//灵活使用案例：
查找元素<span class=”cde”>八佰伴</span>
<span class=”cde”>嘎嘎嘎</span>
<div id=”abc”>
   <form>
            <div>
                   <input>
                      <span class=”cde”>八佰伴</span>
                   </input>
            </div>
   </form>
<div>
分析：该元素，没有唯一性的id，name等标签，并且层级多，上一级也没有唯一性的东西，只能从上上上级开始查找元素。但是从上上级查找元素，xpath的层级多，定位信息复杂，那么有没有办法优化精简呢？答案是肯定的，利用//可以大幅优化精简xpath表达式
方案一：//div[@id=’abc’]/form/div/input/span
方案二：//*[@id=’abc’]/form/div/input/span[@class=’cde’]
方案三：//span[@class=’cde’][2]
方案四：//div[@id=’abc’]//span[@class=’cde’]--此方法最简洁，结构也最清晰，也最稳定

综上xpath定位原则，元素id,name属性优先使用，其次是class等其他，1、在当前节点没有id,name等属性确定元素唯一性的时候，往上找，通过当前节点父亲，祖父，祖父的父亲，祖父的祖父等节点查找当前元素。2、一个元素属性不足够定位当前元素的时候，可以通过and运算符，组合属性来定位使之达到唯一性，尽可能的缩短xpath层级，使xpath定位更稳定。

Firebug使用：
例：
定位用户名输入框可以用//*[@id=’userName’]表示查找当前页面下，id属性为’userName’的所有元素，相当于id定位方式。*代表所有元素。