package com.eligibilityChecker.stepdefinitions;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.Reporter;

import com.eligibilityChecker.testRunner.TestRunner;
import com.eligibilityChecker.utilities.CustomizedMethods;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;


public class EligibilityTest_Steps extends TestRunner{

	CustomizedMethods CM=new CustomizedMethods();
	boolean nextavailable;

	@Given("^I am a citizen of the UK$")
	public void CitizenofUK() throws InterruptedException{

		launchBrowser();
		WebElement Start = driver.findElement(By.xpath("//input[@value='Start now']"));
		log("Clicked on StartNow Button");
		wait.until(ExpectedConditions.visibilityOf(Start));
		Start.click();
		selectcountry();
		nextavailable=true;

	}



	@When("^I put my circumstances into the Checker tool \"([^\"]*)\"$")
	public void Scenario1(String Scenario) throws InterruptedException, IOException {
		FullFlow(Scenario);
	}


	@Then("^I should get a result of whether I will get help or not$")
	public void CheckResults() throws InterruptedException {

		WebElement results = driver.findElement(By.xpath("//h1[@id='result-heading' or @class='heading-xlarge done-panel']"));
		String result=results.getText().trim();
		if(result.contains("You get help with NHS costs")||result.contains("Done")){
			log("Done ********** You get help with NHS costs ************");
			System.out.println("PASS::Done ** You will Get help");
		}else{
			log("Done ********** You will not get help with NHS costs ************");
			Assert.fail();
		}
		List<WebElement> freeoptions= driver.findElements(By.xpath("//h2[contains(text(),'You get free:')]/following-sibling::ul[1]/li"));
		if(freeoptions.size()>0) {
		System.out.println("You get free following items : ");
		log("You get free following items : ");
		for(int i=0;i<freeoptions.size();i++){
			String freeoption=freeoptions.get(i).getText().trim();
			System.out.println(freeoption);
			log(freeoption+" **********");
		}
		}
		log("*********************************");
		
	}


	//Method to Launch Browser and Maximize window
	public void launchBrowser() {

		driver.get("https://services.nhsbsa.nhs.uk/check-for-help-paying-nhs-costs/start");
		try {
			driver.manage().window().maximize();
		}catch(Exception e) {
		}
		log("Opened URL : https://services.nhsbsa.nhs.uk/check-for-help-paying-nhs-costs/start");
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//h1[text()='Check what help you could get to pay for NHS costs']"))));
		boolean HomePage = driver.findElement(By.xpath("//h1[text()='Check what help you could get to pay for NHS costs']")).isDisplayed();
		if(HomePage){
			log("Check what help you could get to pay for NHS cost : Header is Displaying");
		}
		List<WebElement> cookies= driver.findElements(By.xpath("//button[@id='nhsuk-cookie-banner__link_accept_analytics']"));
		if(cookies.size()>0) {
			cookies.get(0).click();
		}
		
	}


	//Method to Select the Country
	public void selectcountry () throws InterruptedException{

		WebElement walesradio = driver.findElement(By.id("label-wales"));
		log("Selected Wales for Which country do you live in? Question");
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", walesradio);
		walesradio.click();
		Thread.sleep(1000);
		driver.findElement(By.id("next-button")).click();
		log("Clicked on Next Button");

	}


	//it will catures the questions from application and related answers captures from TestInput Data Sheet
	public void FullFlow(String Scenario) throws InterruptedException, IOException {

		while(nextavailable){
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//h1[@id='question-heading']"))));
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", driver.findElement(By.xpath("//h1[@id='question-heading']")));
			String question=driver.findElement(By.xpath("//h1[@id='question-heading']")).getText();
			LinkedHashMap<String, String> testdata=CM.readTestdatawithrowvalue("TestData", Scenario);
			String answer=testdata.get(question.trim());
			if(answer.contains("/")){
				driver.findElement(By.xpath("//input[contains(@id,'dob-day')]")).sendKeys(answer.split("/")[0]);
				driver.findElement(By.xpath("//input[contains(@id,'dob-month')]")).sendKeys(answer.split("/")[1]);
				driver.findElement(By.xpath("//input[contains(@id,'dob-year')]")).sendKeys(answer.split("/")[2]);
			}
			else{
				try{
					driver.findElement(By.xpath("//input[contains(@value,'"+answer+"')]/parent::label")).click();
				}catch(Exception e){
					driver.findElement(By.xpath("//input[contains(@value,'"+answer+"')]/parent::div")).click();
				}

			}
			log("for "+question.trim()+" :: We selected : "+answer);
			Thread.sleep(1000);
			driver.findElement(By.id("next-button")).click();
			log("Clicked on Next Button");
			try{
				nextavailable=driver.findElement(By.id("next-button")).isDisplayed();
			}catch(Exception e){
				System.out.println("Next is not available");
				nextavailable=false;
			}

		}
	}

}