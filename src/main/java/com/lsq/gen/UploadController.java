package com.lsq.gen;

import java.io.File;
import java.io.IOException;

import javax.servlet.MultipartConfigElement;

import cn.webank.cnc.mvc.util.ReadExcelTools;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class UploadController {

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 允许cookies跨域
        config.addAllowedOrigin("*");// 允许向该服务器提交请求的URI，*表示全部允许。。这里尽量限制来源域，比如http://xxxx:8080 ,以降低安全风险。。
        config.addAllowedHeader("*");// 允许访问的头信息,*表示全部
        config.setMaxAge(18000L);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了

        config.addAllowedMethod("*");// 允许提交请求的方法，*表示全部允许，也可以单独设置GET、PUT等
        /*
        config.addAllowedMethod("HEAD");
        config.addAllowedMethod("GET");// 允许Get的请求方法
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        */
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    //  private static final Logger logger = LoggerFactory.getLogger(Application.class);
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation("d:/tmp");
        return factory.createMultipartConfig();
    }

    @RequestMapping(value = "/upload")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile file) throws IOException{
        //logger.info("name: "+name);
        ReadExcelTools.readExcel(file);
        System.out.println("文件上传");
        if (file.isEmpty()) {
            return "文件为空";
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        //logger.info("上传的文件名为:" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        // logger.info("上传的后缀名为:" + suffixName);
        // 文件上传路径
       // String filePath = "d:/roncoo/ttt/";
        String filePath = "";
        // 解决中文问题,liunx 下中文路径,图片显示问题
        //fileName = UUID.randomUUID() + suffixName;
        File dest = new File(filePath + fileName);
        System.out.println("文件"+dest.getParentFile());
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            file.transferTo(dest);
            return "上传成功";
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "上传失败";
    }
}