package com.lc.controller;

import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.util.UUID;  
  
import javax.imageio.ImageIO;  
import javax.servlet.http.Cookie;  
import javax.servlet.http.HttpServletResponse;  
  
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.http.MediaType;  
import org.springframework.web.bind.annotation.RequestMapping;  
import org.springframework.web.bind.annotation.RequestMethod;  
import org.springframework.web.bind.annotation.ResponseBody;  
import org.springframework.web.bind.annotation.RestController;

import com.lc.util.JedisAdapter;

import cn.apiclub.captcha.Captcha;  
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;  
import cn.apiclub.captcha.gimpy.FishEyeGimpyRenderer;  
  
@RestController  
@RequestMapping("/captcha")  
public class CaptchaController {  
      
    @Autowired  
    JedisAdapter jedisAdapter;
    
    private static int captchaExpires = 3*60; //超时时间3min  
    private static int captchaW = 200;  
    private static int captchaH = 60;  
      
    @RequestMapping(value = "/getcaptcha", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)  
    public @ResponseBody byte[] getCaptcha(HttpServletResponse response) {  
        //生成验证码  
        String uuid = UUID.randomUUID().toString();  
        Captcha captcha = new Captcha.Builder(captchaW, captchaH)  
                .addText().addBackground(new GradiatedBackgroundProducer())  
                .gimp(new FishEyeGimpyRenderer())  
                .build();  
          
        //将验证码以<key,value>形式缓存到redis  
        jedisAdapter.setExpireObject("CaptchaCode", captcha.getAnswer(), captchaExpires);  
          
        ByteArrayOutputStream bao = new ByteArrayOutputStream();  
        try {  
            ImageIO.write(captcha.getImage(), "png", bao);  
            return bao.toByteArray();  
        } catch (IOException e) {  
            return null;  
        }  
    }  
}  