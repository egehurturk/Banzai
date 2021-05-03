package com.egehurturk.annotations_t;

import com.egehurturk.exceptions.MalformedHandlerException;
import com.egehurturk.httpd.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@DisplayName("Testing annotations package")
public class AnnotationsTest {

    private HttpServer s;

    @BeforeEach
    public void setUp() throws IOException {
        s = new HttpServer();
    }

    @AfterEach
    public void tearDown() throws IOException {
        s.close();
    }

    @Test
    void test1() throws MalformedHandlerException  {
        s.addHandler(ErroredHandler.class);
    }
}
