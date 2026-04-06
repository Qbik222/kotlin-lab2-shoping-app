/**
 * Тест-кейси, які покриває автоматизоване тестування (androidTest).
 * Інструменти: Jetpack Compose UI Test (ShoppingListComposeTest), Room DAO (ShoppingDaoPersistenceTest).
 * Запуск: gradlew.bat connectedDebugAndroidTest (емулятор або пристрій).
 *
 * Колонки: ID, назва, передумови, кроки (як у коді), очікуваний результат, фактичний результат, статус.
 * Після прогону оновіть колонки 6–7 за звітом Gradle / HTML.
 */
const data = [
  [
    "AUTO-01",
    "Compose UI: додавання товару — текст з’являється в списку (addItem_displaysInList)",
    "Інструментований тест на пристрої; @Before видаляє shopping.db; запускається MainActivity",
    "1. Знайти поле за testTag `input_name`, ввести «Автотест Молоко». 2. Натиснути кнопку за testTag `btn_add`. 3. waitForIdle(). 4. assertIsDisplayed для тексту «Автотест Молоко» (substring).",
    "Вузол з текстом товару відображається на екрані; додавання через UI оновлює список",
    "Відповідає очікуваному (при успішному прогоні)",
    "Pass",
  ],
  [
    "AUTO-02",
    "Compose UI: видалення товару — рядок зникає (deleteItem_removesRow)",
    "Той самий клас; БД очищена в @Before; MainActivity з ShoppingScreen",
    "1. Ввести в `input_name` рядок «TestDeleteItem». 2. Натиснути `btn_add`. 3. waitForIdle(). 4. Перевірити відображення назви. 5. performClick на contentDescription «Видалити TestDeleteItem». 6. waitForIdle(). 7. assertDoesNotExist для тексту назви.",
    "Після кліку по іконці видалення текст товару більше не відображається в ієрархії Compose",
    "Відповідає очікуваному (при успішному прогоні)",
    "Pass",
  ],
  [
    "AUTO-03",
    "Room DAO: персистентність після close() і повторного відкриття файлу БД (dataSurvivesCloseAndReopen)",
    "Окремий тестовий файл БД `dao_persist_test.db` у контексті застосунку; попередньо deleteDatabase",
    "1. Створити Room.databaseBuilder(..., dbName).build(), dao.upsert(ShoppingItemEntity persist-1 / Хліб). 2. db.close(). 3. Повторно databaseBuilder з тим самим ім’ям файлу. 4. dao.observeAll().first(). 5. assertEquals розміру списку та полів name, id.",
    "Після закриття першого підключення запис залишається у файлі; друге підключення читає 1 рядок з коректними name та id",
    "Відповідає очікуваному (при успішному прогоні)",
    "Pass",
  ],
];

if (typeof module !== "undefined" && module.exports) {
  module.exports = { data };
}
