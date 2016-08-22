import java.io.IOException;

import org.webdriver.patatiumwebui.pageObject.HomePage;
import org.webdriver.patatiumwebui.utils.Assertion;
import org.webdriver.patatiumwebui.utils.ElementAction;
import org.webdriver.patatiumwebui.action.CommonAction;
import org.webdriver.patatiumwebui.config.Config;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import org.webdriver.patatiumwebui.utils.TestBaseCase;
import org.webdriver.patatiumwebui.utils.Log;
public class HomePageTest extends TestBaseCase{
	Log log=new Log(HomePage.class);
	ElementAction action=new ElementAction();
	HomePage homePage=new HomePage();
	@BeforeClass()
	@Parameters({"Base_Url","UserName","PassWord"})
	public void beforetest(String Base_Url,String UserName,String PassWord) throws IOException
	{
		log.info("输入用户名密码进入后台主页");
		CommonAction.Login(Base_Url+"/login/login", UserName, PassWord);
		action.sleep(5);
	}
	@Test(description="检查预约挂号菜单",priority=1)
	public void checkAppiont_memue() throws IOException
	{
		action.sleep(5);
		action.click(homePage.appiont_memue());
		action.sleep(3);
		Assertion.VerityTextPresent("按医院挂号","点击预约挂号菜单是否成功进入预约挂号页面");
		Assertion.VerityError();

	}

	@Test(description="检查账号信息管理菜单",priority=2)
	public void checkAccount_management() throws IOException
	{
		action.click(homePage.account_management());
		action.sleep(3);
		Assertion.VerityTextPresent("账号注册","点击账号信息管理菜单是否成功进入账号注册页面");
		Assertion.VerityError();
	}


	@Test(description="检查预约明细菜单",priority=3)
	public void checkViewAppointmentDetail() throws IOException
	{
		action.click(homePage.AppointmentDetail());
		action.sleep(3);
		Assertion.VerityTextPresent("预约渠道","点击预约明细菜单是否成功进入预约明细页面");
		Assertion.VerityError();
	}

	@Test(description="检查咨询统计菜单",priority=4)
	public void checkQueryConsultation() throws IOException
	{
		action.click(homePage.queryConsultation());
		action.sleep(3);
		Assertion.VerityTextPresent("测试失败用例","点击咨询统计菜单是否成功进入咨询统计页面");
		Assertion.VerityError();
	}

	@Test(description="检查客服工作统计菜单面",priority=5)
	public void checkQueryCustomerWork() throws IOException
	{
		action.click(homePage.queryCustomerWork());
		action.sleep(3);
		Assertion.VerityTextPresent("注册总数","点击客服工作统计菜单是否成功进入客服工作统计页面");
		Assertion.VerityError();
	}

	@Test(description="检查短信查询菜单",priority=6)
	public void checkQueryMessagePage() throws IOException
	{
		action.click(homePage.queryMessagePage());
		action.sleep(3);
		Assertion.VerityTextPresent("发送时间","点击短信查询统计菜单是否成功进入短信查询页面");
		Assertion.VerityError();
	}




}
