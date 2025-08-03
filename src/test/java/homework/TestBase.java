package homework;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import static com.codeborne.selenide.Selenide.open;

public class TestBase {
    @BeforeAll
    public static void homePage() {
        Configuration.baseUrl = "https://www.labirint.ru";
        Configuration.browserSize = "1920x1080";
        //Configuration.browser = "chrome";
        Configuration.timeout = 10000;
        //Configuration.holdBrowserOpen = true;

        open(Configuration.baseUrl);}
}