package homework;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class ParameterizedTests extends TestBase{
    @BeforeAll
    static public void openPage () {
        homePage();

        // Принять куки, если есть
        if($(".cookie-policy__button").exists()) {
            $(".cookie-policy__button").click();
        }

    }
    // 1. Строка поиска
    @ParameterizedTest(name = "Поиск книги {0} должен возвращать результаты: {1}")
    @Tag("SMOKE")
    @CsvSource({
            "Маленький принц, true",
            "2012, true",
            "6746846465, false"}
    )
    void searchBookTest (String query, boolean shouldHaveResults) {
        $("input[placeholder=\"Поиск по Лабиринту\"]").setValue(query).pressEnter();

        if (shouldHaveResults) {
            $$(".product-card__top").shouldBe(sizeGreaterThan(0)); // хотя бы 1 элемент
            $("h1.index-top-title").shouldHave(text("Все, что мы нашли в Лабиринте\n" +
                    "            по запросу"));
        } else {
            $(".product-card__top").shouldNotBe(visible);
        }
    }

    // 2. Проверка фильтрации
    @ParameterizedTest(name = "Фильтрация канцелярии {0}")
    @Tag("REGRESS")
    @MethodSource("officeProvider")
    void officeNavigationTest(String category, Integer expectedFilter, boolean result) {
        open("/office/");

        $x(String.format("//li[contains(@class, 'gutter-genre-list')]//a[contains(., '%s')]", category))
                .shouldBe(visible, enabled) // ожидаем
                .click();

        // Проверяем количество элементов
        if (result) {
            $$("li.gutter-genre-list")
                    .shouldHave(sizeGreaterThan(expectedFilter)); // Проверяем точное количество
        }
        else {
            $$("li.gutter-genre-list")
                    .shouldHave(size(expectedFilter));
        }
        // Дополнительная проверка, что заголовок содержит название категории
        $("h1").shouldHave(text(category));

    }
    static Stream<Arguments> officeProvider() {
        return Stream.of(
                Arguments.of("Рисование", 4, true),    // Ожидаем больше 5 элементов
                Arguments.of("Сумки", 0, false),     // Элементов не найдено
                Arguments.of("Офисная канцелярия", 1, true) // Ожидаем ровно 2 элемента
        );
    }

    // 3. Добавление игрушки в корзину из файла
    @ParameterizedTest(name = "Добавление {1} игрушки {0} в корзину из файла")
    @Tag("BLOKER")
    @CsvFileSource(resources = "/toys.csv", delimiter = '|', numLinesToSkip = 1)
    void addToToysTest(String toyTitle, String publishing) {
        open("https://www.labirint.ru/");
        $("input[placeholder=\"Поиск по Лабиринту\"]").setValue(toyTitle+publishing).pressEnter();
        $x(String.format("//a[contains(text(), '%s')]", toyTitle)).click();
        $("._label_ssd04_48").shouldHave(text("Добавить в корзину")).click();
        $("button._btn_htzb6_1._minus_htzb6_26").shouldBe(visible);
    }}