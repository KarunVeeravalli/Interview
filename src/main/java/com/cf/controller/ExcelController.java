package com.cf.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.cf.model.Candidate;
import com.cf.model.Domain;
import com.cf.model.User;
import com.cf.repository.ICandidateDao;
import com.cf.repository.IDomainDao;
import com.cf.service.ICandidateService;
import com.cf.service.IDomainService;

@Controller
public class ExcelController {
	@Autowired
	private ICandidateService camdidateService;

	@Autowired
	private IDomainDao domainDao;
	
	@Autowired
	private IDomainService iDomainService;
	
	public Domain findDomain(String domainName) {
		// comment for checking
		Domain domain = domainDao.findByDomainName(domainName);
		return domain;
	}

	@GetMapping("/excelPage")
	public String getExc(HttpServletResponse redirect) {
		if (LoginController.checkUser == null) {
			try {
				redirect.sendRedirect("/login");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "excel";
	}

	int count = 0;

	@PostMapping("/import")
	public String mapReapExcelDatatoDB(@RequestParam("file") MultipartFile reapExcelDataFile, HttpSession session,
			HttpServletResponse redirect) throws IOException {
		boolean isError=false;
		if (LoginController.checkUser == null) {
			try {
				redirect.sendRedirect("/login");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		LocalDate today=LocalDate.now();
		User user = (User) session.getAttribute("loginDetails");
		int count = 0;
		List<Candidate> tempStudentList = new ArrayList<Candidate>();
		try {

			XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
			XSSFSheet worksheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = worksheet.iterator();

			while (rowIterator.hasNext()) {
				Candidate candidate = new Candidate();
				candidate.setUser(user);
				candidate.setStatus("ResumeShortlisted");
				candidate.setCreatedAt(today);
				int i = 0;
				if (count == 0) {
					rowIterator.next();
					count++;
				} else {

					Row row = rowIterator.next();
					// For each row, iterate through all the columns
					Iterator<Cell> cellIterator = row.cellIterator();

					while (cellIterator.hasNext()) {

						Cell cell = cellIterator.next();

						// Check the cell type and format accordingly
						switch (cell.getCellType()) {

						case Cell.CELL_TYPE_NUMERIC:

							if (i == 0) {

								candidate.setCandidateId((int) cell.getNumericCellValue());
//		        		  System.out.print(cell.getNumericCellValue() + "\t");
							} else if (i == 3) {
								candidate.setMobileNumber((long) cell.getNumericCellValue());
//		        		  System.out.print(cell.getNumericCellValue() + "\t");
							} else if (i == 5) {
								candidate.setCgpa((float) cell.getNumericCellValue());
//			        	  System.out.print(cell.getNumericCellValue() + "\t");
							} else if (i == 8) {
								candidate.setExperience((float) cell.getNumericCellValue());
//		        		  System.out.print(cell.getNumericCellValue() + "\t");
							} else if (i == 9) {
								candidate.setAlternateMobileNumber((long) cell.getNumericCellValue());
//		        		  System.out.print(cell.getNumericCellValue() + "\t");
							} else if (i == 10) {
								candidate.setCurrentCtc(((float) cell.getNumericCellValue()));
//		        		  System.out.print(cell.getNumericCellValue() + "\t");
							}
							else if (i == 11) {
								candidate.setExpectedCtc(((float) cell.getNumericCellValue()));
//		        		  System.out.print(cell.getNumericCellValue() + "\t");
							}else if (i == 13) {
								candidate.setMaxRound(((int) cell.getNumericCellValue()));
//				        		  System.out.print(cell.getNumericCellValue() + "\t");
									}
							i++;
							break; 
						case Cell.CELL_TYPE_STRING:
							if (i == 1) {
								candidate.setCandidateName(cell.getStringCellValue());
//			        	  System.out.print(cell.getStringCellValue() + "\t");
							} else if (i == 2) {
								candidate.setEmail(cell.getStringCellValue());
//			        		  System.out.print(cell.getStringCellValue() + "\t");
							} else if (i == 4) {
								candidate.setHighQualification(cell.getStringCellValue());
//			        		  System.out.print(cell.getStringCellValue() + "\t");
							} else if (i == 6) {
								candidate.setRoleAppliedFor(cell.getStringCellValue());
//			        		  System.out.print(cell.getStringCellValue() + "\t");
							} else if (i == 7) {
								candidate.setAlternateEmail(cell.getStringCellValue());
//			        		  System.out.print(cell.getStringCellValue() + "\t");
							} else if (i == 12) {
								String dom = cell.getStringCellValue();
								Domain domain = findDomain(dom);
								if(domain!=null)
								candidate.setDomain(domain);
								else {
									isError=true;
									String error="The Domain Name: "+dom +"in the Domain column does not exist.Kindly Add a new Domain or use existing domain from View Domains Page";
									return "redirect:/errorPage"+"?"+"error="+error;
								}
							}
							i++;
							break;
						}
					}
					tempStudentList.add(candidate);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(tempStudentList);
		if(!isError)
		camdidateService.bulkSaveCandidate(tempStudentList);
		return "redirect:/viewCandidates";
	}
	
	@GetMapping("/excelformatPage")
	public ModelAndView generateExcelPage(HttpServletResponse redirect) {
		if (LoginController.checkUser == null) {
			try {
				redirect.sendRedirect("/login");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Candidate candidate=new Candidate();
		List<Domain> domain = iDomainService.viewDomainList();
		ModelAndView mav=new ModelAndView("GenerateExcelFormat");
		mav.addObject("candidate", candidate);
		mav.addObject("domain", domain);
		return mav;
	}
	
	@PostMapping(value="/generateBulkUploadExcel",consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<byte[]> generateBulkUploadExcel(@RequestParam Integer currentRound,@RequestParam Float experience,@RequestParam Integer maxRound,@RequestParam String roleAppliedFor,@RequestParam Domain domain) throws IOException{
		System.err.println("Entered generate ExcelMethods:"+currentRound);
		Workbook workbook = new XSSFWorkbook();
		LocalDate date=LocalDate.now();
		LocalTime time=LocalTime.now();
		String excelFileName=date.toString();
        // Create a sheet
        Sheet sheet = workbook.createSheet(excelFileName);
     // Create a header row and add heading cells
//        Row headerRow = sheet.createRow(0);
//        Cell headerCell1 = headerRow.createCell(0);
//        headerCell1.setCellValue("CandidateId");
//        Cell headerCell2 = headerRow.createCell(1);
//        headerCell2.setCellValue("Candidate Name");
//        Cell headerCell3 = headerRow.createCell(2);
//        headerCell3.setCellValue("Email");
//        Cell headerCell4 = headerRow.createCell(3);
//        headerCell4.setCellValue("Mobile Number");
//        Cell headerCell5 = headerRow.createCell(4);
//        headerCell5.setCellValue("Qualification");
//        Cell headerCell6 = headerRow.createCell(5);
//        headerCell6.setCellValue("cgpa");
//        Cell headerCell7 = headerRow.createCell(6);
//        headerCell7.setCellValue("Role Applied");
//        Cell headerCell8 = headerRow.createCell(7);
//        headerCell8.setCellValue("Other Email");
//        Cell headerCell9 = headerRow.createCell(8);
//        headerCell9.setCellValue("Experience");
//        Cell headerCell10 = headerRow.createCell(9);
//        headerCell10.setCellValue("Other Mobile");
//        Cell headerCell11 = headerRow.createCell(10);
//        headerCell11.setCellValue("Current CTC");
//        Cell headerCell12 = headerRow.createCell(11);
//        headerCell12.setCellValue("Expected CTC");
//        Cell headerCell13 = headerRow.createCell(12);
//        headerCell13.setCellValue("Domain");
//        Cell headerCell14 = headerRow.createCell(13);
//        headerCell14.setCellValue("Max Rounds");
//        sheet.autoSizeColumn(0);
        Candidate candidate=new Candidate();
        candidate.setMaxRound(maxRound);
        candidate.setCurrentRound(currentRound);
        candidate.setExperience(experience);
        candidate.setRoleAppliedFor(roleAppliedFor);
        candidate.setCurrentCtc(0.0f);
        candidate.setExpectedCtc(0.0f);
//        Domain domain=new Domain();
//        domain.setDomainName(domainName);
        candidate.setDomain(domain);
//        int rowNum = 0;
//        int colNum=0;
//        int i=0;
//       while(i==candidate.getCurrentRound()) {
//    	   i++;
//            Row row = sheet.createRow(rowNum++);
//             colNum = 0;
//                Cell cell = row.createCell(colNum++);
//                if(!candidate.getRoleAppliedFor().isBlank()) {
//                    Row row6 = sheet.createRow(i);
//                    Cell cell6 = row6.createCell(i);
//                    cell6.setCellValue(candidate.getRoleAppliedFor());
//                    }
////                    Row row7 = sheet.createRow(i);
////                    Cell cell7 = row7.createCell(i);
////                    cell7.setCellValue("Hello");
//                    if(candidate.getExperience()>=0) {
//                    Row row8 = sheet.createRow(i);
//                    Cell cell8 = row8.createCell(i);
//                    cell8.setCellValue(candidate.getExperience());
//                    }else {
//                    	Row row8 = sheet.createRow(i);
//                        Cell cell8 = row8.createCell(i);
//                        cell8.setCellValue(0.0);
//                    }
//                    if(candidate.getExperience()>0) {
//                    if(candidate.getCurrentCtc() ==0) {
//                    Row row10 = sheet.createRow(i);
//                    Cell cell10 = row10.createCell(i);
//                    cell10.setCellValue(0.0);
//                    }
//                    if(candidate.getExpectedCtc()==0) {
//                    Row row11 = sheet.createRow(i);
//                    Cell cell11 = row11.createCell(i);
//                    cell11.setCellValue(0.0);
//                    }
//                    }
//                    if(!candidate.getDomain().getDomainName().isBlank()) {
//                    Row row12 = sheet.createRow(i);
//                    Cell cell12 = row12.createCell(i);
//                    cell12.setCellValue(candidate.getDomain().getDomainName());
//                    }
//                    if(candidate.getExperience()==0) {
//                    Row row13 = sheet.createRow(i);
//                    Cell cell13 = row13.createCell(i);
//                    cell13.setCellValue(2);
//                    }else {
//                    	 Row row13 = sheet.createRow(i);
//                         Cell cell13 = row13.createCell(i);
//                         cell13.setCellValue(candidate.getMaxRound());	
//                    }
//                   
//
//                    // Auto-size the columns
//                    sheet.autoSizeColumn(i);
//            
//        }
        int i=0;
        int rowCount=0;
        int columnCount=0;
        while(i<candidate.getCurrentRound()) {
        	Row headerRow = sheet.createRow(rowCount++);
        	if(i==0) {
                Cell headerCell1 = headerRow.createCell(0);
                headerCell1.setCellValue("SL No");
                Cell headerCell2 = headerRow.createCell(1);
                headerCell2.setCellValue("Candidate Name");
                Cell headerCell3 = headerRow.createCell(2);
                headerCell3.setCellValue("Email");
                Cell headerCell4 = headerRow.createCell(3);
                headerCell4.setCellValue("Mobile Number");
                Cell headerCell5 = headerRow.createCell(4);
                headerCell5.setCellValue("Qualification");
                Cell headerCell6 = headerRow.createCell(5);
                headerCell6.setCellValue("cgpa");
                Cell headerCell7 = headerRow.createCell(6);
                headerCell7.setCellValue("Role Applied");
                Cell headerCell8 = headerRow.createCell(7);
                headerCell8.setCellValue("Other Email");
                Cell headerCell9 = headerRow.createCell(8);
                headerCell9.setCellValue("Experience");
                Cell headerCell10 = headerRow.createCell(9);
                headerCell10.setCellValue("Other Mobile");
                Cell headerCell11 = headerRow.createCell(10);
                headerCell11.setCellValue("Current CTC");
                Cell headerCell12 = headerRow.createCell(11);
                headerCell12.setCellValue("Expected CTC");
                Cell headerCell13 = headerRow.createCell(12);
                headerCell13.setCellValue("Domain");
                Cell headerCell14 = headerRow.createCell(13);
                headerCell14.setCellValue("Max Rounds");
                sheet.autoSizeColumn(0);
        	}
        	if(i>0) {
        		Cell cell0 = headerRow.createCell(0);
                cell0.setCellValue(i);
        if(!candidate.getRoleAppliedFor().isBlank()) {
        
        Cell cell6 = headerRow.createCell(6);
        cell6.setCellValue(candidate.getRoleAppliedFor());
        System.err.println("role applied If"+cell6.getStringCellValue());
        }
//        Row row7 = sheet.createRow(i);
//        Cell cell7 = row7.createCell(i);
//        cell7.setCellValue("Hello");
        if(candidate.getExperience()>=0) {      	
        Cell cell8 = headerRow.createCell(8);
        cell8.setCellValue(candidate.getExperience());
        System.err.println("Exp If"+cell8.getNumericCellValue());
        }else {       	
            Cell cell8 = headerRow.createCell(i);
            cell8.setCellValue(0.0);
            System.err.println("Exp else");
        }
        if(candidate.getExperience()==0) {
        if(candidate.getCurrentCtc() ==0) {
        Cell cell10 = headerRow.createCell(10);
        cell10.setCellValue((Float)candidate.getCurrentCtc());
        System.err.println("Current Ctc"+cell10.getNumericCellValue());
        }
        if(candidate.getExpectedCtc()==0) {
        Cell cell11 = headerRow.createCell(11);
        cell11.setCellValue((Float)candidate.getExpectedCtc());
        System.err.println("Expectede Ctc"+cell11.getNumericCellValue());
        }
        }
        if(!candidate.getDomain().getDomainName().isBlank()) {
        Cell cell12 = headerRow.createCell(12);
        cell12.setCellValue((String)candidate.getDomain().getDomainName());
        System.err.println("Domain Name"+cell12.getStringCellValue());
        }
        if(candidate.getExperience()==0) {
        Cell cell13 = headerRow.createCell(13);
        cell13.setCellValue((Integer)2);
        System.err.println("Max Round"+cell13.getNumericCellValue());
        }else {
             Cell cell13 = headerRow.createCell(13);
             cell13.setCellValue((Integer)candidate.getMaxRound());	
        }
        	}

        // Auto-size the columns
        sheet.autoSizeColumn(columnCount);
        columnCount++;
//        	}
        i++;
        }
        // Write the workbook to a byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        String excelFileNameDownload=excelFileName+".xlsx";
        // Set the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", excelFileNameDownload);

        // Return the Excel file as a byte array
        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
	}
}