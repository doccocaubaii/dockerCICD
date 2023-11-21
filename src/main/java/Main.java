import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    private static void start() {
            log.info("Start selenium");
            ChromeOptions chromeOptions = new ChromeOptions();
            WebDriver driver = new ChromeDriver(chromeOptions);
            driver.get("https://cic.gov.vn/#/");
            driver.manage().window().maximize();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            waitMe(1_000);


            try {
                WebElement mainBanner = driver.findElement(By.className("main-banner"));
                WebElement element = mainBanner.findElement(By.cssSelector(".slick-next"));
                // đợi cho đến khi bấm được đổi ảnh
                wait.until(ExpectedConditions.elementToBeClickable(By.className("slick-next")));
                screenShot(driver);
                //thực hiện chuyển ảnh
                for (int i = 0; i <= 6; i++) {
                    element.click();
                    waitMe(1000);
                    screenShot(driver);
                }
                log.info("Click image success!");
                waitMe(-1);

            } catch (Exception e) {
                log.error("Cannot  click image");
                log.error(e.getMessage(), e.getStackTrace());
            }


            int location = 0;
            // lăn con chuột đến đoạn tiếp theo
            try {
                WebElement listPackage = driver.findElement(By.id("list-package"));
                int deltaY1 = listPackage.getRect().y;
                location = 1;

                WebElement cicGuide = driver.findElement(By.id("cic-guide"));
                WebElement tes = driver.findElement(By.className("header-notification"));
                location = 2;
                new Actions(driver)
                        .scrollByAmount(0, cicGuide.getRect().y)
                        .perform();
                waitMe(2000);
                screenShot(driver);
                waitMe(-1);
                log.info("scroll to packageHot success!");
            } catch (NoSuchElementException e) {
                log.error("scroll to packageHot failed");
                if (location == 1) log.error("Not found package-hot");
                else log.error("list-package");
                log.error(e.getMessage(), e.getStackTrace());
            } catch (Exception e) {
                log.error("scroll to packageHot failed");
                log.error(e.getMessage(), e.getStackTrace());
            }


            try {
                //lăn con chuột xuống cuối cùng
                WebElement cicGuide = driver.findElement(By.id("cic-guide"));
                WebElement packageHot = driver.findElement(By.id("package-hot"));

                WebElement footer = driver.findElement(By.className("copyright"));
                new Actions(driver)
//                    .scrollToElement(footer)
                        .scrollByAmount(0,packageHot.getRect().y- cicGuide.getRect().y)
                        .perform();
                screenShot(driver);
                waitMe(-1);
                log.info("scroll to copyright success!");

            } catch (NoSuchElementException e) {
                log.error("scroll to copyright failed. Not found copyright");
                log.error(e.getMessage(), e.getStackTrace());
            } catch (Exception e) {
                log.error("scroll to copyright failed");
                log.error(e.getMessage(), e.getStackTrace());
            }
            driver.quit();
    }

    public static void main(String[] args) {
//         start();
//
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Date dateSchedule = calendar.getTime();
        long period = 24 * 60 * 60 * 1000;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                start();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, dateSchedule, period);
    }
    private static void screenShot(WebDriver driver) {
        File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        // Save screenshot to a specific location
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mmssSSS"));
        String screenshotPath = "D://seleniumImg/"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))+"/"+fileName +".png";
        try {
            org.apache.commons.io.FileUtils.copyFile(screenshotFile, new File(screenshotPath));
            System.out.println("Screenshot saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save the screenshot: " + e.getMessage());
        }

    }

    private static void waitMe(int value) {
        try {
            if (value < 0) Thread.sleep(100);
            else Thread.sleep(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}