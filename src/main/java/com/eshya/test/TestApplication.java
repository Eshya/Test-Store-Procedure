package com.eshya.test;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class TestApplication {

	@Autowired
	private static JdbcTemplate jdbcTemplate;

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(TestApplication.class, args);
//		callStoredProcedure(context,1);
//		callStoredProcedure(context,2);
		generateReport(context,"d05b8c4f-4b45-435c-868f-27ef9303166a", "2023-07-5");
	}

	public static void generateReport(ApplicationContext context,String accountId, String billRun) {
		try {
			// make connection
			JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
			Connection connection = jdbcTemplate.getDataSource().getConnection();
			// Load the JasperReport XML file
			ClassPathResource reportResourcePage1 = new ClassPathResource("Invoice_page1.jrxml");
			ClassPathResource reportResourcePage2 = new ClassPathResource("Invoice_page2.jrxml");
			ClassPathResource reportResourcePage3 = new ClassPathResource("Invoice_page3.jrxml");
			JasperReport jasperReportPage1 = JasperCompileManager.compileReport(reportResourcePage1.getInputStream());
			JasperReport jasperReportPage2 = JasperCompileManager.compileReport(reportResourcePage2.getInputStream());
			JasperReport jasperReportPage3 = JasperCompileManager.compileReport(reportResourcePage3.getInputStream());

			// Create parameters for the query string
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("accountId", accountId);
			parameters.put("billrun", billRun);

			// Create Array List to Contain Jasper Page
			List<JasperPrint> jasperPrintList = new ArrayList<>();
			// Generate the report using JasperReports
			JasperPrint jasperPrintPage1 = JasperFillManager.fillReport(jasperReportPage1, parameters, connection);
			JasperPrint jasperPrintPage2 = JasperFillManager.fillReport(jasperReportPage2, parameters, connection);
			JasperPrint jasperPrintPage3 = JasperFillManager.fillReport(jasperReportPage3, parameters, connection);

			jasperPrintList.add(jasperPrintPage1);
			jasperPrintList.add(jasperPrintPage2);
			jasperPrintList.add(jasperPrintPage3);

			JRPdfExporter exporter = new JRPdfExporter();

			//Set the JasperPrint list as the source
			exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, jasperPrintList);

			// Set the output file path
			String outputFile = "invoice-"+accountId+"-"+billRun+".pdf";
			exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFile);

			// Export the report
			exporter.exportReport();

			System.out.println("Invoice generated successfully. File saved as: " + outputFile);

//			System.out.println("Invoice generated successfully.");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		else if(code == 5){
			String sql = "SELECT * FROM create_invoice('8eab2634-0366-42c8-b2a7-5c630c38cd66')";
			List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

			for (Map<String, Object> row : result) {
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					String column = entry.getKey();
					Object value = entry.getValue();
					System.out.println(column + ": " + value);
				}
				System.out.println("-----------");
			}
		}
		else{
			System.out.println("Wrong code");
		}



	}

}
