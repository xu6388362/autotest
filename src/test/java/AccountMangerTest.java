import java.io.IOException;

import org.webdriver.patatiumwebui.action.CommonAction;
import org.webdriver.patatiumwebui.pageObject.AccountMangerPage;
import org.webdriver.patatiumwebui.pageObject.HomePage;
import org.webdriver.patatiumwebui.utils.Assertion;
import org.webdriver.patatiumwebui.utils.ElementAction;
import org.webdriver.patatiumwebui.utils.ExcelReadUtil;
import org.webdriver.patatiumwebui.utils.TableElement;
import org.webdriver.patatiumwebui.utils.Log;
import org.webdriver.patatiumwebui.utils.TestBaseCase;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class AccountMangerTest extends TestBaseCase {

	Log log=new Log(AccountMangerTest.class);
	HomePage homePage=new HomePage();
	AccountMangerPage accountMangerPage=new AccountMangerPage();
	ElementAction action=new ElementAction();
	@BeforeClass
	@Parameters({"Base_Url","UserName","PassWord"})
	public void beforeclass(String Base_Url,String UserName,String PassWord) throws IOException
	{
		log.info("输入用户名密码进入后台主页");
		CommonAction.Login(Base_Url+"/login/login", UserName, PassWord);
		action.sleep(5);
	}
	/**
	 * 账号注册数据源
	 * @return
	 */
	@DataProvider(name="new_account")
	public Object[][] new_account()
	{
		String filePath="src/main/resources/data/new_account.xls";
		return ExcelReadUtil.case_data_excel(0, 1, 1, 0, 3,filePath);
	}
	@DataProvider(name="new_accountForTel")
	public Object[][] new_accountForTel()
	{
		String filePath="src/main/resources/data/new_account.xls";
		return ExcelReadUtil.case_data_excel(0, 1, 1, 0,0,filePath);
	}
	/**
	 * 账号查询数据源
	 * @return
	 */
	@DataProvider(name="query_account_forTel")
	public Object[][] query_account_forTel()
	{    String filePath="src/main/resources/data/new_account.xls";
		return ExcelReadUtil.case_data_excel(0, 1, 1, 0, 0,filePath);
	}
	@DataProvider(name="query_account_forName")
	public Object[][] query_account_forName()
	{    String filePath="src/main/resources/data/new_account.xls";
		return ExcelReadUtil.case_data_excel(0, 1, 1, 1, 1,filePath);
	}
	@DataProvider(name="query_account_forIdentityCard")
	public Object[][] query_account_forIdentityCard()
	{
		String filePath="src/main/resources/data/new_account.xls";
		return ExcelReadUtil.case_data_excel(0, 1, 1, 3, 3,filePath);
	}
	@Test(description="检查账号注册新增用户功能" ,priority=1,dataProvider="new_account")
	public void regster(String register_tel,String register_patientName,
						String register_identityCardType,String register_identityCardNumber ) throws IOException
	{
		log.info("点击账号信息管理菜单");
		action.click(homePage.account_management());
		action.sleep(3);
		log.info("点击注册按钮");
		action.click(accountMangerPage.registerButton());
		log.info("清除电话号码");
		action.clear(accountMangerPage.register_tel());
		log.info("输入电话号码");
		action.type(accountMangerPage.register_tel(), register_tel);
		log.info("清除姓名");
		action.clear(accountMangerPage.register_patientName());
		log.info("输入姓名");
		action.type(accountMangerPage.register_patientName(), register_patientName);
		log.info("选择身份证");
		action.selectByText(accountMangerPage.register_identityCardType(), register_identityCardType);
		log.info("清除身份证号码");
		action.clear(accountMangerPage.register_identityCardNumber());
		log.info("输入身份证号码");
		action.type(accountMangerPage.register_identityCardNumber(), register_identityCardNumber);
		log.info("点击保存按钮");
		action.click(accountMangerPage.register_save());
		log.info("验证是否弹出保存成功按钮");
		Assertion.VerityTextPresent("保存成功","验证是否成功注册用户");
		log.info("点击确认按钮");
		action.click(accountMangerPage.register_confirm());
		//UserTable userTable=new UserTable();
		//Assertion.VerityBoolean(userTable.CheckSelectUserForMoblePhoneResult(register_tel), true, "检查数据库是否存在注册的用户");
		Assertion.VerityError();
	}
	@Test(description="账号信息管理-按主账号姓名查询",priority=2,dataProvider="query_account_forName")
	public void queryForName(String query_account_forName ) throws IOException
	{
		log.info("点击账号信息管理菜单");
		action.click(homePage.account_management());
		action.sleep(3);
		log.info("输入主账号密码");
		action.clear(accountMangerPage.queryForName());
		action.type(accountMangerPage.queryForName(), query_account_forName);
		log.info("点击查询按钮");
		action.click(accountMangerPage.queryButton());
		log.info("检查查询结果是否正确");
		WebElement queryResultElement=action.findElement(accountMangerPage.queryResultTable());
		//创建表格操作对象
		TableElement queryResultTableActionElement=new TableElement(queryResultElement);
		action.sleep(5);
		String result=queryResultTableActionElement.getCellText(2, 1);
		System.out.println(result);
		Assertion.VerityString(result,query_account_forName,"验证主账号姓名查询是否正确");
		Assertion.VerityError();
	}
	@Test(description="账号信息管理-按主账号手机号码查询",priority=3,dataProvider="query_account_forTel")
	public void queryForMobelePhone(String  query_account_forTel) throws IOException
	{
		log.info("点击账号信息管理菜单");
		action.click(homePage.account_management());
		action.sleep(3);
		log.info("输入主账号手机");
		action.clear(accountMangerPage.queryForMobilePhone());
		action.type(accountMangerPage.queryForMobilePhone(), query_account_forTel);
		log.info("点击查询按钮");
		action.click(accountMangerPage.queryButton());
		log.info("检查查询结果是否正确");
		WebElement queryResultElement=action.findElement(accountMangerPage.queryResultTable());
		//创建表格操作对象
		TableElement queryResultTableActionElement=new TableElement(queryResultElement);
		action.sleep(5);
		//第1行第四列(不包含标题)
		String result=queryResultTableActionElement.getCellText(2, 4);
		System.out.println(result);
		Assertion.VerityString(result, query_account_forTel,"验证手机号码查询是否正确");
		Assertion.VerityError();
	}

	@Test(description="账号信息管理-按主身份证号码查询",priority=4,dataProvider="query_account_forIdentityCard")
	public void queryForIdentityCard(String query_account_forIdentityCard ) throws IOException
	{
		log.info("点击账号信息管理菜单");
		action.click(homePage.account_management());
		action.sleep(3);
		log.info("输入身份证号码");
		action.clear(accountMangerPage.queryForIdentityCard());
		action.type(accountMangerPage.queryForIdentityCard(),query_account_forIdentityCard);
		log.info("点击查询按钮");
		action.click(accountMangerPage.queryButton());
		log.info("检查查询结果是否正确");
		WebElement queryResultElement=action.findElement(accountMangerPage.queryResultTable());
		//创建表格操作对象
		TableElement queryResultTableActionElement=new TableElement(queryResultElement);
		action.sleep(5);
		//第二行第6列
		String result=queryResultTableActionElement.getCellText(2, 6);
		System.out.println(result);
		Assertion.VerityString(result,query_account_forIdentityCard,"验证主账号身份证查询是否正确");
		Assertion.VerityError();
	}
	@Test(description="账号信息管理--编辑账号信息" ,priority=5,dataProvider="query_account_forTel")
	public void user_edit(String query_account_forTel ) throws IOException
	{
		log.info("点击账号信息管理菜单");
		action.click(homePage.account_management());
		action.sleep(3);
		log.info("输入电话号码");
		action.clear(accountMangerPage.queryForMobilePhone());
		action.type(accountMangerPage.queryForMobilePhone(),query_account_forTel);
		log.info("点击查询按钮");
		action.click(accountMangerPage.queryButton());
		log.info("点击编辑按钮");
		WebElement queryResultElement=action.findElement(accountMangerPage.queryResultTable());
		//创建表格操作对象
		TableElement queryResultTableActionElement=new TableElement(queryResultElement);
		action.sleep(5);
		//第二行第四列
		String result_xpath=queryResultTableActionElement.getCellXpath(2, 13);
		String user_edit_xpath=result_xpath+"/a[1]";
		System.out.println(user_edit_xpath);
		WebElement user_editElement=driver.findElement(By.xpath(user_edit_xpath));
		user_editElement.click();
		log.info("检查是否进入账号编辑页面");
		Assertion.VerityTextPresent("新增就诊人","验证是否进入账号编辑页面");
		Assertion.VerityError();
		//System.out.println(result);



	}
	@Test(description="账号注册-添加就诊人",priority=6,dataProvider="query_account_forTel")
	public void add_patient(String query_account_forTel ) throws IOException
	{
		AccountMangerTest accountMangerTest=new AccountMangerTest();
		accountMangerTest.user_edit(query_account_forTel);
		log.info("点击新增就诊人");
		action.click(accountMangerPage.add_paitent());
		log.info("填写就诊人姓名");
		action.clear(accountMangerPage.add_paitent_name());
		action.type(accountMangerPage.add_paitent_name(), "郑永红");
		log.info("选择身份证");
		action.selectByText(accountMangerPage.register_identityCardType(), "身份证");
		log.info("输入身份证号码");
		action.type(accountMangerPage.register_identityCardNumber(), "360726198909193116");
		log.info("点击保存按钮");
		action.click(accountMangerPage.add_paitent_save());
		log.info("验证是否弹出保存成功的提示");
		Assertion.VerityTextPresent("保存成功","验证是否成功添加就诊人");
		log.info("点击确定按钮");
		action.click(accountMangerPage.register_confirm());
		Assertion.VerityError();


	}
	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}

}
