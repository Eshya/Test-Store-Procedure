package com.eshya.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class TestApplication {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(TestApplication.class, args);
		callStoredProcedure(context,3);
	}

	public static void callStoredProcedure(ApplicationContext context,int code) {
		// Mendapatkan bean JdbcTemplate
		JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);

		if(code == 1){
			jdbcTemplate.execute("CALL create_table_test()");
			System.out.println("Prosedur create_table_test() berhasil dipanggil.");
		}
		else if(code == 2){
			jdbcTemplate.execute("CALL create_dummy()");
			System.out.println("Prosedur create_dummy() berhasil dipanggil.");
		}
		else if(code == 3){
			jdbcTemplate.execute("CALL drop_table_test('invoice')");
			jdbcTemplate.execute("CALL drop_table_test('purchase')");
			jdbcTemplate.execute("CALL drop_table_test('discount')");
			jdbcTemplate.execute("CALL drop_table_test('product')");
			jdbcTemplate.execute("CALL drop_table_test('account')");
			System.out.println("Prosedur Drop All Table berhasil dipanggil.");
		}
		else if(code == 4){
			jdbcTemplate.execute("DROP PROCEDURE IF EXISTS drop_table_test(text)");
			System.out.println("Prosedur drop id exist berhasil dipanggil.");
		}
		else{
			System.out.println("Wrong code");
		}



	}

}
