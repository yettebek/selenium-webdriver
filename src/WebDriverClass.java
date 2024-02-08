import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebDriverClass {

    private class FileOperations {
        private static String setPath(String key) {

            // Get the path of the file
            String filePath = "";
            try {
                FileInputStream inputStream = new FileInputStream("config.properties");

                Properties prop = new Properties();
                prop.load(inputStream);
                filePath = prop.getProperty(key);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return filePath;

        }
    }

    private class WebDriverOperations {
        private static void pauseDriver() {

            try {
                Thread.sleep(2900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private static void inputDataToWebdriver() throws InterruptedException {
            WebDriver driver = new ChromeDriver();

            try {
                // read the content's file and store it in a list
                List<String> songsList = Files.readAllLines(Paths.get(FileOperations.setPath("SONGS_INPUT")));

                // Create an empty list to store the URLs
                List<String> urls = new ArrayList<String>();
                String videoUrl = "";

                System.out.println("OPENING GOOGLE...\n");

                // Open Google's homepage
                driver.get("https://www.youtube.com");
                driver.findElement(By.xpath("//button[.//span[text()='Reject all']]")).click();

                pauseDriver();

                for (int i = 0; i < songsList.size(); i++) {

                    // Search by clicking on the search bar
                    WebElement search = driver.findElement(By.name("search_query"));
                    // Enter the song name
                    search.sendKeys(songsList.get(i));
                    search.submit();

                    // wait for a few seconds
                    pauseDriver();

                    // Click on the first result
                    String firstVideoResultXPath = "/html/body/ytd-app/div[1]/ytd-page-manager/ytd-search/div[1]/ytd-two-column-search-results-renderer/"
                            +
                            "div/ytd-section-list-renderer/div[2]/ytd-item-section-renderer/div[3]/ytd-video-renderer[1]/"
                            +
                            "div[1]/div/div[1]/div/h3/a";

                    WebElement link = driver.findElement(By.xpath(firstVideoResultXPath));
                    link.click();

                    // wait for a few seconds
                    pauseDriver();

                    // Keep the video's url
                    videoUrl = String.join("\n", urls);
                    videoUrl = driver.getCurrentUrl();
                    urls.add(videoUrl);

                    // wait for a few seconds
                    pauseDriver();

                    // Go back to the previous page
                    driver.navigate().back();

                    // clear the input element after each search
                    String clearXPath = "/html/body/ytd-app/div[1]/div/ytd-masthead/div[4]/div[2]/ytd-searchbox/form/div[1]/div[2]/ytd-button-renderer/yt-button-shape/button";

                    WebElement clear = driver.findElement(By.xpath(clearXPath));
                    clear.click();
                }
                System.out.println("\nCLOSING DRIVER...");

                driver.quit();
                System.out.println("\nWRITING URLS TO FILE...");

                // Write the list to a file
                String outPath = FileOperations.setPath("SONGS_OUTPUT");
                Files.write(Paths.get(outPath), urls,
                        StandardOpenOption.CREATE);

                System.out.println("\nURLS WRITTEN SUCCESSFULLY");

                System.out.println("DONE");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchElementException e) {
                driver.quit();
                System.err.println(e.getMessage().toUpperCase());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        WebDriverOperations.inputDataToWebdriver();
    }
}
