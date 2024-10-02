package com.example.myproject.controller;

import com.example.myproject.utils.VerifyCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
@RequestMapping("/code")
@Api(value = "验证码接口", tags = {"验证码管理"})
public class CodeController {

    /**
     * 生成验证码图片
     * @param request
     * @param res
     * @throws IOException
     */
    @GetMapping("/code")
    @ApiOperation(value = "生成验证码", notes = "生成并返回一个新的验证码图片")
    public void code(HttpServletRequest request,
                     HttpServletResponse res) throws IOException {
        VerifyCode code = new VerifyCode();
        BufferedImage image = code.getImage();
        String text = code.getText();
        HttpSession session = request.getSession(true);
        session.setAttribute("code", text);
        VerifyCode.output(image,res.getOutputStream());
    }

}

