import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.webdriver.patatiumwebui.action.CommonAction;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.webdriver.patatiumwebui.pageObject.HomePage;
import org.webdriver.patatiumwebui.pageObject.LoginPage;
import org.webdriver.patatiumwebui.utils.Assertion;
import org.webdriver.patatiumwebui.utils.ElementAction;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import org.webdriver.patatiumwebui.utils.TestBaseCase;

import com.thoughtworks.selenium.webdriven.commands.Click;

public class LoginTest extends TestBaseCase {
	ElementAction action=new ElementAction();
	@Test(description="检查是否成功进入登录页面",priority=1)
	@Parameters({"Base_Url"})
	public void checkLoginPage(String Base_Url)
	{
		//LoginPage loginPage=new LoginPage();
		//loginPage.open(Base_Url+"/login/login");
		driver.get(Base_Url+"/login/login");
		Assertion.VerityTextPresent("华康移动医疗");
	}
	@Test(description="登录功能测试",priority=2)
	@Parameters({"Base_Url"})
	public void login(String Base_Url) throws IOException
	{
		log.info("输入空用户名和非空密码");
		//CommonAction.Login("", "123456");
		CommonAction.Login(Base_Url+"/login/login", "", "123456");
		Assertion.VerityTextPresentPrecision("用户名不能为空", "输入空用户名和非空密码，验证是否出现用户名不能为空的提示");
		log.info("输入非空用户名和空密码");
		CommonAction.Login(Base_Url+"/login/login","zhengshuheng", "");
		Assertion.VerityTextPresentPrecision("密码不能为空", "输入非空用户名和空密码，验证是否出现密码不能为空的提示");
		log.info("输入错误的用户名和密码");
		CommonAction.Login(Base_Url+"/login/login","zhengshuheng", "123");
		action.sleep(5);
		Assertion.VerityTextPresentPrecision("用户名或者密码输入错误", "输入错误的用户名和密码，验证是否弹出用户名或者账号错误的提示");
		//action.alertConfirm();
		log.info("输入正确的用户名和密码");
		CommonAction.Login(Base_Url+"/login/login","hljadmin", "111111");
		//action.waitForLoad(driver, 5);
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		action.sleep(5);
		log.info("验证是成功登录，进入系统主页");
		//Assertion.VerityURL(driver, "http://192.168.0.21:8086/Index/Home");
		Assertion.VerityTextPresentPrecision("欢迎你，hljadmin","输入正确的用户名和密码，验证是否成功进入主页");
		log.info("验证用例是否抛出异常，用于判断用例是否失败");
		Assertion.VerityError();
	}
	@Test(description="退出登录功能测试",priority=3)
	public void layout () throws IOException
	{
		HomePage homePage=new HomePage();
		log.info("点击退出登录按钮");
		action.click(homePage.layout());
		log.info("验证是否退出登录，返回到登录页面");
		Assertion.VerityTextPresent("华康移动医疗","点击退出登录按钮，验证是否退出到登录页面");
		//验证用例是否抛出异常
		Assertion.VerityError();
	}

	public static void main(String args[])
	{
		ElementAction action=new ElementAction();
		//String Base_Url="http://hljkfht.hk515.net";
		WebDriver driver=new FirefoxDriver();
		driver.get("http://hljkfht.hk515.net/login/login");
		WebElement webElement=driver.findElement(By.id("userName"));
		webElement.sendKeys("zhengshuheng");
		WebElement webElement2=driver.findElement(By.id("password"));
		webElement2.sendKeys("123");
		WebElement webElement3=driver.findElement(By.xpath("//*[text()=\"用户名或者帐号错误\"]"));
		//action.waitForLoad(driver, 5);
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		action.sleep(5);
		// log.info("验证是成功登录，进入系统主页");
		//Assertion.VerityURL(driver, "http://192.168.0.21:8086/Index/Home");
		Assertion.VerityTextPresentPrecision("欢迎使用华康全景运营后台","验证是否成功进入主页");
		//log.info("验证用例是否抛出异常，用于判断用例是否失败");
		System.out.println(Assertion.errors.size());
		Assertion.VerityError();
	}

}
