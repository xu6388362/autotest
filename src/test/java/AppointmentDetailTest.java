import java.io.IOException;
import java.util.List;

import org.webdriver.patatiumwebui.action.CommonAction;
import org.webdriver.patatiumwebui.pageObject.AppointmentDetailPage;
import org.webdriver.patatiumwebui.pageObject.HomePage;
import org.webdriver.patatiumwebui.pageObject.OmsHomePage;
import org.webdriver.patatiumwebui.utils.Assertion;
import org.webdriver.patatiumwebui.utils.ElementAction;
import org.webdriver.patatiumwebui.utils.ExcelReadUtil;
import org.webdriver.patatiumwebui.utils.Log;
import org.webdriver.patatiumwebui.utils.TableElement;
import org.webdriver.patatiumwebui.utils.TestBaseCase;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class AppointmentDetailTest extends TestBaseCase {

	HomePage homePage=new HomePage();
	AppointmentDetailPage appointmentDetailPage=new AppointmentDetailPage();
	Log log=new Log(AppointmentTest.class);
	ElementAction action=new ElementAction();
	@DataProvider(name="telphone")
	public Object[][] appointmentTelphone() {
		String filePath="src/main/resources/data/new_account.xls";
		return ExcelReadUtil.case_data_excel(0, 1, 2, 0,0,filePath);
	}

	@BeforeClass
	@Parameters({"Base_Url","UserName","PassWord"})
	public void beforeclass(String Base_Url,String UserName,String PassWord) throws IOException
	{
		log.info("输入用户名密码进入后台主页");
		driver.manage().deleteAllCookies();
		CommonAction.Login(Base_Url+"/login/login", UserName, PassWord);
		action.sleep(10);
	}
	@Test(description="取消订单",dataProvider="telphone",priority=1)
	public void dismissAppiont(String appointmentTelphone) throws IOException, InterruptedException
	{

		action.sleep(5);
		log.info("点击预约明细");
		action.click(homePage.AppointmentDetail());
		action.sleep(4);
		log.info("输入电话号码");
		action.clear(appointmentDetailPage.telephone());
		action.type(appointmentDetailPage.telephone(), appointmentTelphone);
		log.info("点击查询按钮");
		action.click(appointmentDetailPage.query_button());
		action.sleep(6);
		List<WebElement> webElements=action.findElements(appointmentDetailPage.dismissAppiont());
		Assertion.VerityNotString(Integer.toString(0), Integer.toString(webElements.size()), "验证该手机号码是否有待就诊订单");
		int  count=webElements.size();
		log.info("带取消订单数"+count);
		log.info("点击取消订单");
		for (int i = 0; i < count; i++) {
			log.info("点击预约明细菜单");
			action.click(homePage.AppointmentDetail());
			action.sleep(4);
			log.info("输入电话号码");
			action.clear(appointmentDetailPage.telephone());
			action.type(appointmentDetailPage.telephone(), appointmentTelphone);
			log.info("点击查询按钮");
			action.click(appointmentDetailPage.query_button());
			action.sleep(5);
			log.info("点击第"+i+"待就诊订单取消订单按钮");
			action.executeJS("$(\"a[class='dscancle']\").eq("
					+ "0"
					+ ").click()");
			action.sleep(5);
			action.click(appointmentDetailPage.dismissAppiontconfirm());
			log.info("取消第"+i+"个订单成功！");

		}
		Assertion.VerityBoolean(action.isElementsPresent(appointmentDetailPage.dismissAppiont(), 5), false, "验证是否把所有订单取消完");
		Assertion.VerityError();
	}

	@AfterClass()
	public void afterClass() throws IOException
	{

		String filePath="src/mian/resources/data/new_account.xls";
		Object[][] telphones=ExcelReadUtil.case_data_excel(0, 1, 2, 0,0,filePath);
		log.info("进入运营管理后台登录页面");
		driver.get("http://yunying.hk515.com/User/Login");
		OmsHomePage omsHomePage=new OmsHomePage();
		log.info("登录运营后台");
		action.type(omsHomePage.username(), "zhengshuheng");
		action.type(omsHomePage.password(),"zhengshuheng");
		action.click(omsHomePage.loginbutton());
		action.sleep(8);
		log.info("点击会员管理");
		action.click(omsHomePage.vip_management()) ;
		action.sleep(5);
		for (int i = 0; i < telphones.length; i++) {
			log.info("点击账户管理");
			action.click(omsHomePage.account_management());
			action.sleep(5);
			log.info("输入账号注册手机号码");
			action.clear(omsHomePage.account_MobilePhone());
			String telphone=telphones[i][0].toString();
			action.type(omsHomePage.account_MobilePhone(), telphone);
			log.info("点击查询按钮");
			action.click(omsHomePage.account_query());
			action.sleep(5);
			WebElement query_result_table=action.findElement(omsHomePage.account_query_table());
			TableElement querTableElement=new TableElement(query_result_table);
			String result_xpath=querTableElement.getCellXpath(2, 6);
			String delete_user_xpath="("+result_xpath+"/a)[5]";
			System.out.println(delete_user_xpath);
			action.sleep(5);
			try {
				log.info("点击注销按钮");
				action.click(omsHomePage.delete_account_button());
				action.sleep(4);
				log.info("点击确认按钮");
				action.click(omsHomePage.delete_account_confirm_button());
				action.sleep(2);
				Assertion.VerityTextPresent("账户注销成功！ ", "验证是否注销"
						+ telphone
						+ "账号成功");
				//Assertion.VerityError();
				log.info("点击确定按钮");
				action.click(omsHomePage.delete_account_success_button());
				action.sleep(4);
			} catch (NoSuchElementException e) {
				// TODO: handle exception
				log.info("该账号已注销,继续下一个账号注销");
				continue;
			}
		}
	}
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}

}
