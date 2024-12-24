import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

public class WebTest{
@Test(description = "Кейс с успешной регистрацией")
public void testRegisterSuccess() {
    Response response = given()
        .contentType(ContentType.JSON)
        .body("{\"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}")
        .when()
        .post("https://reqres.in/api/register")
        .then()
        .statusCode(200) //Статус код OK
        .extract().response();

    // Проверка модели ответа
    assert response.jsonPath().getString("id") != null;
    assert response.jsonPath().getString("token") != null;
}
@Test(description = "Кейс с ошибкой при регистрации")
public void testRegisterFailure() {
    Response response = given()
        .contentType(ContentType.JSON)
        .body("{\"email\": \"sydney@fife\"}")
        .when()
        .post("https://reqres.in/api/register")
        .then()
        .statusCode(400)//Статус код неправильный запрос
        .extract().response();

    // Проверка модели ответа
    assert response.jsonPath().getString("error").equals("Missing password");
}
@Test(description = "Кейс с успешным изменением данных пользователя")
public void testUpdateUserSuccess() {
    Response response = given()
        .contentType(ContentType.JSON)
        .body("{\"name\": \"morpheus\", \"job\": \"zion resident\"}")
        .when()
        .put("https://reqres.in/api/users/2")
        .then()
        .statusCode(200)
        .extract().response();

    // Проверка модели ответа
    assert response.jsonPath().getString("name").equals("morpheus");
    assert response.jsonPath().getString("job").equals("zion resident");
}
@Test(description = "Кейс с ошибкой изменения данных пользователя")
public void testUpdateUserNotFound() {
    Response response = given()
        .contentType(ContentType.JSON)
        .body("{\"name\": \"morpheus\", \"job\": \"zion resident\"}")
        .when()
        .put("https://reqres.in/api/users/23")
        .then()
        .statusCode(200) // Reqres в случае изменения данных несуществующих пользователей возвращает статус код 200, а не 404
        .extract().response();


    String name = response.jsonPath().getString("name");
    String job = response.jsonPath().getString("job");

    // Проверка, что имя и работа соответствуют ожиданиям
    assert name.equals("morpheus"); 
    assert job.equals("zion resident");

    //Дополнительный запрос, чтобы убедиться что не был создан пользователь с id 23
    Response getResponse = given()
        .when()
        .get("https://reqres.in/api/users/23")
        .then()
        .statusCode(404) // Ожидаем статус 404, так как пользователь не существует
        .extract().response();

    // Проверка, что сообщение об ошибке присутствует и равно null
    String errorMessage = getResponse.jsonPath().getString("error");
    assert errorMessage == null; 
}

@Test(description = "Кейс с успешным удалением пользователя")
public void testDeleteUserSuccess() {
    Response response = given()
        .when()
        .delete("https://reqres.in/api/users/2")
        .then()
        .statusCode(204)
        .extract().response();

    // Проверка, что тело ответа пустое
    assert response.getBody().asString().isEmpty();
}
@Test(description = "Кейс с неудавшимся удалением пользователя")
public void testDeleteUserNotFound() {
    //Проверка, что пользователя не существует, перед удалением
    Response getResponse = given()
    .when()
    .get("https://reqres.in/api/users/23")
    .then()
    .statusCode(404) // Ожидаем статус 404, так как пользователь не существует
    .extract().response();

    // Проверка, что сообщение об ошибке есть и равно null
    String errorMessageTwo = getResponse.jsonPath().getString("error");
    assert errorMessageTwo == null;

    Response response = given()
        .when()
        .delete("https://reqres.in/api/users/23")
        .then()
        .statusCode(204)
        .extract().response();
    
    //Проверка модели ответа, что тело ответа пустое
    assert response.getBody().asString().isEmpty();
}

@Test(description = "Кейс с успешным получением информации о пользователе")
public void testGetUserSuccess() {
    Response response = given()
        .when()
        .get("https://reqres.in/api/users/2")
        .then()
        .statusCode(200)
        .extract().response();

    // Проверка модели ответа
    assert response.jsonPath().getString("data.first_name").equals("Janet");
    assert response.jsonPath().getString("data.last_name").equals("Weaver");
}
@Test(description = "Кейс с неудавшимся получением информации о пользователе")
public void testGetUserNotFound() {
    Response response = given()
        .when()
        .get("https://reqres.in/api/users/23")
        .then()
        .statusCode(404)
        .extract().response();

    // Проверка, что сообщение об ошибке присутствует
    String errorMessage = response.jsonPath().getString("error");
    assert errorMessage == null; // Проверка, что сообщение об ошибке равно null
  
}
}
