package com.dotcms.master_control.utils;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Map;

public class PDFCreator {


    private static final Logger log = LogManager.getLogger(PDFCreator.class);

    public static final String OUTPUT_HTML_FILE_PATH = "generatedHTML/";
    public static final String OUTPUT_PDF_FILE_PATH = "generatedPDF/";

    public static String generateHTMLFromFTL(Map<String,Object> fieldMap,String body,String tenantId) throws IOException, TemplateException {
        String filePath = "";
        File generatedHTMLFolder = new File(OUTPUT_HTML_FILE_PATH);
        filePath = OUTPUT_HTML_FILE_PATH + tenantId + ".html";
        log.info("Output HTML file path :::" + filePath);
        String bodyContent=body;
        FileUtils.forceMkdir(generatedHTMLFolder);
        File file= new File(filePath);
        log.info("Reached: : "+ file.exists());
        log.info("File Name: : "+ file.getName());
        Writer fileWriter= new FileWriter(file.getAbsoluteFile());
        log.info("---fileWriter--"+fileWriter.toString());
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("taxHTMLString", bodyContent);
        cfg.setTemplateLoader(stringLoader);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        log.info("Fetching Template from string..");
        Template ftlTemplateObj = cfg.getTemplate("taxHTMLString", "UTF-8");
        log.info("Template in processing state..");
        ftlTemplateObj.process(fieldMap, fileWriter);
        log.info("Processing completed..."+ftlTemplateObj.toString());
        fileWriter.flush();
        fileWriter.close();
        return filePath;
    }

//    public static Map<String, Object> getFieldMap() {
//        Map<String, Object> map = new HashMap();
//        map.put("tenantId", "13");
//        return map;
//    }


    private static String getRunnableCommand(ProcessBuilder processBuilder)
    {
        List<String> commandsList = processBuilder.command();
        StringBuilder runnableCommandBuilder = new StringBuilder();
        int commandIndex = 0;
        for (String command : commandsList)
        {
            if (command.contains(" "))
            {
                runnableCommandBuilder.append("\"");
            }
            runnableCommandBuilder.append(command);

            if (command.contains(" "))
            {
                runnableCommandBuilder.append("\"");
            }

            if (commandIndex != commandsList.size() - 1)
            {
                runnableCommandBuilder.append(" ");
            }
            commandIndex++;
        }
        return runnableCommandBuilder.toString();
    }
    public static Boolean generatePDFFile(String htmlPath, String tenantId) throws IOException {
        try {
            log.info("Entering Class PDFCreator ::: Method ::: generatePDFFile");
            File generatedPDFFolder = new File(OUTPUT_PDF_FILE_PATH);
            FileUtils.forceMkdir(generatedPDFFolder);
            String pdfFileName = OUTPUT_PDF_FILE_PATH + tenantId +".pdf";

//            File file= new File(pdfFileName);
//            file.createNewFile();
//            log.info("Reached  PDF: : "+ file.exists());
//            log.info("File path  PDF: : "+ file.getPath());
//            log.info(file.isDirectory());
//
//
//            File file2= new File(htmlPath);
//            log.info("Reached  HTML: : "+ file2.exists());
//            log.info("File path  HTML: : "+ file2.getPath());
//            log.info(file2.isDirectory());


            ProcessBuilder builder =
                    new ProcessBuilder("wkhtmltopdf","--zoom","1.2","--margin-top","0","--margin-bottom","0", "--margin-left","0","--margin-right","0",htmlPath, pdfFileName);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            log.info(getRunnableCommand(builder));
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                log.info(line);
            }
            return Boolean.TRUE;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return Boolean.FALSE;
        }
    }


}
