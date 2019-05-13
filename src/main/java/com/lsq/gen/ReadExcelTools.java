package com.lsq.gen;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
/**
 * <p>
 * 文件功能说明：
 *       			
 * </p>
 * 
 * @Author linshiqin
 *         <p>
 *         <li>2018年12月6日-下午5:21:32</li>
 *         <li>修改记录</li>
 *         <li>-----------------------------------------------------------</li>
 *         <li>标记：修订内容</li>
 *         <li>linshiqin：创建注释模板</li>
 *         <li>-----------------------------------------------------------</li>
 *         </p>
 */
public class ReadExcelTools {

	 private final static String xls = "xls";
	    private final static String xlsx = "xlsx";

	    /**
	     * 读入excel文件，解析后返回
	     * @param file
	     * @throws IOException
	     */
	    public static List<String[]> readExcel(MultipartFile file) throws IOException{
	        //检查文件
	        checkFile(file);
	        //获得Workbook工作薄对象
	        Workbook workbook = getWorkBook(file);
	        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
	        List<String[]> list = new ArrayList<String[]>();
	        if(workbook != null){
	            for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){
	                //获得当前sheet工作表
	                Sheet sheet = workbook.getSheetAt(sheetNum);
	                if(sheet == null){
	                    continue;
	                }
	                //获得当前sheet的开始行
	                int firstRowNum  = sheet.getFirstRowNum();
	                //获得当前sheet的结束行
	                int lastRowNum = sheet.getLastRowNum();
	                //循环除了第一行的所有行
	                for(int rowNum = firstRowNum+1;rowNum <= lastRowNum;rowNum++){ //为了过滤到第一行因为我的第一行是数据库的列
	                    //获得当前行
	                    Row row = sheet.getRow(rowNum);
	                    if(row == null){
	                        continue;
	                    }
	                    //获得当前行的开始列
	                    int firstCellNum = row.getFirstCellNum();
	                    //获得当前行的列数
	                    int lastCellNum = row.getLastCellNum();//为空列获取
//	                    int lastCellNum = row.getPhysicalNumberOfCells();//为空列不获取
//	                    String[] cells = new String[row.getPhysicalNumberOfCells()];
	                    String[] cells = new String[row.getLastCellNum()];
	                    //循环当前行
	                    for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){
	                        Cell cell = row.getCell(cellNum);
	                        cells[cellNum] = getCellValue(cell);
	                    }
	                    list.add(cells);
	                }
	            }
	        }
	        System.out.println(JSON.toJSONString(list));
	        return list;
	    }
	    
	    public static void checkFile(MultipartFile file) throws IOException{
	        //判断文件是否存在
	        if(null == file){
	            throw new FileNotFoundException("文件不存在！");
	        }
	        //获得文件名
	        String fileName = file.getOriginalFilename();
	        //判断文件是否是excel文件
	        if(!fileName.endsWith(xls) && !fileName.endsWith(xlsx)){
	            throw new IOException(fileName + "不是excel文件");
	        }
	    }
	    public static Workbook getWorkBook(MultipartFile file) {
	        //获得文件名
	        String fileName = file.getOriginalFilename();
	        //创建Workbook工作薄对象，表示整个excel
	        Workbook workbook = null;
	        try {
	            //获取excel文件的io流
	            InputStream is = file.getInputStream();
	            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
	            if(fileName.endsWith(xls)){
	                //2003
	                workbook = new HSSFWorkbook(is);
	            }else if(fileName.endsWith(xlsx)){
	                //2007
	                workbook = new XSSFWorkbook(is);
	            }
	        } catch (IOException e) {
	            System.out.println(e);
	        }
	        return workbook;
	    }
	    public static String getCellValue(Cell cell){
	        String cellValue = "";
	        if(cell == null){
	            return cellValue;
	        }
	        //把数字当成String来读，避免出现1读成1.0的情况
	        if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
	            cell.setCellType(Cell.CELL_TYPE_STRING);
	        }
	        //判断数据的类型
	        switch (cell.getCellType()){
	            case Cell.CELL_TYPE_NUMERIC: //数字
	                cellValue = String.valueOf(cell.getNumericCellValue());
	                break;
	            case Cell.CELL_TYPE_STRING: //字符串
	                cellValue = String.valueOf(cell.getStringCellValue());
	                break;
	            case Cell.CELL_TYPE_BOOLEAN: //Boolean
	                cellValue = String.valueOf(cell.getBooleanCellValue());
	                break;
	            case Cell.CELL_TYPE_FORMULA: //公式
//	                cellValue = String.valueOf(cell.getCellFormula());
	                cellValue = String.valueOf(cell.getStringCellValue());
	                break;
	            case Cell.CELL_TYPE_BLANK: //空值
	                cellValue = "";
	                break;
	            case Cell.CELL_TYPE_ERROR: //故障
	                cellValue = "非法字符";
	                break;
	            default:
	                cellValue = "未知类型";
	                break;
	        }
	        return cellValue;
	    }
}
