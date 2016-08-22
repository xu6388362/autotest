import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.webdriver.patatiumwebui.action.CommonAction;
import org.webdriver.patatiumwebui.pageObject.AppointmentDetailPage;
import org.webdriver.patatiumwebui.pageObject.AppointmentPage;
import org.webdriver.patatiumwebui.pageObject.HomePage;
import org.webdriver.patatiumwebui.db.HospitalTable;
import org.webdriver.patatiumwebui.utils.Assertion;
import org.webdriver.patatiumwebui.utils.ElementAction;
import org.webdriver.patatiumwebui.utils.ExcelReadUtil;
import org.webdriver.patatiumwebui.utils.Log;
import org.webdriver.patatiumwebui.utils.TableElement;
import org.webdriver.patatiumwebui.utils.TestBaseCase;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class AppointmentTest extends TestBaseCase {

	ElementAction action=new ElementAction();
	HomePage homePage=new HomePage();
	AppointmentPage appointmentPage=new AppointmentPage();
	AppointmentDetailPage appointmentDetailPage=new AppointmentDetailPage();
	Log log=new Log(AppointmentTest.class);
	HospitalTable hospitalTable=new HospitalTable();
	@DataProvider(name="hospiatalName")
	public Object[][] hospiatalName()
	{
		String filePath="src/main/resources/data/hospital_list.xls";
		int max=75;
		int min=1;
		Random random=new Random();
		int start_hospital=random.nextInt(max)%(max-min+1)+min;
		int  end_hospital=start_hospital+4;
		return ExcelReadUtil.case_data_excel(0, start_hospital,end_hospital, 0, 1,filePath);

	}
	@DataProvider(name="hospiatalNameForNewUser")
	public Object[][] hospiatalNameForNewUser()
	{
		String filePath="src/main/resources/data/hospital_list.xls";
		int max=79;
		int min=1;
		Random random=new Random();
		int start_hospital=random.nextInt(max)%(max-min+1)+min;
		int  end_hospital=start_hospital;
		return ExcelReadUtil.case_data_excel(0, start_hospital,end_hospital, 0, 1,filePath);
	}
	@BeforeMethod
	@Parameters({"Base_Url","UserName","PassWord"})
	public void beforeclass(String Base_Url,String UserName,String PassWord) throws IOException
	{
		log.info("输入用户名密码进入后台主页");
		driver.manage().deleteAllCookies();
		CommonAction.Login(Base_Url+"/login/login", UserName, PassWord);
		action.sleep(5);
	}

	@Test(description="按医院预约挂号-已注册用户挂号",priority=1,dataProvider="hospiatalName")
	public void appointment(String hospiatalName,String hospiatlPinyin) throws IOException, InterruptedException
	{
		String telphone;
		String filePath="src/main/resources/data/new_account.xls";
		Object[][] telphones=ExcelReadUtil.case_data_excel(0, 1, 1, 0,0,filePath);
		telphone=telphones[0][0].toString();

		action.click(homePage.appiont_memue());
		action.sleep(4);
		List<WebElement> siginSourcElements=null;
		log.info("清除医院名称");
		action.clear(appointmentPage.hospitalPinyinOrName());
		action.sleep(1);
		action.type(appointmentPage.hospitalPinyinOrName(),hospiatalName);
		action.sleep(3);
		//action.executeJS("$('#hospitalList ul li').eq(0).mousedown()");
		if (action.isElementsPresent(appointmentPage.hospital_list(), 3))
		{
			action.executeJS("$(\"#hospital_list li[title='"
					+hospiatlPinyin
					+ " "
					+hospiatalName
					+ "']\").mousedown()");
		}
		action.sleep(3);
		log.info("点击科室按钮，获取科室列表");
		action.click(appointmentPage.departmentId_select());
		action.sleep(3);
		List<WebElement> departmentElements=driver.findElements(By.xpath(appointmentPage.departmentId_select().getElement()+"/option"));
		log.info("科室定位信息："+appointmentPage.departmentId_select().getElement()+"/option");
		final int departmentElementNumbers=departmentElements.size();
		int siginSource=0;
		log.info("获取科室总数"+departmentElementNumbers);
		if (departmentElementNumbers<=1)
		{
			log.info("该医院没有科室继续查找下一个医院");

		}
		Assertion.VerityNotString(String.valueOf(departmentElementNumbers), "1", "验证该医院是否有科室");
		log.info("遍历科室，查找号源");
		for (int j = 1; j < departmentElementNumbers; j++)
		{
			log.info("第"+j+"遍历");
			action.click(appointmentPage.departmentId_select());
			action.sleep(3);
			List<WebElement> departmentElements2=driver.findElements(By.xpath(appointmentPage.departmentId_select().getElement()+"/option"));
			System.out.println(appointmentPage.departmentId_select().getElement()+"/option");
			log.info("选择"+departmentElements2.get(j).getText()+"科室");
			log.info("点击第"+j+"科室");
			action.selectByIndex(appointmentPage.departmentId_select(), j);
			log.info("点击查询按钮");
			action.sleep(2);
			action.executeJS("$('#searchSubmit').click()");
			log.info("等待加载号源");
			action.sleep(4);
			try {
				siginSourcElements=driver.findElements(By.xpath(appointmentPage.SignalSources().getElement()));
				log.info("获取号源数:"+siginSourcElements.size());
				if(siginSourcElements.size()<1)
				{
					log.info("没有号源，继续下一个科室");
					continue;
				}
				else {
					for (int i = 0; i < siginSourcElements.size(); i++) {
						log.info("进入循环查找非当天号源，每一次需要重新查找元素");
						siginSourcElements=driver.findElements(By.xpath(appointmentPage.SignalSources().getElement()));
						log.info("点击出诊表号源");
						siginSourcElements.get(i).click();
						log.info("选择具体号源");
						action.sleep(3);
						log.info("检查号源是否是当天号源");
						String appointmentInfoTitle=action.getText(appointmentPage.appointmentInfoTitle());
						log.info("挂号号源信息："+appointmentInfoTitle);
						Date now = new Date();
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						String appointmentTime=dateFormat.format(now).toString();
						if (appointmentInfoTitle.contains(appointmentTime)&&(!appointmentInfoTitle.isEmpty())) {
							log.info("该号源是当天号源，关闭该号源选择对话框，进行下一个号源挂号");
							action.click(appointmentPage.appointmentInfo_close_button());
							action.sleep(5);
							continue;
						}
						else {
							break;
						}
					}
					log.info("该号源不是当天号源，继续进行挂号操作");
					List<WebElement> timeSignalSources=action.findElements(appointmentPage.timeSignalSources());
					siginSource=siginSource+timeSignalSources.size();
					timeSignalSources.get(0).click();
					log.info("点击选择号源下一步");
					action.click(appointmentPage.nextsetepforfirst());
					log.info("等待5秒检查是否返回信息输入界面");
					action.sleep(5);
					Assertion.VerityTextPresent("温馨提示：给≤14岁的儿童预约，先输入出生日期，会切换到相应的儿童模板", "验证是否打开账号信息输入界面");
					log.info("输入电话号码");
					action.clear(appointmentPage.telephone());
					action.type(appointmentPage.telephone(), telphone);
					log.info("等待4秒");
					action.sleep(4);
					//预约号码
					log.info("点击下一步");
					action.click(appointmentPage.netstepforlast());
					log.info("已选择一个非当天号源进行挂号。退出查找非当天号源的循环");
					log.info("等待14秒");
					action.sleep(14);
					log.info("点击下一步返回信息："+action.getText(appointmentPage.failed_message()));
					if (action.getText(appointmentPage.failed_message()).trim().contains("你已经成功预约过此科室")) {
						log.info("已经成功预约过该科室，继续下一个科室进行挂号");
						continue;
					}
					Assertion.VerityBoolean(action.isElementDisplayed(appointmentPage.appoint_succes_info()), true,"验证是否弹出预约挂号成功的对话框");
					if (!action.isElementDisplayed(appointmentPage.appoint_succes_info())) {
						log.info("关闭信息输入对话框");
						action.click(appointmentPage.patientInfo_closebutton());
					}
					else {
						action.sleep(2);
						log.info("点击关闭按钮");
						action.click(appointmentPage.closebutton());
						action.sleep(4);
					}
					log.info("进入预约明细界面检查是否正常预约成功");
					action.click(homePage.AppointmentDetail());
					action.sleep(5);
					//通过预约号查询订单状态
					action.clear(appointmentDetailPage.telephone());
					action.type(appointmentDetailPage.telephone(), telphone);
					log.info("点击查询按钮");
					action.click(appointmentDetailPage.query_button());
					action.sleep(3);
					WebElement query_table=action.findElement(appointmentDetailPage.query_table());
					TableElement tableElement=new TableElement(query_table);
					for (int i = 1; i <6; i++) {
						String hospiatalName_td=tableElement.getCellText(i, 4);
						if (hospiatalName_td.equals(hospiatalName)) {
							String order_state=tableElement.getCellText(i, 15);
							Assertion.VerityString(order_state, "待就诊", "验证订单状态是否为待就诊");
							break;
						}
					}

				}
				if (siginSourcElements.size()>=1) {
					break;
				}
			} catch (NoSuchElementException e) {
				// TODO: handle exception
				log.info("不存在号源,继续查找下一个科室");
				continue;
			}
		}
		action.sleep(6);
		if (siginSource<1) {
			Assertion.VerityNotString("0", String.valueOf(siginSource), "验证该医院是否有号源");
		}
		Assertion.VerityError();


	}

	@Test(description="按医院预约挂号-新用户直接挂号",priority=2,dataProvider="hospiatalNameForNewUser")
	public void appointment_newuser(String hospiatalName,String hospiatlPinyin) throws IOException, InterruptedException
	{
		String telphone;
		String patientName;
		String idcardtype;
		String identityCardNumber;
		String filePath="src/main/resources/data/new_account.xls";
		Object[][] userinfo=ExcelReadUtil.case_data_excel(0, 2, 2, 0,3,filePath);
		telphone=userinfo[0][0].toString();
		patientName=userinfo[0][1].toString();
		idcardtype=userinfo[0][2].toString();
		identityCardNumber=userinfo[0][3].toString();
		action.click(homePage.appiont_memue());
		action.sleep(4);
		List<WebElement> siginSourcElements=null;
		log.info("清除医院名称");
		action.clear(appointmentPage.hospitalPinyinOrName());
		action.sleep(1);
		action.type(appointmentPage.hospitalPinyinOrName(),hospiatalName);
		action.sleep(3);
		//action.executeJS("$('#hospitalList ul li').eq(0).mousedown()");
		if (action.isElementsPresent(appointmentPage.hospital_list(), 3))
		{
			action.executeJS("$(\"#hospital_list li[title='"
					+hospiatlPinyin
					+ " "
					+hospiatalName
					+ "']\").mousedown()");
		}
		action.sleep(3);
		log.info("点击科室按钮，获取科室列表");
		action.click(appointmentPage.departmentId_select());
		action.sleep(3);
		List<WebElement> departmentElements=driver.findElements(By.xpath(appointmentPage.departmentId_select().getElement()+"/option"));
		log.info("科室定位信息："+appointmentPage.departmentId_select().getElement()+"/option");
		final int departmentElementNumbers=departmentElements.size();
		int siginSource=0;
		log.info("获取科室总数"+departmentElementNumbers);
		if (departmentElementNumbers<=1)
		{
			log.info("该医院没有科室继续查找下一个医院");
			Assertion.VerityNotString(String.valueOf(departmentElementNumbers), "1", "验证该医院是否有科室");

		}
		else {
			Assertion.VerityNotString(String.valueOf(departmentElementNumbers), "1", "验证该医院是否有科室");
			log.info("遍历科室，查找号源");
			for (int j = 1; j < departmentElementNumbers; j++)
			{
				log.info("第"+j+"遍历");
				action.click(appointmentPage.departmentId_select());
				action.sleep(3);
				List<WebElement> departmentElements2=driver.findElements(By.xpath(appointmentPage.departmentId_select().getElement()+"/option"));
				System.out.println(appointmentPage.departmentId_select().getElement()+"/option");
				log.info("选择"+departmentElements2.get(j).getText()+"科室");
				log.info("点击第"+j+"科室");
				action.selectByIndex(appointmentPage.departmentId_select(), j);
				log.info("点击查询按钮");
				action.sleep(2);
				action.executeJS("$('#searchSubmit').click()");
				//action.click(appointmentPage.appointment_query());
				log.info("等待加载号源");
				action.sleep(4);
				try {
					siginSourcElements=driver.findElements(By.xpath(appointmentPage.SignalSources().getElement()));
					log.info("获取号源数:"+siginSourcElements.size());
					if(siginSourcElements.size()<1)
					{
						log.info("没有号源，继续下一个科室");
						continue;
					}
					else {
						for (int i = 0; i < siginSourcElements.size(); i++) {
							log.info("进入循环查找非当天号源，每一次需要重新查找元素");
							siginSourcElements=driver.findElements(By.xpath(appointmentPage.SignalSources().getElement()));
							log.info("点击出诊表号源");
							siginSourcElements.get(i).click();
							log.info("选择具体号源");
							action.sleep(3);
							log.info("检查号源是否是当天号源");
							String appointmentInfoTitle=action.getText(appointmentPage.appointmentInfoTitle());
							log.info("挂号号源信息："+appointmentInfoTitle);
							Date now = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							String appointmentTime=dateFormat.format(now).toString();
							if (appointmentInfoTitle.contains(appointmentTime)&&(!appointmentInfoTitle.isEmpty())) {
								log.info("该号源是当天号源，关闭该号源选择对话框，进行下一个号源挂号");
								action.click(appointmentPage.appointmentInfo_close_button());
								action.sleep(5);
								continue;
							}
							else {
								break;
							}
						}
						log.info("该号源不是当天号源，可以继续进行挂号操作");
						List<WebElement> timeSignalSources=action.findElements(appointmentPage.timeSignalSources());
						siginSource=siginSource+timeSignalSources.size();
						timeSignalSources.get(0).click();
						log.info("点击选择号源下一步");
						action.click(appointmentPage.nextsetepforfirst());
						log.info("等待5秒检查是否返回信息输入界面");
						action.sleep(5);
						Assertion.VerityTextPresent("温馨提示：给≤14岁的儿童预约，先输入出生日期，会切换到相应的儿童模板", "验证是否打开账号信息输入界面");
						log.info("输入电话号码："+telphone);
						action.clear(appointmentPage.telephone());
						action.type(appointmentPage.telephone(), telphone);
						log.info("输入姓名："+patientName);
						action.type(appointmentPage.patientName(),patientName);
						log.info("输入身份证号码："+identityCardNumber);
						action.type(appointmentPage.identityCardNumber(), identityCardNumber);
						log.info("等待4秒");
						action.sleep(4);
						//预约号码
						log.info("点击下一步");
						action.click(appointmentPage.netstepforlast());
						log.info("已选择一个非当天号源进行挂号。退出查找非当天号源的循环");
						log.info("等待14秒");
						action.sleep(14);
						log.info("点击下一步返回信息："+action.getText(appointmentPage.failed_message()));
						if (action.getText(appointmentPage.failed_message()).trim().contains("你已经成功预约过此科室")) {
							log.info("已经成功预约过该科室，继续下一个科室进行挂号");
							continue;
						}
						Assertion.VerityBoolean(action.isElementDisplayed(appointmentPage.appoint_succes_info()), true,"验证是否弹出预约挂号成功的对话框");
						if (!action.isElementDisplayed(appointmentPage.appoint_succes_info())) {
							log.info("关闭信息输入对话框");
							action.click(appointmentPage.patientInfo_closebutton());
						}
						else {
							action.sleep(2);
							log.info("点击关闭按钮");
							action.click(appointmentPage.closebutton());
							action.sleep(4);
						}
						log.info("进入预约明细界面检查是否正常预约成功");
						action.click(homePage.AppointmentDetail());
						action.sleep(2);
						//通过预约号查询订单状态
						action.clear(appointmentDetailPage.telephone());
						action.type(appointmentDetailPage.telephone(), telphone);
						log.info("点击查询按钮");
						action.click(appointmentDetailPage.query_button());
						action.sleep(3);
						WebElement query_table=action.findElement(appointmentDetailPage.query_table());
						TableElement tableElement=new TableElement(query_table);
						for (int i = 1; i <6; i++) {
							String hospiatalName_td=tableElement.getCellText(i, 4);
							if (hospiatalName_td.equals(hospiatalName)) {
								String order_state=tableElement.getCellText(i, 15);
								Assertion.VerityString(order_state, "待就诊", "验证订单状态是否为待就诊");
								break;
							}
						}
					}
					if (siginSourcElements.size()>=1) {
						break;
					}
				} catch (NoSuchElementException e) {
					// TODO: handle exception
					log.info("不存在号源,继续查找下一个科室");
					continue;
				}
			}
			action.sleep(6);
			if (siginSource<1) {
				Assertion.VerityNotString("0", String.valueOf(siginSource), "验证该医院是否有号源");
			}
		}
		Assertion.VerityError();
	}

	@Test(description="检查医院科室是否报错",priority=3,dataProvider="hospiatalName")
	public void checkHospitalDepart(String hospitalName) throws IOException
	{
		log.info("输入用户名密码进入后台主页");
		CommonAction.Login("http://shenzhen.call.hk515.com/login/login", "hljadmin", "111111");
		action.sleep(5);
		action.type(appointmentPage.hospitalPinyinOrName(),hospitalName);
		System.out.println(hospitalName);
		action.sleep(5);
		action.executeJS("$('#hospital_list li').eq(0).mousedown()");
		action.sleep(5);
		log.info("获取科室列表");
		action.click(appointmentPage.departmentId_select());
		action.sleep(20);
		List<WebElement> departmentElements=driver.findElements(By.xpath(appointmentPage.departmentId_select().getElement()+"/option"));
		System.out.println(appointmentPage.departmentId_select().getElement()+"/option");
		int departmentElementNumbers=departmentElements.size();
		System.out.println(hospitalName+"的科室总数："+departmentElementNumbers);
		for (int j = 1; j < departmentElementNumbers; j++) {
			System.out.println(j);
			action.click(appointmentPage.departmentId_select());
			action.sleep(20);
			List<WebElement> departmentElements2=driver.findElements(By.xpath(appointmentPage.departmentId_select().getElement()+"/option"));
			System.out.println(appointmentPage.departmentId_select().getElement()+"/option");
			log.info("选择"+departmentElements2.get(j).getText());
			action.selectByIndex(appointmentPage.departmentId_select(), j);
			log.info("点击查询按钮");
			action.sleep(10);
			action.executeJS("$('#searchSubmit').click()");
			action.sleep(10);
			Assertion.VerityTextPresent("性别");
			Assertion.VerityError();
		}
	}

	public static void main(String[] args) {
		// TODO 自动生成的方法存根

	}


}
