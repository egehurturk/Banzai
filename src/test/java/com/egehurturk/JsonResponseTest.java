//package com.egehurturk;
//
//import com.egehurturk.exceptions.HttpRequestException;
//import com.egehurturk.handlers.FileResponse;
//import com.egehurturk.handlers.JsonResponse;
//import com.egehurturk.httpd.HttpRequest;
//import com.egehurturk.httpd.HttpResponse;
//import com.egehurturk.util.Headers;
//import com.egehurturk.util.Json;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//
//import java.io.*;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Locale;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
///** Test Case Scenarios
// * ---- About validating ----
// * [X] Http request that contains "Accept: ֯֯●/●" (star/star)
// * [X] Http request that contains "Accept: application/json"
// * [X] Http Request that contains both
// * [X] Http request that does not contain any of them
// * [X] If json.validate() is called after toHttpResponse(), this should throw IllegalStateException
// * [X] If http request is not passed as a constructor and the validate method is not called, it should throw IllegalStateException
// * [X]  If http request is not passed as a constructor and the validate method is called, it should work
// *
// * ---- About to Http response ----
// * [X] Date header should be equal to {current}
// * [X] Content language should be equal to "en_US"
// * [X] Mime type should be "application/json"
// * [X] If validate passes, status code should be 200
// * [X] If validate passes, status message should be OK
// * [X] If validate does not passes, status code should be 406
// * [X] If validate does not passes, status message should be Not Acceptable
// * [X] If validate does not passes, body should be {www/406.html}
// * [X] If validate does not passes, mime type should be text/html
// * [X] If body is not set (null), response body should be {defined}
// * [X] If body is not null and validate passes, response scheme should be HTTP/1.1
// * [X] If body is not null and validate passes, response code should be 200
// * [X] If body is not null and validate passes, response message should be OK
// * [X] If body is not null and validate passes, response body should be {defined}
// * [X] If body is not null and validate passes, response mime type should be "application/json"
// * [X] If body is not null and validate passes, response server name should be Banzai
// * [X] If body is not null and validate passes, response content language should be en_US
// * [X] If body is not null and validate passes, response content length should be {defined.length}
// * [X] Prettified JSON
// */
//
//@DisplayName("Tests about returning JSON responses")
//public class JsonResponseTest {
//    public HttpRequest request;
//
//    /* ~ Tests ~ */
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptStar")
//    @DisplayName("Validate get request to /jsontest with accept: */*")
//    public void requestWithAcceptStarShouldValidateTrue(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        Assertions.assertTrue(json.isValid());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("Validate get request to /jsontest with accept: application/json")
//    public void requestWithAcceptJsonShouldValidateTrue(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        Assertions.assertTrue(json.isValid());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithBoth")
//    @DisplayName("Validate get request to /jsontest with accept: application/json and */*")
//    public void requestWithAcceptBothShouldValidateTrue(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        Assertions.assertTrue(json.isValid());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithNone")
//    @DisplayName("Validate get request to /jsontest with accept: tex/html")
//    public void requestWithAcceptNoneShouldValidateFalse(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        Assertions.assertFalse(json.isValid());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptStar")
//    @DisplayName("Date header should be equal to the current date")
//    public void jsonResponseDateHeaderMustBeEqualToCurrentDateTime(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        ZonedDateTime now = ZonedDateTime.now();
//        String dateHeader = now.format(DateTimeFormatter.ofPattern(
//                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(
//                ZoneId.of("GMT")
//                )
//        );
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals(dateHeader, response.headers.get(Headers.DATE.NAME));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptStar")
//    @DisplayName("Content language header should be equal to en_US")
//    public void jsonResponseContentLangMustBeEqualToEnUS(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("en_US", response.headers.get(Headers.CONTENT_LANGUAGE.NAME));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptStar")
//    @DisplayName("Mime Type should be equal to application/json")
//    public void jsonResponseMimeTypeMustBeEqualToJson(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        System.out.println(new String(response.getBody()));
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("application/json", response.headers.get(Headers.CONTENT_TYPE.NAME));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptStar")
//    @DisplayName("Status code should be 200 if JSON is valid")
//    public void jsonResponseStatusCodeShouldBe200(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals(200, response.getCode());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptStar")
//    @DisplayName("Status message should be OK if JSON is valid")
//    public void jsonResponseStatusMessageShouldBeOK(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("OK", response.getMessage());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithNone")
//    @DisplayName("Invalid request: Status code should be 406")
//    public void jsonResponseStatusCodeShouldBe406(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertFalse(json.isValid());
//        Assertions.assertEquals(406, response.getCode());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithNone")
//    @DisplayName("Invalid request: Status message should be Not Acceptable")
//    public void jsonResponseStatusMessageShouldBeNotAcceptable(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertFalse(json.isValid());
//        Assertions.assertEquals("Not Acceptable", response.getMessage());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithNone")
//    @DisplayName("Invalid request: Body should be 406.html")
//    public void jsonResponseBodyShouldBe406Html(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertFalse(json.isValid());
//        FileResponse file = new FileResponse("www/406.html", new PrintWriter(System.out, true));
//        String content = new String(file.toHttpResponse().getBody());
//        Assertions.assertEquals(content, new String(response.getBody()));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithNone")
//    @DisplayName("Invalid request: Mime type should be text/html")
//    public void jsonResponseMimeTypeShouldBeHtml(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertFalse(json.isValid());
//        Assertions.assertEquals("text/html", response.headers.get(Headers.CONTENT_TYPE.NAME));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("JSON body should be the defined if the body is null (not set)")
//    public void jsonResponseBodyNotSetShouldBeTheDefault(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        String JSON = Json.prettyPrintJSON("{\"Server Response\": {\"title\": \"Null Body\", \"body\": \"Body of JSON request is not set (This message is autogenerated by Banzai. Check logs from console)\"}}");
//        Assertions.assertEquals(JSON, new String(response.getBody()));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("If body is not null and validate passes, response scheme should be HTTP/1.1")
//    public void jsonResponseHTTPSchemeShouldBeHttp11(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("HTTP/1.1", response.getScheme());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("If body is not null and validate passes, response code should be 200")
//    public void jsonResponseCodeShouldBe200(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals(200, response.getCode());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("If body is not null and validate passes, response message should be OK")
//    public void jsonResponseMessageShouldBeOK(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("OK", response.getMessage());
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("If body is not null and validate passes, response body should be the body set")
//    public void jsonResponseBodyShouldBeSetted(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        json.setBody("{Hi: Hello}");
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("{Hi: Hello}", new String(response.getBody()));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("If body is not null and validate passes, response mime type should be \"application/json\"")
//    public void jsonResponseContentTypeShouldBeJson(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("application/json", response.headers.get(Headers.CONTENT_TYPE.NAME));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("If body is not null and validate passes, response server name should be Banzai")
//    public void jsonResponseServerNameShouldBeBanzai(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("Banzai", response.headers.get(Headers.SERVER.NAME));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("If body is not null and validate passes, response content language should be en_US")
//    public void jsonResponseContentLangShouldBeEnUS(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals("en_US", response.headers.get(Headers.CONTENT_LANGUAGE.NAME));
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptJson")
//    @DisplayName("If body is not null and validate passes, response content length should be the set length")
//    public void jsonResponseContentLengthShouldBeSet(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        json.validate(request);
//        json.setBody("{Hello: Hi}");
//        HttpResponse response = json.toHttpResponse();
//        Assertions.assertTrue(json.isValid());
//        Assertions.assertEquals(""+json.getBody().getBytes().length, response.headers.get(Headers.CONTENT_LENGTH.NAME));
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Testing prettified JSON")
//    public void PrettifiedJsonShouldBeTheSameAfterPrettifying() {
//       String actualJson = "{\n" +
//               "    \"glossary\": {\n" +
//               "        \"title\": \"example glossary\",\n" +
//               "\t\t\"GlossDiv\": {\n" +
//               "            \"title\": \"S\",\n" +
//               "\t\t\t\"GlossList\": {\n" +
//               "                \"GlossEntry\": {\n" +
//               "                    \"ID\": \"SGML\",\n" +
//               "\t\t\t\t\t\"SortAs\": \"SGML\",\n" +
//               "\t\t\t\t\t\"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
//               "\t\t\t\t\t\"Acronym\": \"SGML\",\n" +
//               "\t\t\t\t\t\"Abbrev\": \"ISO 8879:1986\",\n" +
//               "\t\t\t\t\t\"GlossDef\": {\n" +
//               "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
//               "\t\t\t\t\t\t\"GlossSeeAlso\": [\"GML\", \"XML\"]\n" +
//               "                    },\n" +
//               "\t\t\t\t\t\"GlossSee\": \"markup\"\n" +
//               "                }\n" +
//               "            }\n" +
//               "        }\n" +
//               "    }\n" +
//               "}";
//       String pretty = Json.prettyPrintJSON(actualJson);
//       Assertions.assertNotEquals(actualJson, pretty);
//    }
//
//    @Test
//    @Disabled
//    @DisplayName("Testing prettified JSON")
//    public void JsonShouldBeThePrettifiedAfterPrettifying() {
//        String actualJson = "{hi: {User: ege, test: tyo}}";
//        String pretty = Json.prettyPrintJSON(actualJson);
//        Assertions.assertEquals("{\n" +
//                "   hi:{\n" +
//                "      User:ege,\n" +
//                "      test:tyo\n" +
//                "   }\n" +
//                "}", pretty);
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptStar")
//    @DisplayName("If json.validate() is called after toHttpResponse(), this should throw IllegalStateException")
//    public void validateNullExceptionShouldBeThrown(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true));
//        assertThrows(IllegalStateException.class,
//                json::toHttpResponse
//        );
//    }
//
//    @ParameterizedTest
//    @MethodSource("getRequestWithAcceptStar")
//    @DisplayName("If http request is not passed as a constructor and the validate method is called, it should work")
//    public void validateInConstructor(String requestBody) throws IOException, HttpRequestException {
//        request = new HttpRequest(new BufferedReader(new InputStreamReader(prepareIncomingRequestStream(requestBody))));
//        JsonResponse json = new JsonResponse(new PrintWriter(System.out, true), request);
//        Assertions.assertEquals(200, json.toHttpResponse().getCode());
//        Assertions.assertEquals("OK", json.toHttpResponse().getMessage());
//    }
//
//
//    /* ~ Helper Functions ~ */
//    public InputStream prepareIncomingRequestStream(String stream) throws IOException {
//        return new ByteArrayInputStream(stream.getBytes());
//    }
//
//    private static Stream<Arguments> getRequestWithAcceptStar() {
//        String request =
//                "GET /jsontest HTTP/1.1\r\n" +
//                "Host: localhost:8080\r\n" +
//                "Accept: */*\r\n" +
//                "\r\n";
//        return Stream.of(
//                Arguments.of(request)
//        );
//    }
//
//    private static Stream<Arguments> getRequestWithAcceptJson() {
//        String request =
//                "GET /jsontest HTTP/1.1\r\n" +
//                "Host: localhost:8080\r\n" +
//                "Accept: application/json\r\n" +
//                "\r\n";
//        return Stream.of(
//                Arguments.of(request)
//        );
//    }
//
//    private static Stream<Arguments> getRequestWithBoth() {
//        String request =
//                "GET /jsontest HTTP/1.1\r\n" +
//                "Host: localhost:8080\r\n" +
//                "Accept: */*, application/json\r\n" +
//                "\r\n";
//        return Stream.of(
//                Arguments.of(request)
//        );
//    }
//
//    private static Stream<Arguments> getRequestWithNone() {
//        String request =
//                "GET /jsontest HTTP/1.1\r\n" +
//                "Host: localhost:8080\r\n" +
//                "Accept: text/html\r\n" +
//                "\r\n";
//        return Stream.of(
//                Arguments.of(request)
//        );
//    }
//}
