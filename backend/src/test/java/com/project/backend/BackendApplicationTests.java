package com.project.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootTest
class BackendApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private Environment env;

	@Test
	void contextLoads() {
		// ApplicationContext에서 특정 빈 로드 확인
		assert context.getBean("dataSource") != null : "DataSource must not be null";

		// 환경 변수 확인 로깅
		System.out.println("JASYPT ENCRYPTOR PASSWORD: " + env.getProperty("jasypt.encryptor.password"));
	}
}
